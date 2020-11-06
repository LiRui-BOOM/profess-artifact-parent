package cn.boom.framework.common.exception;

public enum ExceptionCodeEnum {

    SERVICE_CALL_OK(200, "OK!"),
    ILLEGAL_ARGS_EXCEPTION(400, "参数不合法！"),
    DATA_NOT_EXIST_EXCEPTION(401, "此数据在数据库中不存在！"),
    ILLEGAL_ACCESS_EXCEPTION(403, "权限不足，不允许访问！"),
    UNKNOW_EXCEPTION(500,"服务器发生异常！"),
    SERVICE_CALL_RESULT_EXCEPTION(504,"微服务远程调用返回结果异常！"),
    SERVICE_CALL_EXCEPTION(505,"微服务调用失败！"),
    THREE_SERVICE_CALL_EXCEPTION(506, "第三方服务调用出现异常！"),;

    private int code;
    private String msg;

    ExceptionCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
