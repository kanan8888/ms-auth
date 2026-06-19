package az.authservice.messaging;

import az.authservice.configuration.properties.RabbitMQProperties;
import az.authservice.enums.VerificationType;
import az.authservice.event.VerificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventPublisher {

    private final AmqpTemplate amqpTemplate;
    private final RabbitMQProperties rabbitMQProperties;

    public void publishVerification(UUID userId, String recipient, VerificationType type, String otpCode) {
        var event = VerificationEvent.builder()
                .userId(userId)
                .recipient(recipient)
                .otpCode(otpCode)
                .type(type)
                .timestamp(Instant.now().toString())
                .build();

        amqpTemplate.convertAndSend(
                rabbitMQProperties.getExchange(),
                rabbitMQProperties.getRoutingKeys().getVerification(),
                event
        );

        log.info("ActionLog.publishVerification.success - userId: {}, type: {}", userId, type);
    }
}
