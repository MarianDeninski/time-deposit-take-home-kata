package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "withdrawals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class WithdrawalEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    // Plain column instead of @ManyToOne: only the id is needed and no lazy loading is involved.
    @Column(name = "time_deposit_id", nullable = false)
    private int timeDepositId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Builder
    private WithdrawalEntity(Integer id, int timeDepositId, BigDecimal amount, LocalDate date) {
        this.id = id;
        this.timeDepositId = timeDepositId;
        this.amount = amount;
        this.date = date;
    }
}
