package UserIO;

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

    public void write(Object s) throws IOException {
        out.write(s.toString());
        out.flush();
    }

    public void writeln(Object s) throws IOException {
        out.write(s.toString());
        out.newLine();
        out.flush();
    }

    public String read() throws IOException {
        return in.readLine();
    }
}