package com.spms.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class FileCheckCodeUtil {

    public static String generateChecksum(String filePath) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        InputStream fis = new FileInputStream(filePath);
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();

        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
