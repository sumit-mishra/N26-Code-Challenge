package com.n26;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.n26.exception.OutOfScopeTransaction;
import com.n26.exception.UnparsableData;
import com.n26.model.Transaction;
import com.n26.model.TransactionStatistics;
import com.n26.service.StatisticsService;
import com.n26.service.TransactionService;
import com.n26.service.repository.TransactionRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class StatisticsServiceTest {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private StatisticsService statisticsService;

    @Test
    public void countAndSumMustBeZeroWhenRepoIsEmpty() {
        TransactionRepository.getInstance().clear();
        TransactionStatistics statistics = TransactionStatistics.getInstance();

        assertTrue(statistics.getSum() == BigDecimal.ZERO);
        assertTrue(statistics.getCount() == 0);
    }

    @Test
    public void serviceShouldPrepareStatistics() {
        TransactionRepository.getInstance().clear();
        String timestamp = String.valueOf(Instant.now());
        int[] sum = new int[] {0};
        IntStream.range(1, 100).forEach(i -> {
            Transaction transaction = new Transaction(String.valueOf(i), timestamp);
            sum[0] += i;
            try {
                transactionService.createTransaction(transaction);
            } catch (UnparsableData | OutOfScopeTransaction e) {
            }
        });

        TransactionStatistics statistics = statisticsService.prepare();

        assertTrue(statistics.getSum().compareTo(BigDecimal.valueOf(sum[0])) == 0);
        assertTrue(statistics.getCount() == 99);
        assertTrue(statistics.getMin().compareTo(BigDecimal.ONE) == 0);
        assertTrue(statistics.getMax().compareTo(BigDecimal.valueOf(99)) == 0);
    }
}
