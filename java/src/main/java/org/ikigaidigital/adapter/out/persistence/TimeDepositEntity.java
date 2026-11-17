package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "time_deposits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TimeDepositEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "plan_type", nullable = false)
    private String planType;

    @Column(name = "days", nullable = false)
    private int days;

    // Only the balance changes after creation.
    @Setter
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Builder
    private TimeDepositEntity(Integer id, String planType, int days, BigDecimal balance) {
        this.id = id;
        this.planType = planType;
        this.days = days;
        this.balance = balance;
    }
}
