package com.miao.fenzhifa;

import java.util.Scanner;

public class Test {
	
	private static int counts;//�����ĸ���
	private static int position;//��kСԪ�ص�λ��
	private static int k;
	private static int[] sourceData;//ԭ��������
	private static int[] result;//����ı��ݣ�������1��ԭ���鱻�޸�
	
	private static int sum;
	
	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("**********���η�����***********");
			System.out.println(">>>������Ԫ�صĸ�����");
			System.out.print(">>>");
			counts = input.nextInt();
			
			//��ʼ�����鲢�Ӽ��̻�ȡ����
			sourceData = new int[counts + 1];
			result = new int[counts + 1];
			
			sourceData[0] = 0;
			for(int i = 1; i <= counts; i++) {
				sourceData[i] = input.nextInt();
			}
			result =sourceData.clone();
			//����1
			System.out.println("������� kСԪ�ص� k��ֵ��");
			System.out.print(">>>");
			k = input.nextInt();
			
			Test test = new Test();
			
			test.quicksort(1, counts);
			position = findPos(result,sourceData[k]);

			System.out.println("�����е� " + k + "СԪ�ص�ֵΪ��" + sourceData[k] 
					+ "    �������е�λ��Ϊ��" + position);
			
			System.out.println("**********�����ֶ�����**********");
			
			int maxSum = test.MaxSum(1, counts);
			
			System.out.println("���������Ӷκ�Ϊ��" + maxSum);
			
			System.out.println("�����ֶε����Ϊ��" + start + "->" + end);
			
			test.inputBoard();
		
	}
		
	//����1�ĺ���
	
	private static int partition(int start, int end) {
		
		int key = sourceData[start];
		int i = start;
		int j = end;
		
		//����Ԫ��
		while(true) {
			while(sourceData[i] < key) {
				++i;
			}
			while(sourceData[j] > key) {
				--j;
			}
			//Ѱ���������Խ�����Ԫ�أ���� j<= i, ˵��sourceData[j]�������arr[i]���Ұ��򣬻�����ɣ���j��j+1�仮��
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
	
	//����Ԫ�ص�λ��
	public static int findPos(int a[], int num) {
		for(int i = 1; i <= a.length; i++) {
			if(num == a[i]) return i;
		}
		return -1;
	}
	
	//����������left��right�����еĲ���
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
	
	//Ϊ��2�ĺ���
	static int stat = 0;
	static int start = counts, end = -1;
	static int center;
	static int leftsum;
	static int rightsum;
	static int s1,lefts,s2,rights;
	private static int MaxSum(int left,int right) {
		sum = 0;
		if(left == right) {	//������г���Ϊ1��ֱ�����
			if(result[left] > 0) {
				sum = result[left];
			}
			else sum = 0;
		}
		else {
			center = (left + right) / 2;//����
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
	
	//����3�ĺ���
	/*
	 * ���������� board��ʾ���̣�board[0][0]��ʶ���̵����Ͻ�
	 * tile��һ��ȫ�����α�������ʾL�͹��Ƶı�ţ���ʼֵΪ0
	 * tr->�������ϽǷ���ı��
	 * tc->�������ϽǷ�����к�
	 * dr->���ⷽ�����ڵ��к�
	 * dc->���ⷽ�����ڵ��к�
	 * size->2^k�����̵Ĺ��
	 */
	static int tile = 0;
	static int[][] board;
	static int size;
	
	private void inputBoard() {
		System.out.println("**********���̸�������**********");
		System.out.println("���������̵�size����ֵΪ2��n����");
		Scanner input = new Scanner(System.in);
		System.out.print(">>>");
		size = input.nextInt();
		
		board = new int[size][size];
		
		int index_x,index_y;
		System.out.println("���������ⷽ���λ�����꣺");
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
		int t = tile++,		//L�͹��ƺ�
				s = size / 2;//�ָ�����
		//�������Ͻ�������
		if(dr < tr + s && dc < tc + s) 
			//���ⷽ���ڴ�������
			chessBoard(tr, tc, dr, dc, s);
		else {//�����������ⷽ��
			//��t��L�͹��Ƹ������½�
			board[tr + s - 1][tc + s - 1] = t;
			//�������෽��
			chessBoard(tr, tc, tr + s - 1, tc + s - 1, s);
		}
		//�������Ͻ�������
		if(dr < tr + s && dc >= tc + s)
			//���ⷽ���ڴ�������
			chessBoard(tr, tc + s, dr, dc, s);
		else {//�������������ⷽ��
			//��t��L�͹��Ƹ������½�
			board[tr + s - 1][tc + s] = t;
			//�������෽��
			chessBoard(tr, tc + s, tr + s - 1, tc + s, s);
		}
		//�������½�������
		if(dr >= tr + s && dc < tc + s)
			//���ⷽ���ڴ�������
			chessBoard(tr + s, tc, dr, dc, s);
		else {//�������������ⷽ��
			//��t��L�͹��Ƹ������½�
			board[tr + s][tc + s - 1] = t;
			//�������෽��
			chessBoard(tr + s, tc, tr+ s, tc + s - 1, s);
		}
		//�������½�������
		if(dr >= tr + s && dc >= tc + s)
			//���ⷽ���ڴ�������
			chessBoard(tr + s, tc + s, dr, dc, s);
		else {//�������������ⷽ��
			//��t��L�͹��Ƹ������Ͻ�
			board[tr + s][tc + s] = t;
			//�������෽��
			chessBoard(tr + s, tc + s, tr + s, tc + s, s);
		}
	}
}
