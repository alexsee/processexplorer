package org.processmining.plugins.bpmnminer.util;

import java.util.Map;

public class MatrixUtils {
	public static int[][] create2DTaskMatrix(int count, int value) {
		int[][] matrix = new int[count][count];
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				matrix[i][j] = value;
		return matrix;
	}
	
	public static int[] create1DTaskMatrix(int count, int value) {
		int[] matrix = new int[count];
		for (int i = 0; i < matrix.length; i++)
			matrix[i] = value;
		return matrix;
	}
	public static void increaseTaskMatrix(Map<Integer, Integer> task2index, int[][] matrix, int task1, int task2, int value) {
		matrix[task2index.get(task1)][task2index.get(task2)] += value;
	}
	
	public static void increaseTaskMatrix(Map<Integer, Integer> task2index, int[] matrix, int task1, int value) {
		matrix[task2index.get(task1)] += value;
	}
	
	public static int getTaskMatrix(Map<Integer, Integer> task2index, int[][] matrix, int task1, int task2) {
		return matrix[task2index.get(task1)][task2index.get(task2)];
	}
	
	public static int getTaskMatrix(Map<Integer, Integer> task2index, int[] matrix, int task1) {
		return matrix[task2index.get(task1)];
	}
	
	public static void setTaskMatrix(Map<Integer, Integer> task2index, int[][] matrix, int task1, int task2, int value) {
		matrix[task2index.get(task1)][task2index.get(task2)] = value;
	}
	
	public static void setTaskMatrix(Map<Integer, Integer> task2index, int[] matrix, int task1, int value) {
		matrix[task2index.get(task1)] = value;
	}
}
