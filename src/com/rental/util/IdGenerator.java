package com.rental.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class IdGenerator {
    private static final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public static String next(String prefix) {
        AtomicInteger counter = counters.computeIfAbsent(prefix, p -> new AtomicInteger(1));
        int value = counter.getAndIncrement();
        return String.format("%s-%04d", prefix, value);
    }
}
