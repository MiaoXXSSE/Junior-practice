#include <iostream>
#include <fstream>
#include <string>
using namespace std;
/*
ѭ������getToken������ÿ����һ�λ��һ������
�������������ȡ����ĵ���
��ȡ�ַ�ʱ��
*/
#define WORD_LENGTH 130 // ������󳤶�
char token[WORD_LENGTH];

void getToken();//ѭ����ȡ����
void FiletoString(char *filename);//�����ļ����ȶ�̬�����ڴ���м���


char *rwtab[32] = { "auto","double","int","struct","break","else","long","switch",
					"case","enum","register","typedef","char","extern","return","union",
					"const","float","short","unsigned","continue","for","signed","void",
					"default","goto","sizeof","volatile","do","if","while","static" };//�����ؼ���
char *buf;	//�洢�ļ���ȡ�������ַ���
int length;//�ı��ĳ���
int syn;//ÿ��ȡһ�εĽ����ʶ
int p, m = 0, n, row;
char ch;
double sum = 0;   //����Ϊ��������С����ʱ�����ڱ���Դ����
int syn_of_rwtab; //�����ؼ�������
int locate_line; //���������е�λ��

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
	����һ����7�࣬ÿһ�඼��һ��if�ж�
	*/
	//���p�ĳ��ȴﵽĩβ����syn = 0���˳�����
	for (n = 0;n<WORD_LENGTH;n++) token[n] = NULL;
	ch = buf[p++];
	//�ж��Ƿ�ﵽ�ļ�ĩβ������ǵĻ�����syn = 0���˳�����
	if (p > length) {
		syn = 0;
	}
	//****************��6�� �ָ���***********************************//
	else if (ch == ' ' || ch == ';' ||
		ch == '{' || ch == '}' ||
		ch == '(' || ch == ')') {
		syn = 6;
		token[0] = ch;
	}
	//***************1 �ؼ��� 2 ��ʶ��*********************************//
	else if ((ch >= 'a'&&ch <= 'z') || (ch >= 'A'&&ch <= 'Z') || ch == '_')  //�����Ǳ�ʾ�����߱�����
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
		for (n = 0;n<32;n++)  //��ʶ��������ַ����Ѷ���ı�ʾ�����Ƚϣ�
			if (strcmp(token, rwtab[n]) == 0)
			{
				syn = 1;
				syn_of_rwtab = n;
				break;
			}
	}
	//*****************3 ���� 4С��********************************//
	else if ((ch >= '0'&&ch <= '9'))
	{

		bool flag = false; //�Ƿ���С��
		sum = 0;

		while ((ch >= '0'&&ch <= '9'))
		{
			sum = sum * 10 + ch - '0';
			ch = buf[p++];
		}
		if (ch == '.') {
			flag = true;
			ch = buf[p++];
			double tag = 0.1; //��¼С����λ��
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
	//********************5 �ж��ǲ����ַ���****************************//
	else if (ch == '\"') {
		syn = 5;
		m = 0;
		token[m++] = ch;
		while ((ch = buf[p++]) != '\"')
			token[m++] = ch;
		token[m] = ch;
		// p--;
	}
	//******************* ע��*******************//
	else if (ch == '\/') {
		if ((ch = buf[p++]) == '\*') {
			while ((ch = buf[p]) != '\*' && (ch = buf[p++] != '\/'))
				p++;
		}
	}

	//*******************7 �����*******************//
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

//����ļ��Ƿ����,�����ڴ棬��ȡ�ļ���char*������
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