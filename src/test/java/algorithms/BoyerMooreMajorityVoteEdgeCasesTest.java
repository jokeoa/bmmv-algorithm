package algorithms;

import metrics.MetricsCollector;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge cases and performance optimization tests for Boyer-Moore Majority Vote Algorithm
 */
public class BoyerMooreMajorityVoteEdgeCasesTest {

    @Test
    public void testIntegerOverflowPrevention() {
        // Create array larger than Integer.MAX_VALUE / 2
        // Use verification to ensure long arithmetic works
        int[] arr = new int[1000];
        for (int i = 0; i < 501; i++) {
            arr[i] = 1;
        }
        for (int i = 501; i < 1000; i++) {
            arr[i] = 2;
        }

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(1, result, "Should handle large arrays without overflow");
    }

    @Test
    public void testMinAndMaxIntValues() {
        int[] arr = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void testAllZeros() {
        int[] arr = {0, 0, 0, 0};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(0, result);
    }

    @Test
    public void testMixedZerosAndNonZeros() {
        int[] arr = {0, 0, 0, 1, 2};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(0, result);
    }

    @Test
    public void testAlternatingPattern() {
        int[] arr = {1, 2, 1, 2, 1, 2, 1};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(1, result);
    }

    @Test
    public void testAlternatingNoMajority() {
        int[] arr = {1, 2, 1, 2};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertNull(result);
    }

    @Test
    public void testManyDistinctElements() {
        int[] arr = new int[1001];
        for (int i = 0; i < 501; i++) {
            arr[i] = 999;
        }
        for (int i = 501; i < 1001; i++) {
            arr[i] = i;  // All different
        }

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(999, result);
    }

    @Test
    public void testEarlyTerminationEfficiency() {
        // Early termination should stop as soon as majority is confirmed
        int[] arr = new int[10000];
        for (int i = 0; i < 5001; i++) {
            arr[i] = 100;  // Majority confirmed early
        }
        for (int i = 5001; i < 10000; i++) {
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

        Integer result1 = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr.clone(), contextWithET);
        Integer result2 = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr.clone(), contextWithoutET);

        assertEquals(result1, result2);
        assertEquals(100, result1);
    }

    @Test
    public void testStreamProcessingConsistency() {
        int[] arr = {5, 5, 6, 5, 7, 5, 8};

        BoyerMooreMajorityVote.Context streamContext = BoyerMooreMajorityVote.Context.builder()
            .enableStreamProcessing()
            .build();

        Integer streamResult = BoyerMooreMajorityVote.findMajorityElement(arr, streamContext);
        Integer normalResult = BoyerMooreMajorityVote.findMajorityElement(arr, (BoyerMooreMajorityVote.Context) null);

        assertEquals(normalResult, streamResult, "Stream and normal processing should give same result");
    }

    @Test
    public void testStreamProcessingLargeArray() {
        int[] arr = new int[5000];
        for (int i = 0; i < 2501; i++) {
            arr[i] = 42;
        }
        for (int i = 2501; i < 5000; i++) {
            arr[i] = i;
        }

        BoyerMooreMajorityVote.Context streamContext = BoyerMooreMajorityVote.Context.builder()
            .enableStreamProcessing()
            .build();

        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, streamContext);
        assertEquals(42, result);
    }

    @Test
    public void testTwoElementsEqual() {
        int[] arr = {5, 5};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(5, result);
    }

    @Test
    public void testThreeElementsMajority() {
        int[] arr = {1, 2, 1};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(1, result);
    }

    @Test
    public void testThreeElementsNoMajority() {
        int[] arr = {1, 2, 3};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertNull(result);
    }

    @Test
    public void testMajorityAtBoundary() {
        // Test when majority is exactly (n/2) + 1
        int[] arr = {1, 1, 1, 2, 2};  // 3 out of 5
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(1, result);
    }

    @Test
    public void testMultipleCandidateChanges() {
        int[] arr = {1, 2, 3, 4, 5, 5, 5, 5, 5};
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, context);

        assertEquals(5, result);
        assertTrue(metrics.getCandidateChanges() > 0, "Should have multiple candidate changes");
    }

    @Test
    public void testPerformanceMetricsCollection() {
        int[] arr = {1, 1, 2, 2, 1};
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertTrue(metrics.getComparisons() > 0, "Should record comparisons");
        assertTrue(metrics.getExecutionTimeMillis() >= 0, "Should record execution time");
        assertTrue(metrics.getCandidateChanges() >= 0, "Should record candidate changes");
    }

    @Test
    public void testCombinedOptimizations() {
        int[] arr = new int[1000];
        for (int i = 0; i < 600; i++) {
            arr[i] = 777;
        }
        for (int i = 600; i < 1000; i++) {
            arr[i] = i;
        }

        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .enableEarlyTermination()
            .enableStreamProcessing()
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(777, result);
        assertTrue(context.isEarlyTerminationEnabled());
        assertTrue(context.isStreamProcessingEnabled());
        assertNotNull(context.getMetrics());
    }
}