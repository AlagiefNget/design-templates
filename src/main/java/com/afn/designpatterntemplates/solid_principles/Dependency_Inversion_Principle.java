package com.afn.designpatterntemplates.solid_principles;

/**
 * Depend on abstractions, not concrete implementations.
 */

class Order {}

\
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


public class Dependency_Inversion_Principle {

    public static void main(String[] args) {
        // Usage
        OrderService service = new OrderService(new MySQLOrderRepository());

    }
}

