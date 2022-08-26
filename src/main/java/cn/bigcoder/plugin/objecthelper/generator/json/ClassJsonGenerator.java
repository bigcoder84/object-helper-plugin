package cn.bigcoder.plugin.objecthelper.generator.json;

import cn.bigcoder.plugin.objecthelper.generator.AbstractDataObjectGenerator;
import com.google.gson.Gson;
import com.intellij.psi.PsiClass;

import java.util.Map;

/**
 * @author: Jindong.Tian
 * @date: 2021-02-11
 **/
public class ClassJsonGenerator extends AbstractDataObjectGenerator {

    private Gson gson;

    private ClassJsonGenerator(PsiClass psiClass, Gson gson) {
        super(psiClass);
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
        Map<String, Object> jsonMap = processFields();
        return gson.toJson(jsonMap);
    }
}