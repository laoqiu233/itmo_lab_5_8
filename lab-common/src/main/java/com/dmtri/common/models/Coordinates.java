package com.dmtri.common.models;

import java.io.Serializable;

import com.dmtri.common.exceptions.InvalidFieldException;

public class Coordinates implements Serializable {
    public static final Validator VALIDATOR = new Validator();
    private static final long serialVersionUID = 3475182184867235091L;
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
        VALIDATOR.validate(this);
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

    public String toString() {
        return "Coordinates {\n\tx=" + x + ",\n\ty=" + y + ",\n\tz=" + z + "\n}";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Coordinates)) {
            return false;
        }

        Coordinates t = (Coordinates) obj;

        return x.equals(t.x)
            && y.equals(t.y)
            && z.equals(t.z);
    }

    public int hashCode() {
        final int k = 31;
        final int a = 7;
        int hash = a;
        hash = k * hash + x.hashCode();
        hash = k * hash + y.hashCode();
        hash = k * hash + z.hashCode();
        return hash;
    }

    /**
     * Validates fields for model.
     * Restrictions:
     * <ul>
     *  <li>x - Any long value</li>
     *  <li>y - Any double value, NOTNULL</li>
     *  <li>z - Any long value</li>
     * </ul>
     */
    public static class Validator implements AbstractValidator<Coordinates> {
        /**
         * Validates all fields
         * @throws InvalidFieldException if a field does not satisfy the restrictions.
         */
        public void validate(Coordinates coordinates) throws InvalidFieldException {
            validateX(coordinates.x);
            validateY(coordinates.y);
            validateZ(coordinates.z);
        }

        public void validateX(Long x) throws InvalidFieldException {
        }

        public void validateY(Double y) throws InvalidFieldException {
            AbstractValidator.ensureNotNull(y, "Field y for coordinate objects can not be null.");
        }

        public void validateZ(Long z) throws InvalidFieldException {
        }
    };
}
