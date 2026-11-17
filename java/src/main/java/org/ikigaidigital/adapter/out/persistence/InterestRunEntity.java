package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

// Mapped for schema validation and queries; rows are inserted only via InterestRunJpaRepository.insertIfAbsent.
@Entity
@Table(name = "interest_runs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class InterestRunEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "period", nullable = false, unique = true, length = 7)
    private String period;

    @Column(name = "executed_at", nullable = false)
    private Instant executedAt;
}
