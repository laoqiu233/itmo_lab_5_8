package com.dmtri.client.util;

/**
 * Class to handle number conversion and NumberFormatException
 */
public class NumberStringConverter<T extends Number> implements StringConverter<T> {
    StringConverter<T> baseConverter;

    public NumberStringConverter(StringConverter<T> baseConverter) {
        this.baseConverter = baseConverter;
    }

    @Override
    public T convert(String s) {
        try {
            return baseConverter.convert(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
