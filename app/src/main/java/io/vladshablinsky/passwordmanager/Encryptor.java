package io.vladshablinsky.passwordmanager;

/**
 * Created by vlad on 5/13/16.
 */

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {

    private static final String INIT_VECTOR = "RandomInitVector";

    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encode(encrypted));

            return Base64.encode(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static String paddedKey(String s) {
        StringBuilder sb = new StringBuilder("aaaabbbbccccdddd");
        for (int i = 0; i < Math.min(s.length(), 16); ++i) {
            sb.setCharAt(i, s.charAt(i));
        }
        return sb.toString();
    }

    public static String encryptWithKey(String key, String text) {
        return encrypt(paddedKey(key), INIT_VECTOR, text);
    }

    public static String decryptWithKey(String key, String text) {
        return decrypt(paddedKey(key), INIT_VECTOR, text);
    }

    public static void main(String[] args) {
        String key = "aaaabbbbccccfsadfsaddddd"; // 128 bit key
        System.out.println(decryptWithKey(key, encryptWithKey(key, "Hello World")));
    }
}