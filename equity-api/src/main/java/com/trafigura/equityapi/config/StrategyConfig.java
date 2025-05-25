package com.trafigura.equityapi.config;

import com.trafigura.equityapi.model.Transaction;
import com.trafigura.equityapi.strategy.CancelTransactionStrategy;
import com.trafigura.equityapi.strategy.InsertTransactionStrategy;
import com.trafigura.equityapi.strategy.TransactionStrategy;
import com.trafigura.equityapi.strategy.UpdateTransactionStrategy;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class StrategyConfig {

    private final InsertTransactionStrategy insert;
    private final UpdateTransactionStrategy update;
    private final CancelTransactionStrategy cancel;

    @Bean
    public Map<Transaction.ActionType, TransactionStrategy> strategyMap() {
        Map<Transaction.ActionType, TransactionStrategy> map = new EnumMap<>(Transaction.ActionType.class);
        map.put(Transaction.ActionType.INSERT, insert);
        map.put(Transaction.ActionType.UPDATE, update);
        map.put(Transaction.ActionType.CANCEL, cancel);
        return map;
    }
}
