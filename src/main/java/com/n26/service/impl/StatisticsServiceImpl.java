package com.n26.service.impl;

import org.springframework.stereotype.Service;

import com.n26.model.TransactionStatistics;
import com.n26.service.StatisticsService;
import com.n26.service.repository.TransactionRepository;
import com.n26.service.utils.StatisticsCalculator;


@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Override
    public TransactionStatistics prepare() {
        TransactionRepository.getInstance().removeOldTransactions();
        return StatisticsCalculator.getInstance().updateStatisticsInstance();
    }
}
