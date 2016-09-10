#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <mysql.h>
#include "parse.h"

void sentencRegist(unsigned char* buff_rcv, MYSQL* conn_ptr){
	MYSQL_RES	*res_ptr;	// 쿼리문 결과
	MYSQL_ROW	row;		// 값 빼오기
	FILE* 		f;
	int	sentenceSEQ;
	int	data_len;
	char**	word;
	char	query[250];
	char*	data = " An executive is someone who is employed by a business at a senior level. Executives decide what the business should do, and ensure that it is done.";

	data_len = buff_rcv[3];	
	memset(query, '\0', strlen(query));

	sprintf(query, "INSERT INTO TB_SENTENCE(SEN) VALUES('%s')", data);

	if(mysql_query(conn_ptr, query)){	// 0: success
		printf("%s\n", mysql_error(conn_ptr));	// 문장 추가 실패
	}
	else{	// 문장추가 성공
		sprintf(query, "SELECT LAST_INSERT_ID() FROM TB_SENTENCE");	// 최근 추가된 문장의 SEQ를 얻어온다.

		if(mysql_query(conn_ptr, query)){
			printf("%s\n", mysql_error(conn_ptr));		
			printf("LAST_INSERT_ID() failed!\n");
		}
		else{
			printf("LAST_INSERT_ID() success!\n");
			//값 빼오기 
			res_ptr = mysql_store_result(conn_ptr);
			row = mysql_fetch_row(res_ptr);

			if(row){
				sentenceSEQ = atoi(row[0]);
			}
			// 단어 파싱
			parseSentence(data, word, conn_ptr, sentenceSEQ);
			mysql_free_result(res_ptr);
		}
	}
}

// 단어 파싱 함수
void parseSentence(unsigned char* buff_rcv, char** word, MYSQL* conn_ptr, int sentenceSEQ)
{
	MYSQL_RES *res_ptr;	// 쿼리문 결과
	MYSQL_ROW row;	
	char*	pch;			
	char*	temp_data;
	char	query[250];
	int*	dupleCount;
	int		wordCount = 0;
	int 	charCount = 0;
	int 	index;
	int 	wordSEQ;

	temp_data = (char*)malloc(strlen(buff_rcv)+1);	

	strcpy(temp_data, buff_rcv);

	pch = strtok (temp_data," ,.-");
	while (pch != NULL)
	{	
		wordCount++;
		pch = strtok (NULL, " ,.-");
	}

	strcpy(temp_data, buff_rcv);

	word		= (char**)malloc(wordCount*sizeof(char*));
	dupleCount	= (int*)malloc(wordCount*sizeof(int));	

	wordCount = 0;

	for(index = 0 ; index < strlen(temp_data) ; index++ ){
		if(0x41 <= temp_data[index] && temp_data[index] <= 0x5A || 0x61 <= temp_data[index] && temp_data[index] <= 0x7A )
			charCount++;
		else{
			if( charCount != 0 ){
				word[wordCount++] = (char*)malloc(charCount+1);
				charCount = 0;
			}
		}
	}

	wordCount = 0;
	pch = strtok (temp_data," ,.-");
	while (pch != NULL)
	{	
		strcpy(word[wordCount], pch);
		strlwr(word[wordCount++]);
		pch = strtok(NULL, " ,.'");
	}

	countDuplication(word, dupleCount, wordCount);

	// 단어 데이터베이스에 입력
	for(index = 0; index < wordCount; index++){
		if(strcmp(word[index],"\0") != 0){	// word에 있는 문자가 \0이 아닌경우
			sprintf(query, "SELECT COUNT(*) FROM TB_WORD WHERE WORD = '%s'", word[index]);
			if(mysql_query(conn_ptr, query) != 0){
				printf("%s\n", mysql_error(conn_ptr));
			}
			res_ptr = mysql_store_result(conn_ptr);
			row = mysql_fetch_row(res_ptr);

			if(strcmp(row[0], "0") == 0){ // 중복되는 단어가 없다면 단어를 추가하고 SEQ를 받아와서 Location 디비도 추가한다.
				sprintf(query, "INSERT INTO TB_WORD(WORD) VALUES('%s')", word[index]);
				if(mysql_query(conn_ptr, query) != 0){
					printf("%s\n", mysql_error(conn_ptr));	// 단어 추가 실패
				}

				sprintf(query, "SELECT LAST_INSERT_ID() FROM TB_WORD");	// 최근 추가된 단의 SEQ를 얻어온다.
				if(mysql_query(conn_ptr, query) != 0){
					printf("%s\n", mysql_error(conn_ptr));	// SEQ획득 실패
				}

				res_ptr = mysql_store_result(conn_ptr);
				row = mysql_fetch_row(res_ptr);

				if(row){
					wordSEQ = atoi(row[0]);
				}

				sprintf(query, "INSERT INTO TB_WordLocation(SEQ_Word, SEQ_Sentence, WordCount) VALUES('%d','%d','%d')", wordSEQ, sentenceSEQ, dupleCount[index]);

				if(mysql_query(conn_ptr, query) != 0){
					printf("%s\n", mysql_error(conn_ptr));	// 단어 위치 저장 실패
				}
			}	
			else{	// 입력하려고 하는 다어가 이미 데이터베이스에 있는경우
				sprintf(query, "SELECT SEQ_WORD FROM TB_WORD WHERE WORD = '%s'", word[index] );	// 추가하려는 단어의 SEQ를 얻어온다.
				if(mysql_query(conn_ptr, query) != 0){
					printf("%s\n", mysql_error(conn_ptr));	// SEQ획득 실패
				}

				res_ptr = mysql_store_result(conn_ptr);
				row = mysql_fetch_row(res_ptr);

				if(row){
					wordSEQ = atoi(row[0]);
				}
				// 단어번호, 문장번호, 단어가 포함된 횟수 세가지 모두가 같지 않은 경우에 데이터베이스에 입력한다.
				sprintf(query, "INSERT INTO `TB_WordLocation`(`SEQ_Word`, `SEQ_Sentence`, `WordCount`) SELECT '%d', '%d', '%d' FROM DUAL WHERE NOT EXISTS(SELECT * FROM `TB_WordLocation` WHERE SEQ_Word = '%d' AND SEQ_Sentence = '%d' AND WordCount = '%d')", wordSEQ, sentenceSEQ, dupleCount[index], wordSEQ, sentenceSEQ, dupleCount[index]);

				if(mysql_query(conn_ptr, query) != 0){
					printf("%s\n", mysql_error(conn_ptr));	// 단어 위치 저장 실패
				}
			}
		}
	}	

	for(index  = 0; index < wordCount; index++ ){
		free(word[index]);
	}
	free(word);
	free(temp_data);

}

// 단어 중복 횟수 확인
void countDuplication(char** word, int* dupleCount, int wordCount){
	int i, j;

	for(i = 0; i < wordCount; i++){	// 단어의 기본 횟수를 1로 초기화한다.
		dupleCount[i] = 1;	// 문장에 단어가 나오는 횟수를 뜻함
	}
	for(i = 0; i < wordCount-1; i++){
		for(j = i +1; j < wordCount; j++){
			if(strcasecmp(word[i],word[j]) == 0){	
				memset(word[j],'\0',strlen(word[j]));	
				dupleCount[i]++;
			}
		}
	}

	for(i = 0; i < wordCount; i++){
		if(strcmp(word[i],"\0") != 0){
			printf("(%d)%s: %d\n", i, word[i], dupleCount[i]);
		}
	}
}

char *strlwr(char* str){	// 문자열을 소문자로 변환해주는 함수. 없어서 구현
	unsigned char *p = (unsigned char*)str;
	while(*p){
		*p = tolower(*p);
		p++;
	}
	return str;
}








