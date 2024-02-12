package monitoring.service.dev.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {

    public static String load(String property){
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return props.getProperty(property);
    }
}