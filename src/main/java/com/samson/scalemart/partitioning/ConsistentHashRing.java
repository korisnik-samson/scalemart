package com.samson.scalemart.partitioning;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
public class ConsistentHashRing {

    private final SortedMap<Long, String> circle = new TreeMap<>();
    private final int VIRTUAL_NODES = 10; // this improves load balancing

    public void addNode(String nodeIP) {
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            long hash = computeHash(nodeIP + "-" + i);
            circle.put(hash, nodeIP);
        }
    }

    public void removeNode(String nodeIP) {
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            long hash = computeHash(nodeIP + "-" + i);
            circle.remove(hash, nodeIP);
        }
    }

    public String getNodeForKey(String key) {
        if (circle.isEmpty()) return null;
        long hash = computeHash(key);

        if (!circle.containsKey(hash)) {
            SortedMap<Long, String> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }

        return circle.get(hash);
    }

    // MD5 is usually the standard for consistent hashing (e.g., used in Cassandra/Dynamo)
    private long computeHash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));

            return ((long) (digest[3] & 0xFF) << 24) | ((long) (digest[2] & 0xFF) << 16) |
                    ((long) (digest[1] & 0xFF) << 8) | ((long) (digest[0] & 0xFF));

        } catch (Exception e) { throw new RuntimeException(e); }
    }

}
