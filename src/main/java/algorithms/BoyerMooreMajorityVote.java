package algorithms;

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
        if (arr == null || arr.length == 0) {
            return null;
        }

        // Find candidate
        int candidate = arr[0];
        int count = 1;

        for (int i = 1; i < arr.length; i++) {
            if (count == 0) {
                candidate = arr[i];
                count = 1;
            } else if (arr[i] == candidate) {
                count++;
            } else {
                count--;
            }
        }

        return candidate;
    }

    /**
     * @param arr the input array
     * @return the majority element if it exists and is verified, otherwise null
     */
    public static Integer findAndVerifyMajorityElement(int[] arr) {
        Integer candidate = findMajorityElement(arr);

        if (candidate == null) {
            return null;
        }

        // Verify candidate
        int count = 0;
        for (int num : arr) {
            if (num == candidate) {
                count++;
            }
        }

        return count > arr.length / 2 ? candidate : null;
    }
}