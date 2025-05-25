package com.trafigura.equityapi.repository;

import com.trafigura.equityapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findTopByTradeIdOrderByVersionDesc(Integer tradeId);
    List<Transaction> findByTradeId(Integer tradeId);

    @Query(value = """
    SELECT * FROM transaction t
    WHERE (t.trade_id, t.version) IN (
        SELECT trade_id, MAX(version)
        FROM transaction
        GROUP BY trade_id
    )
    """, nativeQuery = true)
    List<Transaction> findLatestTransactionsByTradeId();

    List<Transaction> findAllByTradeId(Integer tradeId);
}
