package metrics;

/**
 * Simple metrics collector for algorithm performance tracking
 */
public class MetricsCollector {
    private int comparisons;
    private int candidateChanges;
    private long startTime;
    private long endTime;

    public MetricsCollector() {
        this.comparisons = 0;
        this.candidateChanges = 0;
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    public void incrementComparisons() {
        this.comparisons++;
    }

    public void incrementCandidateChanges() {
        this.candidateChanges++;
    }

    public int getComparisons() {
        return comparisons;
    }

    public int getCandidateChanges() {
        return candidateChanges;
    }

    public long getExecutionTimeNanos() {
        return endTime - startTime;
    }

    public double getExecutionTimeMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    public void reset() {
        this.comparisons = 0;
        this.candidateChanges = 0;
        this.startTime = 0;
        this.endTime = 0;
    }

    @Override
    public String toString() {
        return String.format(
            "Metrics:\n" +
            "  Comparisons: %d\n" +
            "  Candidate Changes: %d\n" +
            "  Execution Time: %.3f ms",
            comparisons, candidateChanges, getExecutionTimeMillis()
        );
    }
}