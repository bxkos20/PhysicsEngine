package engine.ecs.systems;

import engine.ecs.GameObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base class for ECS systems.
 * 
 * <p>Systems process entities that match a required component signature.
 * Supports both sequential and parallel processing modes.</p>
 * 
 * <h3>Processing Modes:</h3>
 * <ul>
 *   <li>Sequential: Simple for-loop iteration (threading=false)</li>
 *   <li>Parallel: Uses manual thread pool for multi-threaded processing (threading=true)</li>
 * </ul>
 * 
 * @see GameObject#checkSignature(long)
 */
public abstract class System {
    /** Bitmask of required components for entities processed by this system */
    protected final long REQUIRED_SIGNATURE;
    
    /** Whether to use parallel processing */
    protected final boolean THREADING;
    
    /** Last execution time in milliseconds (for profiling) */
    protected float lastExecutionTimeMs;
    
    /** Shared thread pool for all systems - utilizes all available CPU cores */
    private static final ExecutorService THREAD_POOL;
    
    /** Number of available CPU cores */
    private static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();
    
    /** Thread-safe counter for thread naming */
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(0);
    
    static {
        THREAD_POOL = Executors.newFixedThreadPool(CORE_COUNT, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "ECS-System-" + THREAD_COUNTER.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        });
    }

    /**
     * Creates a system with the specified component signature.
     * 
     * @param requiredSignature Bitmask of required components
     * @param threading          Enable parallel processing
     */
    public System(long requiredSignature, boolean threading) {
        this.REQUIRED_SIGNATURE = requiredSignature;
        this.THREADING = threading;
    }

    /**
     * Updates all entities matching the system's signature.
     * 
     * <p>If threading is enabled, uses manual thread pool for parallel processing.
     * Otherwise, processes entities sequentially.</p>
     * 
     * <p>Systems can override {@link #processUpdate(float, List)} for custom logic
     * while still benefiting from the threading infrastructure.</p>
     * 
     * @param dt          Delta time in seconds
     * @param gameObjects List of all game objects to process
     */
    public void update(float dt, List<GameObject> gameObjects) {
        long start = java.lang.System.nanoTime();
        processUpdate(dt, gameObjects);
        this.lastExecutionTimeMs = (java.lang.System.nanoTime() - start) / 1_000_000f;
    }
    
    /**
     * Template method for systems to implement custom update logic.
     * 
     * <p>Default implementation processes entities using {@link #processGameObject(float, GameObject)}.
     * Subclasses can override this for custom processing while still using the base class
     * threading infrastructure via {@link #processParallel(float, List)} and {@link #processSequential(float, List)}.</p>
     * 
     * @param dt          Delta time in seconds
     * @param gameObjects List of all game objects to process
     */
    protected void processUpdate(float dt, List<GameObject> gameObjects) {
        if (THREADING) {
            processParallel(dt, gameObjects);
        } else {
            processSequential(dt, gameObjects);
        }
    }
    
    /**
     * Sequential processing implementation using processGameObject.
     * 
     * @param dt          Delta time in seconds
     * @param gameObjects List of all game objects to process
     */
    protected final void processSequential(float dt, List<GameObject> gameObjects) {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                processGameObject(dt, gameObject);
            }
        }
    }
    
    /**
     * Parallel processing implementation using manual thread pool.
     * Distributes work evenly across all available CPU cores.
     * 
     * @param dt          Delta time in seconds
     * @param gameObjects List of all game objects to process
     */
    protected final void processParallel(float dt, List<GameObject> gameObjects) {
        final int totalObjects = gameObjects.size();
        
        // For small workloads, sequential processing is more efficient
        if (totalObjects < CORE_COUNT * 2) {
            processSequential(dt, gameObjects);
            return;
        }
        
        // CountDownLatch for thread synchronization
        CountDownLatch latch = new CountDownLatch(CORE_COUNT);
        
        // Calculate work distribution
        final int objectsPerThread = totalObjects / CORE_COUNT;
        final int remainder = totalObjects % CORE_COUNT;
        
        // Submit tasks to thread pool
        for (int threadId = 0; threadId < CORE_COUNT; threadId++) {
            final int startIdx = threadId * objectsPerThread + Math.min(threadId, remainder);
            final int endIdx = startIdx + objectsPerThread + (threadId < remainder ? 1 : 0);
            
            THREAD_POOL.submit(() -> {
                try {
                    for (int i = startIdx; i < endIdx; i++) {
                        GameObject gameObject = gameObjects.get(i);
                        if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                            processGameObject(dt, gameObject);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Fallback to sequential processing if interrupted
            processSequential(dt, gameObjects);
        }
    }
    
    /**
     * Custom parallel processing for systems that need special work distribution.
     * 
     * <p>Subclasses can override this method to implement custom parallel logic
     * while still using the shared thread pool. The Runnable should handle
     * its own exception handling and resource cleanup.</p>
     * 
     * @param task Custom parallel task to execute
     */
    protected final void submitParallelTask(Runnable task) {
        THREAD_POOL.submit(task);
    }
    
    /**
     * Helper method for parallel processing of collections with custom work distribution.
     * 
     * <p>Automatically distributes work across all CPU cores and handles synchronization.
     * Uses the provided processor interface to handle each chunk of work.</p>
     * 
     * @param <T> Type of items to process
     * @param items List of items to process
     * @param processor Interface to process a chunk of items with start/end indices
     */
    protected final <T> void processInParallel(List<T> items, ParallelProcessor<T> processor) {
        final int totalItems = items.size();
        
        // For small workloads, sequential processing is more efficient
        if (totalItems < CORE_COUNT * 2) {
            processor.process(items, 0, totalItems);
            return;
        }
        
        CountDownLatch latch = new CountDownLatch(CORE_COUNT);
        
        // Calculate work distribution
        final int itemsPerThread = totalItems / CORE_COUNT;
        final int remainder = totalItems % CORE_COUNT;
        
        // Submit tasks to thread pool
        for (int threadId = 0; threadId < CORE_COUNT; threadId++) {
            final int startIdx = threadId * itemsPerThread + Math.min(threadId, remainder);
            final int endIdx = startIdx + itemsPerThread + (threadId < remainder ? 1 : 0);
            
            submitParallelTask(() -> {
                try {
                    processor.process(items, startIdx, endIdx);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Fallback to sequential processing if interrupted
            processor.process(items, 0, totalItems);
        }
    }
    
    /**
     * Helper method for parallel processing with result collection.
     * 
     * <p>Each thread processes its chunk and contributes to a shared result collection.
     * Useful for collision detection, filtering, or other gather operations.</p>
     * 
     * @param <T> Type of input items
     * @param <R> Type of result items
     * @param items List of items to process
     * @param results Shared collection to store results (must be thread-safe)
     * @param processor Interface to process items and add to results
     */
    protected final <T, R> void processInParallelWithResults(
            List<T> items, 
            List<R> results, 
            ParallelProcessorWithResults<T, R> processor) {
        
        final int totalItems = items.size();
        
        // For small workloads, sequential processing is more efficient
        if (totalItems < CORE_COUNT * 2) {
            processor.process(items, 0, totalItems, results);
            return;
        }
        
        CountDownLatch latch = new CountDownLatch(CORE_COUNT);
        
        // Calculate work distribution
        final int itemsPerThread = totalItems / CORE_COUNT;
        final int remainder = totalItems % CORE_COUNT;
        
        // Submit tasks to thread pool
        for (int threadId = 0; threadId < CORE_COUNT; threadId++) {
            final int startIdx = threadId * itemsPerThread + Math.min(threadId, remainder);
            final int endIdx = startIdx + itemsPerThread + (threadId < remainder ? 1 : 0);
            
            submitParallelTask(() -> {
                try {
                    processor.process(items, startIdx, endIdx, results);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Fallback to sequential processing if interrupted
            processor.process(items, 0, totalItems, results);
        }
    }
    
    /**
     * Functional interface for parallel processing of item chunks.
     */
    @FunctionalInterface
    protected interface ParallelProcessor<T> {
        void process(List<T> items, int startIndex, int endIndex);
    }
    
    /**
     * Functional interface for parallel processing with result collection.
     */
    @FunctionalInterface
    protected interface ParallelProcessorWithResults<T, R> {
        void process(List<T> items, int startIndex, int endIndex, List<R> results);
    }

    /**
     * Processes a single game object. Called for each entity matching the signature.
     * 
     * @param dt         Delta time in seconds
     * @param gameObject The entity to process
     */
    protected abstract void processGameObject(float dt, GameObject gameObject);

    /**
     * Shuts down the shared thread pool.
     * Call this when the application is closing to clean up resources.
     */
    public static void shutdown() {
        THREAD_POOL.shutdown();
    }
    
    /**
     * Gets the number of CPU cores used for parallel processing.
     * 
     * @return Number of available CPU cores
     */
    public static int getCoreCount() {
        return CORE_COUNT;
    }

    /**
     * Returns the last execution time for profiling.
     * 
     * @return Execution time in milliseconds
     */
    private final int EXECUTION_AVERAGING_SIZE = 60;
    private final Queue<Float> executionTimes = new LinkedList<>();

    public float getLastExecutionTimeMs() {
        executionTimes.add(lastExecutionTimeMs);
        if (executionTimes.size() > EXECUTION_AVERAGING_SIZE) {
            executionTimes.poll();
        }
        float sum = 0;
        for (float time : executionTimes) {
            sum += time;
        }
        return sum / executionTimes.size();
    }
    static long getSignature(Class<?>... classes){
        int signature = 0;
        for (Class<?> classType classes){

        }
    }
}