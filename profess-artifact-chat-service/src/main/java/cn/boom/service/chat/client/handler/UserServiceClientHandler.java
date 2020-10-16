package cn.boom.service.chat.client.handler;

import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.common.response.R;
import cn.boom.service.chat.client.UserServiceClient;
import org.springframework.stereotype.Component;

/**
 * 服务降级处理
 */
@Component
public class UserServiceClientHandler implements UserServiceClient {

    @Override
    public R findOneById(Long id) {
        return R.error(ExceptionCodeEnum.SERVICE_CALL_EXCEPTION.getCode(), "User服务调用失败:" + "findUserById(),id=" + id);
    }
}
