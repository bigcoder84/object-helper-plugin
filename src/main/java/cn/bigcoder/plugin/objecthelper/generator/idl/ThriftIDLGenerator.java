package cn.bigcoder.plugin.objecthelper.generator.idl;

import cn.bigcoder.plugin.objecthelper.common.util.IDLUtils;
import cn.bigcoder.plugin.objecthelper.common.util.PsiTypeUtils;
import cn.bigcoder.plugin.objecthelper.common.util.PsiUtils;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;

import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author: Jindong.Tian
 * @date: 2021-08-20
 **/
public class ThriftIDLGenerator implements Generator {

    private PsiClass psiClass;

    /**
     * 保存已经解析过的自定义类型名称，防止出现递归嵌套的情况
     */
    private Set<String> recursiveCache = Sets.newHashSet();

    private ThriftIDLGenerator(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    @Override
    public String generate() {
        return generateStructCode(psiClass).toString();
    }

    /**
     * 生成类的IDL语句
     *
     * @param psiClass
     * @return
     */
    private StringBuilder generateStructCode(PsiClass psiClass) {
        if (psiClass == null) {
            return new StringBuilder();
        }
        if (recursiveCache.contains(psiClass.getQualifiedName())) {
            return new StringBuilder();
        }
        recursiveCache.add(psiClass.getQualifiedName());
        List<String> fields = Lists.newArrayList();
        List<PsiField> allPsiFields = PsiUtils.getAllPsiFields(psiClass);
        StringBuilder code = new StringBuilder();
        for (PsiField allPsiField : allPsiFields) {
            code = processAssociateClass(allPsiField.getType(), code);
            String field = processPsiField(allPsiField);
            if (StringUtils.isNotEmpty(field)) {
                fields.add(field);
            }
        }
        return code.append(generateStructCode(psiClass.getName(), fields));
    }

    /**
     * 生成类泛型关联类的IDL语句
     *
     * @param psiType
     * @param code
     * @return
     */
    private StringBuilder processAssociateClass(PsiType psiType, StringBuilder code) {
        if (PsiTypeUtils.isArrayType(psiType) && PsiTypeUtils.isNotJavaOfficialType(psiType)) {
            //如果是数组类型
            PsiClass arrayContentClass = PsiUtils.getPsiClass(((PsiArrayType) psiType).getComponentType(), psiClass.getProject());
            code = generateStructCode(arrayContentClass).append(code).append("\n");
        } else if (PsiTypeUtils.isListType(psiType) || PsiTypeUtils.isSetType(psiType)) {
            PsiType[] parameters = ((PsiClassReferenceType) psiType).getParameters();
            // 存在泛型且泛型不是Java自带的类型，则需要生成一个新的struct（后期这可以支持配置，配置需要生成的白名单或黑名单）
            if (!ArrayUtils.isEmpty(parameters) && PsiTypeUtils.isNotJavaOfficialType(parameters[0])) {
                //获取泛型
                PsiClass fieldPsiClass = PsiUtils.getPsiClass(parameters[0], psiClass.getProject());
                code = generateStructCode(fieldPsiClass).append(code).append("\n");
            }
        } else if (PsiTypeUtils.isMapType(psiType)) {
            PsiType[] parameters = ((PsiClassReferenceType) psiType).getParameters();
            if (!ArrayUtils.isEmpty(parameters)) {
                if (parameters.length >= 1 && PsiTypeUtils.isNotJavaOfficialType(parameters[0])) {
                    PsiClass fieldPsiClass = PsiUtils.getPsiClass(parameters[0], psiClass.getProject());
                    code = generateStructCode(fieldPsiClass).append(code).append("\n");
                }
                if (parameters.length >= 2 && PsiTypeUtils.isNotJavaOfficialType(parameters[1])) {
                    PsiClass fieldPsiClass = PsiUtils.getPsiClass(parameters[1], psiClass.getProject());
                    code = generateStructCode(fieldPsiClass).append(code).append("\n");
                }
            }
        } else if (PsiTypeUtils.isNotJavaOfficialType(psiType)) {
            //如果是自定义类型
            PsiClass fieldPsiClass = PsiUtils.getPsiClass(psiType, psiClass.getProject());
            code = generateStructCode(fieldPsiClass).append(code).append("\n");
        }
        return code;
    }

    /**
     * 生成struct代码
     * @param structName IDL名称
     * @param fields struct中所有字段声明
     * @return
     */
    private StringBuilder generateStructCode(String structName, List<String> fields) {
        StringBuilder code = new StringBuilder();
        code.append("struct ").append(structName).append(" {\n");
        for (int i = 0; i < fields.size(); i++) {
            code.append("  ").append(i + 1).append(":").append(fields.get(i)).append(";\n");
        }
        code.append("}\n");
        return code;
    }

    /**
     * 生成IDL字段声明
     *
     * @param psiField
     * @return
     */
    private String processPsiField(PsiField psiField) {
        String fieldName = psiField.getName();
        String type = processType(psiField.getType());
        if (type == null) {
            return null;
        }
        return type + " " + fieldName;
    }

    /**
     * 将Java Type转成IDL Type
     *
     * @param psiType
     * @return
     */
    private String processType(PsiType psiType) {
        if (PsiTypeUtils.isDataType(psiType)) {
            //如果是数据类型
            return IDLUtils.convertJavaTypeToIDLType(psiType.getCanonicalText());
        } else if (PsiTypeUtils.isArrayType(psiType)) {
            //如果是数组类型
            PsiClass arrayContentClass = PsiUtils.getPsiClass(((PsiArrayType) psiType).getComponentType(), psiClass.getProject());
            String qualifiedName = arrayContentClass.getQualifiedName();
            String typeName = IDLUtils.convertJavaTypeToIDLType(qualifiedName);
            if (typeName == null) {
                typeName = qualifiedName.substring(qualifiedName.lastIndexOf("."));
            }
            return "list<" + typeName + ">";
        } else if (PsiTypeUtils.isListType(psiType)) {
            PsiType[] parameters = ((PsiClassReferenceType) psiType).getParameters();
            if (ArrayUtils.isEmpty(parameters)) {
                return "list";
            }
            //获取泛型
            PsiType genericType = parameters[0];
            return "list<" + processType(genericType) + ">";
        } else if (PsiTypeUtils.isSetType(psiType)) {
            PsiType[] parameters = ((PsiClassReferenceType) psiType).getParameters();
            if (ArrayUtils.isEmpty(parameters)) {
                return "set";
            }
            //获取泛型
            PsiType genericType = parameters[0];
            return "set<" + processType(genericType) + ">";
        } else if (PsiTypeUtils.isMapType(psiType)) {
            PsiType[] parameters = ((PsiClassReferenceType) psiType).getParameters();
            if (ArrayUtils.isEmpty(parameters) && parameters.length == 2) {
                return "map";
            }
            return "map<" + processType(parameters[0]) + ", " + processType(parameters[1]) + ">";
        } else if (PsiTypeUtils.isNotJavaOfficialType(psiType)) {
            //如果是自定义类型
            String canonicalText = psiType.getCanonicalText();
            return canonicalText.substring(canonicalText.lastIndexOf(".") + 1);
        }
        return null;
    }

    public static ThriftIDLGenerator getInstance(PsiClass psiClass) {
        return new ThriftIDLGenerator(psiClass);
    }
}
