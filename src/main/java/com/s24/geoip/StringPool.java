package com.s24.geoip;

import java.util.HashMap;

/**
 * A simple string pool. For a large number of strings, this is much faster than calling {@code intern} on every
 * string because {@link String#intern()} uses a fixed size hash table.
 *
 * This pool is not thread safe.
 */
public final class StringPool {
    private final HashMap<String, String> pool;

    public StringPool() {
        this.pool = new HashMap<>();
    }

    /**
     * Adds the given string to the pool if it has not been added yet, and returns a reference to the pooled string.
     * 
     * @param s
     *            the string.
     */
    public String pool(String s) {
        String alreadyPooledInstance = pool.putIfAbsent(s, s);
        return alreadyPooledInstance != null ? alreadyPooledInstance : s;
    }

    /**
     * Returns the size of the string pool.
     */
    public int size() {
        return pool.size();
    }
}
