package com.dmtri.common.models;

import com.dmtri.common.exceptions.InvalidFieldException;

public class Coordinates extends AbstractModel {
    private Long x;
    private Double y; // Поле не может быть null
    private Long z;

    /**
     * Creates a coordinates and validates data using the {@link #validate()} method.
     * @param x
     * @param y
     * @param z
     * @throws InvalidFieldException if validation fails.
     */
    public Coordinates(Long x, Double y, Long z) throws InvalidFieldException {
        this.x = x;
        this.y = y;
        this.z = z;
        validate();
    }

    public Long getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Long getZ() {
        return z;
    }

    /**
     * Validates fields for model.
     * Restrictions:
     * <ul>
     *  <li>x - Any long value</li>
     *  <li>y - Any double value, NOTNULL</li>
     *  <li>z - Any long value</li>
     * </ul>
     * @throws InvalidFieldException if a field does not satisfy the restrictions.
     */
    protected void validate() throws InvalidFieldException {
        AbstractModel.ensureNotNull(this.y, "Field y for objects of type coordinates can not be null");
    }

    public String toString() {
        return "Coordinates {\n\tx=" + x + ",\n\ty=" + y + ",\n\tz=" + z + "\n}";
    }
}
