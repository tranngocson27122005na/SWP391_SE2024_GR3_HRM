package com.hrm.utility;

import org.mindrot.jbcrypt.BCrypt;

/**
 * One-off helper to print a BCrypt hash for seed data.
 * Run main() locally, then update sys_user.password_hash.
 */
public final class BcryptHashPrinter {

    private BcryptHashPrinter() {
    }

    public static void main(String[] args) {
        String plain = args.length > 0 ? args[0] : "123456";
        String hash = BCrypt.hashpw(plain, BCrypt.gensalt(10));
        System.out.println("plain = " + plain);
        System.out.println("hash  = " + hash);
        System.out.println("ok    = " + BCrypt.checkpw(plain, hash));
    }
}
