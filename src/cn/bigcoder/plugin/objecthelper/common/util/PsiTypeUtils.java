package cn.bigcoder.plugin.objecthelper.common.util;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import org.apache.commons.lang.time.DateFormatUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: Jindong.Tian
 * @date: 2021-02-12
 **/
public class PsiTypeUtils {
    private static final String STRING_TYPE = "java.lang.String";
    private static final String INTEGER_TYPE = "java.lang.Integer";
    private static final String LONG_TYPE = "java.lang.Long";
    private static final String SHORT_TYPE = "java.lang.Short";
    private static final String BYTE_TYPE = "java.lang.Byte";
    private static final String DOUBLE_TYPE = "java.lang.Double";
    private static final String FLOAT_TYPE = "java.lang.Float";
    private static final String DATE_TYPE = "java.util.Date";
    private static final String LOCAL_DATE_TYPE = "java.time.LocalDate";
    private static final String LOCAL_DATE_TIME_TYPE = "java.time.LocalDateTime";

    private static final String COLLECTION_TYPE = "java.util.Collection";
    private static final String OBJECT_TYPE = "java.lang.Object";

    /**
     * 获取数据类型的默认值
     *
     * @param canonicalText 类的全限定名称
     * @return
     */
    public static Object getDataTypeDefaultValue(String canonicalText) {
        if (StringUtils.isEmpty(canonicalText)) {
            return null;
        }
        switch (canonicalText) {
            case STRING_TYPE:
                return "";
            case INTEGER_TYPE:
                return 1;
            case LONG_TYPE:
                return 1L;
            case SHORT_TYPE:
                return (short) 1;
            case BYTE_TYPE:
                return (byte) 1;
            case DOUBLE_TYPE:
                return 1.0;
            case FLOAT_TYPE:
                return 1.0f;
            case DATE_TYPE:
                return DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            case LOCAL_DATE_TYPE:
                return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case LOCAL_DATE_TIME_TYPE:
                return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            default:
                return null;
        }
    }

    /**
     * 判断是否是数据类型
     *
     * @param psiType
     * @return
     */
    public static boolean isDataType(PsiType psiType) {
        String canonicalName = psiType.getCanonicalText();
        if (STRING_TYPE.equals(canonicalName)
                || INTEGER_TYPE.equals(canonicalName)
                || LONG_TYPE.equals(canonicalName)
                || SHORT_TYPE.equals(canonicalName)
                || BYTE_TYPE.equals(canonicalName)
                || DOUBLE_TYPE.equals(canonicalName)
                || FLOAT_TYPE.equals(canonicalName)
                || DATE_TYPE.equals(canonicalName)
                || LOCAL_DATE_TYPE.equals(canonicalName)
                || LOCAL_DATE_TIME_TYPE.equals(canonicalName)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是数组类型
     *
     * @param psiType
     * @return
     */
    public static boolean isArrayType(PsiType psiType) {
        return psiType instanceof PsiArrayType;
    }

    /**
     * 判断是否是Collection类型
     *
     * @param psiType
     * @return
     */
    public static boolean isCollectionType(PsiType psiType) {
        if (!(psiType instanceof PsiClassType)) {
            return false;
        }
        PsiClassType psiClassReferenceType = ((PsiClassType) psiType);
        PsiClass resolvePsiClass = psiClassReferenceType.resolve();
        if (resolvePsiClass == null) {
            return false;
        }
        if (OBJECT_TYPE.equals(resolvePsiClass.getQualifiedName())) {
            return false;
        }
        if (COLLECTION_TYPE.equals(resolvePsiClass.getQualifiedName())) {
            return true;
        }
        // 如果父类是Collection类型
        for (PsiType parentPsiType : ((PsiClassType) psiType).rawType().getSuperTypes()) {
            if (isCollectionType(parentPsiType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是Java官方类库
     *
     * @param psiType
     * @return
     */
    public static boolean isJavaOfficialType(PsiType psiType) {
        return psiType.getCanonicalText().startsWith("java");
    }

}
