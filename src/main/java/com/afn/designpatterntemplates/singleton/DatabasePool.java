package com.afn.designpatterntemplates.singleton;

// ✅ Modern, cleaner alternative using enum (guaranteed thread-safe by JVM)
public enum DatabasePool {
    INSTANCE;
    private final String url = "jdbc:postgresql://localhost/mydb";
    public String getUrl() { return url; }
}
