package com.trafigura.equityapi.strategy;

import com.trafigura.equityapi.model.Transaction;

public interface TransactionStrategy {
    void processTransaction(Transaction transaction);
}
