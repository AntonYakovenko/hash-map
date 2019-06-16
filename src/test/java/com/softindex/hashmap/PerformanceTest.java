package com.softindex.hashmap;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Tests to compare performance of <tt>HashMapOpenAddressing</tt> implementation
 * with JDK <tt>HashMap</tt> implementation
 */
public class PerformanceTest {

    private static final int ATTEMPTS = 1_000_000;
    private static final Random RANDOM = new Random();

    private MapOpenAddressing mapOpenAddressing;
    private Map<Integer, Long> mapJdk;

    @Test
    public void test_performance_hash_map_open_addressing() {
        mapOpenAddressing = new HashMapOpenAddressing();

        long putStart = System.nanoTime();
        for (int i = 0; i < ATTEMPTS; i++) {
            mapOpenAddressing.put(RANDOM.nextInt(), RANDOM.nextLong());
        }
        long putEnd = System.nanoTime();

        long getStart = System.nanoTime();
        for (int i = 0; i < ATTEMPTS; i++) {
            mapOpenAddressing.get(RANDOM.nextInt());
        }
        long getEnd = System.nanoTime();

        System.out.println("put (open addressing): " + (putEnd - putStart) / 1e9);
        System.out.println("get (open addressing): " + (getEnd - getStart) / 1e9);
    }

    @Test
    public void test_performance_hash_map_jdk() {
        mapJdk = new HashMap<>();

        long putStart = System.nanoTime();
        for (int i = 0; i < ATTEMPTS; i++) {
            mapJdk.put(RANDOM.nextInt(), RANDOM.nextLong());
        }
        long putEnd = System.nanoTime();

        long getStart = System.nanoTime();
        for (int i = 0; i < ATTEMPTS; i++) {
            mapJdk.get(RANDOM.nextInt());
        }
        long getEnd = System.nanoTime();

        System.out.println("put (jdk): " + (putEnd - putStart) / 1e9);
        System.out.println("get (jdk): " + (getEnd - getStart) / 1e9);
    }
}
