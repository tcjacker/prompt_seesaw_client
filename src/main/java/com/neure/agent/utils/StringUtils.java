package com.neure.agent.utils;

/**
 * StringUtils
 *
 * @author tc
 * @date 2024-03-03 00:59
 */
public class StringUtils {
    public static boolean isDecimal(String str) {
        // 正则表达式匹配包括负号和小数点的数字
        // "^-?\\d+\\.\\d+$" 解释：
        // ^ 表示字符串开始
        // -? 可选的负号
        // \\d+ 一个或多个数字
        // \\. 小数点
        // \\d+ 一个或多个数字（小数部分）
        // $ 表示字符串结束
        // 注意：这个正则表达式不匹配整数
        return str.matches("-?\\d+\\.\\d+");
    }

    public static boolean isNotBlank(String url) {
        return url != null && !"".equalsIgnoreCase(url);
    }

    public static boolean isBlank(String content) {
        return !isNotBlank(content);
    }

    /**
     * Checks if the given string can be parsed as a Double.
     *
     * @param str The string to check.
     * @return true if the string can be parsed as a Double, false otherwise.
     */
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
