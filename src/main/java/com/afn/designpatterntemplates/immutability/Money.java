package com.afn.designpatterntemplates;

/**
 * Objects whose state cannot change after construction. Makes code thread-safe, predictable, and side-effect-free.
 * Use when: value objects, DTOs, config, keys in maps, anything shared across threads.
 */

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

//// Usage
//Money price = new Money(100.0, "USD");
//Money tax   = new Money(10.0, "USD");
//Money total = price.add(tax);  // price and tax unchanged
//System.out.println(total);     // 110.0 USD

// Java 16+ record — immutable by default, even cleaner
public record Point(double x, double y) {
    public Point translate(double dx, double dy) {
        return new Point(x + dx, y + dy);
    }
}

