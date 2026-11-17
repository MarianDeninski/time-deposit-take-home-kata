package org.ikigaidigital.application.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.in.TimeDepositWithWithdrawals;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.application.port.out.WithdrawalRepository;
import org.ikigaidigital.domain.Withdrawal;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllTimeDepositsServiceTest {

    @Mock
    private TimeDepositRepository timeDepositRepository;

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @InjectMocks
    private GetAllTimeDepositsService service;

    @Test
    void getAll_returnsEachDepositWithItsOwnWithdrawals_whenWithdrawalsExist() {
        TimeDeposit basic = new TimeDeposit(1, "basic", 1000.00, 31);
        TimeDeposit premium = new TimeDeposit(2, "premium", 10000.00, 46);
        Withdrawal basicWithdrawal = new Withdrawal(10, 1, new BigDecimal("100.00"), LocalDate.of(2026, 1, 15));
        Withdrawal premiumFirst = new Withdrawal(11, 2, new BigDecimal("500.00"), LocalDate.of(2026, 2, 1));
        Withdrawal premiumSecond = new Withdrawal(12, 2, new BigDecimal("200.00"), LocalDate.of(2026, 5, 20));
        when(timeDepositRepository.findAll()).thenReturn(List.of(basic, premium));
        when(withdrawalRepository.findAll()).thenReturn(List.of(premiumFirst, basicWithdrawal, premiumSecond));

        List<TimeDepositWithWithdrawals> result = service.getAll();

        assertThat(result).containsExactly(
                new TimeDepositWithWithdrawals(basic, List.of(basicWithdrawal)),
                new TimeDepositWithWithdrawals(premium, List.of(premiumFirst, premiumSecond)));
    }

    @Test
    void getAll_returnsEmptyWithdrawals_whenDepositHasNone() {
        TimeDeposit student = new TimeDeposit(1, "student", 1000.00, 15);
        when(timeDepositRepository.findAll()).thenReturn(List.of(student));
        when(withdrawalRepository.findAll()).thenReturn(List.of());

        List<TimeDepositWithWithdrawals> result = service.getAll();

        assertThat(result).singleElement()
                .extracting(TimeDepositWithWithdrawals::withdrawals)
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .isEmpty();
    }
}
