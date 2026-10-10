package io.sluice.core;

public record RequestContext(
        String clientId,
        String tier,
        String endpoint,
) {}
