package cn.bigcoder.plugin.objecthelper.ui;

import cn.bigcoder.plugin.objecthelper.common.enums.EnableEnum;
import cn.bigcoder.plugin.objecthelper.common.enums.FieldGenerateModeEnum;
import cn.bigcoder.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import cn.bigcoder.plugin.objecthelper.config.PluginConfigModel;
import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author: Jindong.Tian
 * @date: 2022-08-26
 **/
public class ConfigPage {

    private JPanel mainPanel;
    private JComboBox classToJsonSwitch;
    private JComboBox classToThriftSwitch;
    private JComboBox classToXmlSwitch;
    private JComboBox objectCopyMethodSwitch;
    private JButton tipsButton;
    private JComboBox objectCopyMethodGenerateMode;
    private JComboBox objectCopyMethodGenerateAnnotation;
    private JTextField builderInstanceMethodName;

    public JPanel getMainPanel() {
        initField();
        return mainPanel;
    }

    /**
     * 根据持久化配置，初始化面板
     */
    public void initField() {
        PluginConfigModel instance = PluginConfigState.getInstance();
        this.classToJsonSwitch.setSelectedItem(instance.getJsonSwitch().getCode());
        this.classToThriftSwitch.setSelectedItem(instance.getThriftSwitch().getCode());
        this.classToXmlSwitch.setSelectedItem(instance.getXmlSwitch().getCode());
        this.objectCopyMethodSwitch.setSelectedItem(instance.getObjectCopySwitch().getCode());
        this.objectCopyMethodGenerateAnnotation.setSelectedItem(instance.getObjectCopyMethodFieldGenerateAnnotation().getCode());
        this.objectCopyMethodGenerateMode.setSelectedItem(instance.getObjectCopyMethodFieldGenerateMode().getCode());
        this.builderInstanceMethodName.setText(instance.getBuilderInstanceMethodName());
    }

    /**
     * 获取页面当前的配置信息
     *
     * @return
     */
    public PluginConfigModel getCurrentConfigModel() {
        PluginConfigModel pluginConfigModel = new PluginConfigModel();

        Optional.ofNullable(this.classToJsonSwitch.getSelectedItem()).ifPresent(e -> {
                pluginConfigModel.setJsonSwitch(FunctionSwitchEnum.nameOf(e.toString()));
            }
        );
        Optional.ofNullable(this.classToThriftSwitch.getSelectedItem()).ifPresent(e -> {
                pluginConfigModel.setThriftSwitch(FunctionSwitchEnum.nameOf(e.toString()));
            }
        );
        Optional.ofNullable(this.classToXmlSwitch.getSelectedItem()).ifPresent(e -> {
                pluginConfigModel.setXmlSwitch(FunctionSwitchEnum.nameOf(e.toString()));
            }
        );
        Optional.ofNullable(this.objectCopyMethodSwitch.getSelectedItem()).ifPresent(e -> {
                pluginConfigModel.setObjectCopySwitch(FunctionSwitchEnum.nameOf(e.toString()));
            }
        );
        Optional.ofNullable(this.objectCopyMethodGenerateMode.getSelectedItem()).ifPresent(e -> {
                pluginConfigModel.setObjectCopyMethodFieldGenerateMode(FieldGenerateModeEnum.nameOf(e.toString()));
            }
        );
        Optional.ofNullable(this.objectCopyMethodGenerateAnnotation.getSelectedItem()).ifPresent(e -> {
                pluginConfigModel.setObjectCopyMethodFieldGenerateAnnotation(EnableEnum.nameOf(e.toString()));
            }
        );
        Optional.ofNullable(this.builderInstanceMethodName.getText()).ifPresent(
                pluginConfigModel::setBuilderInstanceMethodName
        );
        return pluginConfigModel;
    }

}
