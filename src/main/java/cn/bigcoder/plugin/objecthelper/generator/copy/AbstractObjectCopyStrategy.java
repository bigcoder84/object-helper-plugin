package cn.bigcoder.plugin.objecthelper.generator.copy;

import cn.bigcoder.plugin.objecthelper.common.enums.EnableEnum;
import cn.bigcoder.plugin.objecthelper.common.enums.FieldGenerateModeEnum;
import cn.bigcoder.plugin.objecthelper.common.util.PsiUtils;
import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: Jindong.Tian
 * @date: 2024-05-05
 **/
public abstract class AbstractObjectCopyStrategy implements Generator {

    /**
     * 对象拷贝源对象类型
     */
    protected PsiClass sourceClass;
    /**
     * 对象拷贝目标对象类型
     */
    protected PsiClass targetClass;
    /**
     * 对象拷贝源参数名称
     */
    protected String sourceParamName;
    /**
     * 对象拷贝目标参数名称
     */
    protected String targetParamName;

    public AbstractObjectCopyStrategy(PsiClass sourceClass, PsiClass targetClass, String sourceParamName,
            String targetParamName) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.sourceParamName = sourceParamName;
        // 生成参数名
        this.targetParamName = targetParamName;
    }

    @Override
    public String generate() {
        StringBuilder result = new StringBuilder();
        // 生成前缀
        result.append(generatePrefix());

        // 字段copy模式
        FieldGenerateModeEnum generateModeEnum = PluginConfigState.getInstance().getObjectCopyMethodFieldGenerateMode();

        // mainClass 代表以哪个类字段为基础生成字段拷贝代码
        PsiClass mainClass = targetClass;
        PsiClass secondClass = sourceClass;
        if (generateModeEnum == FieldGenerateModeEnum.SOURCE) {
            // 字段拷贝使用源字段为蓝本拷贝
            mainClass = sourceClass;
            secondClass = targetClass;
        }

        Set<String> secondFieldNames = PsiUtils.getAllPsiFields(secondClass).stream().filter(PsiUtils::isMemberField)
                .map(PsiField::getName).collect(Collectors.toSet());

        List<String> annotationLine = new LinkedList<>();
        for (PsiField field : PsiUtils.getAllPsiFields(mainClass)) {
            if (!PsiUtils.isMemberField(field)) {
                continue;
            }
            if (secondFieldNames.contains(field.getName())) {
                result.append(generateFiledCopy(field));
            } else if (PluginConfigState.getInstance().getObjectCopyMethodFieldGenerateAnnotation()
                    == EnableEnum.ENABLE) {
                // 如果源对象没有该字段，且开启了以注释模式生成代码的开关，则生成注释
                annotationLine.add("// " + generateFiledCopy(field));
            }
        }
        annotationLine.forEach(result::append);
        result.append(generateSuffix());
        return result.toString();
    }

    protected abstract String generatePrefix();

    protected abstract String generateFiledCopy(PsiField field);

    protected abstract String generateSuffix();
}
