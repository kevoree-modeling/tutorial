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


        Assert.assertEquals("eval: Expression: @smartcity.City\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[3,5]}}\n" +
                "eval: Expression: @smartcity.City[name=MySmartCity]\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[3,5]}}\n" +
                "eval: Expression: @smartcity.City\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[3,5]}}\n" +
                "eval: @smartcity.City[name=MySmartCity] | districts[] \n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_1\",\"contact\":[4],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "eval: @smartcity.City[name=MySmartCity] | districts[name=District_2] \n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "eval: @smartcity.City[name=MySmartCity] | districts[name=District_*]\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_1\",\"contact\":[4],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "eval: @smartcity.City[name=MySmartCity] | districts[name=*trict_*]\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_1\",\"contact\":[4],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "eval: @smartcity.City[name=MySmartCity] | districts[na*=*trict_*]\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_1\",\"contact\":[4],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "eval: @smartcity.City[name=MySmartCity] | district*[na*=*trict_*]\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_1\",\"contact\":[4],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "@smartcity.City[name=MySmartCity] | districts[*] | sensors[] \n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":6,\"data\":{\"name\":\"FakeTempSensor_0\",\"value\":0.5,\"op_District_sensors\":[5]}}\n" +
                "@smartcity.City[name=MySmartCity] | >>districts[*] | <<districts \n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MySmartCity\",\"districts\":[3,5]}}\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_1\",\"contact\":[4],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_1\",\"contact\":[4],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_1\",\"contact\":[4],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "ResultSize:2\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_1\",\"contact\":[4],\"op_City_districts\":[1]}}\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "@smartcity.City[name=MySmartCity] | districts[*] | sensors[] | =value \n" +
                "ResultSize:1\n" +
                "0.5\n" +
                "@smartcity.City[name=MySmartCity] | districts[*] | sensors[] | =value \n" +
                "ResultSize:1\n" +
                "1.5\n" +
                "ResultSize:1\n" +
                "1.5\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":3,\"data\":{\"name\":\"District_1\",\"contact\":[4],\"op_City_districts\":[1]}}\n" +
                "ResultSize:1\n" +
                "{\"universe\":0,\"time\":0,\"uuid\":5,\"data\":{\"name\":\"District_2\",\"sensors\":[6],\"op_City_districts\":[1]}}\n" +
                "===KDeferResultSet===\n" +
                "ResultSize:1\n" +
                "0.5\n" +
                "ResultSize:1\n" +
                "1.5\n", buffer.toString().replaceAll("\r\n","\n"));

        //System.out.println(buffer.toString());
    }

}
