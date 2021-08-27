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

import static cn.bigcoder.plugin.objecthelper.common.constant.JavaClassName.*;

/**
 * @author: Jindong.Tian
 * @date: 2021-02-12
 **/
public class PsiTypeUtils {

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
     * 判断是否是java.util.Collection类型
     *
     * @param psiType
     * @return
     */
    public static boolean isCollectionType(PsiType psiType) {
        return isSpecifiedType(psiType, COLLECTION_TYPE);
    }

    /**
     * 判断是否是java.util.List类型
     *
     * @param psiType
     * @return
     */
    public static boolean isListType(PsiType psiType) {
        return isSpecifiedType(psiType, LIST_TYPE);
    }

    /**
     * 判断是否是java.util.List类型
     *
     * @param psiType
     * @return
     */
    public static boolean isSetType(PsiType psiType) {
        return isSpecifiedType(psiType, SET_TYPE);
    }

    /**
     * 判断是否是java.util.Map类型
     *
     * @param psiType
     * @return
     */
    public static boolean isMapType(PsiType psiType) {
        return isSpecifiedType(psiType, MAP_TYPE);
    }


    /**
     * 判断一个类是否是指定类型子类
     * @param psiType psiType
     * @param qualifiedName 全限定名称
     * @return
     */
    public static boolean isSpecifiedType(PsiType psiType, String qualifiedName){
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
        if (qualifiedName.equals(resolvePsiClass.getQualifiedName())) {
            return true;
        }
        for (PsiType parentPsiType : ((PsiClassType) psiType).rawType().getSuperTypes()) {
            if (isSpecifiedType(parentPsiType, qualifiedName)) {
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

    /**
     * 如果不是Java官方类库则返回true
     *
     * @param psiType
     * @return
     */
    public static boolean isNotJavaOfficialType(PsiType psiType) {
        return !isJavaOfficialType(psiType);
    }


}
