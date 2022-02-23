package com.dmtri.common.models;

import com.dmtri.common.exceptions.InvalidFieldException;

public class Coordinates extends AbstractModel {
    private Long x;
    private Double y; // Поле не может быть null
    private Long z;

    public Coordinates(Long x, Double y, Long z) throws InvalidFieldException {
        this.x = x;
        this.y = y;
        this.z = z;
        validate();
    }

    protected void validate() throws InvalidFieldException {
        AbstractModel.ensureNotNull(this.y, "Field y for objects of type coordinates can not be null");
    }

    public String toString() {
        return "Coordinates {\n\tx=" + x + ",\n\ty=" + y + ",\n\tz=" + z + "\n}";
    }
}