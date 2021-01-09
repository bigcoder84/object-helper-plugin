package cn.bigcoder.plugin.objecthelper.generator.method;

import cn.bigcoder.plugin.objecthelper.common.enums.JavaModify;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public class ObjectCopyMethodGenerator extends AbstractMethodGenerator {

    private ObjectCopyMethodGenerator() {
    }

    private void init(PsiMethod psiMethod) {
        super.methodName = psiMethod.getName();
        super.returnClassName = getReturnClassName(psiMethod);
        super.parameters = Arrays.asList(psiMethod.getParameterList().getParameters());
        super.parameterClass = super.parameters.stream().map(e -> transferPsiClass(e, psiMethod)).collect(Collectors.toList());
        super.methodModifies = getMethodModifies(psiMethod.getModifierList());
    }

    public static ObjectCopyMethodGenerator getInstance(PsiMethod psiMethod) {
        ObjectCopyMethodGenerator objectCopyMethodGenerator = new ObjectCopyMethodGenerator();
        objectCopyMethodGenerator.init(psiMethod);
        return objectCopyMethodGenerator;
    }

    @Override
    protected String generateMethodBody() {
        if (CollectionUtils.isEmpty(parameters)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        String returnObjName = StringUtils.firstLowerCase(returnClassName);
        PsiParameter firstParameter = parameters.get(0);
        PsiClass firstClass = parameterClass.get(0);
        String parameterName = firstParameter.getName();
        result.append(generateNullCheck(parameterName));
        result.append(returnClassName).append(" ").append(returnObjName).append("= new ").append(returnClassName).append("();\n");
        for (PsiField field : firstClass.getFields()) {
            PsiModifierList modifierList = field.getModifierList();
            if (modifierList == null ||
                    modifierList.hasModifierProperty(PsiModifier.STATIC) ||
                    modifierList.hasModifierProperty(PsiModifier.FINAL) ||
                    modifierList.hasModifierProperty(PsiModifier.SYNCHRONIZED)) {
                continue;
            }
            result.append(returnObjName + ".set" + StringUtils.firstUpperCase(field.getName()) + "(" + parameterName + ".get" + StringUtils.firstUpperCase(field.getName()) + "());\n");
        }
        result.append("return " + returnObjName + ";\n");
        return result.toString();
    }

    private static String getReturnClassName(PsiMethod psiMethod) {
        PsiType returnType = psiMethod.getReturnType();
        if (returnType == null) {
            return "void";
        }
        return returnType.getPresentableText();
    }

    private static PsiClass transferPsiClass(PsiParameter psiParameter, PsiMethod psiMethod) {
        //带package的class名称
        String parameterClassWithPackage = psiParameter.getType().getInternalCanonicalText();
        //为了解析字段，这里需要加载参数的class
        JavaPsiFacade facade = JavaPsiFacade.getInstance(psiMethod.getProject());
        return facade.findClass(parameterClassWithPackage, GlobalSearchScope.allScope(psiMethod.getProject()));
    }

    private List<JavaModify> getMethodModifies(PsiModifierList modifierList) {
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

    private String generateNullCheck(String parameterName) {
        return "if ( " + parameterName + "== null ){\nreturn null;\n}\n";
    }
}
