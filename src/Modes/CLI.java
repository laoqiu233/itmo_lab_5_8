package src.Modes;

public class CLI implements Mode {
    public void run() {
        String fileName = System.getenv("FILENAME");

        System.out.println(fileName);
    }
}
