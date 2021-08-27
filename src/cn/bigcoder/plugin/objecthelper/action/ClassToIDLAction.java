package cn.bigcoder.plugin.objecthelper.action;

import cn.bigcoder.plugin.objecthelper.common.util.NotificationUtils;
import cn.bigcoder.plugin.objecthelper.generator.idl.IDLGenerator;
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
public class ClassToIDLAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiClass psiClass = getOperatePsiClass(anActionEvent);
        if (psiClass == null) {
            return;
        }
        String idl = IDLGenerator.getInstance(psiClass).generate();
        CopyPasteManager.getInstance().setContents(new StringSelection(idl));
        NotificationUtils.notifyInfo(anActionEvent.getProject(), "IDL代码成功置入剪贴板：<br>" + idl);
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        // 如果当前光标不在方法中，则不显示ConvertToJson组件
        if (getOperatePsiClass(anActionEvent) == null) {
            setActionInvisible(anActionEvent);
        }
        super.update(anActionEvent);
    }
}