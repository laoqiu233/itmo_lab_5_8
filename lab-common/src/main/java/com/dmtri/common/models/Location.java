package com.dmtri.common.models;

public class Location {
    private Coordinates coordinates;
    private String name; //Поле не может быть null

    public Location(String name, Coordinates coordinates) {
        this.name = name;
        this.coordinates = coordinates;
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