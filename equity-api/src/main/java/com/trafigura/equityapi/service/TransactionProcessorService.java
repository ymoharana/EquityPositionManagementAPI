package com.trafigura.equityapi.service;

import com.trafigura.equityapi.dto.TransactionDto;
import com.trafigura.equityapi.model.Transaction;

import java.util.List;

public interface TransactionProcessorService {
     TransactionDto manageTransaction(TransactionDto transactionDto);

     List<TransactionDto> manageBulkTransaction(List<TransactionDto> transactionDto);
     List<TransactionDto> fetchAllTransaction();
     List<TransactionDto> fetchAllTransactionByTradeId(Integer tradeId);
}
