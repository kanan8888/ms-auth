package az.authservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return () -> Optional.ofNullable(
//                        SecurityContextHolder.getContext()
//                                .getAuthentication()
//                )
//                .map(Authentication::getName);
//    }
}
