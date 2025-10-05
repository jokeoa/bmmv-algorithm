package algorithms;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test cases for Boyer-Moore Majority Vote Algorithm
 */
public class BoyerMooreMajorityVoteBasicTest {

    @Test
    public void testNullArray() {
        Integer result = BoyerMooreMajorityVote.findMajorityElement(null);
        assertNull(result, "Null array should return null");
    }

    @Test
    public void testEmptyArray() {
        int[] arr = {};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertNull(result, "Empty array should return null");
    }

    @Test
    public void testSingleElement() {
        int[] arr = {5};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(5, result, "Single element should be the majority");
    }

    @Test
    public void testTwoElementsSame() {
        int[] arr = {3, 3};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(3, result, "Both same elements should return that element");
    }

    @Test
    public void testTwoElementsDifferent() {
        int[] arr = {3, 5};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertNotNull(result, "Should return a candidate");
        assertTrue(result == 3 || result == 5, "Candidate should be one of the elements");
    }

    @Test
    public void testSimpleMajority() {
        int[] arr = {2, 2, 1, 1, 2};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(2, result, "2 appears 3 times out of 5");
    }

    @Test
    public void testAllSameElements() {
        int[] arr = {7, 7, 7, 7, 7};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(7, result, "All elements are 7");
    }

    @Test
    public void testMajorityAtEnd() {
        int[] arr = {1, 2, 3, 4, 4, 4, 4};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(4, result, "Majority element at the end");
    }

    @Test
    public void testMajorityAtStart() {
        int[] arr = {5, 5, 5, 5, 1, 2, 3};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(5, result, "Majority element at the start");
    }

    @Test
    public void testMajorityScattered() {
        int[] arr = {3, 1, 3, 2, 3, 4, 3};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(3, result, "Majority element scattered throughout");
    }

    @Test
    public void testNoMajorityReturnsCandidate() {
        int[] arr = {1, 2, 3, 4};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertNotNull(result, "Should return a candidate even without majority");
    }

    @Test
    public void testNegativeNumbers() {
        int[] arr = {-1, -1, -1, 2, 2};
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(-1, result, "Should handle negative numbers");
    }

    @Test
    public void testLargeArray() {
        int[] arr = new int[1001];
        for (int i = 0; i < 501; i++) {
            arr[i] = 42;
        }
        for (int i = 501; i < 1001; i++) {
            arr[i] = i;
        }
        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);
        assertEquals(42, result, "Should find majority in large array");
    }
}