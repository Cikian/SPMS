package com.spms.utils;

import java.util.Random;

public class GenerateRandomUsernameUtils {

    private static final String PREFIX = "spms";

    private static Random random = new Random();

    public static String generateRandomUsername() {
        long seed = System.currentTimeMillis();
        random.setSeed(seed);
        int randomNum = random.nextInt(900000) + 100000;
        return PREFIX + randomNum;
    }
}
