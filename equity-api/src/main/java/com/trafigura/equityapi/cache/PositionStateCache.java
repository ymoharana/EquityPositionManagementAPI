package com.trafigura.equityapi.cache;

import com.trafigura.equityapi.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PositionStateCache {
    private final Map<String, Integer> positions = new ConcurrentHashMap<>();
    private final Map<Integer, Transaction> latestTrades = new ConcurrentHashMap<>();
    private final Map<Integer, List<Transaction>> pendingTransactions = new ConcurrentHashMap<>();

    // --- Position methods ---
    public Integer getPosition(String securityCode) { return positions.getOrDefault(securityCode, 0); }
    public void updatePosition(String securityCode, int quantity) { positions.put(securityCode, quantity); }
    public Map<String, Integer> getAllPositions() { return Collections.unmodifiableMap(positions); }
    public int getTotalPositionSize() { return positions.size(); }

    // --- Latest trade methods ---
    public Transaction getLatestTrade(Integer tradeId) { return latestTrades.get(tradeId); }
    public void setLatestTrade(Integer tradeId, Transaction tx) { latestTrades.put(tradeId, tx); }
    public Map<Integer, Transaction> getAllLatestTrades() { return Collections.unmodifiableMap(latestTrades); }
    public int getLatestTradeSize() { return latestTrades.size(); }

    // --- Pending transactions methods ---
    public void queuePendingTransaction(Integer tradeId, Transaction tx) {
        pendingTransactions.computeIfAbsent(tradeId, k -> new ArrayList<>()).add(tx);
    }
    public List<Transaction> getAndRemovePendingTransactions(Integer tradeId) {
        return pendingTransactions.remove(tradeId);
    }
    public Map<Integer, List<Transaction>> getAllPendingTransactions() { return Collections.unmodifiableMap(pendingTransactions); }

    public void clearAll() {
        positions.clear();
        latestTrades.clear();
        pendingTransactions.clear();
    }
}
