package com.sid.demo.exceptions;

public class BalanceNotSufficient extends Exception {
    public BalanceNotSufficient(String message) {
        super(message);
    }
}
