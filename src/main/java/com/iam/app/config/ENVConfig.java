package com.iam.app.config;

import io.github.cdimascio.dotenv.Dotenv;

public class ENVConfig {
    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key){
        return dotenv.get(key);
    }
}
