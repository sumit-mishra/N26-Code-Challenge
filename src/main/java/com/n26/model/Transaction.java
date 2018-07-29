package com.n26.model;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Transaction {
    @NotNull(message = "Amount must not be null.")
    private String amount;

    @NotNull(message = "Timestamp must not be null.")
    private String timestamp;

    public Transaction(String amount, String timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
