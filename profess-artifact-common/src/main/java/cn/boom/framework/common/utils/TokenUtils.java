package cn.boom.framework.common.utils;
/**
 *
 * 用于生成随机的六位验证码
 *
 */
public class TokenUtils {

    private static String token;

    public void setToken(String token) {
        this.token = token;
    }

    public static String getToken(){

        return String.valueOf((int)((Math.random()*9+1)*100000));

    }

    public static void main(String[] args) {

        System.out.println(TokenUtils.getToken());
    }
}
