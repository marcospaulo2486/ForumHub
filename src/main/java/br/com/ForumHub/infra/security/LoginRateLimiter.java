package br.com.ForumHub.infra.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 60_000;

    private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String ip) {
        cleanup();
        AttemptInfo info = attempts.computeIfAbsent(ip, k -> new AttemptInfo());
        if (System.currentTimeMillis() - info.windowStart > WINDOW_MS) {
            info.count.set(0);
            info.windowStart = System.currentTimeMillis();
        }
        return info.count.get() >= MAX_ATTEMPTS;
    }

    public void registerAttempt(String ip) {
        cleanup();
        AttemptInfo info = attempts.computeIfAbsent(ip, k -> new AttemptInfo());
        if (System.currentTimeMillis() - info.windowStart > WINDOW_MS) {
            info.count.set(0);
            info.windowStart = System.currentTimeMillis();
        }
        info.count.incrementAndGet();
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        attempts.entrySet().removeIf(e -> now - e.getValue().windowStart > WINDOW_MS * 2);
    }

    private static class AttemptInfo {
        final AtomicInteger count = new AtomicInteger(0);
        volatile long windowStart = System.currentTimeMillis();
    }
}
