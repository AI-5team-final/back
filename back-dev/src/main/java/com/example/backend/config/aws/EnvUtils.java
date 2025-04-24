package com.example.backend.config.aws;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvUtils {
    private static final Dotenv dotenv;

    static {
        Dotenv temp;
        try {
            temp = Dotenv.configure().ignoreIfMissing().load(); // .env 없어도 무시
        } catch (Exception e) {
            temp = null;
        }
        dotenv = temp;
    }

    public static String get(String key) {
        String env = System.getenv(key);
        if (env != null) return env;

        if (dotenv != null) {
            return dotenv.get(key);
        }

        return null; // 없으면 null
    }
}

