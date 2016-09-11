#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <mysql.h>

#define DB_HOST "127.0.0.1"
#define DB_USER "root"
#define DB_PASS "kutemsys"
#define DB_NAME "onpuri_eng"

MYSQL* conn_ptr;
MYSQL_RES* res_ptr;
MYSQL_ROW row;

unsigned char word[30];
unsigned char mean[150];
char query[200];
int i, j, a, s;

int main(){
	FILE* fp;

	fp = fopen("E-K dictionaryUTF9.txt", "r");

	/*데이터베이스 초기화 및 접속*/
	conn_ptr = mysql_init(NULL);

	if(!conn_ptr){
		printf("Database init error\n");
	}

	conn_ptr = mysql_real_connect(conn_ptr, "127.0.0.1", "root", "kutemsys", "onpuri_eng", 0, NULL, 0);

	if(!conn_ptr){
		printf("connection error\n");
		exit(1);
	}

	i = 0;

	while(!feof(fp)){
		memset(word, '\0', 30);
		memset(mean, '\0', 150);
		memset(query, '\0', 200);
	
//		wordLeng = fread(word, sizeof(char), 30, fp);
//		meanLeng = fread(mean, sizeof(char), 150, fp);
		fgets(word, 30, fp);
		fgets(mean, 150, fp);
		a = strlen(word);
		s = strlen(mean);
		puts(word);
		puts(mean);
		word[a-2] = '\0';
		mean[s-2] = '\0';
//		printf("insert into TB_dictionary2(word, mean) values('%s', '%s')", word, mean);
		/*for(j = 0; j < a; j++){
			printf(" %d", word[j]);
			if(word[j] < 32 || 126 <= word[j] )
				word[j] = '\0';
		}
puts("");
		for(j = 0; j < s; j++){
			printf(" %d", mean[j]);
			if(mean[j] < 32 || 126 <= mean[j] )
				mean[j] = '\0';
		}*/



puts(word);
printf("strlen: %d\n", strlen(word));
puts("--------------");
puts(mean);
printf("strlen: %d\n", strlen(mean));

//		printf("insert into TB_dictionary2(word, mean) values('%s', '%s')\n", word, mean);		

		sprintf(query, "insert into TB_dictionary2(word, mean) values('%s', '%s')", word, mean);

//	while(1){}	
		if(mysql_query(conn_ptr, query)){
			printf("%s\n", mysql_error(conn_ptr));
		}
		break;
	}

/*	memset(query, '\0', 200);		
	sprintf(query, "select word, mean from TB_dictionary");
	mysql_query(conn_ptr, query);
	res_ptr = mysql_store_result(conn_ptr);	
	row = mysql_fetch_row(res_ptr);
	
	while(row != NULL){
		printf("%s", row[0]);
		printf("%s", row[1]);
		row = mysql_fetch_row(res_ptr);
	}

*/
	return 0;
}
