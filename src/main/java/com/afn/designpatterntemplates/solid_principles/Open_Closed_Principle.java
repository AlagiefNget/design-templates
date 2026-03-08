package com.afn.designpatterntemplates.solid_principles;

/**
 * O — Open/Closed Principle
 * Open for extension, closed for modification.
 */

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

public class Open_Closed_Principle {
}
