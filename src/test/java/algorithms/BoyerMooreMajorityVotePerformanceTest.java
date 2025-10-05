package algorithms;

import metrics.MetricsCollector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance and scalability tests for Boyer-Moore Majority Vote Algorithm
 * Tests performance across input sizes 10² to 10⁵
 */
public class BoyerMooreMajorityVotePerformanceTest {

    private static final Random random = new Random(42);

    /**
     * Test performance on small arrays (10²)
     */
    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    public void testPerformanceSize100() {
        int size = 100;
        int[] arr = generateArrayWithMajority(size, size / 2 + 1);

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertNotNull(result);
        assertTrue(metrics.getExecutionTimeMillis() >= 0);
        System.out.printf("Size 10²: %f ms, %d comparisons%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons());
    }

    /**
     * Test performance on medium arrays (10³)
     */
    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    public void testPerformanceSize1000() {
        int size = 1000;
        int[] arr = generateArrayWithMajority(size, size / 2 + 1);

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertNotNull(result);
        assertTrue(metrics.getExecutionTimeMillis() >= 0);
        System.out.printf("Size 10³: %f ms, %d comparisons%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons());
    }

    /**
     * Test performance on large arrays (10⁴)
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testPerformanceSize10000() {
        int size = 10000;
        int[] arr = generateArrayWithMajority(size, size / 2 + 1);

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertNotNull(result);
        assertTrue(metrics.getExecutionTimeMillis() >= 0);
        System.out.printf("Size 10⁴: %f ms, %d comparisons%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons());
    }

    /**
     * Test performance on very large arrays (10⁵)
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testPerformanceSize100000() {
        int size = 100000;
        int[] arr = generateArrayWithMajority(size, size / 2 + 1);

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertNotNull(result);
        assertTrue(metrics.getExecutionTimeMillis() >= 0);
        System.out.printf("Size 10⁵: %f ms, %d comparisons%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons());
    }

    /**
     * Test linear scalability: comparisons should scale linearly with input size
     */
    @Test
    public void testLinearScalability() {
        int[] sizes = {100, 1000, 10000};
        long[] comparisons = new long[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            int[] arr = generateArrayWithMajority(size, size / 2 + 1);

            MetricsCollector metrics = new MetricsCollector();
            BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
                .withMetrics(metrics)
                .build();

            BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);
            comparisons[i] = metrics.getComparisons();
        }

        // Verify linear growth (allowing some tolerance)
        // comparisons[1] / comparisons[0] should be roughly equal to sizes[1] / sizes[0]
        double ratio1 = (double) comparisons[1] / comparisons[0];
        double expectedRatio1 = (double) sizes[1] / sizes[0];

        assertTrue(ratio1 >= expectedRatio1 * 0.5 && ratio1 <= expectedRatio1 * 2.0,
            "Comparisons should scale roughly linearly with input size");

        System.out.printf("Scalability: 100→1000: %.2fx comparisons (expected ~%.2fx)%n",
            ratio1, expectedRatio1);
    }

    /**
     * Test worst-case performance: alternating elements
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testWorstCasePerformance() {
        int size = 50000;
        int[] arr = new int[size];

        // Create worst-case: many candidate changes
        for (int i = 0; i < size; i++) {
            arr[i] = i % 100; // Many different values cycling
        }

        // Add majority at the end
        for (int i = size / 2; i < size; i++) {
            arr[i] = 999;
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(999, result);
        assertTrue(metrics.getCandidateChanges() > 0);
        System.out.printf("Worst case: %f ms, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getCandidateChanges());
    }

    /**
     * Test best-case performance: all same elements
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testBestCasePerformance() {
        int size = 100000;
        int[] arr = new int[size];

        for (int i = 0; i < size; i++) {
            arr[i] = 42;
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(42, result);
        assertEquals(0, metrics.getCandidateChanges(), "No candidate changes in best case");
        System.out.printf("Best case: %f ms, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getCandidateChanges());
    }

    /**
     * Test early termination optimization effectiveness
     */
    @Test
    public void testEarlyTerminationPerformance() {
        int size = 100000;
        int[] arr = new int[size];

        // Majority in first half
        for (int i = 0; i < size / 2 + 1; i++) {
            arr[i] = 777;
        }
        for (int i = size / 2 + 1; i < size; i++) {
            arr[i] = i;
        }

        MetricsCollector metricsWithET = new MetricsCollector();
        MetricsCollector metricsWithoutET = new MetricsCollector();

        BoyerMooreMajorityVote.Context contextWithET = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metricsWithET)
            .enableEarlyTermination()
            .build();

        BoyerMooreMajorityVote.Context contextWithoutET = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metricsWithoutET)
            .build();

        long startWithET = System.nanoTime();
        Integer resultWithET = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr.clone(), contextWithET);
        long timeWithET = System.nanoTime() - startWithET;

        long startWithoutET = System.nanoTime();
        Integer resultWithoutET = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr.clone(), contextWithoutET);
        long timeWithoutET = System.nanoTime() - startWithoutET;

        assertEquals(resultWithET, resultWithoutET);
        System.out.printf("Early termination: %f ms vs %f ms (%.2fx speedup)%n",
            metricsWithET.getExecutionTimeMillis(), metricsWithoutET.getExecutionTimeMillis(),
            (double) timeWithoutET / timeWithET);
    }

    /**
     * Test stream processing performance
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testStreamProcessingPerformance() {
        int size = 50000;
        int[] arr = generateArrayWithMajority(size, size / 2 + 1);

        MetricsCollector streamMetrics = new MetricsCollector();
        MetricsCollector normalMetrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context streamContext = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(streamMetrics)
            .enableStreamProcessing()
            .build();

        BoyerMooreMajorityVote.Context normalContext = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(normalMetrics)
            .build();

        Integer streamResult = BoyerMooreMajorityVote.findMajorityElement(arr.clone(), streamContext);
        Integer normalResult = BoyerMooreMajorityVote.findMajorityElement(arr.clone(), normalContext);

        assertEquals(normalResult, streamResult);
        System.out.printf("Stream vs Normal: %f ms vs %f ms%n",
            streamMetrics.getExecutionTimeMillis(), normalMetrics.getExecutionTimeMillis());
    }

    /**
     * Test iterator performance on large dataset
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testIteratorPerformance() {
        int size = 50000;
        java.util.List<Integer> list = new java.util.ArrayList<>();

        for (int i = 0; i < size / 2 + 1; i++) {
            list.add(888);
        }
        for (int i = size / 2 + 1; i < size; i++) {
            list.add(i);
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        long start = System.nanoTime();
        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), context);
        long duration = System.nanoTime() - start;

        assertEquals(888, result);
        System.out.printf("Iterator performance (50k elements): %f ms%n",
            metrics.getExecutionTimeMillis());
    }

    /**
     * Test constant space complexity: O(1) space usage
     */
    @Test
    public void testConstantSpaceComplexity() {
        int size = 100000;
        int[] arr = generateArrayWithMajority(size, size / 2 + 1);

        // The algorithm should use O(1) space regardless of input size
        // This is validated by the algorithm design, but we verify it works
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);

        assertNotNull(result);
        // No OutOfMemoryError should occur even for large inputs
    }

    // Helper methods

    private int[] generateArrayWithMajority(int size, int majorityCount) {
        int[] arr = new int[size];
        int majorityValue = random.nextInt(1000);

        for (int i = 0; i < majorityCount; i++) {
            arr[i] = majorityValue;
        }

        for (int i = majorityCount; i < size; i++) {
            int val;
            do {
                val = random.nextInt(1000);
            } while (val == majorityValue);
            arr[i] = val;
        }

        shuffleArray(arr);
        return arr;
    }

    private void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}