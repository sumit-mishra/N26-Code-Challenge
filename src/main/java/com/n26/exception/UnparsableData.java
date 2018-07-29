package com.n26.exception;

public class UnparsableData extends Exception {

    private static final long serialVersionUID = 3898533195956573199L;

    public UnparsableData(String msg) {
        super(msg);
    }
}
