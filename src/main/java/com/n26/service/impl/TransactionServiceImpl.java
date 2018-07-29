package com.n26.service.impl;

import org.springframework.stereotype.Service;

import com.n26.exception.OutOfScopeTransaction;
import com.n26.exception.UnparsableData;
import com.n26.model.Transaction;
import com.n26.service.TransactionService;
import com.n26.service.repository.TransactionRepository;
import com.n26.service.utils.StatisticsCalculator;


@Service
public class TransactionServiceImpl implements TransactionService {

    @Override
    public void createTransaction(final Transaction transaction) throws OutOfScopeTransaction, UnparsableData {
        StatisticsCalculator.getInstance().updateStatistics(transaction);
    }

    @Override
    public void clearTransaction() {
        TransactionRepository.getInstance().clear();
    }
}
