package com.dmtri.client.modelmakers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.util.TerminalColors;

public final class BasicParsers {
    public static class Repeater {
        public static interface Parser<T> {
            T parse(BasicUserIO io) throws InvalidFieldException;
        }
    
        public static <T> T doUntilGet(Parser<T> parser, BasicUserIO io) {
            do {
                try {
                    return parser.parse(io);
                } catch (InvalidFieldException e) {
                    io.writeln(e.getMessage());
                    io.writeln("Please try again.");
                }
            } while (true);
        }
    }

    public static Long parseLong(BasicUserIO io, String prompt, String errorMsg) throws InvalidFieldException {
        try {
            String input = io.read(
                TerminalColors.colorString(prompt, TerminalColors.BLUE)
            );

            if (input.trim().isEmpty()) return null;

            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new InvalidFieldException(errorMsg, e);
        }
    }

    public static Double parseDouble(BasicUserIO io, String prompt, String errorMsg) throws InvalidFieldException {
        try {
            String input = io.read(
                TerminalColors.colorString(prompt, TerminalColors.BLUE)
            );

            if (input.trim().isEmpty()) return null;

            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new InvalidFieldException(errorMsg, e);
        }
    }

    public static String parseString(BasicUserIO io, String prompt) {
        String input = io.read(
            TerminalColors.colorString(prompt, TerminalColors.BLUE)
        );

        if (input.trim().isEmpty()) return null;

        return input;
    }

    public static LocalDate parseLocalDate(BasicUserIO io, String prompt, String errorMsg) throws InvalidFieldException {
        try {
            String input = io.read(
                TerminalColors.colorString(prompt, TerminalColors.BLUE)
            );

            if (input.trim().isEmpty()) return null;

            return LocalDate.parse(
                input,
                java.time.format.DateTimeFormatter.ISO_DATE
            );
        } catch (DateTimeParseException e) {
            throw new InvalidFieldException(errorMsg, e);
        }
    }
}
