package cn.bigcoder.plugin.objecthelper.action;

import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.json.ClassJsonGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.psi.PsiClass;

public class ClassToFormatJsonAction extends ClassToJsonAction {

    @Override
    protected Generator getGenerator(PsiClass psiClass) {
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        return ClassJsonGenerator.getInstance(psiClass, gson);
    }
}
