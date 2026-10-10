package io.sluice.cost;

public interface CostModel {
    long getCost(String endpoint);
}