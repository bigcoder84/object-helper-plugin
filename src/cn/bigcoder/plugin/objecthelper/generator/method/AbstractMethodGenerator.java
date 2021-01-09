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
                .append("}\n");
        return result.toString();
    }

    protected StringBuilder generateMethodFirstLine() {
        StringBuilder builder = new StringBuilder();
        methodModifies.forEach(e -> builder.append(e.getName() + " "));
        builder.append(returnClassName + " ")
                .append(methodName)
                .append("(")
                .append(StringUtils.join(parameters, PsiParameter::getText, " ,"))
                .append(") {\n");
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
