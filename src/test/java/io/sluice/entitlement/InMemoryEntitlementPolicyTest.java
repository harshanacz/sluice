package io.sluice.entitlement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryEntitlementPolicyTest {

    @Test
    void shouldReturnCorrectBudgetForKnownTiers() {
        EntitlementPolicy policy = new InMemoryEntitlementPolicy();

        assertEquals(100L, policy.getBudget("free"));
        assertEquals(1000L, policy.getBudget("pro"));
        assertEquals(10000L, policy.getBudget("enterprise"));
    }

    @Test
    void shouldReturnZeroForUnknownOrNullTier() {
        EntitlementPolicy policy = new InMemoryEntitlementPolicy();

        assertEquals(0L, policy.getBudget("unknown"));
        assertEquals(0L, policy.getBudget(null));
    }
}