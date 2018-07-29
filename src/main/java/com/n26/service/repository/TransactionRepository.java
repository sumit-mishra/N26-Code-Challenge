package com.n26.service.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.n26.exception.OutOfScopeTransaction;
import com.n26.exception.UnparsableData;
import com.n26.model.Transaction;
import com.n26.model.TransactionStatistics;
import com.n26.service.utils.StatisticsCalculator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionRepository {

    private static final String DELIMITER = ":";

    private static class Holder {
        private static final TransactionRepository INSTANCE = new TransactionRepository();
    }

    public static TransactionRepository getInstance() {
        return Holder.INSTANCE;
    }

    private Map<String, BigDecimal> recentTransactions = new ConcurrentHashMap<String, BigDecimal>();

    public void updateRepo(final Transaction transaction) throws OutOfScopeTransaction, UnparsableData {
        removeOldTransactions();
        validateTransaction(transaction);
        BigDecimal amount = new BigDecimal(transaction.getAmount());
        recentTransactions.putIfAbsent(UUID.randomUUID() + DELIMITER + transaction.getTimestamp(), amount);
    }

    private void validateTransaction(final Transaction transaction) throws UnparsableData, OutOfScopeTransaction {
        StatisticsCalculator calculator = StatisticsCalculator.getInstance();
        if (!isAmountNumeric(transaction.getAmount()) || !isValidDateFormat(transaction.getTimestamp())) {
            throw new UnparsableData("Transaction timestamp can not in acceptable format.");
        }
        if (calculator.isFutureTransaction(transaction.getTimestamp())) {
            throw new UnparsableData("Transaction timestamp is in future.");
        }
        if (!calculator.isTransactionInScope(transaction.getTimestamp())) {
            throw new OutOfScopeTransaction("Transaction is not recent, seems older than 60 seconds.");
        }
    }

    public void clear() {
        TransactionStatistics.getInstance().clear();
        recentTransactions.clear();
    }

    /**
     * this will remove transactions which are older than last 60 seconds.
     */
    public void removeOldTransactions() {
        for (String uuid : recentTransactions.keySet()) {
            String transactionTime = uuid.substring(uuid.indexOf(DELIMITER) + 1);
            if (!StatisticsCalculator.getInstance().isTransactionInScope(transactionTime)) {
                recentTransactions.remove(uuid);
            }
        }
    }

    private boolean isAmountNumeric(final String amount) {
        try {
            return new BigDecimal(amount).compareTo(BigDecimal.ZERO) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidDateFormat(final String date) {
        try {
            Instant.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmpty() {
        return recentTransactions.isEmpty();
    }

    public int size() {
        return recentTransactions.size();
    }

    public List<BigDecimal> allAmounts() {
        return recentTransactions.values().stream().collect(Collectors.toList());
    }
}
