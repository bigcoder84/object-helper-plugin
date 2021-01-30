package cn.bigcoder.plugin.objecthelper;

import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.method.ObjectCopyMethodGenerator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

public class GenerateO2O extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), () -> {
            generateO2O(getPsiMethodFromContext(anActionEvent));
        });
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
        PsiElement elementAt = getPsiElement(e);
        if (elementAt == null) {
            return null;
        }
        return PsiTreeUtil.getParentOfType(elementAt, PsiMethod.class);
    }

    private PsiElement getPsiElement(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return null;
        }
        //获取当前光标处的PsiElement
        int offset = editor.getCaretModel().getOffset();
        return psiFile.findElementAt(offset);
    }
}
