package com.hrm.service.common;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password hashing helpers (BCrypt, salt rounds = 10).
 */
public class SecurityService {

    private static final int SALT_ROUNDS = 10;

    public String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(SALT_ROUNDS));
    }

    public boolean matches(String plainPassword, String passwordHash) {
        if (plainPassword == null || passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, passwordHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
