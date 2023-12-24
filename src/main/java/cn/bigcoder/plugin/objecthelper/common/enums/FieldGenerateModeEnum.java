package cn.bigcoder.plugin.objecthelper.common.enums;

/**
 * @author: Jindong.Tian
 * @date: 2023-12-24
 **/
public enum FieldGenerateModeEnum implements CommonEnum {
    SOURCE("source"),
    TARGET("target"),
    ;
    private String code;

    FieldGenerateModeEnum(String code) {
        this.code = code;
    }


    public static FieldGenerateModeEnum nameOf(String modify) {
        if (modify == null) {
            return null;
        }
        for (FieldGenerateModeEnum item : values()) {
            if (modify.equals(item.getCode())) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }
}
