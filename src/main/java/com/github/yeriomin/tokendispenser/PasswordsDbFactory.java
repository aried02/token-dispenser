package com.github.yeriomin.tokendispenser;

import java.util.Properties;

public class PasswordsDbFactory {

    static public PasswordsDbInterface get(Properties config) {
        String storage = config.getProperty(Token.PROPERTY_STORAGE, Token.STORAGE_MONGODB);
        //Server.LOG.info("Initializing storage type " + storage);
        if (storage.equals(Token.STORAGE_MONGODB)) {
            return new PasswordsDbMongo(config);
        } else {
            return new PasswordsDbPlaintext(config);
        }
    }
}
