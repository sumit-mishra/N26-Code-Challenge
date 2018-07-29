package com.n26.service.utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import com.n26.exception.OutOfScopeTransaction;
import com.n26.exception.UnparsableData;
import com.n26.model.Transaction;
import com.n26.model.TransactionStatistics;
import com.n26.service.repository.TransactionRepository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticsCalculator {

    private TransactionStatistics statistics = TransactionStatistics.getInstance();
    private TransactionRepository repository = TransactionRepository.getInstance();

    private static class Holder {
        private static final StatisticsCalculator INSTANCE = new StatisticsCalculator();
    }

    public static StatisticsCalculator getInstance() {
        return Holder.INSTANCE;
    }

    public void updateStatistics(final Transaction transaction) throws OutOfScopeTransaction, UnparsableData {
        repository.updateRepo(transaction);
        updateStatisticsInstance();
    }

    public TransactionStatistics updateStatisticsInstance() {
        if (repository.isEmpty()) {
            statistics.clear();
            return statistics;
        }
        updateCount();
        updateSum();
        calculateAverage(statistics.getSum(), statistics.getCount());
        updateMax();
        updateMin();
        return statistics;
    }

    private void updateCount() {
        statistics.setCount(new Long(repository.size()));
    }

    private void updateSum() {
        BigDecimal totalSum = BigDecimal.ZERO;
        for (BigDecimal amount : repository.allAmounts()) {
            totalSum = totalSum.add(amount);
        }
        statistics.setSum(totalSum);
    }

    private void calculateAverage(final BigDecimal sum, final Long count) {
        statistics.setAvg(sum.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP));
    }

    private void updateMax() {
        BigDecimal maxValInRepo = Collections.max(repository.allAmounts());
        if (maxValInRepo.compareTo(statistics.getMax()) == 1 || !repository.allAmounts().contains(statistics.getMax())) {
            statistics.setMax(maxValInRepo);
        }
    }

    private void updateMin() {
        BigDecimal minValInRepo = Collections.min(repository.allAmounts());
        if (statistics.getMin() == BigDecimal.ZERO || minValInRepo.compareTo(statistics.getMin()) == -1
                        || !repository.allAmounts().contains(statistics.getMin())) {
            statistics.setMin(minValInRepo);
        }
    }

    public boolean isTransactionInScope(final String transactionTimestamp) {
        try {
            Instant transactionInstant = Instant.parse(transactionTimestamp);
            Instant cuurentTimeInstant = Instant.now().minusSeconds(60);
            Duration duration = Duration.between(transactionInstant, cuurentTimeInstant);
            return duration.getSeconds() >= -60 && duration.getSeconds() < 0 ? true : false;
        } catch (Exception ex) {
            return false;
        }

    }

    public boolean isFutureTransaction(final String transactionTime) {
        try {
            Instant transactionInstant = Instant.parse(transactionTime);
            return transactionInstant.isAfter(Instant.now());
        } catch (Exception ex) {
            return false;
        }
    }

}
