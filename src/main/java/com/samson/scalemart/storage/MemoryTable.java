package com.samson.scalemart.storage;

import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IMPLEMENTATION: MemTable (Chapter 3)
 * An in-memory buffer using a Red-Black Tree (TreeMap in Java).
 * When size threshold is reached, this must be flushed to an SSTable.
 */

public class MemoryTable {

    private final TreeMap<String, String> dataTable = new TreeMap<>();
    private final AtomicInteger sizeInBytes = new AtomicInteger(0);
    private final int FLUSH_THRESHOLD_BYTES = 4096; // this is a tiny threshold for demo purposes

    public void put(String key, String value) {
        dataTable.put(key, value);
        int entrySize = key.length() + value.length();

        if (sizeInBytes.addAndGet(entrySize) >= FLUSH_THRESHOLD_BYTES)
            throw new FlushRequiredException(new TreeMap<>(dataTable));
    }

    public String get(String key) {
        return dataTable.get(key);
    }

    public void flush() {
        dataTable.clear();
        sizeInBytes.set(0);
    }

    public static class FlushRequiredException extends RuntimeException {
        public final TreeMap<String, String> dataToFlush;

        public FlushRequiredException(TreeMap<String, String> data) {
            this.dataToFlush = data;
        }
    }
}
