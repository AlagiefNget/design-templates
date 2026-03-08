# design-templates

This is a knowledge question with code examples — I'll answer directly without creating a file unless you want one. Let me give you a thorough breakdown of each concept.

---

## 1. SOLID Design Principles

Five principles that make OOP code maintainable, extensible, and testable.

---

### S — Single Responsibility Principle
**One class = one reason to change.**

```java
// ❌ Bad: UserService handles both user logic AND email
class UserService {
    public void createUser(User user) { /* save to DB */ }
    public void sendWelcomeEmail(User user) { /* send email */ }
}

// ✅ Good: separate responsibilities
class UserService {
    public void createUser(User user) { /* save to DB */ }
}

class EmailService {
    public void sendWelcomeEmail(User user) { /* send email */ }
}
```

**Why:** When email logic changes, you shouldn't touch `UserService`. Keeps classes focused and testable.

---

### O — Open/Closed Principle
**Open for extension, closed for modification.**

```java
// ❌ Bad: adding a new discount type requires editing existing code
class DiscountCalculator {
    public double calculate(String type, double price) {
        if (type.equals("SEASONAL")) return price * 0.9;
        if (type.equals("VIP")) return price * 0.8;
        return price;
    }
}

// ✅ Good: extend without modifying
interface Discount {
    double apply(double price);
}

class SeasonalDiscount implements Discount {
    public double apply(double price) { return price * 0.9; }
}

class VipDiscount implements Discount {
    public double apply(double price) { return price * 0.8; }
}

// Adding a new discount = new class, no existing code changed
class FlashDiscount implements Discount {
    public double apply(double price) { return price * 0.5; }
}
```

---

### L — Liskov Substitution Principle
**Subclasses must be substitutable for their parent without breaking behavior.**

```java
// ❌ Bad: Square breaks Rectangle's contract
class Rectangle {
    protected int width, height;
    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public int area() { return width * height; }
}

class Square extends Rectangle {
    public void setWidth(int w) { this.width = this.height = w; } // violates LSP!
}

// ✅ Good: use a shared abstraction instead
interface Shape {
    int area();
}

class Rectangle implements Shape {
    private int width, height;
    public Rectangle(int w, int h) { this.width = w; this.height = h; }
    public int area() { return width * height; }
}

class Square implements Shape {
    private int side;
    public Square(int s) { this.side = s; }
    public int area() { return side * side; }
}
```

---

### I — Interface Segregation Principle
**Don't force classes to implement methods they don't need.**

```java
// ❌ Bad: one fat interface
interface Worker {
    void work();
    void eat();
    void sleep();
}

class Robot implements Worker {
    public void work() { /* ok */ }
    public void eat() { throw new UnsupportedOperationException(); } // robots don't eat!
    public void sleep() { throw new UnsupportedOperationException(); }
}

// ✅ Good: split into focused interfaces
interface Workable { void work(); }
interface Eatable  { void eat(); }
interface Sleepable { void sleep(); }

class Human implements Workable, Eatable, Sleepable {
    public void work()  { System.out.println("Working"); }
    public void eat()   { System.out.println("Eating"); }
    public void sleep() { System.out.println("Sleeping"); }
}

class Robot implements Workable {
    public void work() { System.out.println("Robot working"); }
}
```

---

### D — Dependency Inversion Principle
**Depend on abstractions, not concrete implementations.**

```java
// ❌ Bad: OrderService is tightly coupled to MySQLDatabase
class OrderService {
    private MySQLDatabase db = new MySQLDatabase(); // hardcoded!
    public void placeOrder(Order order) { db.save(order); }
}

// ✅ Good: depend on interface, inject the implementation
interface OrderRepository {
    void save(Order order);
}

class MySQLOrderRepository implements OrderRepository {
    public void save(Order order) { System.out.println("Saved to MySQL"); }
}

class MongoOrderRepository implements OrderRepository {
    public void save(Order order) { System.out.println("Saved to MongoDB"); }
}

class OrderService {
    private final OrderRepository repo;

    public OrderService(OrderRepository repo) { // injected
        this.repo = repo;
    }

    public void placeOrder(Order order) { repo.save(order); }
}

// Usage
OrderService service = new OrderService(new MySQLOrderRepository());
```

---

## 2. Singleton Pattern

**Ensures a class has only one instance throughout the application.**

Use when: shared resources like config, DB connection pool, logger, thread pool.

```java
// Thread-safe Singleton using double-checked locking
public class AppConfig {
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

// Usage — always the same object
AppConfig config1 = AppConfig.getInstance();
AppConfig config2 = AppConfig.getInstance();
System.out.println(config1 == config2); // true

// ✅ Modern, cleaner alternative using enum (guaranteed thread-safe by JVM)
public enum DatabasePool {
    INSTANCE;
    private final String url = "jdbc:postgresql://localhost/mydb";
    public String getUrl() { return url; }
}
```

**Why `volatile`:** Without it, a second thread could see a partially constructed instance due to JVM instruction reordering.

---

## 3. Observables (Observer Pattern)

**A subject notifies registered observers when its state changes.** Core to reactive/event-driven architectures.

Use when: event systems, UI updates, Kafka consumers, pub-sub, real-time feeds.

```java
import java.util.ArrayList;
import java.util.List;

// Observer interface
interface OrderObserver {
    void onOrderStatusChanged(String orderId, String status);
}

// Subject
class OrderService {
    private final List<OrderObserver> observers = new ArrayList<>();

    public void subscribe(OrderObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(OrderObserver observer) {
        observers.remove(observer);
    }

    public void updateOrderStatus(String orderId, String status) {
        System.out.println("Order " + orderId + " is now: " + status);
        // Notify all observers
        for (OrderObserver obs : observers) {
            obs.onOrderStatusChanged(orderId, status);
        }
    }
}

// Concrete observers
class EmailNotifier implements OrderObserver {
    public void onOrderStatusChanged(String orderId, String status) {
        System.out.println("[Email] Order " + orderId + " status: " + status);
    }
}

class AuditLogger implements OrderObserver {
    public void onOrderStatusChanged(String orderId, String status) {
        System.out.println("[Audit] Logged: order=" + orderId + ", status=" + status);
    }
}

// Usage
OrderService svc = new OrderService();
svc.subscribe(new EmailNotifier());
svc.subscribe(new AuditLogger());
svc.updateOrderStatus("ORD-001", "SHIPPED");
// Both EmailNotifier and AuditLogger fire automatically
```

For reactive/async streams, Java also has `java.util.concurrent.Flow` (reactive streams API) and libraries like RxJava.

---

## 4. Multithreading

**Executing multiple tasks concurrently** to improve throughput and utilization.

Use when: parallel processing, background tasks, I/O-bound operations, scheduled jobs.

```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MultithreadingExample {

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
```

---

## 5. Immutability

**Objects whose state cannot change after construction.** Makes code thread-safe, predictable, and side-effect-free.

Use when: value objects, DTOs, config, keys in maps, anything shared across threads.

```java
// ❌ Mutable — dangerous in multithreaded environments
class MutableMoney {
    private double amount;
    private String currency;
    public void setAmount(double amount) { this.amount = amount; } // anyone can change this!
}

// ✅ Immutable class — the Java recipe
public final class Money {           // 1. final class (no subclassing)
    private final double amount;     // 2. final fields
    private final String currency;

    public Money(double amount, String currency) {
        if (amount < 0) throw new IllegalArgumentException("Negative amount");
        this.amount = amount;
        this.currency = currency;
    }

    // 3. No setters — return new instance instead
    public Money add(Money other) {
        if (!this.currency.equals(other.currency))
            throw new IllegalArgumentException("Currency mismatch");
        return new Money(this.amount + other.amount, this.currency); // new object
    }

    public double getAmount()   { return amount; }
    public String getCurrency() { return currency; }

    @Override public String toString() { return amount + " " + currency; }
}

// Usage
Money price = new Money(100.0, "USD");
Money tax   = new Money(10.0, "USD");
Money total = price.add(tax);  // price and tax unchanged
System.out.println(total);     // 110.0 USD

// Java 16+ record — immutable by default, even cleaner
public record Point(double x, double y) {
    public Point translate(double dx, double dy) {
        return new Point(x + dx, y + dy);
    }
}
```

---

## 6. Serialization

**Converting an object to a byte stream (and back)** for storage, network transfer, or caching.

Use when: saving objects to disk, sending over a socket, storing in Redis/cache, deep cloning.

```java
import java.io.*;

// 1. Java built-in serialization — mark with Serializable
public class User implements Serializable {
    private static final long serialVersionUID = 1L; // version control
    private String username;
    private transient String password; // transient = NOT serialized (sensitive data!)
    private int age;

    public User(String username, String password, int age) {
        this.username = username;
        this.password = password;
        this.age = age;
    }
    // getters...
}

public class SerializationDemo {

    // Serialize to file
    public static void serialize(User user, String file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(user);
        }
    }

    // Deserialize from file
    public static User deserialize(String file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (User) ois.readObject();
        }
    }

    public static void main(String[] args) throws Exception {
        User user = new User("alagie", "secret123", 35);
        serialize(user, "user.ser");

        User loaded = deserialize("user.ser");
        System.out.println(loaded.getUsername()); // alagie
        System.out.println(loaded.getPassword()); // null — transient!
    }
}

// 2. JSON serialization with Jackson (more common in modern APIs)
import com.fasterxml.jackson.databind.ObjectMapper;

ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(user);         // object → JSON string
User fromJson = mapper.readValue(json, User.class);    // JSON string → object
```

**`serialVersionUID`** is critical — if you add a field without it and the UID differs, deserialization throws `InvalidClassException`.

---

## 7. Security

Key security practices in Java applications.

```java
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 1. Password hashing with BCrypt (NEVER store plain text)
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12); // cost factor 12
String hashed = encoder.encode("userPassword123");
boolean matches = encoder.matches("userPassword123", hashed); // true

// 2. AES-256 encryption for sensitive data at rest
public class AesEncryption {
    private static final String ALGO = "AES/GCM/NoPadding";

    public static byte[] encrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv); // always random IV
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        // prepend IV to ciphertext
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return result;
    }

    public static SecretKey generateKey() throws Exception {
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(256);
        return gen.generateKey();
    }
}

// 3. SQL Injection prevention — ALWAYS use PreparedStatement
// ❌ Vulnerable
String query = "SELECT * FROM users WHERE name = '" + userInput + "'";

// ✅ Safe
PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
stmt.setString(1, userInput); // parameterized — user input never interpreted as SQL

// 4. Secure random token generation (e.g., for password reset, API keys)
byte[] tokenBytes = new byte[32];
new SecureRandom().nextBytes(tokenBytes);
String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

// 5. Input validation
public void processAge(String input) {
    int age = Integer.parseInt(input); // throws NumberFormatException on invalid input
    if (age < 0 || age > 150) throw new IllegalArgumentException("Invalid age: " + age);
    // proceed safely
}
```

---

## 8. Factory Pattern

**Encapsulates object creation logic** so the caller doesn't need to know the concrete class.

Use when: creating objects based on runtime conditions, decoupling creation from usage, abstracting complex construction.

```java
// Simple Factory
interface Notification {
    void send(String message);
}

class EmailNotification implements Notification {
    public void send(String message) { System.out.println("[Email] " + message); }
}

class SmsNotification implements Notification {
    public void send(String message) { System.out.println("[SMS] " + message); }
}

class PushNotification implements Notification {
    public void send(String message) { System.out.println("[Push] " + message); }
}

// Factory class
class NotificationFactory {
    public static Notification create(String channel) {
        return switch (channel.toUpperCase()) {
            case "EMAIL" -> new EmailNotification();
            case "SMS"   -> new SmsNotification();
            case "PUSH"  -> new PushNotification();
            default      -> throw new IllegalArgumentException("Unknown channel: " + channel);
        };
    }
}

// Usage — caller doesn't care about the concrete class
Notification n = NotificationFactory.create("SMS");
n.send("Your order shipped!"); // [SMS] Your order shipped!
```

---

## 9. Adapter Pattern

**Converts an incompatible interface into one the client expects.** Like a power plug adapter.

Use when: integrating third-party libraries, legacy code, or services with different interfaces.

```java
// Target interface — what your system expects
interface PaymentGateway {
    boolean charge(String userId, double amount);
}

// Adaptee — a third-party payment library with its own interface
class StripeClient {
    public String createCharge(String customerId, int amountInCents, String currency) {
        System.out.println("Stripe charging " + customerId + ": " + amountInCents + " " + currency);
        return "ch_success_123";
    }
}

class PayPalClient {
    public void executePayment(double usd, String payerId) {
        System.out.println("PayPal paying " + payerId + ": $" + usd);
    }
}

// Adapters — wrap the third-party clients to match your interface
class StripeAdapter implements PaymentGateway {
    private final StripeClient stripe;

    public StripeAdapter(StripeClient stripe) { this.stripe = stripe; }

    public boolean charge(String userId, double amount) {
        int cents = (int)(amount * 100);
        String result = stripe.createCharge(userId, cents, "USD");
        return result.startsWith("ch_success");
    }
}

class PayPalAdapter implements PaymentGateway {
    private final PayPalClient paypal;

    public PayPalAdapter(PayPalClient paypal) { this.paypal = paypal; }

    public boolean charge(String userId, double amount) {
        paypal.executePayment(amount, userId);
        return true;
    }
}

// Usage — your billing service only knows PaymentGateway
class BillingService {
    private final PaymentGateway gateway;

    public BillingService(PaymentGateway gateway) { this.gateway = gateway; }

    public void processOrder(String userId, double total) {
        if (gateway.charge(userId, total)) {
            System.out.println("Payment successful for " + userId);
        }
    }
}

// Swap providers without changing BillingService
BillingService with Stripe = new BillingService(new StripeAdapter(new StripeClient()));
BillingService withPayPal  = new BillingService(new PayPalAdapter(new PayPalClient()));
```

---

## Quick Reference Summary

| Concept | When to Use | Core Benefit |
|---|---|---|
| **SOLID** | Every class/module you design | Maintainable, testable, extensible code |
| **Singleton** | Shared resource (config, pool, logger) | One controlled instance |
| **Observer** | Events, pub-sub, reactive flows | Decoupled notification |
| **Multithreading** | Parallel/background work | Throughput, responsiveness |
| **Immutability** | DTOs, value objects, shared state | Thread safety, predictability |
| **Serialization** | Persistence, caching, network | Object ↔ bytes/JSON |
| **Security** | Any user input, credentials, data | Prevent exploits and leaks |
| **Factory** | Runtime type selection | Decoupled object creation |
| **Adapter** | Legacy/3rd-party integration | Interface compatibility |

Given your microservices background with Shillo, FinSphere, and Kunneh, you're already applying many of these — especially DIP (injection), Factory (config-driven dispatching), Observer (Kafka consumers), and Adapter (wrapping Jira/Radar/Clarity APIs). Let me know if you'd like a deep dive on any of these in the context of one of your specific proje