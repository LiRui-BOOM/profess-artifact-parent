package cn.boom.framework.model.enums;

public enum TencentTemplateEnum {

    TENCENT_TEMPLATE_CHECKCODE(747339, "验证码"),;

    private int templateId;
    private String templateName;


    TencentTemplateEnum(int templateId, String templateName) {
        this.templateId = templateId;
        this.templateName = templateName;
    }

    public static boolean contains(int type){
        for(TencentTemplateEnum typeEnum : TencentTemplateEnum.values()){

            if (typeEnum.templateId == type) {
                return true;
            }

        }
        return false;
    }

    public String getTemplateName() {
        return templateName;
    }

    public int getTemplateId() {
        return templateId;
    }
}
