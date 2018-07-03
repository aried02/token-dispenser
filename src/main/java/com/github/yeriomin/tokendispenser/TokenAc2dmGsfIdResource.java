package com.github.yeriomin.tokendispenser;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.GooglePlayException;
import com.github.yeriomin.playstoreapi.PropertiesDeviceInfoProvider;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import spark.Request;
import spark.Response;

import static spark.Spark.halt;

public class TokenAc2dmGsfIdResource extends TokenAc2dmResource {

    @Override
    public String handle() {
        String email = Token.passwords.getRandomEmail();
        String password = Token.passwords.get(email);
        int code = 500;
        String message;
        try {
            String token = getApi().generateToken(email, password);
            String ac2dmToken = getApi().generateAC2DMToken(email, password);
            String gsfId = getApi().generateGsfId(email, ac2dmToken);
            return token + " " + gsfId;
        } catch (GooglePlayException e) {
            e.printStackTrace();
            if (e.getCode() >= 400) {
                code = e.getCode();
            }
            message = e.getMessage();
            Token.LOG.warn(e.getClass().getName() + ": " + message);
            halt(code, message);
        } catch (IOException e) {
            message = e.getMessage();
            Token.LOG.error(e.getClass().getName() + ": " + message);
            halt(code, message);
        }
        return "";
    }
}
