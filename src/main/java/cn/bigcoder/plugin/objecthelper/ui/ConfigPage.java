package cn.bigcoder.plugin.objecthelper.ui;

import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import cn.bigcoder.plugin.objecthelper.config.PluginConfigModel;

import javax.swing.*;

/**
 * @author: Jindong.Tian
 * @date: 2022-08-26
 **/
public class ConfigPage {
    private JPanel mainPanel;
    private JComboBox classToJsonSwitch;
    private JComboBox classToThriftSwitch;
    private JComboBox classToXmlSwitch;

    public JPanel getMainPanel() {
        initField();
        return mainPanel;
    }

    /**
     * 根据持久化配置，初始化面板
     */
    public void initField() {
        PluginConfigModel instance = PluginConfigState.getInstance();
        this.classToJsonSwitch.setSelectedItem(convertComboBoxItem(instance.isJsonSwitch()));
        this.classToThriftSwitch.setSelectedItem(convertComboBoxItem(instance.isThriftSwitch()));
        this.classToXmlSwitch.setSelectedItem(convertComboBoxItem(instance.isXmlSwitch()));
    }

    /**
     * 获取页面当前的配置信息
     *
     * @return
     */
    public PluginConfigModel getCurrentConfigModel() {
        PluginConfigModel pluginConfigModel = new PluginConfigModel();
        pluginConfigModel.setJsonSwitch(this.classToJsonSwitch.getSelectedItem().equals("open"));
        pluginConfigModel.setThriftSwitch(this.classToThriftSwitch.getSelectedItem().equals("open"));
        pluginConfigModel.setXmlSwitch(this.classToXmlSwitch.getSelectedItem().equals("open"));
        return pluginConfigModel;
    }

    private static String convertComboBoxItem(boolean switchTag) {
        if (switchTag) {
            return "open";
        }
        return "close";
    }
}
