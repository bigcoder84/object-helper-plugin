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

    public BuilderObjectCopyStrategy(PsiClass sourceClass, PsiClass targetClass, String sourceParamName) {
        super(sourceClass, targetClass, sourceParamName);
    }


    /**
     * 生成类似如下代码：
     *
     * if (user == null) {
     *     return null;
     * }
     * return UserDto.builder()
     *
     * @return
     */
    @Override
    protected String generatePrefix() {
        return generateNullCheck(sourceParamName) + LINE_SEPARATOR +
                "return" + BLANK_SEPARATOR + getPsiClassName(targetClass) + ".builder()" + LINE_SEPARATOR;
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

    /**
     * 生成示例：{@code if (user == null) {return null;}}
     *
     * @return
     */
    private String generateNullCheck(String sourceParamName) {
        return "if(" + sourceParamName + "==null){return null;}";
    }
}
