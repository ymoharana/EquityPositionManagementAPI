package com.trafigura.equityapi.cache;

import com.trafigura.equityapi.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PositionStateCacheTest {

    private PositionStateCache cache;

    private Transaction tx1, tx2, tx3;

    @BeforeEach
    void setUp() {
        cache = new PositionStateCache();
        tx1 = Transaction.builder()
                .transactionId(1L)
                .tradeId(1)
                .version(1)
                .securityCode("REL")
                .quantity(50)
                .action(Transaction.ActionType.INSERT)
                .direction(Transaction.Direction.Buy)
                .build();

        tx2 = Transaction.builder()
                .transactionId(2L)
                .tradeId(2)
                .version(1)
                .securityCode("ITC")
                .quantity(40)
                .action(Transaction.ActionType.INSERT)
                .direction(Transaction.Direction.Sell)
                .build();

        tx3 = Transaction.builder()
                .transactionId(3L)
                .tradeId(1)
                .version(2)
                .securityCode("REL")
                .quantity(60)
                .action(Transaction.ActionType.UPDATE)
                .direction(Transaction.Direction.Buy)
                .build();
    }

    @Test
    void testUpdateAndGetPosition() {
        cache.updatePosition("REL", 50);
        assertThat(cache.getPosition("REL")).isEqualTo(50);

        cache.updatePosition("REL", 100);
        assertThat(cache.getPosition("REL")).isEqualTo(100);

        assertThat(cache.getPosition("ITC")).isNull();
    }

    @Test
    void testGetAllPositions() {
        cache.updatePosition("REL", 50);
        cache.updatePosition("ITC", -40);

        Map<String, Integer> positions = cache.getAllPositions();
        assertThat(positions).hasSize(2)
                .containsEntry("REL", 50)
                .containsEntry("ITC", -40);
    }

    @Test
    void testSetAndGetLatestTrade() {
        cache.setLatestTrade(1, tx1);
        assertThat(cache.getLatestTrade(1)).isEqualTo(tx1);

        cache.setLatestTrade(2, tx2);
        assertThat(cache.getLatestTrade(2)).isEqualTo(tx2);

        assertThat(cache.getLatestTrade(3)).isNull();
    }

    @Test
    void testGetAllLatestTrades() {
        cache.setLatestTrade(1, tx1);
        cache.setLatestTrade(2, tx2);

        Map<Integer, Transaction> trades = cache.getAllLatestTrades();
        assertThat(trades).hasSize(2)
                .containsEntry(1, tx1)
                .containsEntry(2, tx2);
    }

    @Test
    void testQueueAndGetPendingTransactions() {
        cache.queuePendingTransaction(1, tx3);
        cache.queuePendingTransaction(1, tx1);
        cache.queuePendingTransaction(2, tx2);

        Map<Integer, List<Transaction>> pending = cache.getAllPendingTransactions();
        assertThat(pending).hasSize(2);
        assertThat(pending.get(1)).containsExactly(tx3, tx1);
        assertThat(pending.get(2)).containsExactly(tx2);

        // Remove and get
        List<Transaction> trade1Pending = cache.getAndRemovePendingTransactions(1);
        assertThat(trade1Pending).containsExactly(tx3, tx1);

        // Now pending should have only tradeId 2
        assertThat(cache.getAllPendingTransactions()).hasSize(1).containsKey(2);
    }

    @Test
    void testClearAll() {
        cache.updatePosition("REL", 50);
        cache.setLatestTrade(1, tx1);
        cache.queuePendingTransaction(1, tx3);

        cache.clearAll();

        assertThat(cache.getAllPositions()).isEmpty();
        assertThat(cache.getAllLatestTrades()).isEmpty();
        assertThat(cache.getAllPendingTransactions()).isEmpty();
    }
}
