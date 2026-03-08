package com.afn.designpatterntemplates.serialization;

/**
 * Converting an object to a byte stream (and back) for storage, network transfer, or caching.
 * Use when: saving objects to disk, sending over a socket, storing in Redis/cache, deep cloning.
 *
 */

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


/**
 * serialVersionUID is critical — if you add a field without it and the UID differs, deserialization throws InvalidClassException.
 */