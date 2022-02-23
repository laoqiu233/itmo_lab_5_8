package com.dmtri.common.models;

public class Coordinates {
    private long x;
    private double y; // Поле не может быть null
    private long z;

    public Coordinates(long x, double y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString() {
        return "Coordinates {\n\tx=" + x + ",\n\ty=" + y + ",\n\tz=" + z + "\n}";
    }
}