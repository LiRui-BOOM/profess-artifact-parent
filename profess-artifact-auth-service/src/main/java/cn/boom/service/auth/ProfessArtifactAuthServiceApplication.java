package cn.boom.service.auth;

import cn.boom.service.auth.config.RsaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties({RsaConfiguration.class})
@ComponentScan(basePackages = {"cn.boom.framework.common.exception","cn.boom.service.auth"}) // ExceptionHandler
public class ProfessArtifactAuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProfessArtifactAuthServiceApplication.class, args);
    }
}
