package org.ikigaidigital.config;

import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.application.port.out.InterestRunRepository;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.application.port.out.WithdrawalRepository;
import org.ikigaidigital.application.service.GetAllTimeDepositsService;
import org.ikigaidigital.application.service.UpdateBalancesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;

// Wires application services as beans so the services themselves stay free of Spring annotations.
@Configuration
public class ApplicationConfig {

    @Bean
    GetAllTimeDepositsUseCase getAllTimeDepositsUseCase(TimeDepositRepository timeDepositRepository,
                                                        WithdrawalRepository withdrawalRepository) {
        return new GetAllTimeDepositsService(timeDepositRepository, withdrawalRepository);
    }

    // UTC so the interest period (calendar month) does not depend on the server's time zone.
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    TimeDepositCalculator timeDepositCalculator() {
        return new TimeDepositCalculator();
    }

    @Bean
    UpdateBalancesUseCase updateBalancesUseCase(TimeDepositRepository timeDepositRepository,
                                                InterestRunRepository interestRunRepository,
                                                TimeDepositCalculator timeDepositCalculator,
                                                Clock clock,
                                                TransactionTemplate transactionTemplate) {
        UpdateBalancesService service = new UpdateBalancesService(
                timeDepositRepository, interestRunRepository, timeDepositCalculator, clock);
        return new TransactionalUpdateBalancesUseCase(service, transactionTemplate);
    }
}
