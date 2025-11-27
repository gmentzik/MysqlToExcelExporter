package com.github.gmentzik.mysqlexcelexport.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
docker run \
        -e DB_HOST=my-docker-db \
        -e DB_PORT=3306 \
        -e DB_DATABASE=mydb \
        -e DB_USERNAME=user \
        -e DB_PASSWORD: password \
        -e OUTPUT_FOLDER=/data/output \
myapp:latest
*/

/*
version: '3.8'
services:
myapp:
image: myapp:latest
environment:
DB_HOST: my-docker-db
DB_PORT: 3306
DB_DATABASE: mydb
DB_USERNAME: user

DB_USESSL: false
OUTPUT_FOLDER: /data/output
*/

/*
java -Ddb.host=myhost -Ddb.port=5432 -jar app.jar
 */

public class Config {
    private final Properties properties = new Properties();

    // User can override application properties on command line
    //java -Ddb.database=new_database -jar DBExporter.jar
    public Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties not found in resources");
            }
            properties.load(input);

            setProperty("db.host");
            setProperty("db.port");
            setProperty("db.database");
            setProperty("db.username");
            setProperty("db.password");

        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }



    private void setProperty(String key) {
        String envKey = key.toUpperCase().replace('.', '_'); // db.host → DB_HOST
        String value = System.getenv(envKey); // Check environment variable
        if (value == null) {
            value = properties.getProperty(key); // Fallback to application.properties
        }
        properties.setProperty(key, value);
    }


    public String getDbUrl() {

        String host = properties.getProperty("db.host");
        String port = properties.getProperty("db.port");
        String database = properties.getProperty("db.database");
        String useSSL = properties.getProperty("db.usessl");
        String encoding = properties.getProperty("db.encoding");

        return String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=%s&useUnicode=true&characterEncoding=%s",
                host, port, database, useSSL, encoding
        );

    }
    //java -Ddb.database=new_database -jar DBExporter.jar
    public String getDbDatabase() {
        return properties.getProperty("db.database");
    }

    public String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public String getOutputFolder() {
        return properties.getProperty("output.folder");
    }
}






