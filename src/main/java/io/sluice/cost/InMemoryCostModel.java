package io.sluice.cost;

import java.util.Map;

public class InMemoryCostModel implements CostModel {

    private final Map<String, Long> endpointCosts;

    public InMemoryCostModel() {
        this.endpointCosts = Map.of(
                "/api/ping", 1L,
                "/api/users", 2L,
                "/api/reports", 10L
        );
    }

    @Override
    public long getCost(String endpoint) {
        if (endpoint == null) {
            return 1L;
        }
        return endpointCosts.getOrDefault(endpoint, 1L);
    }
}