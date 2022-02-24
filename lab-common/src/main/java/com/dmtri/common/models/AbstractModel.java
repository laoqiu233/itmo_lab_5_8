package com.dmtri.common.models;

import com.dmtri.common.exceptions.InvalidFieldException;

/**
 * A helper class with a few utility methods.
 */
public abstract class AbstractModel {
    /**
     * Validates all fields in a object.
     * Should be called in a models constructor after initializing
     * all fields.
     * @throws InvalidFieldException if a field contains invalid value
     */
    protected abstract void validate() throws InvalidFieldException;

    /**
     * Helper method that throws a {@link com.dmtri.common.exceptions.InvalidFieldException}
     * the field provided is null.
     * @param field the field to check.
     * @param message message to put in exception
     * @throws InvalidFieldException the provided field is null
     */
    static void ensureNotNull(Object field, String message) throws InvalidFieldException {
        if (field == null) {
            throw new InvalidFieldException(message);
        }
    }
}
