package algorithms;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cross-validation tests comparing Boyer-Moore algorithm with alternative implementations
 * Ensures correctness by comparing against brute-force and HashMap-based approaches
 */
public class BoyerMooreMajorityVoteCrossValidationTest {

    /**
     * Brute-force implementation: Count each element's frequency
     */
    private Integer bruteForceMajority(int[] arr) {
        if (arr == null || arr.length == 0) return null;

        Map<Integer, Integer> counts = new HashMap<>();
        for (int num : arr) {
            counts.put(num, counts.getOrDefault(num, 0) + 1);
        }

        int threshold = arr.length / 2;
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > threshold) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Stream-based implementation using Java 8 features
     */
    private Integer streamBasedMajority(int[] arr) {
        if (arr == null || arr.length == 0) return null;

        Map<Integer, Long> frequencies = Arrays.stream(arr)
            .boxed()
            .collect(Collectors.groupingBy(i -> i, Collectors.counting()));

        return frequencies.entrySet().stream()
            .filter(entry -> entry.getValue() > arr.length / 2)
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }

    /**
     * Sorting-based approach: Sort and check middle element
     */
    private Integer sortingBasedMajority(int[] arr) {
        if (arr == null || arr.length == 0) return null;

        int[] sorted = arr.clone();
        Arrays.sort(sorted);

        // If majority exists, it must be at position n/2
        int candidate = sorted[arr.length / 2];

        // Verify
        int count = 0;
        for (int num : arr) {
            if (num == candidate) count++;
        }

        return count > arr.length / 2 ? candidate : null;
    }

    @Test
    public void testCrossValidationWithMajority() {
        int[] arr = {3, 3, 4, 2, 3};

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertEquals(bruteForce, boyerMoore, "Boyer-Moore must match brute-force");
        assertEquals(streamBased, boyerMoore, "Boyer-Moore must match stream-based");
        assertEquals(sortingBased, boyerMoore, "Boyer-Moore must match sorting-based");
    }

    @Test
    public void testCrossValidationNoMajority() {
        int[] arr = {1, 2, 3, 4};

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertNull(boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }

    @Test
    public void testCrossValidationEmptyArray() {
        int[] arr = {};

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertNull(boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }

    @Test
    public void testCrossValidationSingleElement() {
        int[] arr = {42};

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertEquals(42, boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }

    @Test
    public void testCrossValidationAllSame() {
        int[] arr = {7, 7, 7, 7, 7};

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertEquals(7, boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }

    @Test
    public void testCrossValidationLargeArray() {
        int[] arr = new int[1001];
        for (int i = 0; i < 501; i++) {
            arr[i] = 99;
        }
        for (int i = 501; i < 1001; i++) {
            arr[i] = i;
        }

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertEquals(99, boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }

    @Test
    public void testCrossValidationNegativeNumbers() {
        int[] arr = {-5, -5, -5, 1, 2};

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertEquals(-5, boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }

    @Test
    public void testCrossValidationBoundaryCase() {
        // Exactly n/2 + 1 elements
        int[] arr = {1, 1, 1, 2, 2};

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertEquals(1, boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }

    @Test
    public void testCrossValidationRandomInputs() {
        Random random = new Random(123);

        for (int trial = 0; trial < 50; trial++) {
            int size = 10 + random.nextInt(200);
            int[] arr = new int[size];

            for (int i = 0; i < size; i++) {
                arr[i] = random.nextInt(50);
            }

            Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
            Integer bruteForce = bruteForceMajority(arr);

            assertEquals(bruteForce, boyerMoore,
                "Trial " + trial + ": Boyer-Moore must match brute-force on random input");
        }
    }

    @Test
    public void testCrossValidationAlternatingPattern() {
        int[] arr = {1, 2, 1, 2, 1, 2, 1};

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertEquals(1, boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }

    @Test
    public void testCrossValidationExtremeValues() {
        int[] arr = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE};

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertEquals(Integer.MAX_VALUE, boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }

    @Test
    public void testCrossValidationManyDuplicates() {
        int[] arr = {5, 5, 5, 5, 5, 1, 2, 3, 4};

        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);
        Integer streamBased = streamBasedMajority(arr);
        Integer sortingBased = sortingBasedMajority(arr);

        assertEquals(5, boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }

    @Test
    public void testCrossValidationCandidateOnly() {
        // Test findMajorityElement (without verification) vs brute force candidate
        int[] arr = {1, 2, 3, 4, 5};

        Integer boyerMooreCandidate = BoyerMooreMajorityVote.findMajorityElement(arr);
        Integer verified = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);

        assertNotNull(boyerMooreCandidate, "Should return a candidate");
        assertNull(verified, "Should return null after verification");
        assertEquals(bruteForce, verified, "Verified result must match brute-force");
    }

    @Test
    public void testCrossValidationStreamProcessing() {
        int[] arr = {7, 7, 8, 7, 9, 7};

        BoyerMooreMajorityVote.Context streamContext =
            BoyerMooreMajorityVote.Context.builder()
                .enableStreamProcessing()
                .build();

        Integer boyerMooreStream = BoyerMooreMajorityVote.findMajorityElement(arr, streamContext);
        Integer verified = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        Integer bruteForce = bruteForceMajority(arr);

        assertEquals(verified, bruteForce);
        assertEquals(boyerMooreStream, verified,
            "Stream processing candidate should match verified result");
    }

    @Test
    public void testCrossValidationNullArray() {
        Integer boyerMoore = BoyerMooreMajorityVote.findAndVerifyMajorityElement(null);
        Integer bruteForce = bruteForceMajority(null);
        Integer streamBased = streamBasedMajority(null);
        Integer sortingBased = sortingBasedMajority(null);

        assertNull(boyerMoore);
        assertEquals(bruteForce, boyerMoore);
        assertEquals(streamBased, boyerMoore);
        assertEquals(sortingBased, boyerMoore);
    }
}