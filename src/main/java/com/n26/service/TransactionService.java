package com.n26.service;

import com.n26.exception.OutOfScopeTransaction;
import com.n26.exception.UnparsableData;
import com.n26.model.Transaction;


public interface TransactionService {

    void createTransaction(final Transaction transaction) throws OutOfScopeTransaction, UnparsableData;

    void clearTransaction();
}
