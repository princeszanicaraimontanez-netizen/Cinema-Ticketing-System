/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

/**
 * Cryptographic utility providing secure SHA-256 password hashing and verification.
 * 
 * Architectural Connection in System:
 * - Layer: Security / Utility Layer (com.movie.util)
 * - Consumer: Called by UserService during customer registration, administrative updates,
 *   and user login authentication.
 */
public class PasswordUtil {
    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plain.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    public static boolean matches(String plain, String hashed) {
        return hash(plain).equalsIgnoreCase(hashed);
    }
}
    

