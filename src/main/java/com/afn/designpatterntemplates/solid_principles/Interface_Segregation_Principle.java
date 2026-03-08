package com.afn.designpatterntemplates.solid_principles;

/**
 * Don't force classes to implement methods they don't need.
 */

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

public class Interface_Segregation_Principle {
}
