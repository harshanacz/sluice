package io.sluice.admission;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenBucketTest {

    @Test
    void shouldConsumeTokensWhenBudgetAvailable() {
        TokenBucket bucket = new TokenBucket(100L);

        assertTrue(bucket.tryConsume(30L));
        assertEquals(70L, bucket.getAvailableTokens());
    }

    @Test
    void shouldRejectWhenBudgetExhausted() {
        TokenBucket bucket = new TokenBucket(10L);

        assertTrue(bucket.tryConsume(10L));
        assertEquals(0L, bucket.getAvailableTokens());

        // Tokens ඉවර නිසා reject (false) වෙන්න ඕනේ
        assertFalse(bucket.tryConsume(1L));
    }
}