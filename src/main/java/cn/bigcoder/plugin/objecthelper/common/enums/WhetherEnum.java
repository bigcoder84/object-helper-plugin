package cn.bigcoder.plugin.objecthelper.common.enums;

/**
 * @author: Jindong.Tian
 * @date: 2023-12-24
 **/
public enum WhetherEnum implements CommonEnum {
    YES("yes"),
    NO("no"),
    ;
    private String code;

    WhetherEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static WhetherEnum nameOf(String modify) {
        if (modify == null) {
            return null;
        }
        for (WhetherEnum item : values()) {
            if (modify.equals(item.getCode())) {
                return item;
            }
        }
        return null;
    }
}
