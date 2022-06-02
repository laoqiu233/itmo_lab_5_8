package com.dmtri.common.util;

/**
 * funni colors haha so flashy much wow rgb go brrrr
 */
public enum TerminalColors {
    RESET("\u001b[0m"), GREEN("\u001b[32m"), BLUE("\u001b[34m"), RED("\u001b[31m");

    private static boolean doColoring = true;
    private final String ansiCode;

    TerminalColors(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    public String toString() {
        return ansiCode;
    }

    public static void doColoring(boolean flag) {
        doColoring = flag;
    }

    public static String colorString(String s, TerminalColors color) {
        if (doColoring) {
            StringBuilder sb = new StringBuilder();
            sb.append(color);
            sb.append(s);
            sb.append(RESET);
            return sb.toString();
        } else {
            return s;
        }
    }
}
