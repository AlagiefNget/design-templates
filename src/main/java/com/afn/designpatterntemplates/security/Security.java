package com.afn.designpatterntemplates.security;

/**
 * Key security practices in Java applications.
 */


import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 1. Password hashing with BCrypt (NEVER store plain text)
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12); // cost factor 12
String hashed = encoder.encode("userPassword123");
boolean matches = encoder.matches("userPassword123", hashed); // true

// 2. AES-256 encryption for sensitive data at rest
public class AesEncryption {
    private static final String ALGO = "AES/GCM/NoPadding";

    public static byte[] encrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv); // always random IV
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        // prepend IV to ciphertext
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return result;
    }

    public static SecretKey generateKey() throws Exception {
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(256);
        return gen.generateKey();
    }
}

// 3. SQL Injection prevention — ALWAYS use PreparedStatement
// ❌ Vulnerable
String query = "SELECT * FROM users WHERE name = '" + userInput + "'";

// ✅ Safe
PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
stmt.setString(1, userInput); // parameterized — user input never interpreted as SQL

// 4. Secure random token generation (e.g., for password reset, API keys)
byte[] tokenBytes = new byte[32];
new SecureRandom().nextBytes(tokenBytes);
String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

// 5. Input validation
public void processAge(String input) {
    int age = Integer.parseInt(input); // throws NumberFormatException on invalid input
    if (age < 0 || age > 150) throw new IllegalArgumentException("Invalid age: " + age);
    // proceed safely
}

public class Security {
}
