package cn.bigcoder.plugin.objecthelper.generator.method;

import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getPsiClass;

import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import cn.bigcoder.plugin.objecthelper.generator.AbstractMethodGenerator;
import cn.bigcoder.plugin.objecthelper.generator.copy.AbstractObjectCopyStrategy;
import cn.bigcoder.plugin.objecthelper.generator.copy.strategy.BuilderObjectCopyStrategy;
import cn.bigcoder.plugin.objecthelper.generator.copy.strategy.SetObjectCopyStrategy;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import java.util.regex.Pattern;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public class ObjectCopyMethodGenerator extends AbstractMethodGenerator {

    /**
     * 方法第一个参数名称
     */
    private String firstParameterName;

    private void init(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return;
        }
        super.project = psiMethod.getProject();
        super.psiMethod = psiMethod;
        this.firstParameterName = getFirstParameter().getName();
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
        AbstractObjectCopyStrategy copyStrategy = getCopyStrategy();
        return copyStrategy.generate();
    }

    private AbstractObjectCopyStrategy getCopyStrategy() {
        // mainClass 代表以哪个类字段为基础生成字段拷贝代码
        PsiClass returnClass = getReturnClass();
        PsiClass sourceClass = getFirstParameterClass();
        String builderRegex = PluginConfigState.getInstance().getBuilderInstanceMethodName();

        Pattern pattern = Pattern.compile(builderRegex);
        for (PsiMethod method : returnClass.getMethods()) {
            if (pattern.matcher(method.getName()).matches()) {
                return new BuilderObjectCopyStrategy(sourceClass, returnClass, this.firstParameterName);
            }
        }
        return new SetObjectCopyStrategy(sourceClass, returnClass, this.firstParameterName);
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