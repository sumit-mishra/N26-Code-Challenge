package com.n26.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.n26.exception.OutOfScopeTransaction;
import com.n26.exception.UnparsableData;
import com.n26.model.Transaction;
import com.n26.service.TransactionService;


@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    private static final HttpStatus SUCCESS = CREATED;
    private static final HttpStatus OUT_OF_SCOPE = HttpStatus.NO_CONTENT;
    private static final HttpStatus UNPARSABLE = HttpStatus.UNPROCESSABLE_ENTITY;

    @ExceptionHandler({HttpMessageNotReadableException.class})
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
                    WebRequest request) {
        return new ResponseEntity<>(UNPARSABLE);
    }

    @ResponseStatus(CREATED)
    @PostMapping(value = "/transactions", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTransaction(@Valid @RequestBody Transaction transaction) {
        try {
            transactionService.createTransaction(transaction);
            return new ResponseEntity<>(SUCCESS);
        } catch (OutOfScopeTransaction exception) {
            return new ResponseEntity<>(OUT_OF_SCOPE);
        } catch (UnparsableData exception) {
            return new ResponseEntity<>(UNPARSABLE);
        }
    }

    @DeleteMapping(value = "/transactions")
    public ResponseEntity<Void> delteTransactions() {
        transactionService.clearTransaction();
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
