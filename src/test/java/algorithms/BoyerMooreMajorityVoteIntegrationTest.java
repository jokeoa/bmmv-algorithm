package algorithms;

import metrics.MetricsCollector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for all public APIs of Boyer-Moore Majority Vote Algorithm
 * Tests interactions between different components and end-to-end workflows
 */
public class BoyerMooreMajorityVoteIntegrationTest {

    @Test
    @DisplayName("Integration: Complete workflow without context")
    public void testCompleteWorkflowWithoutContext() {
        int[] arr = {5, 5, 1, 5, 2, 5, 3};

        // Find candidate
        Integer candidate = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertNotNull(candidate);
        assertEquals(5, candidate);

        // Verify candidate
        boolean isValid = BoyerMooreMajorityVote.verifyCandidate(arr, candidate, null);
        assertTrue(isValid);

        // Find and verify in one step
        Integer verified = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(candidate, verified);
    }

    @Test
    @DisplayName("Integration: Complete workflow with context and metrics")
    public void testCompleteWorkflowWithContext() {
        int[] arr = {7, 7, 8, 7, 9, 7, 10};
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .enableEarlyTermination()
            .build();

        // Find with context
        Integer candidate = BoyerMooreMajorityVote.findMajorityElement(arr, context);
        assertNotNull(candidate);
        assertEquals(7, candidate);

        // Verify metrics were collected
        assertTrue(metrics.getComparisons() > 0);
        assertTrue(metrics.getExecutionTimeMillis() >= 0);

        // Verify with context
        MetricsCollector verifyMetrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context verifyContext = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(verifyMetrics)
            .build();

        boolean isValid = BoyerMooreMajorityVote.verifyCandidate(arr, candidate, verifyContext);
        assertTrue(isValid);
        assertEquals(arr.length, verifyMetrics.getComparisons());
    }

    @Test
    @DisplayName("Integration: Array and Iterator consistency")
    public void testArrayIteratorIntegration() {
        int[] arr = {3, 3, 4, 2, 3};

        // Array approach
        Integer arrayResult = BoyerMooreMajorityVote.findMajorityElement(arr);

        // Iterator approach
        List<Integer> list = new ArrayList<>();
        for (int num : arr) {
            list.add(num);
        }
        Integer iteratorResult = BoyerMooreMajorityVote.findMajorityElementIterator(
            list.iterator(), null);

        // Should be consistent
        assertEquals(arrayResult, iteratorResult);

        // Both should verify correctly
        Integer arrayVerified = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(arrayResult, arrayVerified);
    }

    @Test
    @DisplayName("Integration: Stream processing with verification")
    public void testStreamProcessingIntegration() {
        int[] arr = {9, 9, 1, 9, 2, 9};

        BoyerMooreMajorityVote.Context streamContext = BoyerMooreMajorityVote.Context.builder()
            .enableStreamProcessing()
            .build();

        // Find with stream processing
        Integer candidate = BoyerMooreMajorityVote.findMajorityElement(arr, streamContext);
        assertNotNull(candidate);

        // Verify the candidate
        boolean isValid = BoyerMooreMajorityVote.verifyCandidate(arr, candidate, null);
        assertTrue(isValid);

        // Compare with non-stream approach
        Integer normalResult = BoyerMooreMajorityVote.findMajorityElement(arr, (BoyerMooreMajorityVote.Context) null);
        assertEquals(normalResult, candidate);
    }

    @Test
    @DisplayName("Integration: All optimizations enabled")
    public void testAllOptimizationsEnabled() {
        int[] arr = new int[1000];
        for (int i = 0; i < 600; i++) {
            arr[i] = 555;
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

        assertEquals(555, result);
        assertTrue(context.isEarlyTerminationEnabled());
        assertTrue(context.isStreamProcessingEnabled());
        assertNotNull(context.getMetrics());
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    @DisplayName("Integration: Context builder variations")
    public void testContextBuilderIntegration() {
        int[] arr = {1, 1, 2, 1, 3};

        // Only metrics
        MetricsCollector metrics1 = new MetricsCollector();
        BoyerMooreMajorityVote.Context context1 = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics1)
            .build();
        Integer result1 = BoyerMooreMajorityVote.findMajorityElement(arr, context1);

        // Only early termination
        BoyerMooreMajorityVote.Context context2 = BoyerMooreMajorityVote.Context.builder()
            .enableEarlyTermination()
            .build();
        Integer result2 = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context2);

        // Only stream processing
        BoyerMooreMajorityVote.Context context3 = BoyerMooreMajorityVote.Context.builder()
            .enableStreamProcessing()
            .build();
        Integer result3 = BoyerMooreMajorityVote.findMajorityElement(arr, context3);

        // All should give same result
        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertEquals(1, result1);
    }

    @Test
    @DisplayName("Integration: Deprecated API compatibility")
    public void testDeprecatedAPICompatibility() {
        int[] arr = {6, 6, 7, 6, 8};
        MetricsCollector metrics = new MetricsCollector();

        // Deprecated method
        @SuppressWarnings("deprecation")
        Integer deprecatedResult = BoyerMooreMajorityVote.findMajorityElement(arr, metrics);

        // New method
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(new MetricsCollector())
            .build();
        Integer newResult = BoyerMooreMajorityVote.findMajorityElement(arr, context);

        // Should produce same result
        assertEquals(deprecatedResult, newResult);

        // Deprecated verify
        MetricsCollector metrics2 = new MetricsCollector();
        @SuppressWarnings("deprecation")
        Integer deprecatedVerified = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, metrics2);

        // New verify
        Integer newVerified = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(deprecatedVerified, newVerified);
    }

    @Test
    @DisplayName("Integration: Multiple sequential operations")
    public void testMultipleSequentialOperations() {
        Random random = new Random(42);

        for (int trial = 0; trial < 10; trial++) {
            int size = 50 + random.nextInt(100);
            int[] arr = new int[size];

            int majorityValue = random.nextInt(100);
            for (int i = 0; i < size / 2 + 1; i++) {
                arr[i] = majorityValue;
            }
            for (int i = size / 2 + 1; i < size; i++) {
                arr[i] = random.nextInt(100);
            }
            shuffleArray(arr, random);

            // Operation 1: Find
            Integer candidate = BoyerMooreMajorityVote.findMajorityElement(arr);

            // Operation 2: Verify
            boolean isValid = BoyerMooreMajorityVote.verifyCandidate(arr, candidate, null);

            // Operation 3: Find and verify
            Integer verified = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);

            // All should be consistent
            assertNotNull(candidate);
            assertTrue(isValid);
            assertEquals(candidate, verified);
            assertEquals(majorityValue, verified);
        }
    }

    @Test
    @DisplayName("Integration: Null and empty edge cases across all APIs")
    public void testNullEmptyEdgeCasesIntegration() {
        // Null array
        assertNull(BoyerMooreMajorityVote.findMajorityElement(null));
        assertNull(BoyerMooreMajorityVote.findAndVerifyMajorityElement(null));
        assertFalse(BoyerMooreMajorityVote.verifyCandidate(null, 1, null));

        // Empty array
        int[] empty = {};
        assertNull(BoyerMooreMajorityVote.findMajorityElement(empty));
        assertNull(BoyerMooreMajorityVote.findAndVerifyMajorityElement(empty));
        assertFalse(BoyerMooreMajorityVote.verifyCandidate(empty, 1, null));

        // Empty iterator
        assertNull(BoyerMooreMajorityVote.findMajorityElementIterator(
            Collections.emptyIterator(), null));

        // Null iterator should throw
        assertThrows(NullPointerException.class, () ->
            BoyerMooreMajorityVote.findMajorityElementIterator(null, null));
    }

    @Test
    @DisplayName("Integration: Complex data pipeline")
    public void testComplexDataPipeline() {
        // Simulate complex data processing pipeline
        List<Integer> sourceData = new ArrayList<>();
        Random random = new Random(123);

        // Generate data
        for (int i = 0; i < 500; i++) {
            sourceData.add(random.nextInt(50));
        }

        // Add majority element
        for (int i = 0; i < 300; i++) {
            sourceData.add(99);
        }

        Collections.shuffle(sourceData, random);

        // Pipeline step 1: Iterator processing
        Integer iteratorResult = BoyerMooreMajorityVote.findMajorityElementIterator(
            sourceData.iterator(), null);

        // Pipeline step 2: Array conversion and verification
        int[] arr = sourceData.stream().mapToInt(Integer::intValue).toArray();
        Integer arrayResult = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);

        // Pipeline step 3: Stream processing
        BoyerMooreMajorityVote.Context streamContext = BoyerMooreMajorityVote.Context.builder()
            .enableStreamProcessing()
            .build();
        Integer streamResult = BoyerMooreMajorityVote.findMajorityElement(arr, streamContext);

        // All pipeline steps should agree
        assertEquals(iteratorResult, arrayResult);
        assertEquals(arrayResult, streamResult);
        assertEquals(99, streamResult);
    }

    @Test
    @DisplayName("Integration: Concurrent context usage")
    public void testConcurrentContextUsage() throws InterruptedException {
        int[] arr = {4, 4, 5, 4, 6, 4};

        List<Thread> threads = new ArrayList<>();
        List<Integer> results = Collections.synchronizedList(new ArrayList<>());

        // Run multiple threads with different contexts
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                MetricsCollector metrics = new MetricsCollector();
                BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
                    .withMetrics(metrics)
                    .enableEarlyTermination()
                    .build();

                Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(
                    arr.clone(), context);
                results.add(result);
            });
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }

        // All results should be the same
        assertEquals(5, results.size());
        for (Integer result : results) {
            assertEquals(4, result);
        }
    }

    @Test
    @DisplayName("Integration: Large-scale end-to-end test")
    public void testLargeScaleEndToEnd() {
        int size = 100000;
        int[] arr = new int[size];

        // Create majority
        for (int i = 0; i < size / 2 + 1; i++) {
            arr[i] = 12345;
        }
        for (int i = size / 2 + 1; i < size; i++) {
            arr[i] = i;
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .enableEarlyTermination()
            .enableStreamProcessing()
            .build();

        // Complete workflow
        Integer candidate = BoyerMooreMajorityVote.findMajorityElement(arr, context);
        assertNotNull(candidate);
        assertEquals(12345, candidate);

        boolean verified = BoyerMooreMajorityVote.verifyCandidate(arr, candidate, null);
        assertTrue(verified);

        Integer fullResult = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);
        assertEquals(candidate, fullResult);

        // Check metrics
        assertTrue(metrics.getComparisons() > 0);
        assertTrue(metrics.getExecutionTimeMillis() >= 0);

        System.out.printf("Large-scale integration test: %d elements in %f ms%n",
            size, metrics.getExecutionTimeMillis());
    }

    @Test
    @DisplayName("Integration: Error handling across APIs")
    public void testErrorHandlingIntegration() {
        // Iterator with null elements
        List<Integer> listWithNull = Arrays.asList(1, null, 3);
        assertThrows(NullPointerException.class, () ->
            BoyerMooreMajorityVote.findMajorityElementIterator(listWithNull.iterator(), null));

        // Null context should work fine
        int[] arr = {1, 1, 2};
        assertDoesNotThrow(() ->
            BoyerMooreMajorityVote.findMajorityElement(arr, (BoyerMooreMajorityVote.Context) null));
        assertDoesNotThrow(() ->
            BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, (BoyerMooreMajorityVote.Context) null));
        assertDoesNotThrow(() ->
            BoyerMooreMajorityVote.verifyCandidate(arr, 1, null));
    }

    // Helper method
    private void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}