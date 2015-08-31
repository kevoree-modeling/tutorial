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

        Assert.assertEquals("[\n" +
                "{\"@class\":\"City\",\"@uuid\":1,\"name\":\"MySmartCity\",\"districts\":[2,3]},\n" +
                "{\"@class\":\"District\",\"@uuid\":2,\"name\":\"District_1\",\"nbcitizen\":\"10000\",\"op_districts\":[1]},\n" +
                "{\"@class\":\"District\",\"@uuid\":3,\"name\":\"District_2\",\"nbcitizen\":\"50000\",\"op_districts\":[1]}\n" +
                "]\n" +
                "\n" +
                "Visiting...{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"nbcitizen\":10000,\"op_districts\":[1]}}\n" +
                "Visiting...{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_2\",\"nbcitizen\":50000,\"op_districts\":[1]}}\n" +
                "End of the visit\n" +
                "City attribute name, type=-2=MySmartCity\n" +
                "City uuid=1\n" +
                "Resolved={\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[2,3]}}\n", buffer.toString());

        System.out.println(buffer.toString());
        
    }

}
