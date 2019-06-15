package com.softindex.hashmap;

import java.util.Arrays;
import java.util.Objects;

public class HashMapOpenAddressing implements MapOpenAddressing {

    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final double DEFAULT_LOAD_FACTOR = 0.75;
    static final int LINEAR_PROBING_COEFFICIENT = 31; // always relatively prime with current capacity

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
                return Objects.equals(key, that.key) && Objects.equals(value, that.value);
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

    Node[] table;
    int modCount;
    int threshold;
    final double loadFactor;

    public HashMapOpenAddressing(int initialCapacity, double loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0 || Double.isNaN(loadFactor) || Double.isInfinite(loadFactor)) {
            throw new IllegalArgumentException("Illegal loadFactor: " + loadFactor);
        }
        this.loadFactor = loadFactor;
        this.threshold = (int) (tableSizeFor(initialCapacity) * loadFactor);
    }

    public HashMapOpenAddressing(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashMapOpenAddressing() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    static int tableSizeFor(int capacity) {
        if ((capacity & (capacity - 1)) != 0) { // check the capacity is power of 2
            return Integer.highestOneBit(capacity) << 1;
        }
        return capacity;
    }

    final int probe(int x) {
        return LINEAR_PROBING_COEFFICIENT * x;
    }

    // Converts a hash value to an index, stripping negative sign
    // and placing the hash value in the domain [0, table.length)
    final int getHashIndex(int keyHash) {
        return (keyHash & Integer.MAX_VALUE) % this.size();
    }

    @Override
    public int size() {
        return table.length;
    }

    @Override
    public boolean isEmpty() {
        return table.length == 0;
    }

    @Override
    public Long get(Integer key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key not allowed");
        }
        final int offset = getHashIndex(key);
        // TODO: comments
        for (int i = offset, x = 1; ; i = getHashIndex(offset + probe(x++))) {
            if (table[i].key != null) {
                if (table[i].key.equals(key)) {
                    return table[i].value;
                }
            } else { // element not found
                return null;
            }
        }
    }

    @Override
    public Long put(Integer key, Long value) {
        if (key == null) throw new IllegalArgumentException("Null key");
        if (this.size() >= --threshold) { // TODO: check
            resize();
        }
        final int offset = getHashIndex(key);
        for (int i = offset, x = 1; ; i = getHashIndex(offset + probe(x++))) {
            if (table[i].key != null) {
                // TODO: comments
                if (table[i].key.equals(key)) {
                    Long oldValue = table[i].value;
                    table[i].value = value;
                    modCount++;
                    return oldValue;
                }
            } else {
                table[i] = new Node(key, value);
                modCount++;
                return null;
            }
        }
    }

    final void resize() {
        Node[] oldData = table;
        this.clear();
        threshold = (int) (threshold / loadFactor) << 1;
        for (Node node : oldData) {
            this.put(node.key, node.value);
        }
    }


    @Override
    public void clear() {
        modCount++;
        int length = this.size();
        if (table != null && length > 0) {
            for (int i = 0; i < length; i++) {
                table[i] = null;
            }
        }
    }

    @Override
    public Integer[] keySet() {
        return Arrays.stream(table).map(Node::getKey).toArray(Integer[]::new);
    }

    @Override
    public Long[] values() {
        return Arrays.stream(table).map(Node::getValue).toArray(Long[]::new);
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
                if (!this.get(table[i].key).equals(that.get(table[i].key))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (Node node : table) {
            h += node.hashCode();
        }
        return h;
    }

    @Override
    public String toString() {
        int length = this.size();
        if (length == 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i = 0; ; i++) {
            sb.append(table[i].toString());
            if (++i == length) { // TODO: check
                return sb.append("}").toString();
            }
            sb.append(", ");
        }
    }
}
