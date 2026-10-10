package io.sluice.entitlement;

import java.util.Map;

public class InMemoryEntitlementPolicy implements EntitlementPolicy {

    private final Map<String, Long> tierBudgets;

    public InMemoryEntitlementPolicy() {
        this.tierBudgets = Map.of(
                "free", 100L,
                "pro", 1000L,
                "enterprise", 10000L
        );
    }

    @Override
    public long getBudget(String tier) {
        if (tier == null) {
            return 0L;
        }
        return tierBudgets.getOrDefault(tier.toLowerCase(), 0L);
    }
}