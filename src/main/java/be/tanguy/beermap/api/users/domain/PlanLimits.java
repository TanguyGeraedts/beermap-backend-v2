package be.tanguy.beermap.api.users.domain;

import be.tanguy.beermap.api.logs.exception.DraftLimitExceededException;

public final class PlanLimits {
    private PlanLimits() {}

    /// ───────────── Draft Logs ─────────────
    public static int maxActiveDrafts(AccountType type) {
        return switch (type) {
            case PREMIUM, ADMIN -> 50;
            default -> 10;
        };
    }

    public static void verifyActiveDraftLimit(AccountType type, long currentCount) {
        int limit = maxActiveDrafts(type);
        if (currentCount >= limit) {
            throw new DraftLimitExceededException(limit);
        }
    }
}

