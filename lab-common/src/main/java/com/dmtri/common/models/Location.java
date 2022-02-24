package com.dmtri.common.models;

import com.dmtri.common.exceptions.InvalidFieldException;

public class Location extends AbstractModel {
    private Coordinates coordinates;
    private String name; //Поле не может быть null

    /**
     * Creates a location object and validates data using the {@link #validate()} method.
     * @param name
     * @param coordinates
     * @throws InvalidFieldException
     */
    public Location(String name, Coordinates coordinates) throws InvalidFieldException {
        this.name = name;
        this.coordinates = coordinates;
        validate();
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Validates data in fields.
     * Restrictions:
     * <ul>
     *  <li>name - Any string, NOTNULL</li>
     *  <li>coordinates - Any {@link com.dmtri.common.models.Coordinates} object</li>
     * </ul>
     */
    protected void validate() throws InvalidFieldException {
        AbstractModel.ensureNotNull(coordinates, "Coordinates for location can not be null");
        AbstractModel.ensureNotNull(name, "Field name for object of type location can not be null");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Location {");
        sb.append("\n\tname=");
        sb.append(name);
        sb.append("\n\tcoordinates=");
        sb.append(coordinates.toString().replaceAll("\\n", "\n\t"));
        sb.append("\n}");

        return sb.toString();
    }
}
