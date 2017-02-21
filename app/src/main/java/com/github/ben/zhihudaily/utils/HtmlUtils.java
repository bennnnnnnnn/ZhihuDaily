package com.github.ben.zhihudaily.utils;

/**
 * Created on 16/10/11.
 * @author Ben
 */


public class HtmlUtils {
    public static String get(String css, String body, boolean isNightMode) {
        if (isNightMode) {
            return "<html><head><style type=\"text/css\">" + css + "</style>\n" +
                    "</head><body class=\"night\">" + body + "</body></html>";
        } else {
            return "<html><head><style type=\"text/css\">" + css + "</style>\n" +
                    "</head><body>" + body + "</body></html>";
        }
    }
}
