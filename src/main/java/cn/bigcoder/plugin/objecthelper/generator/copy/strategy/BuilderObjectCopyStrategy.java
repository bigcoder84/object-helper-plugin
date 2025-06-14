package cn.bigcoder.plugin.objecthelper.generator.copy.strategy;

import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.BLANK_SEPARATOR;
import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.LINE_SEPARATOR;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getPsiClassName;

import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import cn.bigcoder.plugin.objecthelper.generator.copy.AbstractObjectCopyStrategy;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;

/**
 * @author: Jindong.Tian
 * @date: 2024-05-05
 **/
public class BuilderObjectCopyStrategy extends AbstractObjectCopyStrategy {

    public BuilderObjectCopyStrategy(PsiClass sourceClass, PsiClass targetClass, String sourceParamName,
            String targetParamName) {
        super(sourceClass, targetClass, sourceParamName, targetParamName);
    }

    @Override
    protected String generatePrefix() {
        String className = getPsiClassName(targetClass);
        return className + BLANK_SEPARATOR + super.targetParamName + " = " + className + ".builder()" + LINE_SEPARATOR;
    }


    @Override
    protected String generateFiledCopy(PsiField field) {
        return "." + field.getName() + "("
                + sourceParamName + ".get" + StringUtils.firstUpperCase(field.getName()) + "())"
                + LINE_SEPARATOR;
    }


    @Override
    protected String generateSuffix() {
        return ".build();" + LINE_SEPARATOR;
    }
}
