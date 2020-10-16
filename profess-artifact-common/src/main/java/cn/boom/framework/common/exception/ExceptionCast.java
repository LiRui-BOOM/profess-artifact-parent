package cn.boom.framework.common.exception;

public class ExceptionCast {

    public static void cast(String msg) {
        throw new RRException(msg);
    }

    public static void cast(String msg, int code) {
        throw new RRException(msg,code);
    }
}
