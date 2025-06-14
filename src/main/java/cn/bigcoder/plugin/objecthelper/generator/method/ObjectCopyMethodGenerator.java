package cn.bigcoder.plugin.objecthelper.generator.method;

import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.SEMICOLON_SEPARATOR;

import cn.bigcoder.plugin.objecthelper.generator.AbstractMethodGenerator;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.copy.SmartObjectCopyGenerator;
import com.intellij.psi.PsiMethod;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public class ObjectCopyMethodGenerator extends AbstractMethodGenerator {

    public ObjectCopyMethodGenerator(PsiMethod psiMethod) {
        super(psiMethod);
    }

    public static ObjectCopyMethodGenerator getInstance(PsiMethod psiMethod) {
        return new ObjectCopyMethodGenerator(psiMethod);
    }

    /**
     * 生成对象拷贝方法提
     * 此方法中不应该存在判空的操作，依赖环境的建议重写父类的check方法，在生成早期拦截异常情况
     *
     * @return
     */
    @Override
    protected String generateMethodBody() {
        StringBuilder stringBuilder = new StringBuilder();
        // check null line
        String nullCheckStr = generateNullCheck();
        stringBuilder.append(nullCheckStr);

        // object copy block
        Generator generator = new SmartObjectCopyGenerator(firstParameterClass, targetClass, firstParameterName,
                targetParameterName);
        stringBuilder.append(generator.generate());

        // return line
        stringBuilder.append("return ")
                .append(targetParameterName)
                .append(SEMICOLON_SEPARATOR);
        return stringBuilder.toString();
    }


    /**
     * 生成类似如下代码：
     *
     * if (user == null) {
     * return null;
     * }
     *
     * @return
     */
    private String generateNullCheck() {
        return "if(" + super.firstParameterName + "==null){return null;}";
    }
}