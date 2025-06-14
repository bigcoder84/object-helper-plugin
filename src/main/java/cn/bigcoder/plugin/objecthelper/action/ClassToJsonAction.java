package cn.bigcoder.plugin.objecthelper.action;

import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getOperatePsiClass;

import cn.bigcoder.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import cn.bigcoder.plugin.objecthelper.common.util.NotificationUtils;
import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.json.ClassJsonGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import java.awt.datatransfer.StringSelection;
import org.jetbrains.annotations.NotNull;

public class ClassToJsonAction extends AbstractClassAnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anAction) {
        PsiClass psiClass = getOperatePsiClass(anAction);
        if (psiClass == null) {
            return;
        }
        String json = getGenerator(psiClass).generate();
        CopyPasteManager.getInstance().setContents(new StringSelection(json));
        NotificationUtils.notifyInfo(anAction.getProject(), "JSON字符串成功置入剪贴板:<br>" + json);
    }

    @Override
    public boolean actionShow(AnActionEvent anActionEvent) {
        return PluginConfigState.getInstance().getJsonSwitch() == FunctionSwitchEnum.ENABLE
            && getOperatePsiClass(anActionEvent) != null;
    }

    protected Generator getGenerator(PsiClass psiClass) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return ClassJsonGenerator.getInstance(psiClass, gson);
    }
}
