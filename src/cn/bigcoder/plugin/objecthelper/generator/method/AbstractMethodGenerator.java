package cn.bigcoder.plugin.objecthelper.generator.method;

import cn.bigcoder.plugin.objecthelper.common.enums.JavaModify;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public abstract class AbstractMethodGenerator implements Generator {

    protected static final int FIRST_INDEX = 0;
    protected static final String EMPTY_BODY = "";
    protected static final String BLANK_SEPARATOR = " ";
    protected static final String LINE_SEPARATOR = "\n";
    protected static final String COMMA_SEPARATOR = ",";

    protected static final String VOID_KEYWORD = "void";

    /**
     * 方法修饰符
     */
    protected List<JavaModify> methodModifies;
    /**
     * 方法返回类型名称
     */
    protected String returnClassName;
    /**
     * 方法名称
     */
    protected String methodName;
    /**
     * 方法参数
     */
    protected List<PsiParameter> parameters;
    /**
     * 方法参数
     */
    protected List<PsiClass> parameterClass;


    public String generate() {
        StringBuilder result = generateMethodFirstLine()
                .append(generateMethodBody())
                .append("}");
        return result.toString();
    }

    protected StringBuilder generateMethodFirstLine() {
        StringBuilder builder = new StringBuilder();
        methodModifies.forEach(e -> builder.append(e.getName()).append(BLANK_SEPARATOR));
        builder.append(returnClassName + BLANK_SEPARATOR)
                .append(methodName)
                .append("(")
                .append(StringUtils.join(parameters, PsiParameter::getText, COMMA_SEPARATOR))
                .append("){");
        return builder;
    }

    protected boolean hasModifierProperty(String modify) {
        if (CollectionUtils.isEmpty(methodModifies)) {
            return false;
        }
        for (JavaModify methodModify : methodModifies) {
            if (methodModify.getName().equals(modify)) {
                return true;
            }
        }
        return false;
    }

    abstract String generateMethodBody();
}
