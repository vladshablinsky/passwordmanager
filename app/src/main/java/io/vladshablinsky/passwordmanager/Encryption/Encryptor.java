package io.vladshablinsky.passwordmanager.Encryption;

/**
 * Created by vlad on 5/13/16.
 */

import java.nio.charset.Charset;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.vladshablinsky.passwordmanager.Encryption.Base64;

public class Encryptor {

    private static final String INIT_VECTOR = "RandomInitVector";

    public static String encrypt(byte[] key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

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

    public static String decrypt(byte[] key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static byte[] paddedKey(String s) {
        byte a[] = "aaaabbbbccccdddd".getBytes(Charset.forName("UTF8"));
        byte b[] = s.getBytes(Charset.forName("UTF8"));
        for (int i = 0; i < Math.min(b.length, 16); ++i) {
            a[i] = b[i];
        }
        return a;
    }

    public static String encryptWithKey(String key, String text) {
        return encrypt(paddedKey(key), INIT_VECTOR, text);
    }

    public static String decryptWithKey(String key, String text) {
        return decrypt(paddedKey(key), INIT_VECTOR, text);
    }

    public static void main(String[] args) {
        String key = "aaaabbbbccccfsadfsaddddd"; // 128 bit key
        System.out.println(encryptWithKey("pass", "abacabadabadfjasdlkfj;adskfasdHellofdsafajlkfjasWorld"));
    }

}