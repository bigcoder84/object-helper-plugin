package cn.bigcoder.plugin.objecthelper.generator.copy;

import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.copy.strategy.BuilderObjectCopyStrategy;
import cn.bigcoder.plugin.objecthelper.generator.copy.strategy.SetObjectCopyStrategy;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import java.util.regex.Pattern;

/**
 * 根据 sourceClass 和 targetClass 生成字段拷贝代码
 *
 * @author: bigcoder84
 * @date: 2025-06-08
 **/
public class SmartObjectCopyGenerator implements Generator {

    private AbstractObjectCopyStrategy objectCopyGenerator;


    public SmartObjectCopyGenerator(PsiClass sourceClass, PsiClass targetClass, String sourceParameterName, String targetParameterName) {
        // mainClass 代表以哪个类字段为基础生成字段拷贝代码
        String builderRegex = PluginConfigState.getInstance().getBuilderInstanceMethodName();

        // 判断目标类是否有 builder 方法
        Pattern pattern = Pattern.compile(builderRegex);
        for (PsiMethod method : targetClass.getMethods()) {
            if (pattern.matcher(method.getName()).matches()) {
                // 有 builder 方法，使用 BuilderObjectCopyStrategy
                objectCopyGenerator = new BuilderObjectCopyStrategy(sourceClass, targetClass, sourceParameterName,
                        targetParameterName);
                return;
            }
        }
        objectCopyGenerator = new SetObjectCopyStrategy(sourceClass, targetClass, sourceParameterName,
                targetParameterName);
    }

    @Override
    public String generate() {
        return objectCopyGenerator.generate();
    }
}
