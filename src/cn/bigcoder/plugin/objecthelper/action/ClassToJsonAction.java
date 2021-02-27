package cn.bigcoder.plugin.objecthelper.action;

import cn.bigcoder.plugin.objecthelper.common.util.NotificationUtils;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.json.ClassJsonGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getOperatePsiClass;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.setActionInvisible;

public class ClassToJsonAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anAction) {
        PsiClass psiClass = getOperatePsiClass(anAction);
        if (psiClass == null) {
            return;
        }
        String json = getGenerator(psiClass).generate();
        CopyPasteManager.getInstance().setContents(new StringSelection(json));
        NotificationUtils.notifyInfo(anAction.getProject(), "JSON字符串成功置入剪贴板：<br>" + json);
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        // 如果当前光标不在方法中，则不显示ConvertToJson组件
        if (getOperatePsiClass(anActionEvent) == null) {
            setActionInvisible(anActionEvent);
        }
        super.update(anActionEvent);
    }

    protected Generator getGenerator(PsiClass psiClass) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return ClassJsonGenerator.getInstance(psiClass, gson);
    }
}
