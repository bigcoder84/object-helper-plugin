package cn.bigcoder.plugin.objecthelper.action;

import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getOperatePsiClass;

import cn.bigcoder.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import cn.bigcoder.plugin.objecthelper.common.util.NotificationUtils;
import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.xml.ClassXMLGenerator;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import java.awt.datatransfer.StringSelection;
import org.jetbrains.annotations.NotNull;

public class ClassToXMLAction extends AbstractClassAnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anAction) {
        PsiClass psiClass = getOperatePsiClass(anAction);
        if (psiClass == null) {
            return;
        }
        String xmlStr = getGenerator(psiClass).generate();
        CopyPasteManager.getInstance().setContents(new StringSelection(xmlStr));
        NotificationUtils.notifyInfo(anAction.getProject(), "XML字符串成功置入剪贴板");
    }

    @Override
    public boolean actionShow(@NotNull AnActionEvent anActionEvent) {
        return PluginConfigState.getInstance().getXmlSwitch() == FunctionSwitchEnum.OPEN
            && getOperatePsiClass(anActionEvent) != null;
    }

    protected Generator getGenerator(PsiClass psiClass) {
        return ClassXMLGenerator.getInstance(psiClass);
    }
}
