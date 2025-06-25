package cn.bigcoder.plugin.objecthelper.action;

import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.setActionInvisible;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Jindong.Tian
 * @date: 2023-12-24
 **/
public abstract class AbstractClassAnAction extends AnAction {

    /**
     * 是否开启该功能
     * @return true代表开启该功能，false表示关闭该功能
     */
    public abstract boolean actionShow(AnActionEvent anActionEvent);

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        if (!actionShow(anActionEvent)) {
            setActionInvisible(anActionEvent);
        }
        super.update(anActionEvent);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}
