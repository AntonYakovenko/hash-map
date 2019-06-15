package com.softindex.hashmap;

public interface MapOpenAddressing {

    int size();

    boolean isEmpty();

    Long get(Integer key);

    Long put(Integer key, Long value);

    void clear();

    Integer[] keySet();

    Long[] values();
}
