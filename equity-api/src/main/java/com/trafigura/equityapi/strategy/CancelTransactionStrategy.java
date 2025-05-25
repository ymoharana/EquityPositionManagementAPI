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
public class CancelTransactionStrategy implements TransactionStrategy {
    private final TransactionRepository transactionRepository;
    private final PositionStateCache positionStateCache;
    private final PositionRepository positionRepository;

    @Override
    @Transactional
    public void processTransaction(Transaction cancelTx) {
        Transaction lastTx = positionStateCache.getLatestTrade(cancelTx.getTradeId());
        if (lastTx == null) {
            log.warn("CANCEL received for unknown TradeID {}. Ignoring.", cancelTx.getTradeId());
            return;
        }

        int sign = lastTx.getDirection() == Transaction.Direction.Buy ? -1 : 1;
        int newPosition = positionStateCache.getPosition(lastTx.getSecurityCode()) + sign * lastTx.getQuantity();
        positionStateCache.updatePosition(lastTx.getSecurityCode(), newPosition);
        positionRepository.save(new Position(lastTx.getSecurityCode(), newPosition));

        positionStateCache.setLatestTrade(cancelTx.getTradeId(), cancelTx);
        transactionRepository.save(cancelTx);

        log.info("Processed CANCEL for trade {}: reversed {} {} {}. New position: {}={}",
                cancelTx.getTradeId(),
                lastTx.getDirection(), lastTx.getQuantity(), lastTx.getSecurityCode(),
                lastTx.getSecurityCode(), newPosition);
    }
}


