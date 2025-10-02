package algorithms;

import metrics.MetricsCollector;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Boyer-Moore Majority Vote verification methods
 */
public class BoyerMooreMajorityVoteVerifyTest {

    @Test
    public void testFindAndVerifyWithMajority() {
        int[] arr = {3, 3, 4, 2, 3};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(3, result, "3 appears 3 times out of 5, which is majority");
    }

    @Test
    public void testFindAndVerifyNoMajority() {
        int[] arr = {1, 2, 3, 4};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertNull(result, "No element appears more than n/2 times");
    }

    @Test
    public void testFindAndVerifyNullArray() {
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(null);
        assertNull(result);
    }

    @Test
    public void testFindAndVerifyEmptyArray() {
        int[] arr = {};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertNull(result);
    }

    @Test
    public void testFindAndVerifySingleElement() {
        int[] arr = {42};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(42, result);
    }

    @Test
    public void testFindAndVerifyAllSame() {
        int[] arr = {7, 7, 7, 7, 7};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(7, result);
    }

    @Test
    public void testFindAndVerifyWithMetrics() {
        int[] arr = {5, 5, 1, 5, 2, 5};
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(5, result);
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    public void testFindAndVerifyWithEarlyTermination() {
        int[] arr = new int[1001];
        for (int i = 0; i < 600; i++) {
            arr[i] = 99;
        }
        for (int i = 600; i < 1001; i++) {
            arr[i] = i;
        }

        MetricsCollector metrics = new MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .enableEarlyTermination()
            .build();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, context);

        assertEquals(99, result);
        // With early termination, we might not scan all elements
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    public void testFindAndVerifyDeprecatedMethod() {
        int[] arr = {2, 2, 1, 1, 2};
        MetricsCollector metrics = new MetricsCollector();

        @SuppressWarnings("deprecation")
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr, metrics);

        assertEquals(2, result);
        assertTrue(metrics.getComparisons() > 0);
    }

    @Test
    public void testFindAndVerifyExactlyHalf() {
        // Element appearing exactly n/2 times is NOT a majority
        int[] arr = {1, 1, 2, 2};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertNull(result, "Element appearing exactly n/2 times is not majority");
    }

    @Test
    public void testFindAndVerifyJustOverHalf() {
        int[] arr = {1, 1, 1, 2, 2};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(1, result, "1 appears 3 times out of 5");
    }

    @Test
    public void testFindAndVerifyLargeArray() {
        int[] arr = new int[10001];
        for (int i = 0; i < 5001; i++) {
            arr[i] = 777;
        }
        for (int i = 5001; i < 10001; i++) {
            arr[i] = i;
        }

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(777, result);
    }

    @Test
    public void testFindAndVerifyNegativeNumbers() {
        int[] arr = {-10, -10, -10, 5, 6};
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(-10, result);
    }

    @Test
    public void testVerifyCandidateTrue() {
        int[] arr = {3, 3, 4, 2, 3};
        boolean result = BoyerMooreMajorityVote.verifyCandidate(arr, 3, null);
        assertTrue(result, "3 is the majority element");
    }

    @Test
    public void testVerifyCandidateFalse() {
        int[] arr = {1, 2, 3, 4};
        boolean result = BoyerMooreMajorityVote.verifyCandidate(arr, 1, null);
        assertFalse(result, "1 is not the majority element");
    }

    @Test
    public void testVerifyCandidateNullArray() {
        boolean result = BoyerMooreMajorityVote.verifyCandidate(null, 1, null);
        assertFalse(result);
    }

    @Test
    public void testVerifyCandidateEmptyArray() {
        int[] arr = {};
        boolean result = BoyerMooreMajorityVote.verifyCandidate(arr, 1, null);
        assertFalse(result);
    }

    @Test
    public void testVerifyCandidateSingleElement() {
        int[] arr = {5};
        boolean result = BoyerMooreMajorityVote.verifyCandidate(arr, 5, null);
        assertTrue(result);
    }

    @Test
    public void testVerifyCandidateWithMetrics() {
        int[] arr = {7, 7, 7, 1, 2};
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        boolean result = BoyerMooreMajorityVote.verifyCandidate(arr, 7, context);

        assertTrue(result);
        assertEquals(arr.length, metrics.getComparisons());
    }

    @Test
    public void testVerifyCandidateAllSame() {
        int[] arr = {9, 9, 9, 9};
        boolean result = BoyerMooreMajorityVote.verifyCandidate(arr, 9, null);
        assertTrue(result);
    }

    @Test
    public void testVerifyCandidateNotPresent() {
        int[] arr = {1, 2, 3, 4};
        boolean result = BoyerMooreMajorityVote.verifyCandidate(arr, 99, null);
        assertFalse(result, "Candidate not even in array");
    }
}