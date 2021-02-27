package cn.bigcoder.plugin.objecthelper.generator.json;

import cn.bigcoder.plugin.objecthelper.common.util.PsiTypeUtils;
import cn.bigcoder.plugin.objecthelper.common.util.PsiUtils;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang.ArrayUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: Jindong.Tian
 * @date: 2021-02-11
 **/
public class ClassJsonGenerator implements Generator {

    private PsiClass psiClass;

    /**
     * 保存已经解析过的自定义类型名称，防止出现递归嵌套的情况
     */
    private Set<String> recursiveCache = Sets.newHashSet();

    private Gson gson;

    private ClassJsonGenerator(PsiClass psiClass, Gson gson) {
        this.psiClass = psiClass;
        this.gson = gson;
    }

    /**
     * 获取ClassJsonGenerator实例
     *
     * @param psiClass JSON生成的目标类
     * @param gson     Gson对象
     * @return
     */
    public static ClassJsonGenerator getInstance(PsiClass psiClass, Gson gson) {
        return new ClassJsonGenerator(psiClass, gson);
    }

    @Override
    public String generate() {
        Map<String, Object> jsonMap = processFields(psiClass);
        return gson.toJson(jsonMap);
    }

    private Map<String, Object> processFields(PsiClass psiClass) {
        Map<String, Object> result = Maps.newLinkedHashMap();
        // 当前类所有字段
        List<PsiField> allPsiFields = PsiUtils.getAllPsiFields(psiClass);
        if (CollectionUtils.isEmpty(allPsiFields)) {
            return result;
        }
        for (PsiField psiField : allPsiFields) {
            result.put(psiField.getName(), processField(psiField.getType()));
        }
        return result;
    }

    private Object processField(PsiType psiType) {
        Object defaultValue = null;
        if (PsiTypeUtils.isDataType(psiType)) {
            //如果是数据类型
            defaultValue = PsiTypeUtils.getDataTypeDefaultValue(psiType.getCanonicalText());
        } else if (PsiTypeUtils.isArrayType(psiType)) {
            //如果是数组类型
            List<Object> list = Lists.newArrayList();
            PsiClass arrayContentClass = PsiUtils.getPsiClass(((PsiArrayType) psiType).getComponentType(), psiClass.getProject());
            list.add(PsiTypeUtils.getDataTypeDefaultValue(arrayContentClass.getQualifiedName()));
            defaultValue = list;
        } else if (PsiTypeUtils.isCollectionType(psiType)) {
            defaultValue = Lists.newArrayList();
            //如果是集合类型
            PsiType[] parameters = ((PsiClassReferenceType) psiType).getParameters();
            if (ArrayUtils.isEmpty(parameters)) {
                return defaultValue;
            }
            //获取泛型
            PsiType genericType = parameters[0];
            ((List) defaultValue).add(processField(genericType));
        } else if (!PsiTypeUtils.isJavaOfficialType(psiType)) {
            //如果是自定义类型
            if (recursiveCache.contains(psiType.getCanonicalText())) {
                //出现递归嵌套
                return null;
            }
            recursiveCache.add(psiType.getCanonicalText());
            //如果是自定义类
            defaultValue = processFields(PsiUtils.getPsiClass(psiType, psiClass.getProject()));
        }
        return defaultValue;
    }
}