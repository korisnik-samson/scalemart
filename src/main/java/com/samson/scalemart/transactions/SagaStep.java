package com.samson.scalemart.transactions;

public interface SagaStep {
    String getName();

    void execute() throws Exception;   // The action

    void compensate();                 // The undo action
}
