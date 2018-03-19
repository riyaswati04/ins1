package com.ia.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class StringUtil {

    /**
     * Names, particularly passwords, may contain single quotes. Escape as necessary.
     */
    public static String escapeQuote(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (str.indexOf('\\') != -1) {
            str = str.replaceAll("\\\\", "\\\\\\\\");
        }
        if (str.indexOf('\'') != -1) {
            return str.replaceAll("'", "\\\\'");
        }
        return str;
    }

    /**
     * To return last character from given string.
     *
     * @param str
     * @param length
     */

    public static String getLastCharacters(String str, final int length) {
        if (isEmpty(str)) {
            return null;
        }
        str = str.trim();
        if (str.length() == length) {
            return str;
        }
        else if (str.length() > length) {
            return str.substring(str.length() - length);
        }
        else {
            throw new IllegalArgumentException("String has less than " + length + " characters!");
        }
    }

    public static boolean isEmpty(final String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isLocalhost(final String ipAddress) {
        boolean result = false;

        if (ipAddress.equals("::1") || ipAddress.equals("127.0.0.1")) {
            result = true;
        }

        return result;
    }

    public static String join(final String delimeter, final String... args) {
        return Joiner.on(delimeter).join(args);
    }

    /**
     * Split a string based on a delimiter, handle delimiter as part of data when escaped (followed
     * by '\')
     *
     * @param str the string to be split
     * @param ch delimiter character
     * @return <code>List&lt;String&gt;</code> with all tokens
     */
    public static List<String> split(final String str, final char ch) {
        final ArrayList<String> list = new ArrayList<String>(10);
        if (isEmpty(str)) {
            return list;
        }

        char lastChar = ' ';
        final StringBuilder lastToken = new StringBuilder(10);
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (c == ch && lastChar != '\\') {
                list.add(lastToken.toString());
                lastToken.delete(0, lastToken.length());
            }
            else {
                lastToken.append(c);
            }
            lastChar = c;
        }
        // Add the last token
        list.add(lastToken.toString());

        return list;
    }
}
