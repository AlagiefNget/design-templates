package com.afn.designpatterntemplates.adapter;

/**
 * Adapter Pattern
 * Converts an incompatible interface into one the client expects. Like a power plug adapter.
 * Use when: integrating third-party libraries, legacy code, or services with different interfaces.
 */

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

public class Adapter {

    public static void main(String[] args) {
        // Swap providers without changing BillingService
        BillingService withStripe = new BillingService(new StripeAdapter(new StripeClient()));
        BillingService withPayPal  = new BillingService(new PayPalAdapter(new PayPalClient()));
    }
}

