package cn.boom.service.chat.client;

import cn.boom.framework.common.response.R;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.service.chat.client.handler.UserServiceClientHandler;
import cn.boom.service.chat.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Component
@FeignClient(value = "profess-artifact-user-service", configuration = FeignConfiguration.class, fallback = UserServiceClientHandler.class)
public interface UserServiceClient {

    @GetMapping("user/findOneById/{id}")
    public abstract R findOneById(@PathVariable("id") Long id);
}
