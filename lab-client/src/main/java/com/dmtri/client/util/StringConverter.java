package com.dmtri.client.util;

/**
 * Utility class to convert strings into different classes
 * If provided string is empty, a null value should be returned.
 */
public interface StringConverter<T> {
    T convert(String value);
}
