package org.ikigaidigital.domain;

public class TimeDepositNotFoundException extends RuntimeException {

    public TimeDepositNotFoundException(int timeDepositId) {
        super("Time deposit " + timeDepositId + " not found");
    }
}
