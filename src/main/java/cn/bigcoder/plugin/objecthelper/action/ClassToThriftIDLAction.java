package cn.bigcoder.plugin.objecthelper.action;

import cn.bigcoder.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import cn.bigcoder.plugin.objecthelper.common.util.NotificationUtils;
import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import cn.bigcoder.plugin.objecthelper.generator.idl.ThriftIDLGenerator;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getOperatePsiClass;

/**
 * @author: Jindong.Tian
 * @date: 2021-08-21
 **/
public class ClassToThriftIDLAction extends AbstractClassAnAction {
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
    public boolean actionShow(@NotNull AnActionEvent anActionEvent) {
        return PluginConfigState.getInstance().getThriftSwitch() == FunctionSwitchEnum.OPEN
            && getOperatePsiClass(anActionEvent) != null;
    }
}