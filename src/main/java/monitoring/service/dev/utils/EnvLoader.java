package monitoring.service.dev.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EnvLoader {

    public static String load(String property) {
        Properties props = new Properties();
        try (InputStream in = EnvLoader.class.getClassLoader()
            .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new FileNotFoundException(
                    "Configuration file 'config.properties' doesn't found in resources.");
            }
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return props.getProperty(property);
    }
}