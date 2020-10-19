package cn.boom.framework.model.enums;

public enum TencentSignEnum {

    TENCENT_SIGN_YAO_CHUAN_BU_YONG_JIANG("摇船不用桨");

    private String sign;

    public static boolean contains(String type){
        for(TencentSignEnum typeEnum : TencentSignEnum.values()){

            if (typeEnum.sign.equals(type)) {
                return true;
            }

        }
        return false;
    }

    TencentSignEnum(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }
}
