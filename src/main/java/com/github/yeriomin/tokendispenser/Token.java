package com.github.yeriomin.tokendispenser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.GooglePlayException;
import com.github.yeriomin.playstoreapi.PropertiesDeviceInfoProvider;

//Not sure if these are necessary
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.ipAddress;
import static spark.Spark.notFound;
import static spark.Spark.port;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Locale;

public class Token {
  static public final Logger LOG = LoggerFactory.getLogger(Token.class.getName());

  static private final String CONFIG_FILE = "/config.properties";

  static final String PROPERTY_SPARK_HOST = "spark-host";
  static final String PROPERTY_SPARK_PORT = "spark-port";
  static final String PROPERTY_STORAGE = "storage";
  static final String PROPERTY_STORAGE_PLAINTEXT_PATH = "storage-plaintext-path";
  static final String PROPERTY_MONGODB_HOST = "mongodb-host";
  static final String PROPERTY_MONGODB_PORT = "mongodb-port";
  static final String PROPERTY_MONGODB_USERNAME = "mongodb-username";
  static final String PROPERTY_MONGODB_PASSWORD = "mongodb-password";
  static final String PROPERTY_MONGODB_DB = "mongodb-databaseNameStorage";
  static final String PROPERTY_MONGODB_COLLECTION = "mongodb-collectionName";
  static final String PROPERTY_EMAIL_RETRIEVAL = "enable-email-retrieval";

  static public final String STORAGE_MONGODB = "mongodb";
  static public final String STORAGE_PLAINTEXT = "plaintext";

  static PasswordsDbInterface passwords;
  public static void main(String[] args) {
    Properties config = getConfig();
    Token.passwords = PasswordsDbFactory.get(config);
    Token t = new Token();
    System.out.print(t.getTokenGSFID());
  }

  String getTokenGSFID()
  {
    String email = Token.passwords.getRandomEmail();
    String password = Token.passwords.get(email);
    // System.out.println(email);
    // System.out.println(password);
    int code = 500;
    String message;
    try {
        String token = getApi().generateToken(email, password);
        String ac2dmToken = getApi().generateAC2DMToken(email, password);
        String gsfId = getApi().generateGsfId(email, ac2dmToken);
        return token + " " + gsfId;
    } catch (IOException e) {
        System.out.println("No good");
        e.printStackTrace();
    } 
    return "";
  }
  static Properties getConfig() {
        Properties properties = new Properties();
        try (InputStream input = PasswordsDbMongo.class.getResourceAsStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String host = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
        if (null != host && !host.isEmpty()) {
            properties.put(PROPERTY_MONGODB_HOST, host);
            properties.put(PROPERTY_MONGODB_PORT, System.getenv("OPENSHIFT_MONGODB_DB_PORT"));
            properties.put(PROPERTY_MONGODB_USERNAME, System.getenv("OPENSHIFT_MONGODB_DB_USERNAME"));
            properties.put(PROPERTY_MONGODB_PASSWORD, System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD"));
            properties.put(PROPERTY_MONGODB_DB, System.getenv("OPENSHIFT_APP_NAME"));
        }
        //properties.put(PROPERTY_STORAGE_PLAINTEXT_PATH, System.getenv());
        //System.out.print(properties);
        return properties;
    }

    GooglePlayAPI getApi() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getSystemResourceAsStream("device-gemini.properties"));
        } catch (IOException e) {
            System.out.println("Not found");
        }

        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        deviceInfoProvider.setLocaleString(Locale.ENGLISH.toString());

        GooglePlayAPI api = new GooglePlayAPI();
        api.setClient(new OkHttpClientAdapter());
        api.setDeviceInfoProvider(deviceInfoProvider);
        api.setLocale(Locale.US);
        return api;
    }

}