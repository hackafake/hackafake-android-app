package com.example.alessandro.rokersfun_androidthingcontroller;

public final class Parameters {
    public static final String BASE_URL = "https://api.hackafake.it";

    //News object fields
    public static final String NEWS_URL_FIELD = "url";

    //Main actity parameters
    public static final long BEST_NEWS_DELAY_CHECK_MILLIS = 10 * 1000;
    public static final String FAKENEWS_TOPIC = "/fakenews";
    public static final int FAKENEWS_QUANTITY = 10;
    public static String getTopFakeText(int counter) {
        return "This fake news was shared " + Integer.toString(counter) + " times";
    }

    //counter parameters
    public static final String COUNTER_TOPIC = "/counter";
    public static final long COUNTER_DELAY_MILLIS = 2 * 1000;
    public static final String FAKE_NEWS_COUNTER_FIELD = "fake";
    public static final String REAL_NEWS_COUNTER_FIELD = "real";

    //challenge parameters
    public static final String CHALLENGE_TOPIC = "/challenge";
    public static final String FAKE_NEWS_CHALLENGE_FIELD = "fake";
    public static final String REAL_NEWS_CHALLENGE_FIELD = "real";
    public static final long CHALLENGE_COMPLETED_DELAY = 5 * 1000;
}