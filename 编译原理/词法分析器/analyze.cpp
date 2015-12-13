#include <iostream>
#include <fstream>
#include <string>
using namespace std;
/*
循环调用getToken函数，每调用一次获得一个单词
输出结果后继续读取后面的单词
读取字符时，
*/
#define WORD_LENGTH 130 // 单词最大长度
char token[WORD_LENGTH];

void getToken();//循环获取单词
void FiletoString(char *filename);//根据文件长度动态申请内存进行计算


char *rwtab[32] = { "auto","double","int","struct","break","else","long","switch",
					"case","enum","register","typedef","char","extern","return","union",
					"const","float","short","unsigned","continue","for","signed","void",
					"default","goto","sizeof","volatile","do","if","while","static" };//保留关键字
char *buf;	//存储文件读取的整个字符串
int length;//文本的长度
int syn;//每读取一次的结果标识
int p, m = 0, n, row;
char ch;
double sum = 0;   //类型为整数或者小数的时候，用于保存源数据
int syn_of_rwtab; //遍历关键字数组
int locate_line; //单词在行中的位置

int main()
{
	char filename[50];
	cout << "Please input the File path,including file name\n>>>";
	cin >> filename;
	FiletoString(filename);

	do
	{
		getToken();
		locate_line++;
		switch (syn)
		{
		case 1: cout << "row:" << row << " " << "keywords" << "," << rwtab[syn_of_rwtab] << endl;break;
		case 2: cout << "row:" << row << " " << "identifier" << "," << token << endl; break;
		case 3: cout << "row:" << row << " " << "integer" << "," << sum << endl; break;
		case 4: cout << "row:" << row << " " << "decimals" << "," << sum << endl; break;
		case 5: cout << "row:" << row << " " << "string" << "," << token << endl; break;
		case 6: cout << "row:" << row << " " << "separator" << "," << token[0] << endl; break;
		case 7: cout << "row:" << row << " " << "operator" << "," << token[0] << endl; break;
		case -1: cout << "Error in row " << row << "!" << endl; break;
		case -2: row = row++;break;
		}
	} while (syn != 0);
	cin >> filename;
	return 0;
}
void getToken()
{
	/*
	这里一共有7类，每一类都有一个if判断
	*/
	//如果p的长度达到末尾，则syn = 0，退出程序
	for (n = 0;n<WORD_LENGTH;n++) token[n] = NULL;
	ch = buf[p++];
	//判断是否达到文件末尾，如果是的话，则syn = 0，退出程序
	if (p > length) {
		syn = 0;
	}
	//****************第6类 分隔符***********************************//
	else if (ch == ' ' || ch == ';' ||
		ch == '{' || ch == '}' ||
		ch == '(' || ch == ')') {
		syn = 6;
		token[0] = ch;
	}
	//***************1 关键字 2 标识符*********************************//
	else if ((ch >= 'a'&&ch <= 'z') || (ch >= 'A'&&ch <= 'Z') || ch == '_')  //可能是标示符或者变量名
	{
		m = 0;
		while ((ch >= '0'&&ch <= '9') || (ch >= 'a'&&ch <= 'z') || (ch >= 'A'&&ch <= 'Z') || ch == '_')
		{
			token[m++] = ch;
			ch = buf[p++];
		}
		token[m++] = '\0';
		p--;
		syn = 2;
		for (n = 0;n<32;n++)  //将识别出来的字符和已定义的标示符作比较，
			if (strcmp(token, rwtab[n]) == 0)
			{
				syn = 1;
				syn_of_rwtab = n;
				break;
			}
	}
	//*****************3 整数 4小数********************************//
	else if ((ch >= '0'&&ch <= '9'))
	{

		bool flag = false; //是否是小数
		sum = 0;

		while ((ch >= '0'&&ch <= '9'))
		{
			sum = sum * 10 + ch - '0';
			ch = buf[p++];
		}
		if (ch == '.') {
			flag = true;
			ch = buf[p++];
			double tag = 0.1; //记录小数的位数
			while ((ch >= '0' && ch <= '9')) {
				sum += (ch - '0')*tag;
				tag = tag*0.1;
				ch = buf[p++];
			}
		}//if
		p--;
		if (flag) syn = 4;
		else syn = 3;
		if (sum>32767)
			syn = -1;
	}
	//********************5 判断是不是字符串****************************//
	else if (ch == '\"') {
		syn = 5;
		m = 0;
		token[m++] = ch;
		while ((ch = buf[p++]) != '\"')
			token[m++] = ch;
		token[m] = ch;
		// p--;
	}
	//******************* 注释*******************//
	else if (ch == '\/') {
		if ((ch = buf[p++]) == '\*') {
			while ((ch = buf[p]) != '\*' && (ch = buf[p++] != '\/'))
				p++;
		}
	}

	//*******************7 运算符*******************//
	else switch (ch)
	{
	case'<':
		m = 0;
		token[m++] = ch;
		ch = buf[p++];
		if (ch == '>')
		{
			syn = 7;
			token[m++] = ch;
		}
		else if (ch == '=')
		{
			syn = 7;
			token[m++] = ch;
		}
		else
		{
			syn = 7;
			p--;
		}
		break;
	case'>':
		m = 0;
		token[m++] = ch;
		ch = buf[p++];
		if (ch == '=')
		{
			syn = 7;
			token[m++] = ch;
		}
		else
		{
			syn = 7;
			p--;
		}
		break;
	case':':
		m = 0;token[m++] = ch;
		ch = buf[p++];
		if (ch == '=')
		{
			syn = 7;
			token[m++] = ch;
		}
		else
		{
			syn = 7;
			p--;
		}
		break;
	case'*':syn = 7;token[0] = ch;break;
	case'/':syn = 7;token[0] = ch;break;
	case'+':syn = 7;token[0] = ch;break;
	case'-':syn = 7;token[0] = ch;break;
	case'=':syn = 7;token[0] = ch;break;
	case'\n':syn = -2;locate_line = 0;break;
	default: syn = -1;break;
	}
}

//检测文件是否存在,申请内存，读取文件到char*数组中
void FiletoString(char *filename)
{
	ifstream stream(filename);
	if (!stream.is_open()) {
		cout << "Error opening file";
		exit(1);
	}
	stream.seekg(0, ios::end);
	length = stream.tellg();
	stream.seekg(0, ios::beg);
	buf = new char[length];
	stream.read(buf, length);
	cout << buf << endl;
	stream.close();
}