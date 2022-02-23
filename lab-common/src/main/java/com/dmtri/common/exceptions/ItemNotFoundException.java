package com.dmtri.common.exceptions;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(long id) {
        super("Cannot find item with specified id (" + id + ").");
    }
}