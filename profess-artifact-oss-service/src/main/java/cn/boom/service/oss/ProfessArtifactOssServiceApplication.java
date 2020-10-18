package cn.boom.service.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@ComponentScan(basePackages = {"cn.boom.framework.common.exception","cn.boom.service.oss"})
@SpringBootApplication
public class ProfessArtifactOssServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProfessArtifactOssServiceApplication.class, args);
    }
}
