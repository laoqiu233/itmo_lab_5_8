package com.dmtri.common.models;

import com.dmtri.common.exceptions.InvalidFieldException;

public abstract class AbstractModel {
    protected abstract void validate() throws InvalidFieldException;

    static void ensureNotNull(Object field, String message) throws InvalidFieldException {
        if (field == null)
            throw new InvalidFieldException(message);
    }
}
