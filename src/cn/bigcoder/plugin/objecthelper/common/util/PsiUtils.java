package cn.bigcoder.plugin.objecthelper.common.util;

import cn.bigcoder.plugin.objecthelper.common.constant.JavaSeparator;
import cn.bigcoder.plugin.objecthelper.common.enums.JavaModify;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-31
 **/
public class PsiUtils {

    /**
     * 获取光标所在处的{@code PsiElement}
     * @param anActionEvent
     * @return
     */
    public static PsiElement getCursorPsiElement(AnActionEvent anActionEvent) {
        PsiFile psiFile = anActionEvent.getData(LangDataKeys.PSI_FILE);
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            anActionEvent.getPresentation().setEnabled(false);
            return null;
        }
        //获取当前光标处的PsiElement
        int offset = editor.getCaretModel().getOffset();
        return psiFile.findElementAt(offset);
    }

    /**
     * 根据{@code PsiType}获取对应的{@code PsiClass}
     *
     * @param psiType
     * @param project
     * @return
     */
    public static PsiClass getPsiClass(PsiType psiType, Project project) {
        if (psiType == null) {
            return null;
        }
        //带package的class名称
        String parameterClassWithPackage = psiType.getInternalCanonicalText();
        //为了解析字段，这里需要加载参数的class
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        return facade.findClass(parameterClassWithPackage, GlobalSearchScope.allScope(project));
    }

    /**
     * 获取方法名称
     * @param psiMethod
     * @return
     */
    public static String getMethodName(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return null;
        }
        return psiMethod.getName();
    }

    /**
     * 获取方法返回名称
     * @param psiMethod
     * @return
     */
    @NotNull
    public static String getMethodReturnClassName(PsiMethod psiMethod) {
        PsiType returnType = psiMethod.getReturnType();
        if (returnType == null) {
            return JavaSeparator.VOID;
        }
        return returnType.getPresentableText();
    }

    /**
     * 获取方法的参数列表
     * @param psiMethod
     * @return
     */
    @NotNull
    public static List<PsiParameter> getPsiParameters(PsiMethod psiMethod){
        return Arrays.asList(psiMethod.getParameterList().getParameters());
    }

    /**
     * 获取方法的修饰符
     *
     * @param modifierList
     * @return
     */
    @NotNull
    public static List<JavaModify> getMethodModifies(PsiModifierList modifierList) {
        List<JavaModify> result = new ArrayList<>();
        if (modifierList.hasModifierProperty(JavaModify.PUBLIC.getName())) {
            result.add(JavaModify.PUBLIC);
        } else if (modifierList.hasModifierProperty(JavaModify.PROTECTED.getName())) {
            result.add(JavaModify.PROTECTED);
        } else if (modifierList.hasModifierProperty(JavaModify.PRIVATE.getName())) {
            result.add(JavaModify.PRIVATE);
        }
        if (modifierList.hasModifierProperty(JavaModify.STATIC.getName())) {
            result.add(JavaModify.STATIC);
        }
        if (modifierList.hasModifierProperty(JavaModify.FINAL.getName())) {
            result.add(JavaModify.FINAL);
        }
        return result;
    }
}
