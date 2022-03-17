package com.dmtri.client.userio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

public class BasicUserIO {
    private LinkedList<BufferedReader> in;
    private BufferedWriter out;

    public BasicUserIO() {
        this(System.in, System.out);
    }

    public BasicUserIO(InputStream in, OutputStream out) {
        this.in = new LinkedList<>();
        this.in.add(new BufferedReader(new InputStreamReader(in)));
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
                input = in.getLast().readLine();
                if (input == null) {
                    // The current stream ended,
                    // move on to next stream.
                    removeInAndClose();
                    continue;
                }
                break;
            } while (in.size() > 0);
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

    public List<BufferedReader> getIn() {
        return in;
    }

    public void addIn(BufferedReader in) {
        this.in.add(in);
    }

    public BufferedReader removeIn() {
        return this.in.removeLast();
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
