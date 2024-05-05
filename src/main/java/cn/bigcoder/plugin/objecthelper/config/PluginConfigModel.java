package cn.bigcoder.plugin.objecthelper.config;

import cn.bigcoder.plugin.objecthelper.common.enums.FieldGenerateModeEnum;
import cn.bigcoder.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import cn.bigcoder.plugin.objecthelper.common.enums.WhetherEnum;
import java.util.Objects;

/**
 * @author: Jindong.Tian
 * @date: 2022-08-31
 **/
public class PluginConfigModel {

    /**
     * 是否开启 Class To Json 功能，默认为开启状态
     */
    private FunctionSwitchEnum jsonSwitch = FunctionSwitchEnum.OPEN;
    /**
     * 是否开启 Class To Thrift IDL 功能，默认为开启状态
     */
    private FunctionSwitchEnum thriftSwitch = FunctionSwitchEnum.OPEN;
    /**
     * 是否开启 Class To XML 功能，默认为开启状态
     */
    private FunctionSwitchEnum xmlSwitch = FunctionSwitchEnum.OPEN;
    /**
     * 是否开启 Object Copy Method 功能，默认为开启状态
     */
    private FunctionSwitchEnum objectCopySwitch = FunctionSwitchEnum.OPEN;
    /**
     * Object Copy Method 功能中，以Source/Target对象为基础生成字段拷贝
     */
    private FieldGenerateModeEnum objectCopyMethodFieldGenerateMode = FieldGenerateModeEnum.TARGET;

    /**
     * Object Copy Method 功能中，Source 和 Target 对象之间差异的字段，是否以代码注释的形式生成代码
     */
    private WhetherEnum objectCopyMethodFieldGenerateAnnotation = WhetherEnum.YES;

    /**
     * Object Copy Method 功能中，使用builder模式生成拷贝代码时的判断依据，当目标对象类中包含正则所指定的方法，则默认按照builder模式生成，否则使用set模式生成
     */
    private String builderInstanceMethodName = ".*(builder|newBuilder).*";

    public FunctionSwitchEnum getJsonSwitch() {
        return jsonSwitch;
    }

    public void setJsonSwitch(FunctionSwitchEnum jsonSwitch) {
        this.jsonSwitch = jsonSwitch;
    }

    public FunctionSwitchEnum getThriftSwitch() {
        return thriftSwitch;
    }

    public void setThriftSwitch(FunctionSwitchEnum thriftSwitch) {
        this.thriftSwitch = thriftSwitch;
    }

    public FunctionSwitchEnum getXmlSwitch() {
        return xmlSwitch;
    }

    public void setXmlSwitch(FunctionSwitchEnum xmlSwitch) {
        this.xmlSwitch = xmlSwitch;
    }

    public FunctionSwitchEnum getObjectCopySwitch() {
        return objectCopySwitch;
    }

    public void setObjectCopySwitch(FunctionSwitchEnum objectCopySwitch) {
        this.objectCopySwitch = objectCopySwitch;
    }

    public FieldGenerateModeEnum getObjectCopyMethodFieldGenerateMode() {
        return objectCopyMethodFieldGenerateMode;
    }

    public void setObjectCopyMethodFieldGenerateMode(
        FieldGenerateModeEnum objectCopyMethodFieldGenerateMode) {
        this.objectCopyMethodFieldGenerateMode = objectCopyMethodFieldGenerateMode;
    }

    public WhetherEnum getObjectCopyMethodFieldGenerateAnnotation() {
        return objectCopyMethodFieldGenerateAnnotation;
    }

    public void setObjectCopyMethodFieldGenerateAnnotation(
        WhetherEnum objectCopyMethodFieldGenerateAnnotation) {
        this.objectCopyMethodFieldGenerateAnnotation = objectCopyMethodFieldGenerateAnnotation;
    }

    public String getBuilderInstanceMethodName() {
        return builderInstanceMethodName;
    }

    public void setBuilderInstanceMethodName(String builderInstanceMethodName) {
        this.builderInstanceMethodName = builderInstanceMethodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PluginConfigModel that = (PluginConfigModel) o;
        return jsonSwitch == that.jsonSwitch && thriftSwitch == that.thriftSwitch && xmlSwitch == that.xmlSwitch
                && objectCopySwitch == that.objectCopySwitch
                && objectCopyMethodFieldGenerateMode == that.objectCopyMethodFieldGenerateMode
                && objectCopyMethodFieldGenerateAnnotation == that.objectCopyMethodFieldGenerateAnnotation
                && Objects.equals(builderInstanceMethodName, that.builderInstanceMethodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jsonSwitch, thriftSwitch, xmlSwitch, objectCopySwitch, objectCopyMethodFieldGenerateMode,
                objectCopyMethodFieldGenerateAnnotation, builderInstanceMethodName);
    }
}
