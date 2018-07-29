package com.n26;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.n26.exception.OutOfScopeTransaction;
import com.n26.exception.UnparsableData;
import com.n26.model.Transaction;
import com.n26.service.repository.TransactionRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TransactionRepositoryTest {

    private TransactionRepository repository;

    @Before
    public void before() {
        repository = TransactionRepository.getInstance();
    }

    @Test
    public void repositorySizeIsEmptyIfInvalidTransation() {
        repository.clear();
        Transaction transaction = new Transaction("66.6", "2017-07-29T15:35:45.119Z");
        try {
            repository.updateRepo(transaction);
        } catch (OutOfScopeTransaction | UnparsableData ex) {
        }
        assertEquals(0, repository.size());
    }

    @Test(expected = UnparsableData.class)
    public void exceptionWhenInvalidTransactionDone() throws OutOfScopeTransaction, UnparsableData {
        Transaction transaction = new Transaction("55.55", "2019-07-29T15:35:45.119Z");
        repository.updateRepo(transaction);
    }

    @Test
    public void verifyAllConcurrentCallsCreatedTrasactionSuccessfully() {
        repository.clear();
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            IntStream.range(0, 2).forEach(i -> {
                executor.execute(() -> {
                    Transaction transaction = new Transaction("11", String.valueOf(Instant.now()));
                    try {
                        /**
                         * adding delay to make it feel real
                         */
                        Thread.sleep(1);
                        repository.updateRepo(transaction);
                    } catch (OutOfScopeTransaction | UnparsableData | InterruptedException exception) {
                        System.out.println(exception);
                    }
                });
            });
        } finally {
            executor.shutdown();
        }

        try {
            /**
             * making thread sleep for 2sec to complete all execution
             */
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        assertEquals(2, repository.size());
    }
}
