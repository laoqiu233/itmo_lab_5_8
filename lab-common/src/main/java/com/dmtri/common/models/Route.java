package com.dmtri.common.models;

import java.io.Serializable;

import com.dmtri.common.exceptions.InvalidFieldException;

public class Route implements Comparable<Route>, Serializable {
    public static final Validator VALIDATOR = new Validator();
    private static final long serialVersionUID = -1284965260632671008L;
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Location from; //Поле не может быть null
    private Location to; //Поле не может быть null
    private Double distance; //Значение поля должно быть больше 1
    private Long ownerId = null;

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
        VALIDATOR.validate(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getOwnerId() {
        return ownerId;
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
        sb.append(",\n\towner=");
        sb.append(ownerId);
        sb.append("\n}");

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Route)) {
            return false;
        }
        Route t = (Route) obj;

        return id.equals(t.id)
            && name.equals(t.name)
            && distance.equals(t.distance)
            && creationDate.equals(t.creationDate)
            && from.equals(t.from)
            && to.equals(t.to);
    }

    @Override
    public int hashCode() {
        final int k = 31;
        final int a = 7;
        int hash = a;
        hash = k * hash + id.hashCode();
        hash = k * hash + distance.hashCode();
        hash = k * hash + name.hashCode();
        hash = k * hash + creationDate.hashCode();
        hash = k * hash + from.hashCode();
        hash = k * hash + to.hashCode();
        return hash;
    }

    @Override
    public int compareTo(Route o) {
        if (this.distance == null || o.distance == null) {
            return Long.compare(this.id, o.id);
        }
        return Double.compare(distance, o.distance);
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
     *  <li>distance - Any double value greater than 1</li>
     * </ul>
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
            if (distance != null && distance <= 1) {
                throw new InvalidFieldException("distance of routes should be greater than 1");
            }
        }

        public void validateCreationDate(java.time.LocalDate creationDate) throws InvalidFieldException {
            AbstractValidator.ensureNotNull(creationDate, "creation date of routes can not be null");
        }
    }
}
