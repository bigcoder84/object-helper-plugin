package cn.bigcoder.plugin.objecthelper.generator.method;

import cn.bigcoder.plugin.objecthelper.common.util.PsiUtils;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.*;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.*;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public abstract class AbstractMethodGenerator implements Generator {

    protected static final int FIRST_INDEX = 0;

    protected Project project;
    protected PsiMethod psiMethod;

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
        PsiUtils.getMethodModifies(psiMethod.getModifierList()).forEach(e -> builder.append(e.getName()).append(BLANK_SEPARATOR));
        builder.append(getMethodReturnClassName(psiMethod) + BLANK_SEPARATOR)
                .append(getMethodName(psiMethod))
                .append(PARENTHESIS_OPEN)
                .append(StringUtils.join(getParameters(), PsiParameter::getText, COMMA_SEPARATOR))
                .append(PARENTHESIS_CLOSE + BRACE_OPEN);
        return builder;
    }

    /**
     * 生成方法体
     * @return
     */
    abstract String generateMethodBody();

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
}
