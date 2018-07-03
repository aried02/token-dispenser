package com.github.yeriomin.tokendispenser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class PasswordsDbPlaintext implements PasswordsDbInterface {

    static private final String FIELD_SEPARATOR = " ";
    static private final String LINE_SEPARATOR = "\n";

    private String path;
    private Map<String, String> passwords = new HashMap<>();

    public PasswordsDbPlaintext(Properties config) {
        path = config.getProperty(Token.PROPERTY_STORAGE_PLAINTEXT_PATH, "passwords.txt");
        //System.out.print(path);
        try {
            readStorage();
        } catch (IOException e) {
            Token.LOG.error("Could not read " + path);
        }
    }

    @Override
    public String getRandomEmail() {
        List<String> emails = new ArrayList<>(passwords.keySet());
        //System.out.println(emails);
        return emails.get(new Random().nextInt(emails.size()));
    }

    @Override
    public String get(String email) {
        Token.LOG.info(email + (passwords.containsKey(email) ? "" : " NOT") + " found");
        return passwords.get(email);
    }

    @Override
    public void put(String email, String password) {
        passwords.put(email, password);
        try {
            writeStorage();
        } catch (IOException e) {
            Token.LOG.error("Could not write to " + path);
        }
    }

    private void readStorage() throws IOException {
        Token.LOG.info("Reading " + path);
        int lineNum = 0;
        for (String line: Files.readAllLines(Paths.get(path))) {
            // System.out.  ln(line);
            lineNum++;
            String[] pair = line.split(FIELD_SEPARATOR);
            if (pair.length != 2) {
                Token.LOG.warn("Line " + lineNum + " is invalid");
                continue;
            }
            // System.out.println(pair[1]);
            passwords.put(pair[0], pair[1]);
        }
        //System.out.println(passwords);
    }

    private void writeStorage() throws IOException {
        Token.LOG.info("Writing to " + path);
        StringBuilder builder = new StringBuilder();
        for (String username: passwords.keySet()) {
            builder.append(username).append(FIELD_SEPARATOR).append(passwords.get(username)).append(LINE_SEPARATOR);
        }
        Files.write(Paths.get(path), builder.toString().getBytes());
    }
}
