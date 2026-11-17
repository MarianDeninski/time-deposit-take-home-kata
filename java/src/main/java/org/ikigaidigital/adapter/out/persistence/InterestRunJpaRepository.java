package org.ikigaidigital.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

interface InterestRunJpaRepository extends JpaRepository<InterestRunEntity, Long> {

    // ON CONFLICT lets the unique constraint decide atomically, also for concurrent calls, without
    // raising an error that would abort the surrounding PostgreSQL transaction.
    @Modifying
    @Query(value = """
            INSERT INTO interest_runs (period, executed_at)
            VALUES (:period, :executedAt)
            ON CONFLICT (period) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(@Param("period") String period, @Param("executedAt") Instant executedAt);
}
