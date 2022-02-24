package com.dmtri.common.models;

import com.dmtri.common.exceptions.InvalidFieldException;

public class Route {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Location from; //Поле не может быть null
    private Location to; //Поле не может быть null
    private Double distance; //Значение поля должно быть больше 1

    /**
     * Creates a route object and validates data using the {@link #validate()} method.
     * @param id
     * @param name
     * @param creationDate
     * @param from
     * @param to
     * @param distance
     * @throws InvalidFieldException if a field is invalid.
     */
    public Route(Long id, String name, java.time.LocalDate creationDate, Location from, Location to, Double distance) throws InvalidFieldException {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
        validator.validate(this);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getDistance() {
        return distance;
    }

    public java.time.LocalDate getCreationDate() {
        return creationDate;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Route #");
        sb.append(id);
        sb.append(" {\n\tname=");
        sb.append(name);
        sb.append(",\n\tcreationDate=");
        sb.append(creationDate);
        sb.append(",\n\tfrom=");
        sb.append(from.toString().replaceAll("\\n", "\n\t"));
        sb.append(",\n\tto=");
        sb.append(to.toString().replaceAll("\\n", "\n\t"));
        sb.append(",\n\tdistance=");
        sb.append(distance);
        sb.append("\n}");

        return sb.toString();
    }

    /**
     * Validates data in fields.
     * Restrictions:
     * <ul>
     *  <li>id - Any Long value greater than 0, NOTNULL.</li>
     *  <li>name - Any non-empty string, NOTNULL.</li>
     *  <li>creationDate - Any {@link java.time.LocalDate} object, should be
     *  generated automatically, NOTNULL.</li>
     *  <li>from - Any {@link com.dmtri.common.models.Location} object, NOTNULL.</li>
     *  <li>to - Any {@link com.dmtri.common.models.Location} object, NOTNULL.</li>
     *  <li>distance - Any double value greater than 1, NOTNULL</li>
     * </ul>
     * @throws InvalidFieldException if a field is invalid.
     */
    public static class Validator implements AbstractValidator<Route> {
        public void validate(Route route) throws InvalidFieldException {
            validateId(route.id);
            validateName(route.name);
            validateCreationDate(route.creationDate);
            validateFrom(route.from);
            validateTo(route.to);
            validateDistance(route.distance);
        }
        
        public void validateId(Long id) throws InvalidFieldException {
            AbstractValidator.ensureNotNull(id, "id of routes can not be null");
            if (id <= 0) {
                throw new InvalidFieldException("id of routes should be greater than 0");
            }
        }

        public void validateName(String name) throws InvalidFieldException {
            AbstractValidator.ensureNotNull(name, "name of routes can not be null");
            if (name.length() == 0) {
                throw new InvalidFieldException("name of routes can not be an empty string");
            }
        }

        public void validateFrom(Location from) throws InvalidFieldException {
            AbstractValidator.ensureNotNull(from, "starting point of routes can not be null");
        }

        public void validateTo(Location to) throws InvalidFieldException {
            AbstractValidator.ensureNotNull(to, "ending point of routes can not be null");
        }

        public void validateDistance(Double distance) throws InvalidFieldException {
            AbstractValidator.ensureNotNull(distance, "distance of routes can not be null");
            if (distance <= 1) {
                throw new InvalidFieldException("distance of routes should be greater than 1");
            }
        }

        public void validateCreationDate(java.time.LocalDate creationDate) throws InvalidFieldException {
            AbstractValidator.ensureNotNull(creationDate, "creation date of routes can not be null");
        }
    }

    public static Validator validator = new Validator();
}
