package com.samson.scalemart.controllers;

import com.samson.scalemart.partitioning.ConsistentHashRing;
import com.samson.scalemart.storage.SSTableManager;
import com.samson.scalemart.transactions.OrderSaga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/research")
public class ResearchController {

    private final SSTableManager storageEngine;
    private final ConsistentHashRing hashRing;
    private final OrderSaga orderSaga;

    @Autowired
    public ResearchController(SSTableManager storageEngine, ConsistentHashRing hashRing, OrderSaga orderSaga) {
        this.storageEngine = storageEngine;
        this.hashRing = hashRing;
        this.orderSaga = orderSaga;
    }

    // Experiment 1: Write Throughput (LSM Tree)
    @PostMapping("/storage/write/{count}")
    public String benchmarkWrite(@PathVariable int count) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) storageEngine.write("key-" + i, "value-" + i);

        long duration = System.currentTimeMillis() - start;

        return "LSM Write: " + count + " records in " + duration + "ms";
    }

    // Experiment 2: Partitioning Rebalance
    @GetMapping("/partitioning/simulate")
    public String simulateRebalance() {
        hashRing.addNode("Node-A");
        hashRing.addNode("Node-B");

        String node1 = hashRing.getNodeForKey("Order-12345");

        hashRing.addNode("Node-C"); // Add new node
        String node2 = hashRing.getNodeForKey("Order-12345");

        return "Key 'Order-12345' moved from " + node1 + " to " + node2;
    }

    // Experiment 3: Distributed Transaction Failure
    @PostMapping("/saga/checkout")
    public String triggerSaga() {
        orderSaga.placeOrder("ORD-999");
        return "Check server logs for SAGA execution/compensation flow.";
    }
}
