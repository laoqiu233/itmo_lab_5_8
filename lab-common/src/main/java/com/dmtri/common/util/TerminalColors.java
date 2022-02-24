package com.dmtri.common.util;

public enum TerminalColors {
    RESET("\u001b[0m"), GREEN("\u001b[32m");

    private final String ansiCode;

    TerminalColors(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    public String toString() {
        return ansiCode;
    }

    public static String colorString(String s, TerminalColors color) {
        StringBuilder sb = new StringBuilder();
        sb.append(color);
        sb.append(s);
        sb.append(RESET);
        return sb.toString();
    }
}
