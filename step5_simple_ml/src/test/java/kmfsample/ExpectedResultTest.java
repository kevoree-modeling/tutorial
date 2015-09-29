package kmfsample;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

public class ExpectedResultTest {

    StringBuffer buffer = new StringBuffer();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Test
    public void testOutput() {

        PrintStream out = System.out;
        PrintStream err = System.err;

        System.setOut(new PrintStream(baos));
        App.main(null);

        //Wait all callback end
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.setOut(out);
        System.setErr(err);


        try {
            String[] results = baos.toString("UTF-8").split("\n");
            System.out.println("LastInsert ; LastRead => " + Arrays.toString(results));
            Assert.assertEquals(Double.valueOf(results[1]), Double.valueOf(results[0]), 0.000000000001);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
