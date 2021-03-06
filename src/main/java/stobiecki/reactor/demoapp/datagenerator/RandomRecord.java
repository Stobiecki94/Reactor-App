package stobiecki.reactor.demoapp.datagenerator;


import stobiecki.reactor.demoapp.reader.BillingRecord;

import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;

class RandomRecord extends BillingRecord {

    private final static String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final static Random RANDOM = new Random();

    public RandomRecord() {
        super(generateRandomString(50), generateRandomString(50), generateRandomString(10), generateRandomDate(), generateRandomDuration());
    }

    private static int generateRandomDuration() {
        return RANDOM.nextInt(3600);
    }

    private static Date generateRandomDate() {
        return new Date(new Date().getTime() + RANDOM.nextInt(1000 * 60 * 60 * 24 * 365));
    }

    private static String generateRandomString(int maxLength) {
        return RANDOM
                .ints()
                .limit(maxLength)
                .map(Math::abs)
                .map(number -> number % ALPHABET.length())
                .mapToObj(ALPHABET::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
