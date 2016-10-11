package com.example.ben.zhihudaily.utils;

/**
 * Created by Zhou bangquan on 16/10/11.
 */


public class HtmlUtils {
    public static String get(String css, String body, boolean isNightMode) {
        if (isNightMode) {
            return "<head><style type=\"text/css\">" + css + "</style>\n" +
                    "</head><body class=\"night\">" + body + "</body></html>";
        } else {
            return "<head><style type=\"text/css\">" + css + "</style>\n" +
                    "</head><body>" + body + "</body></html>";
        }
    }
}
