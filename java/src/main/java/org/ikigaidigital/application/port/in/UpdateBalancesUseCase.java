package org.ikigaidigital.application.port.in;

public interface UpdateBalancesUseCase {

    // Applies one month of interest to all time deposits, at most once per calendar month.
    void updateBalances();
}
