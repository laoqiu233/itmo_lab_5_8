package com.dmtri.client;

import java.io.IOException;

public final class Client {
    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        try {
            ConsoleClient console = new ConsoleClient(args[0], Integer.valueOf(args[1]));
            console.run();
        } catch (IOException e) {
            System.out.println("Failed to launch app");
            e.printStackTrace();
            return;
        }
    }
}
