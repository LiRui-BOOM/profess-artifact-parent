package cn.boom.service.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"cn.boom.service.gateway"}) // ExceptionHandler
public class ProfessArtifactUserGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProfessArtifactUserGatewayApplication.class, args);
    }
}
