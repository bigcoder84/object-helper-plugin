package cn.bigcoder.plugin.objecthelper.config;

import cn.bigcoder.plugin.objecthelper.ui.ConfigPage;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 该类是配置页面的入口
 * https://plugins.jetbrains.com/docs/intellij/settings-tutorial.html
 *
 * @author: Jindong.Tian
 * @date: 2022-08-31
 **/
public class ObjectHelperConfigurable implements SearchableConfigurable {
    private ConfigPage configPage = new ConfigPage();

    @Override
    public @NotNull String getId() {
        return "cn.bigcoder.plugin.objecthelper.config.SettingFactory";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Object Helper";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return configPage.getMainPanel();
    }

    @Override
    public boolean isModified() {
        PluginConfigModel instance = PluginConfigState.getInstance();
        PluginConfigModel currentConfigModel = configPage.getCurrentConfigModel();
        return !instance.equals(currentConfigModel);
    }

    @Override
    public void apply() {
        PluginConfigModel instance = PluginConfigState.getInstance();
        PluginConfigModel currentConfigModel = configPage.getCurrentConfigModel();
        instance.setJsonSwitch(currentConfigModel.isJsonSwitch());
        instance.setThriftSwitch(currentConfigModel.isThriftSwitch());
        instance.setXmlSwitch(currentConfigModel.isXmlSwitch());
        instance.setObjectCopySwitch(currentConfigModel.isObjectCopySwitch());
    }

}
