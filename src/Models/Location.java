package Models;

public class Location {
    private Coordinates coordinates;
    private String name; //Поле не может быть null

    public Location(String name, Coordinates coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public String toString() {
        return "Location { name=" + name + ", coordinates=" + coordinates + " }"; 
    }
}