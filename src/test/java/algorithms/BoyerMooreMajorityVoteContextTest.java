package algorithms;

import metrics.MetricsCollector;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Boyer-Moore Majority Vote Algorithm with Context
 */
public class BoyerMooreMajorityVoteContextTest {

    @Test
    public void testWithMetricsCollector() {
        int[] arr = {3, 3, 4, 2, 3};
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, context);

        assertEquals(3, result);
        assertTrue(metrics.getComparisons() > 0, "Should have recorded comparisons");
        assertTrue(metrics.getExecutionTimeMillis() >= 0, "Should have recorded execution time");
    }

    @Test
    public void testWithStreamProcessing() {
        int[] arr = {5, 5, 1, 2, 5, 3, 5};
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .enableStreamProcessing()
            .build();

        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, context);

        assertEquals(5, result);
        assertTrue(context.isStreamProcessingEnabled());
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    public void testWithEarlyTermination() {
        int[] arr = new int[1000];
        for (int i = 0; i < 600; i++) {
            arr[i] = 42;
        }
        for (int i = 600; i < 1000; i++) {
            arr[i] = i;
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .enableEarlyTermination()
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(42, result);
        assertTrue(context.isEarlyTerminationEnabled());
    }

    @Test
    public void testContextBuilderAllFeatures() {
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .enableEarlyTermination()
            .enableStreamProcessing()
            .build();

        assertNotNull(context.getMetrics());
        assertTrue(context.isEarlyTerminationEnabled());
        assertTrue(context.isStreamProcessingEnabled());
    }

    @Test
    public void testContextWithoutMetrics() {
        int[] arr = {7, 7, 7, 1, 2};

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .enableStreamProcessing()
            .build();

        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, context);

        assertEquals(7, result);
        assertNull(context.getMetrics());
    }

    @Test
    public void testNullContext() {
        int[] arr = {1, 1, 2};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, (BoyerMooreMajorityVote.Context) null);
        assertEquals(1, result);
    }

    @Test
    public void testDeprecatedMethodWithMetrics() {
        int[] arr = {8, 8, 9, 8};
        MetricsCollector metrics = new MetricsCollector();

        @SuppressWarnings("deprecation")
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, metrics);

        assertEquals(8, result);
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    public void testMetricsCandidateChanges() {
        int[] arr = {1, 2, 3, 4, 4, 4, 4};
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        BoyerMooreMajorityVote.findMajorityElement(arr, context);

        assertTrue(metrics.getCandidateChanges() >= 0, "Should track candidate changes");
    }

    @Test
    public void testStreamProcessingEmptyArray() {
        int[] arr = {};

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .enableStreamProcessing()
            .build();

        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, context);
        assertNull(result);
    }

    @Test
    public void testStreamProcessingSingleElement() {
        int[] arr = {99};

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .enableStreamProcessing()
            .build();

        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, context);
        assertEquals(99, result);
    }
}