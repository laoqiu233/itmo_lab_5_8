package com.dmtri.common.models;

import com.dmtri.common.exceptions.InvalidFieldException;

public class Route extends AbstractModel {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Location from; //Поле не может быть null
    private Location to; //Поле не может быть null
    private Double distance; //Значение поля должно быть больше 1

    public Route(Long id, String name, java.time.LocalDate creationDate, Location from, Location to, Double distance) throws InvalidFieldException {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
        validate();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void validate() throws InvalidFieldException {
        ensureNotNull(id, "id of routes can not be null");
        if (id <= 0) {
            throw new InvalidFieldException("id of routes should be greater than 0");
        }

        ensureNotNull(name, "name of routes can not be null");
        if (name.length() == 0) {
            throw new InvalidFieldException("name of routes can not be an empty string");
        }

        ensureNotNull(creationDate, "creation date of routes can not be null");

        ensureNotNull(from, "starting point of routes can not be null");

        ensureNotNull(to, "ending point of routes can not be null");

        ensureNotNull(distance, "distance of routes can not be null");

        if (distance <= 1) {
            throw new InvalidFieldException("distance of routes should be greater than 1");
        }
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
}
