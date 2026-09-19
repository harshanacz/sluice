# sluice

**Health-aware admission control for Java services.**

Not a rate limiter, an admission gate. Instead of asking *"how many requests may this client send?"*, sluice asks: given who's calling, how expensive this request is, and how much capacity the backend actually has right now, **should this request be admitted?**

> 🚧 Work in progress. See [Status](#status) for what's actually built vs. planned.

---

## Why this exists

Rate limiting, cost-weighted quotas, and health-adaptive concurrency all exist as separate, mature ideas in the Java ecosystem : [Bucket4j](https://github.com/bucket4j/bucket4j) covers tiered/weighted quotas, [Netflix's concurrency-limits](https://github.com/Netflix/concurrency-limits) covers latency-adaptive concurrency. But no small, embeddable Java library composes **tenant entitlement + request cost + live backend health** into one admission decision. That's the gap sluice fills.

Equivalent early efforts already exist in other ecosystems : [failsafe-go](https://failsafe-go.dev/adaptive-limiter/) (Go), a [TypeScript port](https://github.com/joinava/adaptive-concurrency) of Netflix's library (Node) — but not yet in Java, despite Java having all the underlying building blocks.

## How it works
          ┌─────────────────────┐
          │  EntitlementPolicy   │  tier → base budget
          └──────────┬───────────┘
                     │
          ┌──────────▼───────────┐
          │      CostModel        │  endpoint → relative weight
          └──────────┬───────────┘
                     │
          ┌──────────▼───────────┐
          │    HealthTracker      │  rolling window of backend
          │                       │  latency/error signals
          └──────────┬───────────┘
                     │
          ┌──────────▼───────────┐
          │  AdaptiveController   │  derives current capacity,
          │                       │  bounded gradual recovery
          └──────────┬───────────┘
                     │
          ┌──────────▼───────────┐
          │  AdmissionController  │  decide(RequestContext)
          └───────────────────────┘



Each layer is independently pluggable — swap `EntitlementPolicy` or `CostModel` without touching anything else.

## Usage (planned API)

```java
AdmissionController controller = AdmissionController.builder()
    .entitlementPolicy(new TieredEntitlementPolicy())
    .costModel(new StaticCostModel())
    .adaptiveController(new AimdAdaptiveController())
    .build();

Decision decision = controller.decide(
    RequestContext.builder()
        .clientId("acme-corp")
        .tier(Tier.PRO)
        .endpoint("POST /reports")
        .estimatedCost(10)
        .build()
);

if (decision.isAdmitted()) {
    // proceed
    controller.record(Outcome.success(Duration.ofMillis(132)));
} else {
    // reject, decision.reason() explains why
}
```

## Status

- [x] `RequestContext` + `EntitlementPolicy` (tiered budgets)
- [x] `CostModel` (per-endpoint weighting)
- [x] Concurrent multi-client state (`ConcurrentHashMap`-backed)
- [x] Reproduced and fixed a real race condition on shared counter state (see `docs/race-condition.md` — kept both the broken and fixed versions in history deliberately)
- [ ] `HealthTracker` — hand-rolled rolling window with error classification
- [ ] `AdaptiveController` — AIMD-style bounded recovery
- [ ] Demo harness (composed backend calls + artificial-latency toggle)
- [ ] Benchmark: static vs. adaptive under a simulated load spike
- [ ] Distributed/multi-instance design doc (not planned for implementation)

## Design notes

- Health tracking uses a rolling window/EWMA, not a plain average — a raw average conflates "backend is unhealthy" with "traffic mix got more expensive."
- Only timeouts, connection failures, and 5xx count against backend health. Client-caused 4xx does not.
- Recovery is bounded and additive, never an instant snap-back, to avoid oscillation.
- Scoped to a single JVM instance. Multi-instance coordination is a documented design extension, not implemented.

## Comparison

| | Tier/identity | Request cost | Backend-health adaptive |
|---|---|---|---|
| Bucket4j | ✅ | ✅ | ❌ |
| Netflix concurrency-limits | Partial | ❌ | ✅ |
| **sluice** | ✅ | ✅ | ✅ (planned) |

## Why Java

The core problem — mutable rate/health state under real concurrent access — is a shared-memory concurrency problem, which Java's `java.util.concurrent` primitives model directly. Java also has the underlying building blocks (Bucket4j, resilience4j, Netflix's own library) without anyone having packaged them into one clean abstraction — a gap other ecosystems are starting to close.

## License

MIT
