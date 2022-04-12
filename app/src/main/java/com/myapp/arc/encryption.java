package com.myapp.arc;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class encryption {
    MessageDigest md;

    public encryption() throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance("SHA-256");
    }

    public String hashing(String input){

        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

        // Convert byte array of hash into digest
        BigInteger number = new BigInteger(1, hash);

        // Convert the digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();


    }
}
