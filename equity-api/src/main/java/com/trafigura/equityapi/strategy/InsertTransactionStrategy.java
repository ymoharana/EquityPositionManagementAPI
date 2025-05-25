package com.trafigura.equityapi.strategy;

import com.trafigura.equityapi.cache.PositionStateCache;
import com.trafigura.equityapi.model.Position;
import com.trafigura.equityapi.model.Transaction;
import com.trafigura.equityapi.repository.PositionRepository;
import com.trafigura.equityapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InsertTransactionStrategy implements TransactionStrategy {
    private final PositionRepository positionRepository;
    private final TransactionRepository transactionRepository;
    private final PositionStateCache positionStateCache;

    @Override
    @Transactional
    public void processTransaction(Transaction transaction) {
        int sign = transaction.getDirection() == Transaction.Direction.Buy ? 1 : -1;
        int newPosition = positionStateCache.getPosition(transaction.getSecurityCode()) + sign * transaction.getQuantity();
        positionStateCache.updatePosition(transaction.getSecurityCode(), newPosition);
        positionStateCache.setLatestTrade(transaction.getTradeId(), transaction);
        positionRepository.save(new Position(transaction.getSecurityCode(), newPosition));
        transactionRepository.save(transaction);
        log.info("Processed INSERT: {} => Updated Position: {}", transaction, newPosition);
    }
}

