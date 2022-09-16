package cn.bigcoder.plugin.objecthelper.action;

import cn.bigcoder.plugin.objecthelper.common.util.NotificationUtils;
import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import cn.bigcoder.plugin.objecthelper.generator.idl.ThriftIDLGenerator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getOperatePsiClass;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.setActionInvisible;

/**
 * @author: Jindong.Tian
 * @date: 2021-08-21
 **/
public class ClassToThriftIDLAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiClass psiClass = getOperatePsiClass(anActionEvent);
        if (psiClass == null) {
            return;
        }
        String idl = ThriftIDLGenerator.getInstance(psiClass).generate();
        CopyPasteManager.getInstance().setContents(new StringSelection(idl));
        NotificationUtils.notifyInfo(anActionEvent.getProject(), "Thrift IDL代码成功置入剪贴板：<br>" + idl);
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        if (!PluginConfigState.getInstance().isThriftSwitch()) {
            setActionInvisible(anActionEvent);
        } else if (getOperatePsiClass(anActionEvent) == null) {
            // 如果当前光标不在类名上，则不显示ConvertToJson组件
            setActionInvisible(anActionEvent);
        }
        super.update(anActionEvent);
    }
}