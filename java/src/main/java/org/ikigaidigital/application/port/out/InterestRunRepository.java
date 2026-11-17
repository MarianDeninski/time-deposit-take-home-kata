package org.ikigaidigital.application.port.out;

import java.time.Instant;
import java.time.YearMonth;

public interface InterestRunRepository {

    /**
     * Records the period atomically, relying on the unique constraint instead of select-then-insert.
     *
     * @return true if the period was recorded now, false if it had already been recorded
     */
    boolean recordIfAbsent(YearMonth period, Instant executedAt);
}
