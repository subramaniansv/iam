package com.iam.app.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConfig {
    private static final String URL = ENVConfig.get("DB_URL");
    private static final String USERNAME = ENVConfig.get("DB_USERNAME");
    private static final String PASSWORD = ENVConfig.get("DB_PASWORD");

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("PostgreSQL connection failed", e);
        }
    }
}