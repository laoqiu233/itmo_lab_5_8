package com.dmtri.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.dmtri.common.util.TerminalColors;

public final class Client {
    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        try {
            InetSocketAddress addr = new InetSocketAddress(args[0], Integer.valueOf(args[1]));
            ConsoleClient console = new ConsoleClient(addr);
            console.run();
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.out.println(
                TerminalColors.colorString("Unable to parse host address and port from arguments. You should pass them in as arguments", TerminalColors.RED)
            );
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(
                TerminalColors.colorString("Failed to launch app", TerminalColors.RED)
            );
            e.printStackTrace();
            return;
        }
    }
}
