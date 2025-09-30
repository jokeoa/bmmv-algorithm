package algorithms;

import metrics.MetricsCollector;

/**
 * Implementation of the Boyer-Moore Majority Vote Algorithm
 * in O(n) time and O(1) space.
 */
public class BoyerMooreMajorityVote {

    /**
     * @param arr the input array
     * @return the majority element if it exists, otherwise returns the candidate
     */
    public static Integer findMajorityElement(int[] arr) {
        return findMajorityElement(arr, null);
    }

    /**
     * @param arr the input array
     * @param metrics optional metrics collector to track algorithm performance
     * @return the majority element if it exists, otherwise returns the candidate
     */
    public static Integer findMajorityElement(int[] arr, MetricsCollector metrics) {
        if (metrics != null) {
            metrics.startTimer();
        }

        if (arr == null || arr.length == 0) {
            if (metrics != null) {
                metrics.stopTimer();
            }
            return null;
        }

        // Find candidate
        int candidate = arr[0];
        int count = 1;

        for (int i = 1; i < arr.length; i++) {
            if (metrics != null) {
                metrics.incrementComparisons();
            }

            if (count == 0) {
                candidate = arr[i];
                count = 1;
                if (metrics != null) {
                    metrics.incrementCandidateChanges();
                }
            } else if (arr[i] == candidate) {
                count++;
            } else {
                count--;
            }
        }

        if (metrics != null) {
            metrics.stopTimer();
        }

        return candidate;
    }

    /**
     * @param arr the input array
     * @return the majority element if it exists and is verified, otherwise null
     */
    public static Integer findAndVerifyMajorityElement(int[] arr) {
        return findAndVerifyMajorityElement(arr, null);
    }

    /**
     * @param arr the input array
     * @param metrics optional metrics collector to track algorithm performance
     * @return the majority element if it exists and is verified, otherwise null
     */
    public static Integer findAndVerifyMajorityElement(int[] arr, MetricsCollector metrics) {
        Integer candidate = findMajorityElement(arr, metrics);

        if (candidate == null) {
            return null;
        }

        // Verify candidate
        int count = 0;
        for (int num : arr) {
            if (metrics != null) {
                metrics.incrementComparisons();
            }
            if (num == candidate) {
                count++;
            }
        }

        return count > arr.length / 2 ? candidate : null;
    }
}