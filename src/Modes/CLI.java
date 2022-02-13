package src.Modes;

import src.CollectionManagers.FileCollectionManager;

public class CLI implements Mode {
    public void run() {
        String fileName = System.getenv("FILENAME");

        try {
            FileCollectionManager cm = new FileCollectionManager(fileName); 
            
            cm.getCollection().forEach(System.out::println);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
