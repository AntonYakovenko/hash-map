package com.softindex.hashmap;

import org.junit.Test;

import java.util.Random;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests for structural modifications and <tt>MapOpenAddressing</tt> interface operations
 */
public class HashMapOpenAddressingTest {

    private static final int SMALL_INITIAL_CAPACITY = 2;
    private static final int ATTEMPTS = 1_000;
    private static final Random RANDOM = new Random();

    private static final int KEY_1 = 1;
    private static final int KEY_2 = 2;
    private static final long VALUE_1 = 1L;
    private static final long VALUE_2 = 2L;

    private HashMapOpenAddressing map; // need class type to test hash table properties modification

    @Test
    public void test_initial_capacity_is_power_of_two() {
        map = new HashMapOpenAddressing(3);
        assertThat(map.capacity & (map.capacity - 1), is(0));
    }

    @Test
    public void test_hash_is_in_correct_interval() {
        map = new HashMapOpenAddressing();
        int hash;
        for (int i = 0; i < ATTEMPTS; i++) {
            hash = map.hash(RANDOM.nextInt());
            assertThat(hash, greaterThanOrEqualTo(0));
            assertThat(hash, lessThan(map.capacity));
        }
    }

    @Test
    public void test_element_is_put() {
        map = new HashMapOpenAddressing(SMALL_INITIAL_CAPACITY);
        map.put(KEY_1, VALUE_1);
        assertThat(map.capacity, is(SMALL_INITIAL_CAPACITY));
        assertThat(map.size, is(1));
        assertThat(map.get(KEY_1), is(VALUE_1));
    }

    @Test
    public void test_collision_is_resolved() {
        map = new HashMapOpenAddressing();
        map.put(KEY_1, VALUE_1);
        final int collisionKey = KEY_1 + map.capacity;
        map.put(collisionKey, VALUE_2);
        assertThat(map.size, is(2));
        assertThat(map.get(KEY_1), is(VALUE_1));
        assertThat(map.get(collisionKey), is(VALUE_2));
    }

    @Test
    public void test_map_is_correct_resized() {
        map = new HashMapOpenAddressing(SMALL_INITIAL_CAPACITY);
        map.put(KEY_1, VALUE_1);
        // check we achieve threshold to resize
        int size = (int) (map.capacity * map.loadFactor);
        assertThat(map.size, is(size));
        // put next element
        map.put(KEY_2, VALUE_2);
        // check hash table properties changed correct
        assertThat(map.capacity, is(SMALL_INITIAL_CAPACITY << 1));
        assertThat(map.size, is(++size));
        assertThat(map.get(KEY_1), is(VALUE_1));
        assertThat(map.get(KEY_2), is(VALUE_2));
    }

    @Test
    public void test_clear() {
        map = new HashMapOpenAddressing();
        populateMapByRandoms();
        assertFalse(map.isEmpty());
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    public void test_contains_key() {
        map = new HashMapOpenAddressing();
        final Integer key = RANDOM.nextInt();
        map.put(key, RANDOM.nextLong());
        populateMapByRandoms();
        assertTrue(map.containsKey(key));
    }

    @Test
    public void test_contains_value() {
        map = new HashMapOpenAddressing();
        populateMapByRandoms();
        final Long value = RANDOM.nextLong();
        map.put(KEY_1, value);
        assertTrue(map.containsValue(value));
        map.put(KEY_2, null);
        assertTrue(map.containsValue(null));
    }

    @Test
    public void test_keys() {
        map = new HashMapOpenAddressing();
        populateMapByRandoms();
        for (Integer key : map.keys()) {
            assertTrue(map.containsKey(key));
        }
    }

    @Test
    public void test_values() {
        map = new HashMapOpenAddressing();
        populateMapByRandoms();
        map.put(KEY_1, null);
        for (Long value : map.values()) {
            assertTrue(map.containsValue(value));
        }
    }

    @Test
    public void test_equals() {
        map = new HashMapOpenAddressing();
        MapOpenAddressing anotherMap = new HashMapOpenAddressing();
        for (int i = 0; i < ATTEMPTS; i++) {
            final int key = RANDOM.nextInt();
            final long value = RANDOM.nextLong();
            if (key % 2 == 0) {
                map.put(key, value);
                anotherMap.put(key, value);
            } else {
                map.put(key, null);
                anotherMap.put(key, null);
            }
        }
        assertEquals(map, anotherMap);
    }

    private void populateMapByRandoms() {
        for (int i = 0; i < ATTEMPTS; i++) {
            map.put(RANDOM.nextInt(), RANDOM.nextLong());
        }
    }
}
