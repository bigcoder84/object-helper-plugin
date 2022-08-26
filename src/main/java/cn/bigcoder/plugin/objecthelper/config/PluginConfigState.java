package cn.bigcoder.plugin.objecthelper.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 该类用于持久化配置对象，每个想要在IDE重启时保持状态的组件都应该实现 PersistentStateComponent 接口
 *
 * @author: Jindong.Tian
 * @date: 2022-08-31
 **/
@State(
        name = "cn.bigcoder.plugin.objecthelper.config.PluginConfigState",
        storages = @Storage("object-helper.xml")
)
public class PluginConfigState implements PersistentStateComponent<PluginConfigModel> {

    @Override
    public @Nullable PluginConfigModel getState() {
        return new PluginConfigModel();
    }

    @Override
    public void loadState(@NotNull PluginConfigModel state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static PluginConfigModel getInstance() {
        return ApplicationManager.getApplication().getService(PluginConfigModel.class);
    }
}
