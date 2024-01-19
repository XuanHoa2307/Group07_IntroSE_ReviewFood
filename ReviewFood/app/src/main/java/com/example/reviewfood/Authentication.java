package com.example.reviewfood;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

public class Authentication {
    private String email;
    private String password;

    public Authentication() {
    }

    public Authentication(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    public static String hashPass(String pass) {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(pass.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }
        // For specifying wrong message digest algorithms
        catch (Exception e) {
            return null;
        }
    }
}
