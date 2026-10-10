package io.sluice.admission;

import io.sluice.core.RequestContext;
import io.sluice.cost.CostModel;
import io.sluice.entitlement.EntitlementPolicy;

import java.util.concurrent.ConcurrentHashMap;

public class AdmissionController {

    private final EntitlementPolicy entitlementPolicy;
    private final CostModel costModel;
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public AdmissionController(EntitlementPolicy entitlementPolicy, CostModel costModel) {
        this.entitlementPolicy = entitlementPolicy;
        this.costModel = costModel;
    }

    public AdmissionDecision tryAcquire(RequestContext context) {
        long cost = costModel.getCost(context.endpoint());

        // If client dosent have backet,make a bucket
        TokenBucket bucket = buckets.computeIfAbsent(context.clientId(), id -> {
            long budget = entitlementPolicy.getBudget(context.tier());
            return new TokenBucket(budget);
        });

        if (bucket.tryConsume(cost)) {
            return AdmissionDecision.ADMIT;
        }

        return AdmissionDecision.REJECT;
    }
}