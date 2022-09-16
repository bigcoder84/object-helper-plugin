package cn.bigcoder.plugin.objecthelper.config;

import java.util.Objects;

/**
 * @author: Jindong.Tian
 * @date: 2022-08-31
 **/
public class PluginConfigModel {
    /**
     * 是否开启 Class To Json 功能，默认为开启状态
     */
    private boolean jsonSwitch = true;
    /**
     * 是否开启 Class To Thrift IDL 功能，默认为开启状态
     */
    private boolean thriftSwitch = true;
    /**
     * 是否开启 Class To XML 功能，默认为开启状态
     */
    private boolean xmlSwitch = true;
    /**
     * 是否开启 Object Copy Method 功能，默认为开启状态
     */
    private boolean objectCopySwitch = true;

    public boolean isJsonSwitch() {
        return jsonSwitch;
    }

    public void setJsonSwitch(boolean jsonSwitch) {
        this.jsonSwitch = jsonSwitch;
    }

    public boolean isThriftSwitch() {
        return thriftSwitch;
    }

    public void setThriftSwitch(boolean thriftSwitch) {
        this.thriftSwitch = thriftSwitch;
    }

    public boolean isXmlSwitch() {
        return xmlSwitch;
    }

    public void setXmlSwitch(boolean xmlSwitch) {
        this.xmlSwitch = xmlSwitch;
    }

    public boolean isObjectCopySwitch() {
        return objectCopySwitch;
    }

    public void setObjectCopySwitch(boolean objectCopySwitch) {
        this.objectCopySwitch = objectCopySwitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginConfigModel that = (PluginConfigModel) o;
        return jsonSwitch == that.jsonSwitch && thriftSwitch == that.thriftSwitch && xmlSwitch == that.xmlSwitch && objectCopySwitch == that.objectCopySwitch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jsonSwitch, thriftSwitch, xmlSwitch, objectCopySwitch);
    }
}
