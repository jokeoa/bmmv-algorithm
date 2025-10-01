package algorithms;

import metrics.MetricsCollector;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * Implementation of the Boyer-Moore Majority Vote Algorithm
 * in O(n) time and O(1) space.
 */
public class BoyerMooreMajorityVote {

    /**
     * Context class encapsulating metrics collection and optimization settings.
     * Memory-efficient design with optional components.
     */
    public static class Context {
        private final MetricsCollector metrics;
        private final boolean enableEarlyTermination;
        private final boolean enableStreamProcessing;

        private Context(Builder builder) {
            this.metrics = builder.metrics;
            this.enableEarlyTermination = builder.enableEarlyTermination;
            this.enableStreamProcessing = builder.enableStreamProcessing;
        }

        public MetricsCollector getMetrics() {
            return metrics;
        }

        public boolean isEarlyTerminationEnabled() {
            return enableEarlyTermination;
        }

        public boolean isStreamProcessingEnabled() {
            return enableStreamProcessing;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private MetricsCollector metrics;
            private boolean enableEarlyTermination = false;
            private boolean enableStreamProcessing = false;

            public Builder withMetrics(MetricsCollector metrics) {
                this.metrics = metrics;
                return this;
            }

            public Builder enableEarlyTermination() {
                this.enableEarlyTermination = true;
                return this;
            }

            public Builder enableStreamProcessing() {
                this.enableStreamProcessing = true;
                return this;
            }

            public Context build() {
                return new Context(this);
            }
        }
    }

    /**
     * @param arr the input array
     * @return the majority element if it exists, otherwise returns the candidate
     */
    public static Integer findMajorityElement(int[] arr) {
        return findMajorityElement(arr, (Context) null);
    }

    /**
     * @param arr the input array
     * @param metrics optional metrics collector to track algorithm performance
     * @return the majority element if it exists, otherwise returns the candidate
     * @deprecated Use {@link #findMajorityElement(int[], Context)} instead
     */
    @Deprecated
    public static Integer findMajorityElement(int[] arr, MetricsCollector metrics) {
        Context ctx = metrics != null ? Context.builder().withMetrics(metrics).build() : null;
        return findMajorityElement(arr, ctx);
    }

    /**
     * @param arr the input array
     * @param context optional context with metrics and optimizations
     * @return the majority element if it exists, otherwise returns the candidate
     */
    public static Integer findMajorityElement(int[] arr, Context context) {
        if (context != null && context.getMetrics() != null) {
            context.getMetrics().startTimer();
        }

        try {
            // Handle null or empty array
            if (arr == null || arr.length == 0) {
                return null;
            }

            // Single element array is always the majority
            if (arr.length == 1) {
                return arr[0];
            }

            // Use streaming approach if enabled
            if (context != null && context.isStreamProcessingEnabled()) {
                return findMajorityElementStream(arr, context);
            }

            // Find candidate
            int candidate = arr[0];
            int count = 1;

            for (int i = 1; i < arr.length; i++) {
                if (context != null && context.getMetrics() != null) {
                    context.getMetrics().incrementComparisons();
                }

                if (count == 0) {
                    candidate = arr[i];
                    count = 1;
                    if (context != null && context.getMetrics() != null) {
                        context.getMetrics().incrementCandidateChanges();
                    }
                } else if (arr[i] == candidate) {
                    count++;
                } else {
                    count--;
                }
            }

            return candidate;
        } finally {
            if (context != null && context.getMetrics() != null) {
                context.getMetrics().stopTimer();
            }
        }
    }

    /**
     * Memory-efficient streaming implementation using IntStream.
     * Processes elements one at a time without additional memory allocation.
     */
    private static Integer findMajorityElementStream(int[] arr, Context context) {
        
        class State {
            int candidate = arr[0];
            int count = 1;
        }

        State state = new State();
        MetricsCollector metrics = context != null ? context.getMetrics() : null;

        IntStream.range(1, arr.length).forEach(i -> {
            if (metrics != null) {
                metrics.incrementComparisons();
            }

            if (state.count == 0) {
                state.candidate = arr[i];
                state.count = 1;
                if (metrics != null) {
                    metrics.incrementCandidateChanges();
                }
            } else if (arr[i] == state.candidate) {
                state.count++;
            } else {
                state.count--;
            }
        });

        if (metrics != null) {
            metrics.stopTimer();
        }

        return state.candidate;
    }

    /**
     * Memory-efficient iterator-based implementation.
     * Useful for processing large datasets that don't fit in memory.
     *
     * @param elements iterator over elements (must not be null)
     * @param context optional context with metrics and optimizations
     * @return the majority element candidate, or null if iterator is null/empty
     * @throws NullPointerException if elements iterator is null
     */
    public static Integer findMajorityElementIterator(Iterator<Integer> elements, Context context) {
        if (elements == null) {
            throw new NullPointerException("Iterator cannot be null");
        }

        if (context != null && context.getMetrics() != null) {
            context.getMetrics().startTimer();
        }

        try {
            if (!elements.hasNext()) {
                return null;
            }

            Integer first = elements.next();
            if (first == null) {
                throw new NullPointerException("Iterator contains null elements");
            }

            // Single element case
            if (!elements.hasNext()) {
                return first;
            }

            int candidate = first;
            int count = 1;

            while (elements.hasNext()) {
                Integer current = elements.next();
                if (current == null) {
                    throw new NullPointerException("Iterator contains null elements");
                }

                if (context != null && context.getMetrics() != null) {
                    context.getMetrics().incrementComparisons();
                }

                if (count == 0) {
                    candidate = current;
                    count = 1;
                    if (context != null && context.getMetrics() != null) {
                        context.getMetrics().incrementCandidateChanges();
                    }
                } else if (current.equals(candidate)) {
                    count++;
                } else {
                    count--;
                }
            }

            return candidate;
        } finally {
            if (context != null && context.getMetrics() != null) {
                context.getMetrics().stopTimer();
            }
        }
    }

    /**
     * @param arr the input array
     * @return the majority element if it exists and is verified, otherwise null
     */
    public static Integer findAndVerifyMajorityElement(int[] arr) {
        return findAndVerifyMajorityElement(arr, (Context) null);
    }

    /**
     * @param arr the input array
     * @param metrics optional metrics collector to track algorithm performance
     * @return the majority element if it exists and is verified, otherwise null
     * @deprecated Use {@link #findAndVerifyMajorityElement(int[], Context)} instead
     */
    @Deprecated
    public static Integer findAndVerifyMajorityElement(int[] arr, MetricsCollector metrics) {
        Context ctx = metrics != null ? Context.builder().withMetrics(metrics).build() : null;
        return findAndVerifyMajorityElement(arr, ctx);
    }

    /**
     * @param arr the input array
     * @param context optional context with metrics and optimizations
     * @return the majority element if it exists and is verified, otherwise null
     */
    public static Integer findAndVerifyMajorityElement(int[] arr, Context context) {
        Integer candidate = findMajorityElement(arr, context);

        if (candidate == null) {
            return null;
        }

        // Verify candidate with early termination optimization
        // Use long to prevent integer overflow for large arrays
        long count = 0;
        long threshold = arr.length / 2L;

        for (int num : arr) {
            if (context != null && context.getMetrics() != null) {
                context.getMetrics().incrementComparisons();
            }
            if (num == candidate) {
                count++;
                // Early termination: if we've already found majority, stop
                if (context != null && context.isEarlyTerminationEnabled() && count > threshold) {
                    return candidate;
                }
            }
        }

        return count > threshold ? candidate : null;
    }

    /**
     * Memory-efficient verification using streaming approach.
     *
     * @param arr the input array
     * @param candidate the candidate to verify
     * @param context optional context with metrics and optimizations
     * @return true if candidate is majority element
     */
    public static boolean verifyCandidate(int[] arr, int candidate, Context context) {
        if (arr == null || arr.length == 0) {
            return false;
        }

        long count = IntStream.of(arr)
            .filter(num -> {
                if (context != null && context.getMetrics() != null) {
                    context.getMetrics().incrementComparisons();
                }
                return num == candidate;
            })
            .count();

        return count > arr.length / 2;
    }
}