package io.sluice.entitlement;

public interface EntitlementPolicy {
    long getBudget(String tier);
}
