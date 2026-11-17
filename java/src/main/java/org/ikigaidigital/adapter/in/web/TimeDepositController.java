package org.ikigaidigital.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.adapter.in.web.api.TimeDepositsApi;
import org.ikigaidigital.adapter.in.web.model.TimeDepositDto;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
class TimeDepositController implements TimeDepositsApi {

    private final GetAllTimeDepositsUseCase getAllTimeDepositsUseCase;
    private final TimeDepositWebMapper mapper;

    @Override
    public ResponseEntity<List<TimeDepositDto>> getAllTimeDeposits() {
        List<TimeDepositDto> timeDeposits = getAllTimeDepositsUseCase.getAll().stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(timeDeposits);
    }
}
