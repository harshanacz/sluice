package io.sluice.admission;

public class TokenBucket {

    private final long capacity;
    private long availableTokens;

    public TokenBucket(long capacity) {
        this.capacity = capacity;
        this.availableTokens = capacity;
    }

    public boolean tryConsume(long tokens) {
        if (tokens <= 0) {
            return true;
        }

        if (availableTokens >= tokens) {
            availableTokens -= tokens;
            return true;
        }

        return false;
    }

    public long getAvailableTokens() {
        return availableTokens;
    }

    public long getCapacity() {
        return capacity;
    }
}