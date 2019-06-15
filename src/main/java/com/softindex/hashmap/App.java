package com.softindex.hashmap;

public class App {
    public static void main(String[] args) {

        int[] arr = new int[]{3, 2, 1};
        int[] arr1 = new int[]{0, -1, -2};
        int[] arr2 = new int[]{-3, -4, -5};
        printArr(arr);
        printArr(arr1);
        printArr(arr2);

        System.out.println("-------------------------");

        for (int i = 0; i < 10; i++) {
            System.out.println(i + ": " + highestOneBit(i) + " " + tableSizeFor(i));
        }
    }

    static void printArr(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i] + ": " + hash(arr[i], arr.length));
        }
    }

    static int hash(int key, int length) {
        return (key & Integer.MAX_VALUE) % length;
    }

    static int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : n + 1;
    }

    static int highestOneBit(int i) {
        if ((i & (i - 1)) != 0) {
            return Integer.highestOneBit(i) << 1;
        }
        return i;
    }
}
