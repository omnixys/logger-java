package com.omnixys.logger.utils;

/**
 * Lightweight SLF4J-style formatter.
 */
public final class LogFormatter {

    private LogFormatter() {}

    public static String format(String template, Object... args) {
        if (args == null || args.length == 0) return template;

        StringBuilder sb = new StringBuilder();
        int argIndex = 0;

        for (int i = 0; i < template.length(); i++) {
            if (i < template.length() - 1 &&
                    template.charAt(i) == '{' &&
                    template.charAt(i + 1) == '}') {

                if (argIndex < args.length) {
                    sb.append(args[argIndex++]);
                } else {
                    sb.append("{}");
                }
                i++;
            } else {
                sb.append(template.charAt(i));
            }
        }

        return sb.toString();
    }
}