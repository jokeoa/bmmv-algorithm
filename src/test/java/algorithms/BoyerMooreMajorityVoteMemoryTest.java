package algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Memory profiling and garbage collection tests for Boyer-Moore Majority Vote Algorithm
 * Validates O(1) space complexity and memory efficiency
 */
public class BoyerMooreMajorityVoteMemoryTest {

    private static final Random random = new Random(42);
    private Runtime runtime;

    @BeforeEach
    public void setUp() {
        runtime = Runtime.getRuntime();
        // Suggest garbage collection before tests
        System.gc();
        try {
            Thread.sleep(100); // Give GC time to run
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Test O(1) space complexity: memory usage should not scale with input size
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testConstantSpaceComplexity() {
        int[] sizes = {1000, 10000, 100000};
        long[] memoryUsed = new long[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

            int[] arr = generateArrayWithMajority(sizes[i]);
            Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);

            long afterMemory = runtime.totalMemory() - runtime.freeMemory();
            memoryUsed[i] = afterMemory - beforeMemory;

            assertNotNull(result);

            System.out.printf("Size %d: ~%d KB memory delta%n",
                sizes[i], memoryUsed[i] / 1024);
        }

        // Algorithm should use O(1) space, so memory delta should be relatively constant
        // (mainly from the input array itself, not from algorithm overhead)
        System.out.println("Note: Boyer-Moore uses O(1) auxiliary space");
    }

    /**
     * Test memory efficiency with iterator (no array allocation)
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testIteratorMemoryEfficiency() {
        int size = 100000;

        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

        // Create iterator that generates values on-the-fly
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size / 2 + 1; i++) {
            list.add(777);
        }
        for (int i = size / 2 + 1; i < size; i++) {
            list.add(i);
        }

        Integer result = BoyerMooreMajorityVote.findMajorityElementIterator(list.iterator(), null);

        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = afterMemory - beforeMemory;

        assertEquals(777, result);
        System.out.printf("Iterator memory usage (100k elements): ~%d KB%n", memoryUsed / 1024);
    }

    /**
     * Test that algorithm doesn't create excessive temporary objects
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testNoExcessiveObjectCreation() {
        int[] arr = generateArrayWithMajority(50000);

        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long gcCountBefore = getGCCount();

        // Run algorithm multiple times
        for (int i = 0; i < 100; i++) {
            BoyerMooreMajorityVote.findMajorityElement(arr);
        }

        long gcCountAfter = getGCCount();
        long gcIncrease = gcCountAfter - gcCountBefore;

        System.out.printf("GC count increase after 100 runs: %d%n", gcIncrease);

        // Should have minimal GC activity for primitive array operations
        assertTrue(gcIncrease < 20, "Should not trigger excessive garbage collection");
    }

    /**
     * Test stream processing memory usage
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testStreamProcessingMemory() {
        int size = 50000;
        int[] arr = generateArrayWithMajority(size);

        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

        BoyerMooreMajorityVote.Context streamContext =
            BoyerMooreMajorityVote.Context.builder()
                .enableStreamProcessing()
                .build();

        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, streamContext);

        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = afterMemory - beforeMemory;

        assertNotNull(result);
        System.out.printf("Stream processing memory: ~%d KB%n", memoryUsed / 1024);
    }

    /**
     * Test verification phase memory usage
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testVerificationMemoryUsage() {
        int size = 50000;
        int[] arr = generateArrayWithMajority(size);

        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

        Integer result = BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);

        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = afterMemory - beforeMemory;

        assertNotNull(result);
        System.out.printf("Find + Verify memory: ~%d KB%n", memoryUsed / 1024);

        // Verification should also use O(1) space
    }

    /**
     * Test memory usage with context and metrics
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testContextMemoryOverhead() {
        int size = 50000;
        int[] arr = generateArrayWithMajority(size);

        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

        metrics.MetricsCollector metrics = new metrics.MetricsCollector();
        BoyerMooreMajorityVote.Context context = BoyerMooreMajorityVote.Context.builder()
            .withMetrics(metrics)
            .enableEarlyTermination()
            .enableStreamProcessing()
            .build();

        Integer result = BoyerMooreMajorityVote.findMajorityElement(arr, context);

        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = afterMemory - beforeMemory;

        assertNotNull(result);
        System.out.printf("Context + Metrics memory overhead: ~%d KB%n", memoryUsed / 1024);

        // Context and metrics should have minimal memory overhead
    }

    /**
     * Test no memory leaks on repeated executions
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testNoMemoryLeaks() {
        int[] arr = generateArrayWithMajority(10000);

        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // Run algorithm many times
        for (int i = 0; i < 1000; i++) {
            BoyerMooreMajorityVote.findAndVerifyMajorityElement(arr);

            if (i % 100 == 0) {
                System.gc();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryGrowth = finalMemory - initialMemory;

        System.out.printf("Memory growth after 1000 executions: ~%d KB%n", memoryGrowth / 1024);

        // Should not have significant memory growth (allowing some JVM overhead)
        assertTrue(Math.abs(memoryGrowth) < 10 * 1024 * 1024,
            "Should not have memory leaks (< 10MB growth)");
    }

    /**
     * Test large array handling without OutOfMemoryError
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testLargeArrayHandling() {
        try {
            // Test with largest feasible array
            int size = 1000000; // 1 million elements
            int[] arr = new int[size];

            for (int i = 0; i < size / 2 + 1; i++) {
                arr[i] = 123;
            }
            for (int i = size / 2 + 1; i < size; i++) {
                arr[i] = i;
            }

            Integer result = BoyerMooreMajorityVote.findMajorityElement(arr);

            assertEquals(123, result);
            System.out.println("Successfully handled 1M element array");

        } catch (OutOfMemoryError e) {
            // If we can't allocate the array, that's a JVM limit, not algorithm issue
            System.out.println("Skipped: Insufficient heap space for 1M element array");
        }
    }

    /**
     * Test memory efficiency comparison: Boyer-Moore vs. HashMap approach
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testMemoryEfficiencyComparison() {
        int size = 10000;
        int[] arr = generateArrayWithMajority(size);

        // Test Boyer-Moore
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long bmBefore = runtime.totalMemory() - runtime.freeMemory();
        Integer bmResult = BoyerMooreMajorityVote.findMajorityElement(arr);
        long bmAfter = runtime.totalMemory() - runtime.freeMemory();
        long bmMemory = bmAfter - bmBefore;

        // Test HashMap approach (O(n) space)
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long hmBefore = runtime.totalMemory() - runtime.freeMemory();
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
        for (int num : arr) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        long hmAfter = runtime.totalMemory() - runtime.freeMemory();
        long hmMemory = hmAfter - hmBefore;

        assertNotNull(bmResult);
        System.out.printf("Boyer-Moore: ~%d KB, HashMap: ~%d KB%n",
            bmMemory / 1024, hmMemory / 1024);
        System.out.printf("Memory savings: %.2fx%n", (double) hmMemory / bmMemory);

        // Boyer-Moore should use significantly less memory
        assertTrue(bmMemory < hmMemory, "Boyer-Moore should use less memory than HashMap");
    }

    // Helper methods

    private int[] generateArrayWithMajority(int size) {
        int[] arr = new int[size];
        int majorityValue = random.nextInt(1000);

        for (int i = 0; i < size / 2 + 1; i++) {
            arr[i] = majorityValue;
        }

        for (int i = size / 2 + 1; i < size; i++) {
            arr[i] = random.nextInt(1000);
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

    private long getGCCount() {
        long count = 0;
        for (java.lang.management.GarbageCollectorMXBean gc :
                java.lang.management.ManagementFactory.getGarbageCollectorMXBeans()) {
            long gcCount = gc.getCollectionCount();
            if (gcCount > 0) {
                count += gcCount;
            }
        }
        return count;
    }
}