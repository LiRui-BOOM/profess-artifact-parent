package cn.boom.service.user;

import cn.boom.service.user.config.RsaConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties({RsaConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan(basePackages = {"cn.boom.service.user.dao"})
//@ComponentScan(basePackages = {"cn.boom.framework.common.exception","cn.boom.service.user"}) // ExceptionHandler
public class ProfessArtifactUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProfessArtifactUserServiceApplication.class, args);
    }
}
