package com.trafigura.equityapi.factory;

import com.trafigura.equityapi.model.Transaction;
import com.trafigura.equityapi.strategy.TransactionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TransactionStrategyFactory {
    private final Map<Transaction.ActionType, TransactionStrategy> strategyMap;

    public TransactionStrategy getStrategy(Transaction.ActionType actionType) {
        return strategyMap.get(actionType);
    }
}
