package com.miao.fenzhifa;

import java.util.Scanner;

public class Test {
	
	private static int counts;//整数的个数
	private static int position;//第k小元素的位置
	private static int k;
	private static int[] sourceData;//原整数数组
	private static int[] result;//数组的备份，在问题1中原数组被修改
	
	private static int sum;
	
	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("**********分治法试验***********");
			System.out.println(">>>请输入元素的个数：");
			System.out.print(">>>");
			counts = input.nextInt();
			
			//初始化数组并从键盘获取输入
			sourceData = new int[counts + 1];
			result = new int[counts + 1];
			
			sourceData[0] = 0;
			for(int i = 1; i <= counts; i++) {
				sourceData[i] = input.nextInt();
			}
			result =sourceData.clone();
			//问题1
			System.out.println("请输入第 k小元素的 k的值：");
			System.out.print(">>>");
			k = input.nextInt();
			
			Test test = new Test();
			
			test.quicksort(1, counts);
			position = findPos(result,sourceData[k]);

			System.out.println("数组中第 " + k + "小元素的值为：" + sourceData[k] 
					+ "    在数组中的位置为：" + position);
			
			System.out.println("**********连续字段问题**********");
			
			int maxSum = test.MaxSum(1, counts);
			
			System.out.println("最大的连续子段和为：" + maxSum);
			
			System.out.println("连续字段的序号为：" + start + "->" + end);
			
			test.inputBoard();
		
	}
		
	//问题1的函数
	
	private static int partition(int start, int end) {
		
		int key = sourceData[start];
		int i = start;
		int j = end;
		
		//交换元素
		while(true) {
			while(sourceData[i] < key) {
				++i;
			}
			while(sourceData[j] > key) {
				--j;
			}
			//寻找两个可以交换的元素，如果 j<= i, 说明sourceData[j]在左半域，arr[i]在右半域，划分完成，在j，j+1间划分
			if(i < j) {
				swap(i,j);
			}
			else {
				return j;
			}
		}
	}
	
	private static void swap(int i, int j) {
		int tmp = sourceData[i];
		sourceData[i] = sourceData[j];
		sourceData[j] = tmp;
	}
	
	//查找元素的位置
	public static int findPos(int a[], int num) {
		for(int i = 1; i <= a.length; i++) {
			if(num == a[i]) return i;
		}
		return -1;
	}
	
	//快速排序，排left到right区间中的部分
	public void quicksort(int low, int high) {
		
		if(low < high) {
			int middle = partition(low, high);
			if(k < middle) {
				quicksort(low, middle);
			}
			else if(k > middle) {
				quicksort(middle + 1, high);
			}
		}
		
	}
	
	//为题2的函数
	static int stat = 0;
	static int start = counts, end = -1;
	static int center;
	static int leftsum;
	static int rightsum;
	static int s1,lefts,s2,rights;
	private static int MaxSum(int left,int right) {
		sum = 0;
		if(left == right) {	//如果序列长度为1，直接求解
			if(result[left] > 0) {
				sum = result[left];
			}
			else sum = 0;
		}
		else {
			center = (left + right) / 2;//划分
			leftsum = MaxSum(left, center);
			rightsum = MaxSum(center + 1, right);
//			System.out.println("sum:" + leftsum + " " + rightsum);
//			System.out.println("left:" + left + " " + "center:" + center + " " + "right" + right);
			s1 = 0;
			lefts = 0;
			for(int i = center; i >= left; i--) {
				lefts += result[i];
				if(lefts > s1) s1 = lefts;
			}
			s2 = 0; rights = 0;
			for(int j = center + 1; j <= right; j++) {
				rights += result[j];
				if(rights > s2) s2 = rights;
			}
			sum = s1 + s2;//
			if(sum < leftsum) {
				sum = leftsum;
			}
			if(sum < rightsum) {
				sum = rightsum;
			}
			
//			System.out.println(sum + " test:" + left + "->" + right);
			
			if(stat < sum) {
				stat = sum;
				start = left;
				end = right;
			}
		}
		return sum;
	}
	
	//问题3的函数
	/*
	 * 用整型数组 board表示棋盘，board[0][0]标识棋盘的左上角
	 * tile是一个全局整形变量，表示L型骨牌的编号，初始值为0
	 * tr->棋盘左上角方格的编号
	 * tc->棋盘左上角方格的列号
	 * dr->特殊方格所在的行号
	 * dc->特殊方格所在的列号
	 * size->2^k，棋盘的规格
	 */
	static int tile = 0;
	static int[][] board;
	static int size;
	
	private void inputBoard() {
		System.out.println("**********棋盘覆盖问题**********");
		System.out.println("请输入棋盘的size，数值为2的n次幂");
		Scanner input = new Scanner(System.in);
		System.out.print(">>>");
		size = input.nextInt();
		
		board = new int[size][size];
		
		int index_x,index_y;
		System.out.println("请输入特殊方块的位置坐标：");
		System.out.print(">>>");
		index_x = input.nextInt();
		index_y = input.nextInt();
		
		chessBoard(0, 0, index_x, index_y, size);
		
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	private void chessBoard(int tr, int tc, int dr, int dc, int size) {
		if(size == 1) return;
		int t = tile++,		//L型骨牌号
				s = size / 2;//分割棋盘
		//覆盖左上角子棋盘
		if(dr < tr + s && dc < tc + s) 
			//特殊方格在此棋盘中
			chessBoard(tr, tc, dr, dc, s);
		else {//此棋盘无特殊方格
			//用t号L型骨牌覆盖右下角
			board[tr + s - 1][tc + s - 1] = t;
			//覆盖其余方格
			chessBoard(tr, tc, tr + s - 1, tc + s - 1, s);
		}
		//覆盖右上角子棋盘
		if(dr < tr + s && dc >= tc + s)
			//特殊方格在此棋盘中
			chessBoard(tr, tc + s, dr, dc, s);
		else {//此棋盘中无特殊方格
			//用t号L型骨牌覆盖左下角
			board[tr + s - 1][tc + s] = t;
			//覆盖其余方格
			chessBoard(tr, tc + s, tr + s - 1, tc + s, s);
		}
		//覆盖左下角子棋盘
		if(dr >= tr + s && dc < tc + s)
			//特殊方格在此棋盘中
			chessBoard(tr + s, tc, dr, dc, s);
		else {//此棋盘中无特殊方格
			//用t号L型骨牌覆盖右下角
			board[tr + s][tc + s - 1] = t;
			//覆盖其余方格
			chessBoard(tr + s, tc, tr+ s, tc + s - 1, s);
		}
		//覆盖右下角子棋盘
		if(dr >= tr + s && dc >= tc + s)
			//特殊方格在此棋盘中
			chessBoard(tr + s, tc + s, dr, dc, s);
		else {//此棋盘中无特殊方格
			//用t号L型骨牌覆盖左上角
			board[tr + s][tc + s] = t;
			//覆盖其余方格
			chessBoard(tr + s, tc + s, tr + s, tc + s, s);
		}
	}
}
