/**
 * This private project is a project which automatizate workflow in medical center AVESTA (http://avesta-center.com) called "MedRegistry".
 * The "MedRegistry" demonstrates my programming skills to * potential employers.
 *
 * Here is short description: ( for more detailed description please read README.md or
 * go to https://github.com/theshamuel/medregistry )
 *
 * Front-end: JS, HTML, CSS (basic simple functionality)
 * Back-end: Spring (Spring Boot, Spring IoC, Spring Data, Spring Test), JWT library, Java8
 * DB: MongoDB
 * Tools: git,maven,docker.
 *
 * My LinkedIn profile: https://www.linkedin.com/in/alex-gladkikh-767a15115/
 */
package com.theshamuel.medreg.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * The Utils class.
 *
 * @author Alex Gladkikh
 */
public class Utils {

    public static String[] badSymbols = new String[]{"_",".",":","@",","," ","-","\\\\","/","'"};
    /**
     * Encrypt password to SHA-256 string.
     *
     * @param password the password
     * @param salt     the salt
     * @return string string
     */
    public static String pwd2sha256(String password, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            password = password.concat(salt);
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < bytes.length; ++i) {
                sb.append(Integer.toString((bytes[i] & 255) + 256, 16).substring(1));
            }

            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedPassword.toString().substring(0,16);
    }


    /**
     * Convert to md5.
     *
     * @param password the password
     * @return the string
     */
    public static String convertToMd5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Gen salt for password.
     *
     * @return the string
     */
    public static String genSalt() {
        SecureRandom random;
        byte[] salt = new byte[20];
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return salt.toString();
    }


    /**
     * Delete not need symbol string.
     *
     * @param str     the inout string
     * @param symbols the symbols which should be deleted from input string
     * @return the new string without symbols
     */
    public static String deleteNotNeedSymbol(String str, String[] symbols) {
        StringBuilder result = new StringBuilder();
        result.append(str);
        for (int i=0; i<symbols.length;i++){
            String[] tookens = result.toString().split(symbols[i]);
            if (tookens.length>1){
                result = new StringBuilder();
                for (int j = 0; j < tookens.length; j++)
                    result.append(tookens[j]);
            }
        }
        return result.toString();
    }
}
