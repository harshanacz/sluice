package io.sluice.cost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryCostModelTest {

    @Test
    void shouldReturnCorrectCostForConfiguredEndpoints() {
        CostModel model = new InMemoryCostModel();

        assertEquals(1L, model.getCost("/api/ping"));
        assertEquals(2L, model.getCost("/api/users"));
        assertEquals(10L, model.getCost("/api/reports"));
    }

    @Test
    void shouldReturnDefaultCostForUnknownOrNullEndpoint() {
        CostModel model = new InMemoryCostModel();

        assertEquals(1L, model.getCost("/api/other"));
        assertEquals(1L, model.getCost(null));
    }
}