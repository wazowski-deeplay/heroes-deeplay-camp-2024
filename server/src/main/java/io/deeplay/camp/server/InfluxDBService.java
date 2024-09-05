package io.deeplay.camp.server;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import java.time.Instant;

public class InfluxDBService {
    private static char[] token = "6zv83Q4t24-Vl7f_Ra74nOWO4pchAUd50bgRKdYFYTIl7zY470KPX2Djs2zcwYJ0q83XQcyOVVlp4khLqzVtzw=="
            .toCharArray();
    private static String org = "heroes-team";
    private static String bucket = "heroes";
    private static String URL = "http://localhost:8086";

    private final InfluxDBClient client;

    public InfluxDBService() {
        this.client = InfluxDBClientFactory.create(URL,token,org,bucket);
    }

    public void writeData(String measurement, String field,double value){
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        Point point = Point.measurement(measurement).addField(field,value).time(Instant.now(), WritePrecision.MS);
        writeApi.writePoint(point);
    }

    public void closeInfluxConnection(){
        client.close();
    }

}
