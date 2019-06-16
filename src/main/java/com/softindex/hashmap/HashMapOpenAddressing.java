package com.softindex.hashmap;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Hash table implementation with open addressing using linear probing
 * probe sequence
 *
 * <p>This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>) if the hash function evenly
 * distribute elements among the buckets
 *
 * <p>Features of this implementation:
 * <ul>
 * <li>Hash table does not allow <tt>null</tt> keys</li>
 * <li>Capacity of hash table is always a power of 2. So that it is always
 * relatively prime with linear probing coefficient (31) and never produces
 * a cycle when resolving collisions</li>
 * <li>An instance of <tt>HashMapOpenAddressing</tt> has two parameters that affect
 * its performance: <i>initial capacity</i> and <i>load factor</i>. Their
 * influence is the same as in {@link java.util.HashMap} implementation</li>
 * <li>This implementation is not synchronized</li>
 * </ul>
 */
public class HashMapOpenAddressing implements MapOpenAddressing {

    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // 16
    static final double DEFAULT_LOAD_FACTOR = 0.5;
    static final int LINEAR_PROBING_COEFFICIENT = 31; // always relatively prime with current capacity

    /**
     * Basic hash bin node, used for key-value pairs
     */
    static class Node {
        final Integer key;
        Long value;

        public Node(Integer key, Long value) {
            this.key = key;
            this.value = value;
        }

        public final Integer getKey() {
            return key;
        }

        public final Long getValue() {
            return value;
        }

        public final Long setValue(Long newValue) {
            Long oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Node) {
                Node that = (Node) o;
                return key.equals(that.key) && Objects.equals(value, that.value);
            }
            return false;
        }

        public final int hashCode() {
            return key ^ ((int) (value ^ (value >>> 32)));
        }

        public final String toString() {
            return key + "=" + value;
        }
    }

    /**
     * The table, initialized on first use, and resized as necessary.
     * When allocated, length is always a power of two.
     */
    Node[] table;

    /**
     * The number of times this HashMap has been structurally modified
     */
    int modCount;

    /**
     * The number of key-value mappings contained in this map
     */
    int size;

    /**
     * The capacity value (threshold / load factor)
     */
    int capacity;

    /**
     * The load factor for the hash table
     */
    final double loadFactor;

    public HashMapOpenAddressing(int initialCapacity, double loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0 || Double.isNaN(loadFactor) || Double.isInfinite(loadFactor)) {
            throw new IllegalArgumentException("Illegal loadFactor: " + loadFactor);
        }
        this.loadFactor = loadFactor;
        this.capacity = tableSizeFor(initialCapacity);
    }

    public HashMapOpenAddressing(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashMapOpenAddressing() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Checks the capacity is a power of 2 and returns it or nearest higher power of 2
     * Example: 8 -> 8, 9 -> 16
     *
     * @param capacity the capacity
     * @return the capacity or nearest higher power of 2 of the given capacity
     */
    static int tableSizeFor(int capacity) {
        return (capacity & (capacity - 1)) != 0 ? Integer.highestOneBit(capacity) << 1 : capacity;
    }

    /**
     * Function for linear probing
     *
     * @param x argument of a function
     * @return the result
     */
    static int probe(int x) {
        return LINEAR_PROBING_COEFFICIENT * x;
    }

    /**
     * Converts a hash value to an index, removing negative sign
     * and allocating a hash value in the interval [0, table.length)
     *
     * @param keyHash a hashcode of the key
     * @return a hash value from the interval [0, table.length)
     */
    final int hash(int keyHash) {
        return (keyHash & Integer.MAX_VALUE) % capacity;
    }

    /**
     * @see Map#size()
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * @see Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key
     *
     * @param key the key
     * @return the value associated with the key or {@code null} if
     * this map contains no mapping for the key
     * @throws IllegalArgumentException if {@code null} key is passed
     */
    @Override
    public Long get(Integer key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key not allowed");
        }
        if (table == null || table.length == 0) {
            return null;
        }
        final int offset = hash(key);
        // linearly probe from original hash until find an element, otherwise return null
        for (int i = offset, x = 1; ; i = hash(offset + probe(x++))) {
            if (table[i] != null) { // check a non-null key
                if (table[i].key.equals(key)) { // element found
                    return table[i].value;
                }
            } else { // element not found
                return null;
            }
        }
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * (A <tt>null</tt> return can also indicate that the map
     * previously associated <tt>null</tt> with <tt>key</tt>.)
     * @throws IllegalArgumentException if {@code null} key is passed
     */
    @Override
    public Long put(Integer key, Long value) {
        if (key == null) {
            throw new IllegalArgumentException("Null key not allowed");
        }
        if (table == null) {
            resize();
        }
        modCount++;
        final int offset = hash(key);
        for (int i = offset, x = 1; ; i = hash(offset + probe(x++))) {
            if (table[i] != null) { // current bucket already contains a key
                if (table[i].key.equals(key)) { // update a key and return the old value
                    Long oldValue = table[i].value;
                    table[i].value = value;
                    return oldValue;
                }
            } else { // create a new bucket
                table[i] = new Node(key, value);
                final int threshold = (int) (capacity * loadFactor);
                if (++size > threshold) { // resize if threshold achieved
                    resize();
                }
                return null;
            }
        }
    }

    /**
     * Initializes or doubles table size and rehashes elements.
     * If initial capacity of the table is 0, assign default capacity value to it
     */
    final void resize() {
        if (table == null) {
            table = new Node[capacity != 0 ? capacity : DEFAULT_INITIAL_CAPACITY];
        } else {
            capacity = capacity << 1;
            final Node[] oldData = Arrays.copyOf(table, table.length);
            this.clear();
            table = new Node[capacity];
            for (Node node : oldData) {
                if (node != null) {
                    this.put(node.key, node.value);
                }
            }
        }
    }

    /**
     * @see Map#clear()
     */
    @Override
    public void clear() {
        modCount++;
        if (table != null && size > 0) {
            size = 0;
            for (int i = 0; i < table.length; i++) {
                table[i] = null;
            }
        }
    }

    /**
     * @see Map#containsKey(Object)
     */
    @Override
    public boolean containsKey(final Integer key) {
        return Arrays.stream(table)
                .filter(Objects::nonNull)
                .map(Node::getKey)
                .anyMatch(k -> k.equals(key));
    }

    /**
     * @see Map#containsValue(Object)
     */
    @Override
    public boolean containsValue(final Long value) {
        return Arrays.stream(table)
                .filter(Objects::nonNull)
                .map(Node::getValue)
                .anyMatch(v -> Objects.equals(v, value));
    }

    /**
     * @return an array of keys contained in this map
     */
    @Override
    public Integer[] keys() {
        return Arrays.stream(table)
                .filter(Objects::nonNull)
                .map(Node::getKey)
                .toArray(Integer[]::new);
    }

    /**
     * @return an array of values contained in this map
     */
    @Override
    public Long[] values() {
        return Arrays.stream(table)
                .filter(Objects::nonNull)
                .map(Node::getValue)
                .toArray(Long[]::new);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MapOpenAddressing) {
            MapOpenAddressing that = (MapOpenAddressing) o;
            if (this.size() != that.size()) {
                return false;
            }
            for (int i = 0; i < this.size(); i++) {
                if (table[i] != null) {
                    Integer key = this.table[i].key;
                    Long value = this.table[i].value;
                    if (value != null) {
                        if (!value.equals(that.get(key))) {
                            return false;
                        }
                    } else {
                        if (!(that.get(key) == null && that.containsKey(key))) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.stream(table)
                .map(Node::hashCode)
                .reduce(Integer::sum)
                .orElse(0);
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i = 0; ; i++) {
            if (i == table.length) {
                return sb.append('}').toString().replace(", }", "}");
            }
            if (table[i] != null) {
                sb.append(table[i].toString());
                sb.append(',').append(' ');
            }
        }
    }
}
