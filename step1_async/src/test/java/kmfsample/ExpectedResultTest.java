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


        Assert.assertEquals("eval: Expression: @root\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[2,4]}}\n" +
                "eval: Expression: @root\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[2,4]}}\n" +
                "eval: @root | districts[]ￂﾠ\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"contact\":[3],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "eval: @root | districts[name=District_2]ￂﾠ\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "eval: @root | districts[name=District_*]\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"contact\":[3],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "eval: @root | districts[name=*trict_*]\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"contact\":[3],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "eval: @root | districts[na*=*trict_*]\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"contact\":[3],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "eval: @root | district*[na*=*trict_*]\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"contact\":[3],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "@root | districts[*]ￂﾠ| sensors[]ￂﾠ\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"FakeTempSensor_0\",\"value\":0.5,\"op_District_sensors\":[4]}}\n" +
                "@root | >>districts[*]ￂﾠ| <<districtsￂﾠ\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[2,4]}}\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"contact\":[3],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"contact\":[3],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"contact\":[3],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"contact\":[3],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "@root | districts[*]ￂﾠ| sensors[] | =value \n" +
                "ResultSize:1\n" +
                "0.5\n" +
                "@root | districts[*]ￂﾠ| sensors[] | =value \n" +
                "ResultSize:1\n" +
                "1.5\n" +
                "ResultSize:1\n" +
                "1.5\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":2,\"data\":{\"name\":\"District_1\",\"contact\":[3],\"op_City_districts\":[1]}}\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":4,\"data\":{\"name\":\"District_2\",\"sensors\":[5],\"op_City_districts\":[1]}}\n" +
                "===KDeferResultSet===\n" +
                "ResultSize:1\n" +
                "0.5\n" +
                "ResultSize:1\n" +
                "1.5\n", buffer.toString());

        System.out.println(buffer.toString());
    }

}
