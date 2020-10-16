package cn.boom.service.user.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("cn.edu.acat.backstage.interview.dao")
public class MybatisPlusConfig {
    /**
     * 配置Mybatis-plus分页插件,解决分页total、pages总是0的bug
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}