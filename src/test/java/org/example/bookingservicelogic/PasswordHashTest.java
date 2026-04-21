package org.example.bookingservicelogic;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Test to generate and verify BCrypt password hashes.
 * Run this test to get correct hashes for the database.
 */
public class PasswordHashTest {

    @Test
    void generatePasswordHashes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "pass123";
        String hash = encoder.encode(password);

        System.out.println("===========================================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("===========================================");

        // Verify the hash works
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verification: " + (matches ? "SUCCESS" : "FAILED"));

        String dbHash = "$2a$10$EqKcp1WFKVQISheBxkguKuIw4/LqVRVaEVHAhQQPDqPSXF.hRxGCm";

        System.out.println("\n===========================================");
        System.out.println("Testing hash from database:");
        System.out.println("Hash: " + dbHash);


        String[] testPasswords = {"pass123", "password123", "admin123", "password", "123456"};
        for (String pwd : testPasswords) {
            boolean match = encoder.matches(pwd, dbHash);
            System.out.println("  '" + pwd + "' -> " + (match ? "MATCH!" : "no match"));
        }
        System.out.println("===========================================");
    }
}
