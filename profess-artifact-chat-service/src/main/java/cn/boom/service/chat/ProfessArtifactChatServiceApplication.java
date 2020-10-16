package cn.boom.service.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = {"cn.boom.service.chat"}) // ExceptionHandler
public class ProfessArtifactChatServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProfessArtifactChatServiceApplication.class, args);
    }
}
