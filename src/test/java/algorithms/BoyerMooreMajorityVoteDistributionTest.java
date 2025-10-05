package algorithms;

import metrics.MetricsCollector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Input distribution performance tests for Boyer-Moore Majority Vote Algorithm
 * Tests on random, sorted, reverse-sorted, and nearly-sorted data
 */
public class BoyerMooreMajorityVoteDistributionTest {

    private static final int TEST_SIZE = 10000;
    private static final Random random = new Random(42);

    /**
     * Test performance on random distribution
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testRandomDistribution() {
        int[] arr = new int[TEST_SIZE];
        int majorityValue = 500;

        // Create random distribution with majority
        for (int i = 0; i < TEST_SIZE / 2 + 1; i++) {
            arr[i] = majorityValue;
        }
        for (int i = TEST_SIZE / 2 + 1; i < TEST_SIZE; i++) {
            arr[i] = random.nextInt(1000);
        }
        shuffleArray(arr);

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(majorityValue, result);
        System.out.printf("Random distribution: %f ms, %d comparisons, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons(), metrics.getCandidateChanges());
    }

    /**
     * Test performance on sorted distribution
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testSortedDistribution() {
        int[] arr = new int[TEST_SIZE];

        // Create sorted array with majority in the middle
        int idx = 0;
        for (int i = 0; i < 1000; i++) {
            arr[idx++] = i;
        }

        int majorityValue = 1000;
        for (int i = 0; i < TEST_SIZE / 2 + 1; i++) {
            if (idx < TEST_SIZE) arr[idx++] = majorityValue;
        }

        for (int i = 1001; i < 2000 && idx < TEST_SIZE; i++) {
            arr[idx++] = i;
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(majorityValue, result);
        System.out.printf("Sorted distribution: %f ms, %d comparisons, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons(), metrics.getCandidateChanges());
    }

    /**
     * Test performance on reverse-sorted distribution
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testReverseSortedDistribution() {
        int[] arr = new int[TEST_SIZE];

        // Create reverse-sorted array
        int majorityValue = 1000;
        int idx = 0;

        for (int i = 2000; i > 1001 && idx < TEST_SIZE / 3; i--) {
            arr[idx++] = i;
        }

        for (int i = 0; i < TEST_SIZE / 2 + 1; i++) {
            if (idx < TEST_SIZE) arr[idx++] = majorityValue;
        }

        for (int i = 999; i >= 0 && idx < TEST_SIZE; i--) {
            arr[idx++] = i;
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(majorityValue, result);
        System.out.printf("Reverse-sorted distribution: %f ms, %d comparisons, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons(), metrics.getCandidateChanges());
    }

    /**
     * Test performance on nearly-sorted distribution (90% sorted)
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testNearlySortedDistribution() {
        int[] arr = new int[TEST_SIZE];

        // Create sorted array
        for (int i = 0; i < TEST_SIZE; i++) {
            arr[i] = i;
        }

        // Make one value the majority
        int majorityValue = 5000;
        for (int i = 0; i < TEST_SIZE / 2 + 1; i++) {
            arr[i] = majorityValue;
        }

        // Perform limited shuffling (10% of array)
        for (int i = 0; i < TEST_SIZE / 10; i++) {
            int idx1 = random.nextInt(TEST_SIZE);
            int idx2 = random.nextInt(TEST_SIZE);
            int temp = arr[idx1];
            arr[idx1] = arr[idx2];
            arr[idx2] = temp;
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(majorityValue, result);
        System.out.printf("Nearly-sorted distribution: %f ms, %d comparisons, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons(), metrics.getCandidateChanges());
    }

    /**
     * Test performance on uniform distribution (many same values)
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testUniformDistribution() {
        int[] arr = new int[TEST_SIZE];

        // All elements are the same
        int value = 42;
        for (int i = 0; i < TEST_SIZE; i++) {
            arr[i] = value;
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(value, result);
        assertEquals(0, metrics.getCandidateChanges(), "No candidate changes in uniform distribution");
        System.out.printf("Uniform distribution: %f ms, %d comparisons, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons(), metrics.getCandidateChanges());
    }

    /**
     * Test performance on bimodal distribution (two dominant values)
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testBimodalDistribution() {
        int[] arr = new int[TEST_SIZE];

        int value1 = 100;
        int value2 = 200;

        // value1 is majority
        for (int i = 0; i < TEST_SIZE / 2 + 1; i++) {
            arr[i] = value1;
        }

        // value2 is close but not majority
        for (int i = TEST_SIZE / 2 + 1; i < TEST_SIZE; i++) {
            arr[i] = value2;
        }

        shuffleArray(arr);

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(value1, result);
        System.out.printf("Bimodal distribution: %f ms, %d comparisons, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons(), metrics.getCandidateChanges());
    }

    /**
     * Test performance on sparse distribution (many unique values)
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testSparseDistribution() {
        int[] arr = new int[TEST_SIZE];

        int majorityValue = 777;

        // Majority element
        for (int i = 0; i < TEST_SIZE / 2 + 1; i++) {
            arr[i] = majorityValue;
        }

        // Each remaining element is unique
        for (int i = TEST_SIZE / 2 + 1; i < TEST_SIZE; i++) {
            arr[i] = i + 10000; // Ensure uniqueness
        }

        shuffleArray(arr);

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(majorityValue, result);
        System.out.printf("Sparse distribution: %f ms, %d comparisons, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons(), metrics.getCandidateChanges());
    }

    /**
     * Test performance on clustered distribution (majority in clusters)
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testClusteredDistribution() {
        int[] arr = new int[TEST_SIZE];

        int majorityValue = 999;
        int clusterSize = 100;
        int idx = 0;

        // Create clusters of majority value
        int majorityCount = TEST_SIZE / 2 + 1;
        int clustersNeeded = majorityCount / clusterSize;

        for (int cluster = 0; cluster < clustersNeeded; cluster++) {
            // Cluster of majority
            for (int i = 0; i < clusterSize && idx < TEST_SIZE; i++) {
                arr[idx++] = majorityValue;
            }

            // Cluster of random values
            for (int i = 0; i < clusterSize && idx < TEST_SIZE; i++) {
                arr[idx++] = random.nextInt(1000);
            }
        }

        // Fill remainder
        while (idx < TEST_SIZE) {
            if (majorityCount > 0) {
                arr[idx++] = majorityValue;
                majorityCount--;
            } else {
                arr[idx++] = random.nextInt(1000);
            }
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(majorityValue, result);
        System.out.printf("Clustered distribution: %f ms, %d comparisons, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons(), metrics.getCandidateChanges());
    }

    /**
     * Test performance on alternating pattern
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testAlternatingPattern() {
        int[] arr = new int[TEST_SIZE];

        int majorityValue = 555;
        int otherValue = 666;

        // Alternating pattern with slight majority
        for (int i = 0; i < TEST_SIZE; i++) {
            if (i < TEST_SIZE / 2 + 1) {
                arr[i] = majorityValue;
            } else {
                arr[i] = otherValue;
            }
        }

        // Create alternating pattern
        Arrays.sort(arr);
        int[] alternating = new int[TEST_SIZE];
        int left = 0, right = TEST_SIZE / 2 + 1;
        for (int i = 0; i < TEST_SIZE; i++) {
            if (i % 2 == 0 && left < TEST_SIZE / 2 + 1) {
                alternating[i] = arr[left++];
            } else if (right < TEST_SIZE) {
                alternating[i] = arr[right++];
            } else if (left < TEST_SIZE / 2 + 1) {
                alternating[i] = arr[left++];
            }
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(alternating, context);

        assertEquals(majorityValue, result);
        assertTrue(metrics.getCandidateChanges() > 0, "Should have many candidate changes");
        System.out.printf("Alternating pattern: %f ms, %d comparisons, %d candidate changes%n",
            metrics.getExecutionTimeMillis(), metrics.getComparisons(), metrics.getCandidateChanges());
    }

    /**
     * Compare performance across all distributions
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testCompareAllDistributions() {
        System.out.println("\n=== Distribution Performance Comparison ===");

        testRandomDistribution();
        testSortedDistribution();
        testReverseSortedDistribution();
        testNearlySortedDistribution();
        testUniformDistribution();
        testBimodalDistribution();
        testSparseDistribution();
        testClusteredDistribution();
        testAlternatingPattern();

        System.out.println("=== End of Distribution Comparison ===\n");
    }

    // Helper method
    private void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}