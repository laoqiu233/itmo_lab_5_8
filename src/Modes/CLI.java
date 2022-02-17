package Modes;

import java.io.IOException;

import CollectionManagers.FileCollectionManager;
import UserIO.BasicUserIO;
import Models.Route;

public class CLI implements Mode {
    public void run() {
        String fileName = System.getenv("FILENAME");
        BasicUserIO io;

        try {
            FileCollectionManager cm = new FileCollectionManager(fileName); 

            io = new BasicUserIO();

            for (Route c : cm.getCollection()) {
                io.writeln(c);
            }

            while (true) {
                try {
                    String s = io.read();
                    io.write("\"");
                    for (int i=0; i<s.length();i ++) {
                        if (i%2 == 1) io.write(Character.toUpperCase(s.charAt(i)));
                        else io.write(Character.toLowerCase(s.charAt(i)));
                    }
                    io.writeln("\" ну ты и еблан хахахахаха");
    
                    if (s.equals("quit")) {
                        System.out.println("ну и пошел ты нахуй гандон");
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("сука соси хуй б");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
