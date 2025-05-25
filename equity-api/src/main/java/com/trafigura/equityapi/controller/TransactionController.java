package com.trafigura.equityapi.controller;

import com.trafigura.equityapi.dto.TransactionDto;
import com.trafigura.equityapi.service.TransactionProcessorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/equity")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionProcessorService transactionProcessorService;

    @PostMapping("/transactions")
    public ResponseEntity<TransactionDto> addTransaction(final @Valid @RequestBody TransactionDto transactionDto) {
        TransactionDto transaction = transactionProcessorService.manageTransaction(transactionDto);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions() {
        List<TransactionDto> transactionDtos = transactionProcessorService.fetchAllTransaction();
        return ResponseEntity.ok(transactionDtos);
    }

    @PostMapping("/transactions/batch")
    public ResponseEntity<List<TransactionDto>> addBulkTransaction(final @RequestBody @Valid List<TransactionDto> transactionDto) {
        List<TransactionDto> transactions = transactionProcessorService.manageBulkTransaction(transactionDto);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transactions/{tradeId}")
    public ResponseEntity<List<TransactionDto>> getTransactionByTradeId(@PathVariable final String tradeId ) {
        List<TransactionDto> transactions = transactionProcessorService.fetchAllTransactionByTradeId(Integer.valueOf(tradeId));
        return ResponseEntity.ok(transactions);
    }

}