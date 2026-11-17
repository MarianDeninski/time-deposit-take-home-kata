package org.ikigaidigital.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.application.port.out.InterestRunRepository;
import org.ikigaidigital.application.port.out.TimeDepositRepository;

import java.time.Clock;
import java.time.YearMonth;
import java.util.List;

/**
 * Must run inside one transaction (see config) so the recorded period and the new balances
 * commit or roll back together.
 */
@Slf4j
@RequiredArgsConstructor
public class UpdateBalancesService implements UpdateBalancesUseCase {

    private final TimeDepositRepository timeDepositRepository;
    private final InterestRunRepository interestRunRepository;
    private final TimeDepositCalculator timeDepositCalculator;
    private final Clock clock;

    @Override
    public void updateBalances() {
        YearMonth period = YearMonth.now(clock);
        // Idempotency check. Assumption: interest is applied once per calendar month; repeated calls
        // within the same month are no-ops to prevent double crediting (retries, double clicks,
        // a scheduler firing twice).
        if (!interestRunRepository.recordIfAbsent(period, clock.instant())) {
            log.info("Interest for period {} already applied, skipping", period);
            return;
        }
        List<TimeDeposit> timeDeposits = timeDepositRepository.findAll();
        timeDepositCalculator.updateBalance(timeDeposits);
        timeDepositRepository.saveAll(timeDeposits);
        log.info("Applied interest for period {} to {} time deposits", period, timeDeposits.size());
    }
}
