import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import java.util.List;

public class SortingAlgorithms {

    public static void bubbleSort(JsonArray arr, List<int[]> steps) {
        int[] array = jsonArrayToIntArray(arr);
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    steps.add(array.clone());
                }
            }
        }
    }

    public static void insertionSort(JsonArray arr, List<int[]> steps) {
        int[] array = jsonArrayToIntArray(arr);
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;
                steps.add(array.clone());
            }
            array[j + 1] = key;
            steps.add(array.clone());
        }
    }

    public static void selectionSort(JsonArray arr, List<int[]> steps) {
        int[] array = jsonArrayToIntArray(arr);
        for (int i = 0; i < array.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[minIdx]) {
                    minIdx = j;
                }
            }
            int temp = array[minIdx];
            array[minIdx] = array[i];
            array[i] = temp;
            steps.add(array.clone());
        }
    }

    public static void mergeSort(JsonArray arr, int l, int r, List<int[]> steps) {
        int[] array = jsonArrayToIntArray(arr);
        mergeSortInternal(array, l, r, steps);
    }

    private static void mergeSortInternal(int[] array, int l, int r, List<int[]> steps) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSortInternal(array, l, m, steps);
            mergeSortInternal(array, m + 1, r, steps);
            merge(array, l, m, r, steps);
        }
    }

    private static void merge(int[] array, int l, int m, int r, List<int[]> steps) {
        int n1 = m - l + 1;
        int n2 = r - m;
        int[] L = new int[n1];
        int[] R = new int[n2];
        System.arraycopy(array, l, L, 0, n1);
        System.arraycopy(array, m + 1, R, 0, n2);
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                array[k++] = L[i++];
            } else {
                array[k++] = R[j++];
            }
            steps.add(array.clone());
        }
        while (i < n1) {
            array[k++] = L[i++];
            steps.add(array.clone());
        }
        while (j < n2) {
            array[k++] = R[j++];
            steps.add(array.clone());
        }
    }

    public static void quickSort(JsonArray arr, int low, int high, List<int[]> steps) {
        int[] array = jsonArrayToIntArray(arr);
        quickSortInternal(array, low, high, steps);
    }

    private static void quickSortInternal(int[] array, int low, int high, List<int[]> steps) {
        if (low < high) {
            int pi = partition(array, low, high, steps);
            quickSortInternal(array, low, pi - 1, steps);
            quickSortInternal(array, pi + 1, high, steps);
        }
    }

    private static int partition(int[] array, int low, int high, List<int[]> steps) {
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                int temp = array[++i];
                array[i] = array[j];
                array[j] = temp;
                steps.add(array.clone());
            }
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        steps.add(array.clone());
        return i + 1;
    }

    public static void heapSort(JsonArray arr, List<int[]> steps) {
        int[] array = jsonArrayToIntArray(arr);
        int n = array.length;
        for (int i = n / 2 - 1; i >= 0; i--) heapify(array, n, i, steps);
        for (int i = n - 1; i > 0; i--) {
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;
            steps.add(array.clone());
            heapify(array, i, 0, steps);
        }
    }

    private static void heapify(int[] arr, int n, int i, List<int[]> steps) {
        int largest = i, l = 2 * i + 1, r = 2 * i + 2;
        if (l < n && arr[l] > arr[largest]) largest = l;
        if (r < n && arr[r] > arr[largest]) largest = r;
        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
            steps.add(arr.clone());
            heapify(arr, n, largest, steps);
        }
    }

    public static void shellSort(JsonArray arr, List<int[]> steps) {
        int[] array = jsonArrayToIntArray(arr);
        int n = array.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = array[i];
                int j = i;
                while (j >= gap && array[j - gap] > temp) {
                    array[j] = array[j - gap];
                    j -= gap;
                    steps.add(array.clone());
                }
                array[j] = temp;
                steps.add(array.clone());
            }
        }
    }

    public static void radixSort(JsonArray arr, List<int[]> steps) {
        int[] array = jsonArrayToIntArray(arr);
        int max = getMax(array);
        for (int exp = 1; max / exp > 0; exp *= 10)
            countSort(array, exp, steps);
    }

    private static int getMax(int[] arr) {
        int max = arr[0];
        for (int val : arr) if (val > max) max = val;
        return max;
    }

    private static void countSort(int[] arr, int exp, List<int[]> steps) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10];

        for (int i = 0; i < n; i++) count[(arr[i] / exp) % 10]++;
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];
        for (int i = n - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % 10;
            output[--count[digit]] = arr[i];
        }
        for (int i = 0; i < n; i++) {
            arr[i] = output[i];
            steps.add(arr.clone());
        }
    }

    private static int[] jsonArrayToIntArray(JsonArray arr) {
        int[] result = new int[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            result[i] = arr.get(i).getAsInt();
        }
        return result;
    }
}
