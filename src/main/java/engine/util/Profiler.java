package engine.util;

public class Profiler {

    private final int EXECUTION_AVERAGING_SIZE;
    private final float[] executionTimes;
    private int executionIdx = 0;
    private int executionCount = 0;
    private long start;

    public Profiler(int executionAveragingSize) {
        this.EXECUTION_AVERAGING_SIZE = executionAveragingSize;
        executionTimes = new float[EXECUTION_AVERAGING_SIZE];
    }

    public void startExecution(){
        start = java.lang.System.nanoTime();
    }

    public void endExecution(){
        executionTimes[executionIdx] = (java.lang.System.nanoTime() - start) / 1_000_000f;
        executionIdx = (executionIdx + 1) % EXECUTION_AVERAGING_SIZE;
        if (executionCount < EXECUTION_AVERAGING_SIZE) executionCount++;
    }

    public float getExecutionAverageTimeMs() {
        if (executionCount == 0) return 0;

        float sum = 0;
        for (int i = 0; i < executionCount; i++) {
            sum += executionTimes[i];
        }
        return sum / executionCount;
    }
}