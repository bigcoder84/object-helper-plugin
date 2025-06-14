package cn.bigcoder.plugin.objecthelper.generator;

import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.BLANK_SEPARATOR;
import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.BRACE_OPEN;
import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.COMMA_SEPARATOR;
import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.PARENTHESIS_CLOSE;
import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.PARENTHESIS_OPEN;
import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.VOID;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getMethodName;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getMethodReturnClassName;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getPsiClass;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getPsiClassName;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getPsiParameters;

import cn.bigcoder.plugin.objecthelper.common.util.PsiUtils;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public abstract class AbstractMethodGenerator implements Generator {

    protected static final int FIRST_INDEX = 0;

    protected Project project;
    protected PsiMethod psiMethod;

    /**
     * 方法第一个参数名称
     */
    protected String firstParameterName;
    /**
     * 方法返回参数名称
     */
    protected String targetParameterName;
    /**
     * 方法第一个参数类型
     */
    protected PsiClass firstParameterClass;
    /**
     * 方法返回参数类型
     */
    protected PsiClass targetClass;

    public AbstractMethodGenerator(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return;
        }
        this.project = psiMethod.getProject();
        this.psiMethod = psiMethod;
        // 获取参数列表第一个参数
        PsiParameter firstParameter = getParameters().get(FIRST_INDEX);
        this.firstParameterClass = getPsiClass(firstParameter.getType(), project);
        // 获取返回值类型
        this.targetClass = getPsiClass(getReturnType(), project);
        this.firstParameterName = firstParameter.getName();
        this.targetParameterName = StringUtils.firstLowerCase(Objects.requireNonNull(getPsiClassName(targetClass)));
        // 防止方法入参和返回参数名称一致
        if (firstParameterName.equals(this.targetParameterName)) {
            this.targetParameterName = this.targetParameterName + "Res";
        }
    }

    public String generate() {
        if (!check()) {
            return null;
        }
        StringBuilder result = generateMethodFirstLine()
                .append(generateMethodBody())
                .append("}");
        return result.toString();
    }

    protected StringBuilder generateMethodFirstLine() {
        StringBuilder builder = new StringBuilder();
        PsiUtils.getMethodModifies(psiMethod.getModifierList())
                .forEach(e -> builder.append(e.getName()).append(BLANK_SEPARATOR));
        builder.append(getMethodReturnClassName(psiMethod)).append(BLANK_SEPARATOR).append(getMethodName(psiMethod))
                .append(PARENTHESIS_OPEN)
                .append(StringUtils.join(getParameters(), PsiParameter::getText, COMMA_SEPARATOR))
                .append(PARENTHESIS_CLOSE + BRACE_OPEN);
        return builder;
    }

    /**
     * 生成方法体
     *
     * @return
     */
    protected abstract String generateMethodBody();


    /**
     * 检查是否具备生成方法所需要的环境
     *
     * @return true代表校验通过
     */
    protected boolean check() {
        if (CollectionUtils.isEmpty(getParameters()) || VOID.equals(getMethodReturnClassName(psiMethod))) {
            return false;
        }
        return true;
    }

    protected List<PsiParameter> getParameters() {
        return getPsiParameters(psiMethod);
    }

    protected PsiType getReturnType() {
        return psiMethod.getReturnType();
    }
}
