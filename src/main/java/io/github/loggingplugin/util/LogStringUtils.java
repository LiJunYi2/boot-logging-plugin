package io.github.loggingplugin.util;

/**
 * @version 1.0.0
 * @className: LogStringUtils
 * @description: 日志字符串处理类
 * @author: LiJunYi
 * @create: 2023/3/10 10:20
 */
public class LogStringUtils {

    public static final String EMPTY = "";

    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }
}
