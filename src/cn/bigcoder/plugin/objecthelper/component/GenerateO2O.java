package cn.bigcoder.plugin.objecthelper.component;

import cn.bigcoder.plugin.objecthelper.common.util.PsiUtils;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.method.ObjectCopyMethodGenerator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class GenerateO2O extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), () -> {
            generateO2O(getPsiMethodFromContext(anActionEvent));
        });
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        // 如果当前光标不在方法中，则不显示GernerateO2O组件
        if (getPsiMethodFromContext(anActionEvent) == null) {
            anActionEvent.getPresentation().setEnabled(false);
        }
        super.update(anActionEvent);
    }

    private void generateO2O(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return;
        }
        // 初始化生成器
        Generator generator = ObjectCopyMethodGenerator.getInstance(psiMethod);
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiMethod.getProject());
        // 生成新的PsiMethod
        PsiMethod toMethod = elementFactory.createMethodFromText(generator.generate(), psiMethod);
        psiMethod.replace(toMethod);
    }

    private PsiMethod getPsiMethodFromContext(AnActionEvent e) {
        PsiElement elementAt = PsiUtils.getCursorPsiElement(e);
        if (elementAt == null) {
            return null;
        }
        return PsiTreeUtil.getParentOfType(elementAt, PsiMethod.class);
    }
}
