package cn.bigcoder.plugin.objecthelper.generator.method;

import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import com.intellij.psi.*;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import static cn.bigcoder.plugin.objecthelper.common.constant.JavaSeparator.*;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getMethodReturnClassName;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getPsiClass;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public class ObjectCopyMethodGenerator extends AbstractMethodGenerator {

    private void init(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return;
        }
        super.project = psiMethod.getProject();
        super.psiMethod = psiMethod;
    }

    public static ObjectCopyMethodGenerator getInstance(PsiMethod psiMethod) {
        ObjectCopyMethodGenerator objectCopyMethodGenerator = new ObjectCopyMethodGenerator();
        objectCopyMethodGenerator.init(psiMethod);
        return objectCopyMethodGenerator;
    }

    @Override
    protected String generateMethodBody() {
        if (CollectionUtils.isEmpty(getParameters()) || VOID.equals(getMethodReturnClassName(psiMethod))) {
            return EMPTY_BODY;
        }
        StringBuilder result = new StringBuilder();
        String returnObjName = StringUtils.firstLowerCase(getMethodReturnClassName(psiMethod));
        PsiParameter firstParameter = getParameters().get(FIRST_INDEX);
        PsiClass firstParameterClass = getPsiClass(firstParameter.getType(), project);
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
        return getMethodReturnClassName(psiMethod) + BLANK_SEPARATOR + returnObjName + "= new " + getMethodReturnClassName(psiMethod) + "();" + LINE_SEPARATOR;
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
        return "return " + returnObjName + SEMICOLON_SEPARATOR;
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
}