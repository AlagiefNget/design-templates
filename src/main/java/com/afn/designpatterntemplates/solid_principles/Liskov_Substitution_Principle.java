package com.afn.designpatterntemplates.solid_principles;

/**
 * Subclasses must be substitutable for their parent without breaking behavior.
 */

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

public class Liskov_Substitution_Principle {
}
