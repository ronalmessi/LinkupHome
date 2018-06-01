package com.ihomey.linkuphome;


class Solution {
    public static int[][] flipAndInvertImage(int[][] A) {
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                if (A[i][j] == A[i][A[0].length - j - 1]) {
                    if (A[i][j] == 0) {
                        A[i][j] = 1;
                        A[i][A[0].length - j - 1] = 1;
                    } else {
                        A[i][j] = 0;
                        A[i][A[0].length - j - 1] = 0;
                    }
                }
            }
        }
        return A;
    }

    public static void main(String[] args){
        int[][] A={{1,1,0},{1,0,1},{0,0,0}};
        System.out.print(flipAndInvertImage(A));
    }
}
