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


        Assert.assertEquals("null\n" +
                "{\"universe\":0,\"time\":8,\"uuid\":4,\"data\":{\"name\":\"district_2_t7\"}}\n" +
                "{\"universe\":0,\"time\":8,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[3]}}\n" +
                "1\n" +
                "{\"universe\":0,\"time\":13,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[3,4]}}\n" +
                "2\n" +
                " -> {\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[3]}}\n", buffer.toString());

        System.out.println(buffer.toString());
    }

}
