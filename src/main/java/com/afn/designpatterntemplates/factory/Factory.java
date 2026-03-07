package com.afn.designpatterntemplates.factory;

/**
 * Encapsulates object creation logic so the caller doesn't need to know the concrete class.
 * Use when: creating objects based on runtime conditions, decoupling creation from usage, abstracting complex construction.
 */


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

public class Factory {

    public static void main(String[] args) {
        // Usage — caller doesn't care about the concrete class
        Notification n = NotificationFactory.create("SMS");
        n.send("Your order shipped!"); // [SMS] Your order shipped!
    }
}
