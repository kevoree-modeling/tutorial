package kmfsample;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ExpectedResultTest {

    StringBuffer buffer = new StringBuffer();

    @Test
    public void testOutput() {

        PrintStream out = System.out;
        PrintStream err = System.err;

        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                buffer.append((char) b);
            }
        }));
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                buffer.append(b);
            }
        }));
        App.main(null);

        //Wait all callback end
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.setOut(out);
        System.setErr(err);

        Assert.assertEquals("{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[2,4]}}\n", buffer.toString());

        System.out.println(buffer.toString());
        
    }

}
