package cn.bigcoder.plugin.objecthelper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.CollectionListModel;

import java.util.List;

public class ObjectHelper extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("789");
        PsiMethod method = getPsiMethodFromContext(e);
        generateO2OMethod(method);
    }

    /**
     * 启动写线程
     *
     * @param psiMethod
     */
    private void generateO2OMethod(PsiMethod psiMethod) {
        new WriteCommandAction.Simple(psiMethod.getProject(), psiMethod.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                createO2O(psiMethod);
            }
        }.execute();
    }

    private void createO2O(PsiMethod psiMethod) {
        String methodName = psiMethod.getName();
        PsiType returnType = psiMethod.getReturnType();
        if (returnType == null) {
            return;
        }
        String returnClassName = returnType.getPresentableText();
        PsiParameter psiParameter = psiMethod.getParameterList().getParameters()[0];
        //带package的class名称
        String parameterClassWithPackage = psiParameter.getType().getInternalCanonicalText();
        //为了解析字段，这里需要加载参数的class
        JavaPsiFacade facade = JavaPsiFacade.getInstance(psiMethod.getProject());
        PsiClass parameterClass = facade.findClass(parameterClassWithPackage, GlobalSearchScope.allScope(psiMethod.getProject()));
        if (parameterClass == null) {
            return;
        }
        List<PsiField> parameterFields = new CollectionListModel<PsiField>(parameterClass.getFields()).getItems();
        String methodText = getMethodText(methodName, returnClassName, psiParameter, parameterFields);
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiMethod.getProject());
        PsiMethod toMethod = elementFactory.createMethodFromText(methodText, psiMethod);
        psiMethod.replace(toMethod);
    }

    /**
     * @param methodName      方法名称
     * @param returnClassName 返回的值的class名称
     * @param psiParameter    方法参数第一个值
     * @param parameterFields 方法参数的class里field 列表
     * @return 方法体的字符串
     */
    private String getMethodText(String methodName, String returnClassName, PsiParameter psiParameter, List<PsiField> parameterFields) {
        String returnObjName = returnClassName.substring(0, 1).toLowerCase() + returnClassName.substring(1);
        String parameterClass = psiParameter.getText();
        String parameterName = psiParameter.getName();
        StringBuilder builder = new StringBuilder("public static " + returnClassName + " " + methodName + " (");
        builder.append(parameterClass + " ) {\n");
        builder.append("if ( " + parameterName + "== null ){\n").append("return null;\n}").append(returnClassName + " " + returnObjName + "= new " + returnClassName + "();\n");
        for (PsiField field : parameterFields) {
            PsiModifierList modifierList = field.getModifierList();
            if (modifierList == null || modifierList.hasModifierProperty(PsiModifier.STATIC) || modifierList.hasModifierProperty(PsiModifier.FINAL) || modifierList.hasModifierProperty(PsiModifier.SYNCHRONIZED)) {
                continue;
            }
            builder.append(returnObjName + ".set" + getFirstUpperCase(field.getName()) + "(" + parameterName + ".get" + getFirstUpperCase(field.getName()) + "());\n");
        }
        builder.append("return " + returnObjName + ";\n");
        builder.append("}\n");
        return builder.toString();
    }

    private String getFirstUpperCase(String oldStr) {
        return oldStr.substring(0, 1).toUpperCase() + oldStr.substring(1);
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
        //用来获取当前光标处的PsiElement
        int offset = editor.getCaretModel().getOffset();
        return psiFile.findElementAt(offset);
    }
}
