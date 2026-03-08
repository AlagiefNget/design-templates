package com.afn.designpatterntemplates.solid_principles;

/**
 * 1. SOLID Design Principles
 * Five principles that make OOP code maintainable, extensible, and testable.
 *
 * S — Single Responsibility Principle
 * One class = one reason to change.
 *
 * Why:
 * When email logic changes, you shouldn't touch UserService. Keeps classes focused and testable.
 */

class User {

}

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

public class SingleResponsibility {
}
