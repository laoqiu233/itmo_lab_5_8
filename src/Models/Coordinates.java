package src.Models;

public class Coordinates {
    private long x;
    private double y;
    private long z;

    public Coordinates(long x, double y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString() {
        return "Coordinates { x=" + x + ", y=" + y + ", z=" + z + " }";
    }
}