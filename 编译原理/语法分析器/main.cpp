#include<stdlib.h>
#include<stdio.h>
#include<string.h>

/*
1.���뻺����Ϊһ���ַ������飬����������������ʽ�������ڴˣ��ԡ�$��������
2.����һ�����Ӧ���������飬�����뻺�����е��ַ�ת��Ϊ��Ӧ�Ĵ��Ų����棻
3.����һ���ṹ�壬�Ա����ķ���ĳ������ʽ���ýṹ��������Ԫ�أ����α������������ʽ�󲿷��ս�����š��������飬�������ʽ�Ҳ��ַ����Ĵ��š����ͱ������������ʽ�Ҳ����ȣ�
4.����ýṹ�����飬�����ķ������в���ʽ��
5.����������ά�������飬goto��action����ֵ����������ƽ�������С��������Լ������������״̬���Լ�õ��Ĳ���ʽ�־���ֵ��ʾ�������������ִ��󡣵�������ֵ999����acc.״̬��
*/

char buffer[30];/*��������Ŵ�w������������*/
int decode[30];/*��������ַ���ת��Ϊ������ʽ*/
int analysis_stack[50];/*�������ջ*/

int up = -1;/*upΪջ��Ԫ���±�*/
int position = 0;/*positionΪdecode������±�*/
int locating;/*locatingΪ��ǰ��ȡ��������ŵĴ���*/

bool flag = true;/*ѭ����־*/

typedef struct{/*�����ķ�����ʽ�Ľṹ*/
	int Vn;/*�ķ�����ʽ���󲿷��ս���Ĵ���*/
	int Str[4];/*�ķ�����ʽ�Ҳ��Ĵ��봮*/
	int size;/*�ķ�����ʽ�Ҳ��ĳ���*/
}Gramer;

Gramer production[11] = {/*���ķ������в���ʽ*/
	{ 0 },
	{ 101, { 0, 102 }, 1 },/*S -> E*/
	{ 102, { 0, 102, 3, 103 }, 3 },/*E -> E+T*/
	{ 102, { 0, 102, 4, 103 }, 3 },/*E -> E-T*/
	{ 102, { 0, 103 }, 1 },/*E -> T*/
	{ 103, { 0, 103, 5, 104 }, 3 },/*T -> T*F*/
	{ 103, { 0, 103, 6, 104 }, 3 },/*T -> T/F*/
	{ 103, { 0, 104 }, 1 },/*T -> F*/
	{ 104, { 0, 7 }, 1 },/*F -> id*/
	{ 104, { 0, 1, 101, 2 }, 3 },/*F -> (E)*/
	{ 104, { 0, 8 }, 1 },/*F -> num*/
};
int GOTO[17][5] = {/*LR������goto*/
	{ 0, 0, 1, 2, 3 },/*0*/
	{ 0, 0, 0, 0, 0 },/*1*/
	{ 0, 0, 0, 0, 0 },/*2*/
	{ 0, 0, 0, 0, 0 },/*3*/
	{ 0, 0, 0, 0, 0 },/*4*/
	{ 0, 0, 11, 2, 3 },/*5*/
	{ 0, 0, 0, 0, 0 },/*6*/
	{ 0, 0, 0, 12, 3 },/*7*/
	{ 0, 0, 0, 13, 3 },/*8*/
	{ 0, 0, 0, 0, 14 },/*9*/
	{ 0, 0, 0, 0, 15 },/*10*/
	{ 0, 0, 0, 0, 0 },/*11*/
	{ 0, 0, 0, 0, 0 },/*12*/
	{ 0, 0, 0, 0, 0 },/*13*/
	{ 0, 0, 0, 0, 0 },/*14*/
	{ 0, 0, 0, 0, 0 },/*15*/
	{ 0, 0, 0, 0, 0 }/*16*/
};
int Action[17][9] = {/*LR������action*/
	{ 51, 5, 52, 51, 51, 51, 51, 4, 6 },//0
	{ 999, 53, 52, 7, 8, 0, 0, 53, 53 },//1
	{ -4, 53, -4, -4, -4, 9, 10, 53, 53 },//2
	{ -7, 0, -7, -7, -7, -7, -7, 0, 0 },//3
	{ -8, 0, -8, -8, -8, -8, -8, 0, 0 },//4
	{ 0, 5, 52, 51, 51, 51, 51, 4, 6 },//5
	{ -10, 0, -10, -10, -10, -10, -10, 0, 0 },//6
	{ 51, 5, 52, 51, 51, 51, 51, 4, 6 },//7
	{ 51, 5, 52, 51, 51, 51, 51, 4, 6 },//8
	{ 51, 5, 52, 51, 51, 51, 51, 4, 6 },//9
	{ 51, 5, 52, 51, 51, 51, 51, 4, 6 },//10
	{ 0, 53, 16, 7, 8, 0, 0, 53, 53 },//11
	{ -2, 53, -2, -2, -2, 9, 10, 53, 53 },//12
	{ -3, 53, -3, -3, -3, 9, 10, 53, 53 },//13
	{ -5, 0, -5, -5, -5, -5, -5, 0, 0 },//14
	{ -6, 0, -6, -6, -6, -6, -6, 0, 0 },//15
	{ -9, 0, -9, -9, -9, -9, -9, 0, 0 }//16
};

void Transformer();/*�������buffer�������ݰ��ַ�ת��Ϊ��Ӧ���Ŵ���decode������*/
void GetFromCode();/*ȡ�õ�ǰ������Ŵ���Ԫ��*/
void push(int A, int S);/*��ջ����*/
void pop();/*��ջ����*/
void shiftIn();/*�������*/
void Reduce();/*��Լ����*/
void ACC();/*���ܲ���*/
/*������*/
void Exception1();
void Exception2();
void Exception3();

int main()
{
	int c = 1;
	printf("��������ʽ,����$��β�����磺'a+b$'\n");
	scanf("%s", buffer);/*������ʽ��������buffer��*/
	printf("\n");
	Transformer();

	push(0, 0);
	GetFromCode();//�����뻺������ȡһ���ַ� 

	while (flag){
		if (Action[analysis_stack[up]][locating] > 0 && Action[analysis_stack[up]][locating] <50){/*�����������*/
			shiftIn();
			GetFromCode();
		}
		else if (Action[analysis_stack[up]][locating] < 0){/*���й�Լ����*/
			Reduce();
		}
		else if (Action[analysis_stack[up]][locating] == 999){/*�����ɹ�*/
			ACC();
		}
		else
		{
			switch (Action[analysis_stack[up]][locating])
			{
			case 51: 
				Exception1(); 
				break;
			case 52: 
				Exception2();
				GetFromCode(); 
				break;
			case 53: 
				Exception3(); 
				GetFromCode(); 
				break;
			}
		}
	}
	system("pause");
	return 0;
}
void Transformer(){/*�������buffer�������ݰ��ַ�ת��Ϊ��Ӧ���Ŵ���w������*/
	int x = 0, y = 0;/*xΪָ��buffer������±� */
	while (buffer[x] != '$'){
		if (buffer[x] >= 48 && buffer[x] <= 57){/*��ǰ�ַ�Ϊ���֣���num*/
			while (buffer[x] >= 48 && buffer[x] <= 57) x++;
			decode[y++] = 8;/*��buffer����������w��д�����num��8,����y����*/
		}
		else if ((buffer[x] >= 97 && buffer[x] <= 122) || (buffer[x] >= 65 && buffer[x] <= 90)){/*��ǰ�ַ�Ϊ��ĸ����id*/
			while ((buffer[x] >= 97 && buffer[x] <= 122) || (buffer[x] >= 65 && buffer[x] <= 90)) x++;
			decode[y++] = 7;/*��buffer����������w��д�����id��7������y����*/
		}
		else{
			switch (buffer[x++]){/*��buffer�е��ַ�Ϊһ������һ��ʱ���Զ�ת��Ϊ��Ӧ�Ĵ��Ŵ���decode������*/
			case '&': decode[y++] = 0; break;
			case '(': decode[y++] = 1; break;
			case ')': decode[y++] = 2; break;
			case '+': decode[y++] = 3; break;
			case '-': decode[y++] = 4; break;
			case '*': decode[y++] = 5; break;
			case '/': decode[y++] = 6; break;
			}
		}
	}
	decode[y] = 0;
}
void GetFromCode(){/*ȡ�õ�ǰ������Ŵ���Ԫ��*/
	locating = decode[position++];
}
void push(int A, int S){/*��ջ����*/
	up++;/*��ջָ��upָ������Ԫ�� */
	analysis_stack[up] = A;/*������A����ջ��*/
	up++;
	analysis_stack[up] = S;/*��״̬S����ջ��*/
};
void pop(){/*��ջ����*/
	up = up - 2;/*����һ��ջ��Ԫ�أ�ջָ��up���ƣ�ָ���µ�ջ��*/
};
void shiftIn(){/*�������*/
	int x;
	x = Action[analysis_stack[up]][locating];/*����goto����ȷ����ǰ����������������״̬*/
	push(locating, x);/*����ǰ������ַ���״̬ѹ��ջ*/
	printf("s%d\t��ջ.\n", x);
}
void Reduce(){/*��Լ����*/
	int x, y;
	x = -Action[analysis_stack[up]][locating];/*����action����ȷ����ǰ��Լ�����������ĸ�����ʽ��Լ*/
	printf("r%d\t��Լ\t", x);
	switch (production[x].Vn){
	case 101: printf("S -> "); break;
	case 102: printf("E -> "); break;
	case 103: printf("T -> "); break;
	case 104: printf("F -> "); break;
	}
	for (int i = 1; i <= production[x].size; i++){
		pop();/*��ջ��Ԫ�أ�����Լ����ʽ���Ҳ����Ƚ��е�������*/
		switch (production[x].Str[i]){
		case 0: printf("$"); break;
		case 1: printf("("); break;
		case 2: printf(")"); break;
		case 3: printf("+"); break;
		case 4: printf("-"); break;
		case 5: printf("*"); break;
		case 6: printf("/"); break;
		case 7: printf("id"); break;
		case 8: printf("num"); break;
		case 101: printf("S"); break;
		case 102: printf("E"); break;
		case 103: printf("T"); break;
		case 104: printf("F"); break;
		}
	}
	printf("\n");
	y = GOTO[analysis_stack[up]][production[x].Vn - 100];/*����goto����ȷ����Լ��ɺ���Ҫת����״̬*/
	push(production[x].Vn, y);/*����Լ����ʽ����ѹ��ջ�У�������Ӧ��ת��״̬ѹ��ջ��*/
}
void ACC(){
	flag = false;
	printf("ACC!\n");
}
void Exception1()
{//�ڴ�(������������ַ������������������$
	printf("e1\t\t\tȱ���������,idѹ��ջ\n");
	push(7, 4);
}
void Exception2()
{//���Ų�ƥ�䣬ɾ�������� 
	printf("e2\t\t\t���Ų�ƥ��,ɾ��������\n");

}
void Exception3()
{//�ڴ�������ţ�������(��������� 
	int pointer = position;
	switch (analysis_stack[up])
	{
	case 1: push(3, 7); printf("e3\t\t\tȱ�������,����������ջ"); break;
	case 2: case 12: case 13: push(5, 9); printf("e3\t\t\tȱ�������,����������ջ"); break;
	case 11:
		if (decode[pointer] >= 3 && decode[pointer] <= 6)//�ڴ��������
		{
			push(2, 16); printf("e3\t\t\tȱ��������,�����������ջ");
		}
		else if (decode[pointer] >= 7 && decode[pointer] <= 8)
		{
			push(3, 7); printf("e3\t\t\tȱ�������,����������ջ");
		}
		break;
	}
}