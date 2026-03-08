package com.afn.designpatterntemplates.observer;

/**
 * A subject notifies registered observers when its state changes. Core to reactive/event-driven architectures.
 * Use when: event systems, UI updates, Kafka consumers, pub-sub, real-time feeds.
 *
 */

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

public class Observer_Pattern {
    public static void main(String[] args) {
        // Usage
        OrderService svc = new OrderService();
        svc.subscribe(new EmailNotifier());
        svc.subscribe(new AuditLogger());
        svc.updateOrderStatus("ORD-001", "SHIPPED");
// Both EmailNotifier and AuditLogger fire automatically
    }
}


/**
 * For reactive/async streams, Java also has java.util.concurrent.Flow (reactive streams API) and libraries like RxJava.
 */