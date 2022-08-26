package cn.bigcoder.plugin.objecthelper.common.util;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Function;

/**
 * @author: Jindong.Tian
 * @date: 2021-01-09
 **/
public class StringUtils {

    public static String join(Collection<String> items, String separator) {
        return join(items, Function.identity(), separator);
    }

    public static <T> String join(Collection<T> items, Function<T, String> handle, String separator) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        int index = 0;
        for (T item : items) {
            result.append(handle.apply(item));
            if (index < items.size() - 1) {
                result.append(separator);
            }
            index++;
        }
        return result.toString();
    }

    public static boolean isEmpty(String str) {
        if (str == null || " ".equals(str)) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 将首字母转为大写
     * @param oldStr
     * @return
     */
    public static String firstUpperCase(String oldStr) {
        return oldStr.substring(0, 1).toUpperCase() + oldStr.substring(1);
    }

    /**
     * 将首字母转为小写
     * @param oldStr
     * @return
     */
    public static String firstLowerCase(String oldStr) {
        return oldStr.substring(0, 1).toLowerCase(Locale.ROOT) + oldStr.substring(1);
    }
}
