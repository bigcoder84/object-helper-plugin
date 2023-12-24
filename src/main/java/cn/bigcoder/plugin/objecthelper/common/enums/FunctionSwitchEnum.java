package cn.bigcoder.plugin.objecthelper.common.enums;

/**
 * @author: Jindong.Tian
 * @date: 2023-12-24
 **/
public enum FunctionSwitchEnum implements CommonEnum {
    OPEN("open"),
    CLOSE("close"),
    ;
    private String code;

    FunctionSwitchEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static FunctionSwitchEnum nameOf(String modify) {
        if (modify == null) {
            return null;
        }
        for (FunctionSwitchEnum item : values()) {
            if (modify.equals(item.getCode())) {
                return item;
            }
        }
        return null;
    }
}
