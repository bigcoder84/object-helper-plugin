package cn.bigcoder.plugin.objecthelper.generator.method;

import cn.bigcoder.plugin.objecthelper.common.enums.JavaModify;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public class ObjectCopyMethodGenerator extends AbstractMethodGenerator {

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
        if (CollectionUtils.isEmpty(parameters) || VOID_KEYWORD.equals(returnClassName)) {
            return EMPTY_BODY;
        }
        StringBuilder result = new StringBuilder();
        String returnObjName = StringUtils.firstLowerCase(returnClassName);
        PsiParameter firstParameter = parameters.get(FIRST_INDEX);
        PsiClass firstParameterClass = parameterClass.get(FIRST_INDEX);
        if (firstParameterClass == null) {
            return EMPTY_BODY;
        }
        result.append(generateNullCheck(firstParameter.getName()));
        result.append(generateObjectCreateLine(returnObjName));
        for (PsiField field : firstParameterClass.getFields()) {
            PsiModifierList modifierList = field.getModifierList();
            if (modifierList == null ||
                    modifierList.hasModifierProperty(PsiModifier.STATIC) ||
                    modifierList.hasModifierProperty(PsiModifier.FINAL) ||
                    modifierList.hasModifierProperty(PsiModifier.SYNCHRONIZED)) {
                continue;
            }
            result.append(generateFieldCopyLine(returnObjName, firstParameter.getName(), field));
        }
        result.append(generateReturnLine(returnObjName));
        return result.toString();
    }

    /**
     * 生成示例：{@code UserDTO userDTO = new UserDTO();}
     *
     * @param returnObjName
     * @return
     */
    @NotNull
    private String generateObjectCreateLine(String returnObjName) {
        return returnClassName + BLANK_SEPARATOR + returnObjName + "= new " + returnClassName + "();" + LINE_SEPARATOR;
    }

    /**
     * 生成示例：{@code userDTO.setId(user.getId());}
     *
     * @param returnObjName
     * @param parameterName
     * @param field
     * @return
     */
    @NotNull
    private String generateFieldCopyLine(String returnObjName, String parameterName, PsiField field) {
        return returnObjName + ".set" + StringUtils.firstUpperCase(field.getName()) + "(" + parameterName + ".get" + StringUtils.firstUpperCase(field.getName()) + "());" + LINE_SEPARATOR;
    }

    /**
     * 生成示例：{@code return userDTO;}
     *
     * @param returnObjName
     * @return
     */
    @NotNull
    private String generateReturnLine(String returnObjName) {
        return "return " + returnObjName + ";" + LINE_SEPARATOR;
    }

    /**
     * 生成示例：{@code if (user == null) {return null;}}
     *
     * @param parameterName
     * @return
     */
    private String generateNullCheck(String parameterName) {
        return "if(" + parameterName + "==null){return null;}";
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
}