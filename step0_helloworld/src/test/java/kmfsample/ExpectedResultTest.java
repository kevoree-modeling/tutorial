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

        Assert.assertEquals("NewCreatedCity==>{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\"}}\n" +
                "ModifiedCity==>{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[2,3]}}\n" +
                "Navigated districts:\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_2\",\"sensors\":[4],\"op_City_districts\":[1]}}\n" +
                "\n" +
                "\n" +
                "Districts extracted:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_2\",\"sensors\":[4],\"op_City_districts\":[1]}}\n" +
                "FullModel:[\n" +
                "{\"@class\":\"smartcity.City\",\"@uuid\":1,\"name\":\"MySmartCity\",\"districts\":[2,3]},\n" +
                "{\"@class\":\"smartcity.District\",\"@uuid\":2,\"name\":\"District_1\",\"op_City_districts\":[1]},\n" +
                "{\"@class\":\"smartcity.District\",\"@uuid\":3,\"name\":\"District_2\",\"sensors\":[4],\"op_City_districts\":[1]},\n" +
                "{\"@class\":\"smartcity.Sensor\",\"@uuid\":4,\"name\":\"TempSensor_0\",\"value\":\"0.5\",\"op_District_sensors\":[3]}\n" +
                "]\n" +
                "\n" +
                "CityByFindMethod==>{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[2,3]}}\n", buffer.toString().replaceAll("\r\n","\n"));

        System.out.println(buffer.toString());
    }

}
