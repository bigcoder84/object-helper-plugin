package cn.bigcoder.plugin.objecthelper.common.util;

import cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord;
import cn.bigcoder.plugin.objecthelper.common.enums.JavaModifyEnum;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.compress.utils.Lists;
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
     * 获取光标处上下文的{@code PsiMethod}
     *
     * @param actionEvent
     * @return
     */
    public static PsiMethod getCursorPsiMethod(AnActionEvent actionEvent) {
        PsiElement elementAt = getCursorPsiElement(actionEvent);
        if (elementAt == null) {
            return null;
        }
        return PsiTreeUtil.getParentOfType(elementAt, PsiMethod.class);
    }

    /**
     * 获取当前操作下的 {@code PsiClass}
     *
     * @param actionEvent
     * @return
     */
    public static PsiClass getOperatePsiClass(AnActionEvent actionEvent) {
        PsiElement psiElement = getOperatePsiElement(actionEvent);
        if (psiElement instanceof PsiClass) {
            return (PsiClass) psiElement;
        }
        return null;
    }

    /**
     * 通过偏移量获取光标所在处的{@code PsiElement}
     * 这将返回指定偏移量中的最低级别元素，这通常是一个 lexer 令牌。最有可能你应该使用{@code PsiTreeUtil.getParentOfType()}来查找您真正需要的元素。
     *
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
     * 从操作中获取{@code PsiElement}。注意：如果编辑器当前处于打开状态，并且 caret 中的元素是引用，则这将返回解析引用的结果。这可能是你需要的，也可能不是。
     *
     * @param anActionEvent
     * @return
     */
    public static PsiElement getOperatePsiElement(AnActionEvent anActionEvent) {
        return anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
    }

    /**
     * 设置当前组件不可用
     *
     * @param anActionEvent
     */
    public static void setActionDisabled(AnActionEvent anActionEvent) {
        anActionEvent.getPresentation().setEnabled(false);
    }

    /**
     * 设置当前组件不可见
     *
     * @param anActionEvent
     */
    public static void setActionInvisible(AnActionEvent anActionEvent) {
        anActionEvent.getPresentation().setVisible(false);
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
     *
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
     *
     * @param psiMethod
     * @return
     */
    @NotNull
    public static String getMethodReturnClassName(PsiMethod psiMethod) {
        PsiType returnType = psiMethod.getReturnType();
        if (returnType == null) {
            return JavaKeyWord.VOID;
        }
        return returnType.getPresentableText();
    }

    /**
     * 获取方法的参数列表
     *
     * @param psiMethod
     * @return
     */
    @NotNull
    public static List<PsiParameter> getPsiParameters(PsiMethod psiMethod) {
        return Arrays.asList(psiMethod.getParameterList().getParameters());
    }

    /**
     * 获取方法的修饰符
     *
     * @param modifierList
     * @return
     */
    @NotNull
    public static List<JavaModifyEnum> getMethodModifies(PsiModifierList modifierList) {
        List<JavaModifyEnum> result = new ArrayList<>();
        if (modifierList.hasModifierProperty(JavaModifyEnum.PUBLIC.getName())) {
            result.add(JavaModifyEnum.PUBLIC);
        } else if (modifierList.hasModifierProperty(JavaModifyEnum.PROTECTED.getName())) {
            result.add(JavaModifyEnum.PROTECTED);
        } else if (modifierList.hasModifierProperty(JavaModifyEnum.PRIVATE.getName())) {
            result.add(JavaModifyEnum.PRIVATE);
        }
        if (modifierList.hasModifierProperty(JavaModifyEnum.STATIC.getName())) {
            result.add(JavaModifyEnum.STATIC);
        }
        if (modifierList.hasModifierProperty(JavaModifyEnum.FINAL.getName())) {
            result.add(JavaModifyEnum.FINAL);
        }
        return result;
    }

    /**
     * 获取{@code PsiClass}所有字段（包含父类字段）
     *
     * @param psiClass
     * @return
     */
    @NotNull
    public static List<PsiField> getAllPsiFields(PsiClass psiClass) {
        List<PsiField> result = Lists.newArrayList();
        recursiveAllFields(psiClass, result);
        return result;
    }

    /**
     * 判断一个字段是否是类的成员属性
     *
     * @param psiField
     * @return
     */
    public static boolean isMemberField(PsiField psiField) {
        PsiModifierList modifierList = psiField.getModifierList();
        if (modifierList == null ||
            modifierList.hasModifierProperty(PsiModifier.STATIC) ||
            modifierList.hasModifierProperty(PsiModifier.FINAL) ||
            modifierList.hasModifierProperty(PsiModifier.SYNCHRONIZED)) {
            return false;
        }
        return true;
    }

    /**
     * 递归获取类中所有字段
     *
     * @param psiClass
     * @param psiFields
     */
    private static void recursiveAllFields(PsiClass psiClass, List<PsiField> psiFields) {
        if (psiClass == null) {
            return;
        }
        psiFields.addAll(Arrays.asList(psiClass.getFields()));
        recursiveAllFields(psiClass.getSuperClass(), psiFields);
    }
}
