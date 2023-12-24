package cn.bigcoder.plugin.objecthelper.generator.method;

import cn.bigcoder.plugin.objecthelper.common.enums.FieldGenerateModeEnum;
import cn.bigcoder.plugin.objecthelper.common.enums.WhetherEnum;
import cn.bigcoder.plugin.objecthelper.common.util.PsiUtils;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import cn.bigcoder.plugin.objecthelper.generator.AbstractMethodGenerator;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
        FieldGenerateModeEnum generateModeEnum = PluginConfigState.getInstance()
            .getObjectCopyMethodFieldGenerateMode();
        // mainClass 代表以哪个类字段为基础生成字段拷贝代码
        PsiClass mainClass = getReturnClass();
        PsiClass secondClass = getFirstParameterClass();
        if (generateModeEnum == FieldGenerateModeEnum.SOURCE) {
            mainClass = getFirstParameterClass();
            secondClass = getReturnClass();
        }

        Set<String> secondFieldNames = PsiUtils.getAllPsiFields(secondClass).stream().filter(e -> PsiUtils.isMemberField(e))
            .map(PsiField::getName).collect(Collectors.toSet());

        List<String> annotationLine = new LinkedList<>();
        for (PsiField field : PsiUtils.getAllPsiFields(mainClass)) {
            if (!PsiUtils.isMemberField(field)) {
                continue;
            }
            if (secondFieldNames.contains(field.getName())) {
                result.append(generateFieldCopyLine(field));
            } else if (PluginConfigState.getInstance().getObjectCopyMethodFieldGenerateAnnotation()
                == WhetherEnum.YES) {
                // 如果源对象没有该字段，且开启了以注释模式生成代码的开关，则生成注释
                annotationLine.add("// "+ generateFieldCopyLine(field));
            }
        }
        annotationLine.forEach(result::append);
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

    private PsiClass getReturnClass() {
        return getPsiClass(getReturnType(), project);
    }
}