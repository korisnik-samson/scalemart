package com.samson.scalemart.storage;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Component
public class SSTableManager {

    public final MemoryTable memoryTable = new MemoryTable();
    private final List<TreeMap<String, String>> diskSegments = new ArrayList<>();

    public void write(String key, String value) {
        try {
            memoryTable.put(key, value);

        } catch (MemoryTable.FlushRequiredException e) {
            flushToDisk(e.dataToFlush);
            memoryTable.flush();
            memoryTable.put(key, value);
        }
    }

    public String read(String key) {
        // Check the memory table - most recent data
        String value = memoryTable.get(key);
        if (value != null) return value;

        // Check SSTable segments on the disk (newest to oldest)
        // it demonstrates why reads can be slower in LSM trees without Bloom filters
        for (int i = diskSegments.size() - 1; i >= 0; i--) {
            value = diskSegments.get(i).get(key);
            if (value != null) return value;
        }

        return null;
    }

    private void flushToDisk(TreeMap<String, String> data) {
        System.out.println("[StorageEngine] Flushing MemTable to new SSTable Segment...");

        diskSegments.add(data);
        // "Compaction" would be triggered here in a background thread
    }

}
