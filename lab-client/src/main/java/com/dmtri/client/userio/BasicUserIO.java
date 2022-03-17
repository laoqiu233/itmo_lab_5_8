package com.dmtri.client.userio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

public class BasicUserIO {
    private LinkedList<BufferedReader> inStack;
    private BufferedWriter out;

    public BasicUserIO() {
        this(System.in, System.out);
    }

    public BasicUserIO(InputStream in, OutputStream out) {
        this.inStack = new LinkedList<>();
        this.inStack.add(new BufferedReader(new InputStreamReader(in)));
        this.out = new BufferedWriter(new OutputStreamWriter(out));
    }

    public void write(Object s) {
        try {
            out.write(s.toString());
            out.flush();
        } catch (IOException e) {
            System.err.println("Exception while writing to output stream: \n" + e);
        }
    }

    public void writeln(Object s) {
        try {
            out.write(s.toString());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            System.err.println("Exception while writing to output stream: \n" + e);
        }
    }

    public String read() {
        try {
            String input;
            do {
                input = inStack.getLast().readLine();
                if (input == null) {
                    // The current stream ended,
                    // move on to next stream.
                    removeInAndClose();
                    continue;
                }
                break;
            } while (inStack.size() > 0);

            if (inStack.size() == 0) {
                System.exit(0);
            }

            return input;
        } catch (IOException e) {
            System.err.println("Exception while reading from input stream: \n" + e);
            return "";
        }
    }

    public String read(String msg) {
        write(msg);
        return read();
    }

    public BufferedReader getIn() {
        return inStack.getLast();
    }

    public void addIn(BufferedReader in) {
        inStack.add(in);
    }

    public BufferedReader removeIn() {
        return inStack.removeLast();
    }

    public BufferedReader removeInAndClose() {
        BufferedReader t = this.removeIn();
        try {
            t.close();
        } catch (IOException e) {
            System.err.println("Failed to close input stream");
            System.err.println(e.getMessage());
        }
        return t;
    }

    public BufferedWriter getOut() {
        return out;
    }
}
