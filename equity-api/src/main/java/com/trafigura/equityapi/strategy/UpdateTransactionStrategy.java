package com.trafigura.equityapi.strategy;

import com.trafigura.equityapi.cache.PositionStateCache;
import com.trafigura.equityapi.model.Position;
import com.trafigura.equityapi.model.Transaction;
import com.trafigura.equityapi.repository.PositionRepository;
import com.trafigura.equityapi.repository.TransactionRepository;
import com.trafigura.equityapi.service.PositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateTransactionStrategy implements TransactionStrategy {
    private final TransactionRepository transactionRepository;
    private final PositionRepository positionRepository;
    private final PositionStateCache positionStateCache;

    @Override
    @Transactional
    public void processTransaction(Transaction transaction) {
        Transaction prevTx = positionStateCache.getLatestTrade(transaction.getTradeId());
        if (prevTx == null) {
            log.warn("UPDATE received for unknown TradeID {}. Ignoring.", transaction.getTradeId());
            return;
        }

        // Reverse previous effect
        int prevSign = prevTx.getDirection() == Transaction.Direction.Buy ? -1 : 1;
        int prevNewPos = positionStateCache.getPosition(prevTx.getSecurityCode()) + prevSign * prevTx.getQuantity();
        positionStateCache.updatePosition(prevTx.getSecurityCode(), prevNewPos);
        positionRepository.save(new Position(prevTx.getSecurityCode(), prevNewPos));

        // Apply new UPDATE effect
        int newSign = transaction.getDirection() == Transaction.Direction.Buy ? 1 : -1;
        int newNewPos = positionStateCache.getPosition(transaction.getSecurityCode()) + newSign * transaction.getQuantity();
        positionStateCache.updatePosition(transaction.getSecurityCode(), newNewPos);
        positionRepository.save(new Position(transaction.getSecurityCode(), newNewPos));

        positionStateCache.setLatestTrade(transaction.getTradeId(), transaction);
        transactionRepository.save(transaction);

        log.info("Processed UPDATE for TradeID {}: prev [{} {} {}], new [{} {} {}].",
                transaction.getTradeId(),
                prevTx.getDirection(), prevTx.getQuantity(), prevTx.getSecurityCode(),
                transaction.getDirection(), transaction.getQuantity(), transaction.getSecurityCode());
    }

}