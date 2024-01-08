package com.sid.demo.exceptions;

public class BankAccountNotFound extends Exception {
    public BankAccountNotFound(String message) {
        super(message);
    }
}
