package src;
import src.Modes.*;

class Main {
    public static void main(String[] args) {
        Mode mode = new CLI();

        mode.run();
    } 
}