package UserIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.stream.Stream;

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

    public void write(Object ...o) throws IOException {
        out.write(Arrays.stream(o).map(x -> o.toString()).reduce((a,b) -> a+' '+b));
    }

    public void writeln(Object ...o) throws IOException {
        write(o);
    }

    public String read() throws IOException {
        return in.readLine();
    }
}