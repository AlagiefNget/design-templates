package com.afn.designpatterntemplates.multithreading;

/**
 * Executing multiple tasks concurrently to improve throughput and utilization.
 * Use when: parallel processing, background tasks, I/O-bound operations, scheduled jobs.
 */

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Multithreading {

    // 1. Basic thread
    public static void basicThread() throws InterruptedException {
        Thread t = new Thread(() -> System.out.println("Running in: " + Thread.currentThread().getName()));
        t.start();
        t.join(); // wait for it to finish
    }

    // 2. ExecutorService (preferred over raw threads)
    public static void executorService() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);

        Future<String> future = pool.submit(() -> {
            Thread.sleep(100);
            return "Result from thread";
        });

        System.out.println(future.get()); // blocks until done
        pool.shutdown();
    }

    // 3. Synchronized — prevent race conditions
    static class Counter {
        private int count = 0;

        public synchronized void increment() { count++; }
        public synchronized int getCount() { return count; }
    }

    // 4. AtomicInteger — lock-free thread-safe counter
    static class AtomicCounter {
        private final AtomicInteger count = new AtomicInteger(0);

        public void increment() { count.incrementAndGet(); }
        public int getCount()   { return count.get(); }
    }

    // 5. CompletableFuture — async composition
    public static void asyncExample() throws Exception {
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> "fetch user")           // runs async
                .thenApply(user -> user + " -> enrich")    // transform result
                .thenApply(result -> result + " -> done");

        System.out.println(future.get());
    }

    public static void main(String[] args) throws Exception {
        basicThread();
        executorService();
        asyncExample();

        // Race condition demo — always use AtomicInteger or synchronized for shared state
        AtomicCounter counter = new AtomicCounter();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) pool.submit(counter::increment);
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Final count: " + counter.getCount()); // always 1000
    }
}