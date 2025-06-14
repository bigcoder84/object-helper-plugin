package cn.bigcoder.plugin.objecthelper.common.enums;

/**
 * @author: Jindong.Tian
 * @date: 2023-12-24
 **/
public enum EnableEnum implements CommonEnum {
    ENABLE("enable"),
    DISABLE("disable"),
    ;
    private String code;

    EnableEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static EnableEnum nameOf(String modify) {
        if (modify == null) {
            return null;
        }
        for (EnableEnum item : values()) {
            if (modify.equals(item.getCode())) {
                return item;
            }
        }
        return null;
    }
}
