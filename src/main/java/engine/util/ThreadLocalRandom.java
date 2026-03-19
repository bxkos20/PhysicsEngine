package engine.util;

import java.util.Random;

/**
 * Thread-local random number generator for better performance in multi-threaded environments.
 * Each thread gets its own Random instance, avoiding contention.
 */
public class ThreadLocalRandom {
    private static final ThreadLocal<Random> random = ThreadLocal.withInitial(() -> new Random());
    
    /**
     * Get the Random instance for current thread
     */
    public static Random get() {
        return random.get();
    }
    
    /**
     * Generate a random float between 0.0 (inclusive) and 1.0 (exclusive)
     */
    public static float nextFloat() {
        return get().nextFloat();
    }
    
    /**
     * Generate a random float in a range
     */
    public static float nextFloat(float min, float max) {
        return min + (max - min) * get().nextFloat();
    }
    
    /**
     * Generate a random integer
     */
    public static int nextInt(int bound) {
        return get().nextInt(bound);
    }

    /**
     * Generate a random boolean
     */
    public static boolean nextBoolean() {
        return get().nextBoolean();
    }
}
