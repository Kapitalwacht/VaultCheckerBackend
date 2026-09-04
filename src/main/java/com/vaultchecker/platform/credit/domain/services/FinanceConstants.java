package com.vaultchecker.platform.credit.domain.services;

import java.math.RoundingMode;

/**
 * Mandatory calculation conventions from the project brief (Anexo A):
 * <ul>
 *     <li>360-day year ({@link #DAY_COUNT_BASE}).</li>
 *     <li>30-day commercial month ({@link #COMMERCIAL_MONTH_DAYS}).</li>
 *     <li>Monetary amounts rounded to 2 decimals ({@link #MONEY_SCALE}).</li>
 *     <li>Interest rates carried with at least 7 decimals ({@link #RATE_SCALE}).</li>
 * </ul>
 */
public final class FinanceConstants {

    private FinanceConstants() {
    }

    /** Day-count base: a commercial year has 360 days. */
    public static final int DAY_COUNT_BASE = 360;

    /** A commercial month has 30 days. */
    public static final int COMMERCIAL_MONTH_DAYS = 30;

    /** Monetary scale: amounts are rounded to 2 decimals. */
    public static final int MONEY_SCALE = 2;

    /** Rate scale: rates keep at least 7 decimals. */
    public static final int RATE_SCALE = 7;

    /** Rounding mode applied to monetary amounts. */
    public static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    /** Rounding mode applied to rates. */
    public static final RoundingMode RATE_ROUNDING = RoundingMode.HALF_UP;
}
