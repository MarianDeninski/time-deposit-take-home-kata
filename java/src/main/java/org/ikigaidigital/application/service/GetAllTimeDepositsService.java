package org.ikigaidigital.application.service;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.TimeDepositWithWithdrawals;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.application.port.out.WithdrawalRepository;
import org.ikigaidigital.domain.Withdrawal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GetAllTimeDepositsService implements GetAllTimeDepositsUseCase {

    private final TimeDepositRepository timeDepositRepository;
    private final WithdrawalRepository withdrawalRepository;

    // Two queries in total, grouped in memory, to avoid one withdrawal query per deposit (N+1).
    @Override
    public List<TimeDepositWithWithdrawals> getAll() {
        Map<Integer, List<Withdrawal>> withdrawalsByDepositId = withdrawalRepository.findAll().stream()
                .collect(Collectors.groupingBy(Withdrawal::timeDepositId));
        return timeDepositRepository.findAll().stream()
                .map(deposit -> withWithdrawals(deposit, withdrawalsByDepositId))
                .toList();
    }

    private TimeDepositWithWithdrawals withWithdrawals(TimeDeposit deposit, Map<Integer, List<Withdrawal>> withdrawalsByDepositId) {
        List<Withdrawal> withdrawals = withdrawalsByDepositId.getOrDefault(deposit.getId(), List.of());
        return new TimeDepositWithWithdrawals(deposit, withdrawals);
    }
}
