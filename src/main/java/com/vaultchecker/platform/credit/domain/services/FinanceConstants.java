package com.vaultchecker.platform.credit.domain.services;

import java.math.RoundingMode;

public final class FinanceConstants {

    private FinanceConstants() {
    }

    public static final int DAY_COUNT_BASE = 360;

    public static final int COMMERCIAL_MONTH_DAYS = 30;

    public static final int MONEY_SCALE = 2;

    public static final int RATE_SCALE = 7;

    public static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    public static final RoundingMode RATE_ROUNDING = RoundingMode.HALF_UP;
}
