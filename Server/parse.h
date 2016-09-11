#include <stdio.h>
#include <mysql.h>
void countDuplication(char**, int*, int);
void parseSentence(unsigned char*,char**, MYSQL*, int);
void sentencRegist(unsigned char*, MYSQL*);
char *strlwr(char*);
