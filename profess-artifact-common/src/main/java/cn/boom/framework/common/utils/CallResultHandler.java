package cn.boom.framework.common.utils;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.common.response.R;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CallResultHandler{

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void callResCheck(R res) {

        if (res == null) {
            ExceptionCast.cast("远程调用返回值为null！");
        }

        if ((Integer) res.get("code") != ExceptionCodeEnum.SERVICE_CALL_OK.getCode()) {
            ExceptionCast.cast("远程调用返回值状态非法：" + res.get("msg"));
        }
    }

    public static <T> T getData(R res, Object key, Class<T> cls) {

        if (key == null) {
            ExceptionCast.cast("Object key 不能为空！");
        }

        callResCheck(res);

        return mapper.convertValue(res.get(key), cls);
    }
}
