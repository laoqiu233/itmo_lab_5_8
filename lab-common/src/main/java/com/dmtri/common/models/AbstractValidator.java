package com.dmtri.common.models;

import com.dmtri.common.exceptions.InvalidFieldException;

/**
 * Validator for models.
 * Models should have a static validator object that extends this class.
 */
public interface AbstractValidator<T> {
    /**
     * Validates all fields in a object.
     * Should be called in a models constructor after initializing
     * all fields.
     * @throws InvalidFieldException if a field contains invalid value
     */
    void validate(T toValidate) throws InvalidFieldException;

    /**
     * Helper method that throws a {@link com.dmtri.common.exceptions.InvalidFieldException}
     * the field provided is null.
     * @param field the field to check.
     * @param message message to put in exception
     * @throws InvalidFieldException the provided field is null
     */
    static void ensureNotNull(Object field, String message, String localeKey) throws InvalidFieldException {
        if (field == null) {
            throw new InvalidFieldException(message, localeKey);
        }
    }
}
