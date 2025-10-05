package algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based testing for Boyer-Moore Majority Vote Algorithm
 * Validates correctness across random inputs with various distributions
 */
public class BoyerMooreMajorityVotePropertyBasedTest {

    private static final Random random = new Random(42); // Fixed seed for reproducibility

    /**
     * Property: If an element appears more than n/2 times, it must be found and verified
     */
    @RepeatedTest(20)
    public void testPropertyMajorityAlwaysFound() {
        int size = 100 + random.nextInt(900); // 100 to 1000
        int[] arr = new int[size];
        int majorityValue = random.nextInt(1000);
        int majorityCount = size / 2 + 1 + random.nextInt(size / 2);

        // Fill majority element
        for (int i = 0; i < majorityCount; i++) {
            arr[i] = majorityValue;
        }

        // Fill rest with random values (ensuring they're different from majority)
        for (int i = majorityCount; i < size; i++) {
            int val;
            do {
                val = random.nextInt(1000);
            } while (val == majorityValue);
            arr[i] = val;
        }

        // Shuffle array
        shuffleArray(arr);

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(majorityValue, result,
            "Majority element must be found when it appears > n/2 times");
    }

    /**
     * Property: If no element appears more than n/2 times, verification must return null
     */
    @RepeatedTest(20)
    public void testPropertyNoMajorityReturnsNull() {
        int size = 100 + random.nextInt(400); // 100 to 500
        int[] arr = new int[size];

        // Create array where no element appears more than n/2 times
        int distinct = 3 + random.nextInt(7); // 3 to 10 distinct values
        int maxCountPerValue = size / distinct;

        int index = 0;
        for (int value = 0; value < distinct && index < size; value++) {
            int count = Math.min(maxCountPerValue, size - index);
            for (int i = 0; i < count; i++) {
                arr[index++] = value;
            }
        }

        shuffleArray(arr);

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);

        // Verify manually that no majority exists
        Map<Integer, Integer> counts = new HashMap<>();
        for (int num : arr) {
            counts.put(num, counts.getOrDefault(num, 0) + 1);
        }

        boolean hasMajority = counts.values().stream().anyMatch(c -> c > arr.length / 2);

        if (hasMajority) {
            assertNotNull(result, "If majority exists, it should be found");
        } else {
            assertNull(result, "No element appears > n/2 times, should return null");
        }
    }

    /**
     * Property: Algorithm should give same result regardless of element order (permutation invariant)
     */
    @RepeatedTest(10)
    public void testPropertyPermutationInvariance() {
        int[] arr = {5, 5, 1, 5, 2, 5, 3};
        int[] copy1 = arr.clone();
        int[] copy2 = arr.clone();
        int[] copy3 = arr.clone();

        shuffleArray(copy1);
        shuffleArray(copy2);
        shuffleArray(copy3);

        Integer result1 = BoyerMooreMajorityVote.findAndVerifyMajorityElement(copy1);
        Integer result2 = BoyerMooreMajorityVote.findAndVerifyMajorityElement(copy2);
        Integer result3 = BoyerMooreMajorityVote.findAndVerifyMajorityElement(copy3);

        assertEquals(result1, result2, "Results must be same for different permutations");
        assertEquals(result2, result3, "Results must be same for different permutations");
    }

    /**
     * Property: Verification must be consistent with manual counting
     */
    @RepeatedTest(20)
    public void testPropertyVerificationConsistency() {
        int size = 50 + random.nextInt(200);
        int[] arr = generateRandomArray(size, 20);

        Integer candidate = BoyerMooreMajorityVote.findMajorityElement(arr);

        if (candidate != null) {
            // Manual count
            int count = 0;
            for (int num : arr) {
                if (num == candidate) count++;
            }

            boolean isMajority = count > arr.length / 2;
            boolean verified = BoyerMooreMajorityVote.verifyCandidate(arr, candidate, null);

            assertEquals(isMajority, verified,
                "Verification result must match manual counting");
        }
    }

    /**
     * Property: Algorithm works correctly on sorted arrays
     */
    @RepeatedTest(10)
    public void testPropertySortedArrays() {
        int size = 100 + random.nextInt(400);
        int[] arr = new int[size];
        int majorityValue = random.nextInt(100);
        int majorityCount = size / 2 + 1;

        // Create sorted array with majority element
        for (int i = 0; i < majorityCount; i++) {
            arr[i] = majorityValue;
        }
        for (int i = majorityCount; i < size; i++) {
            arr[i] = majorityValue + 1 + random.nextInt(50);
        }

        // Array is already sorted
        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(majorityValue, result, "Must work on sorted arrays");
    }

    /**
     * Property: Algorithm works correctly on reverse-sorted arrays
     */
    @RepeatedTest(10)
    public void testPropertyReverseSortedArrays() {
        int size = 100 + random.nextInt(400);
        int[] arr = new int[size];
        int majorityValue = random.nextInt(100);
        int majorityCount = size / 2 + 1;

        // Create reverse-sorted array
        for (int i = 0; i < size - majorityCount; i++) {
            arr[i] = majorityValue + 50 + random.nextInt(50);
        }
        for (int i = size - majorityCount; i < size; i++) {
            arr[i] = majorityValue;
        }

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(majorityValue, result, "Must work on reverse-sorted arrays");
    }

    /**
     * Property: Algorithm works with nearly-sorted data (few swaps)
     */
    @RepeatedTest(10)
    public void testPropertyNearlySortedArrays() {
        int size = 200;
        int[] arr = new int[size];
        int majorityValue = 50;
        int majorityCount = 120;

        // Create mostly sorted array
        for (int i = 0; i < majorityCount; i++) {
            arr[i] = majorityValue;
        }
        for (int i = majorityCount; i < size; i++) {
            arr[i] = i;
        }

        // Perform a few random swaps (10% of array size)
        for (int i = 0; i < size / 10; i++) {
            int idx1 = random.nextInt(size);
            int idx2 = random.nextInt(size);
            int temp = arr[idx1];
            arr[idx1] = arr[idx2];
            arr[idx2] = temp;
        }

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);
        assertEquals(majorityValue, result, "Must work on nearly-sorted arrays");
    }

    /**
     * Property: Algorithm handles arrays with duplicate values correctly
     */
    @RepeatedTest(20)
    public void testPropertyDuplicateValues() {
        int size = 100 + random.nextInt(200);
        int[] arr = new int[size];

        // Fill with only 2-3 distinct values
        int numDistinct = 2 + random.nextInt(2);
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(numDistinct);
        }

        Integer candidate = BoyerMooreMajorityVote.findMajorityElement(arr);
        Integer verified = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);

        assertNotNull(candidate, "Should always find a candidate");

        // Manually verify
        Map<Integer, Integer> counts = new HashMap<>();
        for (int num : arr) {
            counts.put(num, counts.getOrDefault(num, 0) + 1);
        }

        Integer expectedMajority = null;
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > arr.length / 2) {
                expectedMajority = entry.getKey();
                break;
            }
        }

        assertEquals(expectedMajority, verified,
            "Verification must match expected majority");
    }

    /**
     * Property: Extreme value distributions (all min, all max, mixed extremes)
     */
    @Test
    public void testPropertyExtremeValues() {
        // All minimum values
        int[] allMin = new int[100];
        for (int i = 0; i < 100; i++) {
            allMin[i] = Integer.MIN_VALUE;
        }
        assertEquals(Integer.MIN_VALUE,
            BoyerMooreMajorityVote.findAndVerifyMajorityElement(allMin));

        // All maximum values
        int[] allMax = new int[100];
        for (int i = 0; i < 100; i++) {
            allMax[i] = Integer.MAX_VALUE;
        }
        assertEquals(Integer.MAX_VALUE,
            BoyerMooreMajorityVote.findAndVerifyMajorityElement(allMax));

        // Mixed extremes with majority
        int[] mixed = new int[101];
        for (int i = 0; i < 51; i++) {
            mixed[i] = Integer.MIN_VALUE;
        }
        for (int i = 51; i < 101; i++) {
            mixed[i] = Integer.MAX_VALUE;
        }
        assertEquals(Integer.MIN_VALUE,
            BoyerMooreMajorityVote.findAndVerifyMajorityElement(mixed));
    }

    /**
     * Property: Consistency between array and iterator implementations
     */
    @RepeatedTest(10)
    public void testPropertyArrayIteratorConsistency() {
        int size = 100 + random.nextInt(200);
        int[] arr = generateRandomArrayWithMajority(size);

        Integer arrayResult = BoyerMooreMajorityVote.findMajorityElement(arr);

        // Convert to iterator
        java.util.List<Integer> list = new java.util.ArrayList<>();
        for (int num : arr) {
            list.add(num);
        }
        Integer iteratorResult = BoyerMooreMajorityVote.findMajorityElementIterator(
            list.iterator(), null);

        assertEquals(arrayResult, iteratorResult,
            "Array and iterator implementations must give same result");
    }

    // Helper methods

    private void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private int[] generateRandomArray(int size, int maxValue) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(maxValue);
        }
        return arr;
    }

    private int[] generateRandomArrayWithMajority(int size) {
        int[] arr = new int[size];
        int majorityValue = random.nextInt(100);
        int majorityCount = size / 2 + 1;

        for (int i = 0; i < majorityCount; i++) {
            arr[i] = majorityValue;
        }
        for (int i = majorityCount; i < size; i++) {
            arr[i] = random.nextInt(100);
        }

        shuffleArray(arr);
        return arr;
    }
}