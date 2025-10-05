package algorithms;

import metrics.MetricsCollector;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Boyer-Moore Majority Vote Algorithm with Iterator
 */
public class BoyerMooreMajorityVoteIteratorTest {

    @Test
    public void testIteratorNullThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            BoyerMooreMajorityVote.findMajorityElementIterator(null, null);
        }, "Null iterator should throw NullPointerException");
    }

    @Test
    public void testIteratorEmpty() {
        Iterator<Integer> iterator = Collections.emptyIterator();
        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(iterator, null);
        assertNull(result, "Empty iterator should return null");
    }

    @Test
    public void testIteratorSingleElement() {
        List<Integer> list = Collections.singletonList(42);
        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), null);
        assertEquals(42, result);
    }

    @Test
    public void testIteratorSimpleMajority() {
        List<Integer> list = Arrays.asList(3, 3, 1, 3, 2);
        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), null);
        assertEquals(3, result);
    }

    @Test
    public void testIteratorWithMetrics() {
        List<Integer> list = Arrays.asList(5, 5, 6, 5, 7, 5);
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), context);

        assertEquals(5, result);
        assertTrue(metrics.getComparisons() > 0);
        assertTrue(metrics.getExecutionTimeMillis() >= 0);
    }

    @Test
    public void testIteratorAllSame() {
        List<Integer> list = Arrays.asList(9, 9, 9, 9);
        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), null);
        assertEquals(9, result);
    }

    @Test
    public void testIteratorNoMajority() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), null);
        assertNotNull(result, "Should return a candidate");
    }

    @Test
    public void testIteratorNegativeNumbers() {
        List<Integer> list = Arrays.asList(-5, -5, -5, 1, 2);
        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), null);
        assertEquals(-5, result);
    }

    @Test
    public void testIteratorLargeDataset() {
        Integer[] arr = new Integer[1001];
        for (int i = 0; i < 501; i++) {
            arr[i] = 100;
        }
        for (int i = 501; i < 1001; i++) {
            arr[i] = i;
        }

        List<Integer> list = Arrays.asList(arr);
        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), null);
        assertEquals(100, result);
    }

    @Test
    public void testIteratorWithNullElementThrowsException() {
        List<Integer> list = Arrays.asList(1, null, 3);

        assertThrows(NullPointerException.class, () -> {
            BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), null);
        }, "Iterator with null elements should throw NullPointerException");
    }

    @Test
    public void testIteratorFirstElementNull() {
        List<Integer> list = Arrays.asList(null, 1, 2);

        assertThrows(NullPointerException.class, () -> {
            BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), null);
        }, "Iterator starting with null should throw NullPointerException");
    }

    @Test
    public void testIteratorCandidateChangesTracked() {
        List<Integer> list = Arrays.asList(1, 2, 3, 3, 3);
        MetricsCollector metrics = new MetricsCollector();

        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .build();

        BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), context);

        assertTrue(metrics.getCandidateChanges() >= 0);
    }

    @Test
    public void testIteratorTwoElements() {
        List<Integer> list = Arrays.asList(7, 8);
        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), null);
        assertNotNull(result);
        assertTrue(result == 7 || result == 8);
    }
}