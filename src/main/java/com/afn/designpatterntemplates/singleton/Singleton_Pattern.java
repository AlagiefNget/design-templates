package com.afn.designpatterntemplates.singleton;

/**
 * Ensures a class has only one instance throughout the application.
 * Use when: shared resources like config, DB connection pool, logger, thread pool.
 *
 * Why volatile:
 * Without it, a second thread could see a partially constructed instance due to JVM instruction reordering.
 *
 */

// Thread-safe Singleton using double-checked locking
class AppConfig {
    private static volatile AppConfig instance;
    private final String dbUrl;

    private AppConfig() {
        // load from env/file
        this.dbUrl = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost/mydb");
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {  // double-check
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    public String getDbUrl() { return dbUrl; }
}


class Singleton_Pattern {

    public static void main(String[] args) {
        // Usage — always the same object
        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();
        System.out.println(config1 == config2); // true
    }
}
