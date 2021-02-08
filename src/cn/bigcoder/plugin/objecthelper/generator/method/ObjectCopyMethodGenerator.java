package cn.bigcoder.plugin.objecthelper.generator.method;

import cn.bigcoder.plugin.objecthelper.common.util.PsiUtils;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.*;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getMethodReturnClassName;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getPsiClass;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public class ObjectCopyMethodGenerator extends AbstractMethodGenerator {

    /**
     * 方法第一个参数名称
     */
    private String firstParameterName;
    /**
     * 方法返回局部变量名称
     */
    private String returnObjName;

    private void init(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return;
        }
        super.project = psiMethod.getProject();
        super.psiMethod = psiMethod;
        this.firstParameterName = getFirstParameter().getName();
        this.returnObjName = StringUtils.firstLowerCase(getMethodReturnClassName(psiMethod));
        // 防止方法入参和返回参数名称一致
        if (firstParameterName.equals(returnObjName)) {
            this.returnObjName = this.returnObjName + "1";
        }
    }

    public static ObjectCopyMethodGenerator getInstance(PsiMethod psiMethod) {
        ObjectCopyMethodGenerator objectCopyMethodGenerator = new ObjectCopyMethodGenerator();
        objectCopyMethodGenerator.init(psiMethod);
        return objectCopyMethodGenerator;
    }

    /**
     * 此方法中不应该存在判空的操作，依赖环境的建议重写父类的check方法，在生成早期拦截异常情况
     *
     * @return
     */
    @Override
    protected String generateMethodBody() {
        StringBuilder result = new StringBuilder();
        result.append(generateNullCheck());
        result.append(generateObjectCreateLine());
        for (PsiField field : PsiUtils.getAllPsiFields(getFirstParameterClass())) {
            PsiModifierList modifierList = field.getModifierList();
            if (modifierList == null ||
                    modifierList.hasModifierProperty(PsiModifier.STATIC) ||
                    modifierList.hasModifierProperty(PsiModifier.FINAL) ||
                    modifierList.hasModifierProperty(PsiModifier.SYNCHRONIZED)) {
                continue;
            }
            result.append(generateFieldCopyLine(field));
        }
        result.append(generateReturnLine());
        return result.toString();
    }

    /**
     * 生成示例：{@code UserDTO userDTO = new UserDTO();}
     *
     * @return
     */
    @NotNull
    private String generateObjectCreateLine() {
        return getMethodReturnClassName(psiMethod) + BLANK_SEPARATOR + returnObjName + "= new " + getMethodReturnClassName(psiMethod) + "();" + LINE_SEPARATOR;
    }

    /**
     * 生成示例：{@code userDTO.setId(user.getId());}
     *
     * @param field
     * @return
     */
    @NotNull
    private String generateFieldCopyLine(PsiField field) {
        return returnObjName + ".set" + StringUtils.firstUpperCase(field.getName()) + "(" + firstParameterName + ".get" + StringUtils.firstUpperCase(field.getName()) + "());" + LINE_SEPARATOR;
    }

    /**
     * 生成示例：{@code return userDTO;}
     *
     * @return
     */
    @NotNull
    private String generateReturnLine() {
        return "return " + returnObjName + SEMICOLON_SEPARATOR;
    }

    /**
     * 生成示例：{@code if (user == null) {return null;}}
     *
     * @return
     */
    private String generateNullCheck() {
        return "if(" + getFirstParameter().getName() + "==null){return null;}";
    }

    /**
     * 获取参数列表第一个参数的{@code PsiParameter}
     *
     * @return
     */
    private PsiParameter getFirstParameter() {
        return getParameters().get(FIRST_INDEX);
    }

    /**
     * 获取参数列表第一个参数的{@code PsiClass}
     *
     * @return
     */
    private PsiClass getFirstParameterClass() {
        return getPsiClass(getFirstParameter().getType(), project);
    }
}