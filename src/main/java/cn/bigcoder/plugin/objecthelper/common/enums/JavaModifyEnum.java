package cn.bigcoder.plugin.objecthelper.common.enums;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public enum JavaModifyEnum {
    PUBLIC("public", 1),
    PROTECTED("protected", 1),
    PRIVATE("private", 1),
    STATIC("static", 2),
    FINAL("final", 3),
    ;
    private String name;
    private Integer priority;

    JavaModifyEnum(String name, Integer priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public JavaModifyEnum nameOf(String modify){
        if (modify == null){
            return null;
        }
        for (JavaModifyEnum item : values()) {
            if (modify.equals(item.getName())){
                return item;
            }
        }
        return null;
    }
}
