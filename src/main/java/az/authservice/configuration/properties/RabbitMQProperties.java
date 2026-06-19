package az.authservice.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
@Getter @Setter
public class RabbitMQProperties {
    private String exchange;
    private RoutingKeys routingKeys;

    @Getter @Setter
    public static class RoutingKeys {
        private String verification;
    }
}
