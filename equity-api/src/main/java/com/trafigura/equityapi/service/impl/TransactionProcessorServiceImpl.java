package com.trafigura.equityapi.service.impl;

import com.trafigura.equityapi.cache.PositionStateCache;
import com.trafigura.equityapi.dto.TransactionDto;
import com.trafigura.equityapi.factory.TransactionStrategyFactory;
import com.trafigura.equityapi.model.Position;
import com.trafigura.equityapi.model.Transaction;
import com.trafigura.equityapi.repository.PositionRepository;
import com.trafigura.equityapi.repository.TransactionRepository;
import com.trafigura.equityapi.service.TransactionProcessorService;
import com.trafigura.equityapi.strategy.TransactionStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProcessorServiceImpl implements TransactionProcessorService {

    private final TransactionRepository transactionRepository;
    private final TransactionStrategyFactory strategyFactory;
    private final PositionStateCache positionStateCache;
    private final PositionRepository positionRepository;

    @Override
    @Transactional
    public TransactionDto manageTransaction(TransactionDto transactionDto) {

        int tradeId = transactionDto.getTradeId();
        Transaction tx = transactionDto.toEntity();

        // If INSERT (version 1), process immediately
        if (transactionDto.getVersion() == 1 && transactionDto.getAction() == Transaction.ActionType.INSERT) {
            TransactionStrategy strategy = strategyFactory.getStrategy(transactionDto.getAction());
            strategy.processTransaction(transactionDto.toEntity());
            // Process any queued transactions for this tradeId
            processQueuedTransactions(tradeId);
        } else {
            // For UPDATE/CANCEL, check if INSERT has already been processed
            if (positionStateCache.getLatestTrade(tradeId) != null) {
                TransactionStrategy strategy = strategyFactory.getStrategy(transactionDto.getAction());
                strategy.processTransaction(transactionDto.toEntity());
            } else {
                // INSERT not yet seen, queue this transaction
                positionStateCache.queuePendingTransaction(tradeId, transactionDto.toEntity());
                log.info("Queued {} for TradeID {} until INSERT arrives.", tx.getAction(), tradeId);
            }
        }
        return transactionDto;
    }

    @Override
    @Transactional
    public List<TransactionDto> manageBulkTransaction(List<TransactionDto> transactionDto) {
        return transactionDto.stream().map(this::manageTransaction).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TransactionDto> fetchAllTransaction() {
        return transactionRepository.findAll().stream().map(Transaction::toTransactionDto).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> fetchAllTransactionByTradeId(Integer tradeId) {
        return transactionRepository.findAllByTradeId(tradeId).stream().map(Transaction::toTransactionDto).collect(Collectors.toList());
    }

    private void processQueuedTransactions(int tradeId) {
        List<Transaction> queue = positionStateCache.getAndRemovePendingTransactions(tradeId);
        if (queue != null) {
            queue.sort(Comparator.comparing(Transaction::getVersion));
            for (Transaction tx : queue) {
                strategyFactory.getStrategy(tx.getAction()).processTransaction(tx);
                positionStateCache.setLatestTrade(tradeId, tx);
            }
        }
    }

    @PostConstruct
    @Transactional
    public void preloadCache() {
        log.info("Preloading position and trade cache at application startup...");

        List<Position> persistedPositions = positionRepository.findAll();
        for (Position p : persistedPositions) {
            positionStateCache.updatePosition(p.getSecurityCode(), p.getQuantity());
        }
        log.info("Loaded {} positions into cache.", positionStateCache.getTotalPositionSize());

        List<Transaction> trades = transactionRepository.findLatestTransactionsByTradeId();
        for (Transaction tx : trades) {
            positionStateCache.setLatestTrade(tx.getTradeId(), tx);
        }
        log.info("Loaded {} trades into latestTrades cache.", positionStateCache.getLatestTradeSize());
    }
}

