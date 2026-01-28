package com.samson.scalemart.transactions;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderSaga {

    public void placeOrder(String orderId) {
        List<SagaStep> stepsExecuted = new ArrayList<>();

        // step 1: inventory
        SagaStep inventory = new SagaStep() {
            @Contract(pure = true) @Override
            public @NonNull String getName() { return "Reserve Inventory"; }

            @Override
            public void execute() throws Exception { System.out.println("[Saga] Reserving inventory for order " + orderId); }

            @Override
            public void compensate() { System.out.println("[Saga] Releasing inventory for order " + orderId); }
        };

        SagaStep payment = new SagaStep() {
            @Override
            public @NonNull String getName() { return "ProcessPayment"; }

            @Override
            public void execute() throws Exception {
                System.out.println("Attempting payment...");
                if (Math.random() > 0.5) throw new Exception("Payment Gateway Timeout");

                System.out.println("Payment Successful.");
            }

            @Override
            public void compensate() { System.out.println("UNDO: Refunded payment."); }
        };

        try {
            inventory.execute();
            stepsExecuted.add(inventory);

            payment.execute();
            stepsExecuted.add(payment);

        } catch (Exception e) {
            System.err.println("SAGA FAILURE at " + e.getMessage());
            rollback(stepsExecuted);
        }
    }

    private void rollback(List<SagaStep> steps) {
        System.out.println("--- INITIATING COMPENSATION ---");

        // Must compensate in REVERSE order of execution
        Collections.reverse(steps);

        for (SagaStep step : steps) step.compensate();
    }

}
