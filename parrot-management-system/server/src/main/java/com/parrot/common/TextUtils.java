package com.parrot.common;

/**
 * 项目里偶尔用到的字符串判断，保持简单就够了。
 */
public class TextUtils {

    private TextUtils() {
    }

    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean hasText(String text) {
        return !isBlank(text);
    }
}
