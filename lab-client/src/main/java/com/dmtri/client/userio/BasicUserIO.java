package com.dmtri.client.userio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class BasicUserIO {
    BufferedReader in;
    BufferedWriter out;

    public BasicUserIO() {
        this(System.in, System.out);
    }

    public BasicUserIO(InputStream in, OutputStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = new BufferedWriter(new OutputStreamWriter(out));
    }

    public void write(Object s) {
        try {
            out.write(s.toString());
            out.flush();
        } catch (IOException e) {
            System.out.println("Exception while writing to output stream: \n" + e);
        }
    }

    public void writeln(Object s) {
        try {
            out.write(s.toString());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            System.out.println("Exception while writing to output stream: \n" + e);
        }
    }

    public String read() throws IOException {
        return in.readLine();
    }
}