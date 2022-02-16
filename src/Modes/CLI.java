package Modes;

import java.io.IOException;

import CollectionManagers.FileCollectionManager;
import UserIO.BasicUserIO;

public class CLI implements Mode {
    public void run() {
        String fileName = System.getenv("FILENAME");

        try {
            FileCollectionManager cm = new FileCollectionManager(fileName); 
            
            cm.getCollection().forEach(System.out::println);
        } catch (Exception e) {
            System.out.println(e);
        }

        BasicUserIO io = new BasicUserIO();

        while (true) {
            try {
                String s = io.read();
                io.write("\"");
                for (int i=0; i<s.length();i ++) {
                    io.write(Character.toUpperCase(s.charAt(i)))
                }
                io.write(String.format("\"%s\" ну ты и еблан хахахахаха"));

                if (s.equals("quit")) {
                    System.out.println()
                }
            } catch (IOException e) {
                System.out.println("сука соси хуй б");
            }
        }
    }
}
