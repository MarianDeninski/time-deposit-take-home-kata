package org.ikigaidigital.config;

import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.application.port.out.WithdrawalRepository;
import org.ikigaidigital.application.service.GetAllTimeDepositsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Wires application services as beans so the services themselves stay free of Spring annotations.
@Configuration
public class ApplicationConfig {

    @Bean
    GetAllTimeDepositsUseCase getAllTimeDepositsUseCase(TimeDepositRepository timeDepositRepository,
                                                        WithdrawalRepository withdrawalRepository) {
        return new GetAllTimeDepositsService(timeDepositRepository, withdrawalRepository);
    }
}
