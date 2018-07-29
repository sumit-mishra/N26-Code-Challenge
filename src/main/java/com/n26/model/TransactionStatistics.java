package com.n26.model;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionStatistics {

    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal avg = BigDecimal.ZERO;;
    private BigDecimal max = BigDecimal.ZERO;;
    private BigDecimal min = BigDecimal.ZERO;;
    private Long count = 0L;

    private static class Holder {
        private static final TransactionStatistics INSTANCE = new TransactionStatistics();
    }

    public static TransactionStatistics getInstance() {
        return Holder.INSTANCE;
    }

    public void clear() {
        this.sum = BigDecimal.ZERO;
        this.avg = BigDecimal.ZERO;
        this.max = BigDecimal.ZERO;
        this.min = BigDecimal.ZERO;
        this.count = 0L;
    }

    @Override
    public String toString() {
        return "{" + "\"sum\": \"" + getSum() + "\" " + ",\"avg\": \"" + getAvg() + "\" " + ",\"max\": \"" + getMax() + "\" " + ",\"min\": \""
                        + getMin() + "\" " + ",\"count\":" + getCount() + "}";
    }

}
