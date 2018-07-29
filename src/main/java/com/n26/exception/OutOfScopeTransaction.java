package com.n26.exception;

public class OutOfScopeTransaction extends Exception {

    private static final long serialVersionUID = -8473840251882185521L;

    public OutOfScopeTransaction(String msg) {
        super(msg);
    }

}
