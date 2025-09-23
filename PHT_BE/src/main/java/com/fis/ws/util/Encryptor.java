package com.fis.ws.util;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String defaultSecretKey = "TCSEncryptor";
    private static final int IV_SIZE = 12; // GCM thường dùng 12 bytes IV
    private static final int TAG_LENGTH = 128; // Độ dài tag GCM (bits)
    private Key secretKeySpec;

    public Encryptor() throws Exception {
        this(null);
    }

    public Encryptor(String secretKey) throws Exception {
        this.secretKeySpec = generateKey(secretKey);
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Tạo IV ngẫu nhiên
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);

        // Khởi tạo Cipher với GCMParameterSpec
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKeySpec, gcmSpec);

        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Ghi IV + dữ liệu mã hóa thành một mảng
        byte[] ivAndCiphertext = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, ivAndCiphertext, 0, iv.length);
        System.arraycopy(encrypted, 0, ivAndCiphertext, iv.length, encrypted.length);

        return asHexString(ivAndCiphertext);
    }

    public String decrypt(String encryptedString) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        byte[] ivAndCiphertext = toByteArray(encryptedString);

        // Lấy IV từ dữ liệu mã hóa
        byte[] iv = Arrays.copyOfRange(ivAndCiphertext, 0, IV_SIZE);
        byte[] ciphertext = Arrays.copyOfRange(ivAndCiphertext, IV_SIZE, ivAndCiphertext.length);

        // Khởi tạo Cipher với IV
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, this.secretKeySpec, gcmSpec);

        byte[] original = cipher.doFinal(ciphertext);
        return new String(original, "UTF-8");
    }

    private Key generateKey(String secretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (secretKey == null) {
            secretKey = defaultSecretKey;
        }
        byte[] key = secretKey.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // Dùng 16 bytes = 128 bit khóa AES

        return new SecretKeySpec(key, "AES");
    }

    private String asHexString(byte[] buf) {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
        for (byte b : buf) {
            strbuf.append(String.format("%02X", b));
        }
        return strbuf.toString();
    }

    private byte[] toByteArray(String hexString) {
        int arrLength = hexString.length() / 2;
        byte[] buf = new byte[arrLength];
        for (int i = 0; i < arrLength; i++) {
            int index = i * 2;
            buf[i] = (byte) Integer.parseInt(hexString.substring(index, index + 2), 16);
        }
        return buf;
    }

    public static void main(String[] args) throws Exception {
        Encryptor encryptor = new Encryptor("HDB2021");

        // Mã hóa
        String encrypted = encryptor.encrypt("Hana@#123");
        System.out.println("Encrypted: " + encrypted);

        // Giải mã
        String decrypted = encryptor.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
    }
}
