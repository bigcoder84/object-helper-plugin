package cn.bigcoder.plugin.objecthelper.common.util;

import static cn.bigcoder.plugin.objecthelper.common.constant.JavaClassName.*;
import static cn.bigcoder.plugin.objecthelper.common.constant.IDLTypeName.*;

/**
 * @author: Jindong.Tian
 * @date: 2021-08-21
 **/
public class IDLUtils {

    /**
     * 将Java类型转换为IDL类型声明
     * @param canonicalText 类的全限定名称
     * @return
     */
    public static String convertJavaTypeToIDLType(String canonicalText){
        if (StringUtils.isEmpty(canonicalText)) {
            return null;
        }
        switch (canonicalText) {
            case STRING_TYPE:
                return STRING;
            case INTEGER_TYPE:
                return I32;
            case LONG_TYPE:
                return I64;
            case SHORT_TYPE:
                return I16;
            case BYTE_TYPE:
                return BYTE;
            case DOUBLE_TYPE:
                return DOUBLE;
            case FLOAT_TYPE:
                return DOUBLE;
            case DATE_TYPE:
                return I64;
            case LOCAL_DATE_TYPE:
                return I64;
            case LOCAL_DATE_TIME_TYPE:
                return I64;
            default:
                return null;
        }
    }


}
