package cn.boom.service.note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
//@EnableConfigurationProperties({RsaConfiguration.class})
@ComponentScan(basePackages = {"cn.boom.framework.common.exception", "cn.boom.service.note"}) // ExceptionHandler
public class ProfessArtifactNoteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProfessArtifactNoteServiceApplication.class, args);
    }
}
