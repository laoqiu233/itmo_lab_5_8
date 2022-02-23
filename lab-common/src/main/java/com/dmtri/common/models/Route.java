package com.dmtri.common.models;

public class Route {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Location from; //Поле не может быть null
    private Location to; //Поле не может быть null
    private double distance; //Значение поля должно быть больше 1

    public Route(long id, String name, java.time.LocalDate creationDate, Location from, Location to, double distance) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
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