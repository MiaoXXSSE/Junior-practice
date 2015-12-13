#include<stdlib.h>
#include<stdio.h>
#include<string.h>

/*
1.输入缓冲区为一个字符型数组，读入输入的算术表达式并保存在此，以’$’结束；
2.构建一个相对应的整型数组，将输入缓冲区中的字符转换为相应的代号并保存；
3.构造一个结构体，以保存文法的某个产生式，该结构包括三个元素：整形变量，保存产生式左部非终结符代号。整型数组，保存产生式右部字符串的代号。整型变量，保存产生式右部长度；
4.定义该结构的数组，保存文法的所有产生式；
5.定义两个二维整形数组，goto和action，其值大于零代表移进操作，小于零代表规约操作，引进的状态或规约用到的产生式又绝对值表示。等于零代表出现错误。等于特殊值999代表acc.状态。
*/

char buffer[30];/*将输入符号串w保存于数组中*/
int decode[30];/*将读入的字符串转换为代号形式*/
int analysis_stack[50];/*定义分析栈*/

int up = -1;/*up为栈顶元素下标*/
int position = 0;/*position为decode数组的下标*/
int locating;/*locating为当前获取的输入符号的代号*/

bool flag = true;/*循环标志*/

typedef struct{/*定义文法产生式的结构*/
	int Vn;/*文法产生式的左部非终结符的代码*/
	int Str[4];/*文法产生式右部的代码串*/
	int size;/*文法产生式右部的长度*/
}Gramer;

Gramer production[11] = {/*该文法的所有产生式*/
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
int GOTO[17][5] = {/*LR分析表goto*/
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
int Action[17][9] = {/*LR分析表action*/
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

void Transformer();/*将读入的buffer数组内容按字符转换为相应代号存入decode数组中*/
void GetFromCode();/*取得当前输入符号串的元素*/
void push(int A, int S);/*入栈操作*/
void pop();/*出栈操作*/
void shiftIn();/*移入操作*/
void Reduce();/*规约操作*/
void ACC();/*接受操作*/
/*错误处理*/
void Exception1();
void Exception2();
void Exception3();

int main()
{
	int c = 1;
	printf("请输入表达式,并以$结尾。例如：'a+b$'\n");
	scanf("%s", buffer);/*读入表达式，保存至buffer中*/
	printf("\n");
	Transformer();

	push(0, 0);
	GetFromCode();//从输入缓冲区读取一个字符 

	while (flag){
		if (Action[analysis_stack[up]][locating] > 0 && Action[analysis_stack[up]][locating] <50){/*进行移入操作*/
			shiftIn();
			GetFromCode();
		}
		else if (Action[analysis_stack[up]][locating] < 0){/*进行规约操作*/
			Reduce();
		}
		else if (Action[analysis_stack[up]][locating] == 999){/*分析成功*/
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
void Transformer(){/*将读入的buffer数组内容按字符转换为相应代号存入w数组中*/
	int x = 0, y = 0;/*x为指向buffer数组的下标 */
	while (buffer[x] != '$'){
		if (buffer[x] >= 48 && buffer[x] <= 57){/*当前字符为数字，即num*/
			while (buffer[x] >= 48 && buffer[x] <= 57) x++;
			decode[y++] = 8;/*在buffer的替身数组w中写入代表num的8,并且y后移*/
		}
		else if ((buffer[x] >= 97 && buffer[x] <= 122) || (buffer[x] >= 65 && buffer[x] <= 90)){/*当前字符为字母，即id*/
			while ((buffer[x] >= 97 && buffer[x] <= 122) || (buffer[x] >= 65 && buffer[x] <= 90)) x++;
			decode[y++] = 7;/*在buffer的替身数组w中写入代表id的7，并且y后移*/
		}
		else{
			switch (buffer[x++]){/*当buffer中的字符为一下任意一种时，自动转换为相应的代号存入decode数组中*/
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
void GetFromCode(){/*取得当前输入符号串的元素*/
	locating = decode[position++];
}
void push(int A, int S){/*入栈操作*/
	up++;/*将栈指针up指向最新元素 */
	analysis_stack[up] = A;/*将符号A移入栈顶*/
	up++;
	analysis_stack[up] = S;/*将状态S移入栈顶*/
};
void pop(){/*出栈操作*/
	up = up - 2;/*弹出一个栈顶元素，栈指针up下移，指向新的栈顶*/
};
void shiftIn(){/*移入操作*/
	int x;
	x = Action[analysis_stack[up]][locating];/*查找goto表以确定当前移入操作所需移入的状态*/
	push(locating, x);/*将当前读入的字符与状态压入栈*/
	printf("s%d\t入栈.\n", x);
}
void Reduce(){/*规约操作*/
	int x, y;
	x = -Action[analysis_stack[up]][locating];/*查找action表以确定当前规约操作，即用哪个产生式规约*/
	printf("r%d\t规约\t", x);
	switch (production[x].Vn){
	case 101: printf("S -> "); break;
	case 102: printf("E -> "); break;
	case 103: printf("T -> "); break;
	case 104: printf("F -> "); break;
	}
	for (int i = 1; i <= production[x].size; i++){
		pop();/*将栈中元素，按规约产生式的右部长度进行弹出操作*/
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
	y = GOTO[analysis_stack[up]][production[x].Vn - 100];/*查找goto表以确定规约完成后需要转换的状态*/
	push(production[x].Vn, y);/*将规约产生式的左部压入栈中，并将相应的转移状态压入栈中*/
}
void ACC(){
	flag = false;
	printf("ACC!\n");
}
void Exception1()
{//期待(或运算对象首字符，但出现运算符或者$
	printf("e1\t\t\t缺少运算对象,id压入栈\n");
	push(7, 4);
}
void Exception2()
{//括号不匹配，删掉右括号 
	printf("e2\t\t\t括号不匹配,删除右括号\n");

}
void Exception3()
{//期待运算符号，但出现(或运算对象 
	int pointer = position;
	switch (analysis_stack[up])
	{
	case 1: push(3, 7); printf("e3\t\t\t缺少运算符,添加运算符至栈"); break;
	case 2: case 12: case 13: push(5, 9); printf("e3\t\t\t缺少运算符,添加运算符至栈"); break;
	case 11:
		if (decode[pointer] >= 3 && decode[pointer] <= 6)//期待运算符号
		{
			push(2, 16); printf("e3\t\t\t缺少右括号,添加右括号至栈");
		}
		else if (decode[pointer] >= 7 && decode[pointer] <= 8)
		{
			push(3, 7); printf("e3\t\t\t缺少运算符,添加运算符至栈");
		}
		break;
	}
}