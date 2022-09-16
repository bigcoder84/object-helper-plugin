package cn.bigcoder.plugin.objecthelper.generator.xml;

import cn.bigcoder.plugin.objecthelper.common.util.XMLUtils;
import cn.bigcoder.plugin.objecthelper.generator.AbstractDataObjectGenerator;
import com.intellij.psi.PsiClass;

import java.util.Map;

/**
 * @author: Jindong.Tian
 * @date: 2021-02-11
 **/
public class ClassXMLGenerator extends AbstractDataObjectGenerator {

    private ClassXMLGenerator(PsiClass psiClass) {
        super(psiClass);
    }

    /**
     * 获取ClassJsonGenerator实例
     *
     * @param psiClass JSON生成的目标类
     * @return
     */
    public static ClassXMLGenerator getInstance(PsiClass psiClass) {
        return new ClassXMLGenerator(psiClass);
    }

    @Override
    public String generate() {
        Map<String, Object> jsonMap = processFields();
        return XMLUtils.mapToXml(jsonMap);
    }
}