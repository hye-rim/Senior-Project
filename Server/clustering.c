#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <mysql.h>
#include "clustering.h"

void clusteringStart(unsigned char* buff_rcv, MYSQL* conn_ptr){
	MYSQL_RES	*res_ptr;
	MYSQL_ROW	row;

	char query[250];
	int numOfSentence;
	int* sentenceArr;
	int i;

	memset(query, '\0', sizeof(query));

	sprintf(query, "select count(*) from TB_SENTENCE");

	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
		printf("MYSQL query failed\n");
	}else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		numOfSentence = atoi(row[0]);	// total sentence count from TB_SENTENCE
		sentenceArr = (int*)calloc(numOfSentence, sizeof(int));

		printf("TOTAL SENTENCE COUNT = %d\n", numOfSentence);

		memset(query, '\0', sizeof(query));
	
		sprintf(query, "select SEQ from TB_SENTENCE order by rand()");

		if(mysql_query(conn_ptr, query)){
			printf("%s\n", mysql_error(conn_ptr));
		}
		else{
			res_ptr = mysql_store_result(conn_ptr);

			for(i = 0;  i < numOfSentence; i++){
				row = mysql_fetch_row(res_ptr);
				sentenceArr[i] = atoi(row[0]);
			}	


			for(i  = 0; i < numOfSentence; i++){
				memset(query, '\0', sizeof(query));
				sprintf(query, "select SEQ from TB_USER where clust = -1 AND SEQ = ANY( \
					select userSeq from TB_SentenceClick where sentenceSeq = %d)", sentenceArr[i]);

				if(mysql_query(conn_ptr, query)){
					printf("%s\n", mysql_error(conn_ptr));
				}
				else{
					res_ptr = mysql_store_result(conn_ptr);
					do{
					row = mysql_fetch_row(res_ptr);
					if(row != NULL){
						memset(query, '\0', sizeof(query));

						sprintf(query, "update TB_USER set clust = %d where clust = -1 AND \
						SEQ = ANY(select userSeq from TB_SentenceClick where sentenceSeq = %d)", sentenceArr[i], sentenceArr[i]);

						if(mysql_query(conn_ptr, query)){
							printf("%s\n", mysql_error(conn_ptr));
						}
					}
					}while(row != NULL);
				}
			}
		}
		
		free(sentenceArr);		
	}
}

void countInClust(unsigned char* buff_rcv, MYSQL* conn_ptr){
	MYSQL_RES*	res_ptr;
	MYSQL_RES*	res_ptr1;
	MYSQL_ROW	row;
	MYSQL_ROW	row1;

	int numOfInquiry;
	int i, clust;
	char query[250];

	memset(query, '\0', sizeof(query));	

	sprintf(query, "select count(*) from TB_SentenceClick");
	
	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);
	
		numOfInquiry = atoi(row[0]);

		memset(query, '\0', sizeof(query));	
		
		sprintf(query, "select * from TB_SentenceClick");

		if(mysql_query(conn_ptr, query)){
			printf("%s\n", mysql_error(conn_ptr));
		}
		else{
			res_ptr = mysql_store_result(conn_ptr);

			for(i = 0; i < numOfInquiry; i++){
				row = mysql_fetch_row(res_ptr);
			
				memset(query, '\0', sizeof(query));	
				
				sprintf(query, "select clust from TB_USER where seq = '%s'", row[2]);	

				if(mysql_query(conn_ptr, query)){
					printf("%s\n", mysql_error(conn_ptr));
				}
				else{
					res_ptr1 = mysql_store_result(conn_ptr);
					row1 = mysql_fetch_row(res_ptr1);
					
					clust = atoi(row1[0]);
					
					memset(query, '\0', sizeof(query));

					sprintf(query, "select count(*) from TB_clustChart where clustSeq = %d and sentenceSeq = '%s'", clust, row[1]);

					if(mysql_query(conn_ptr, query)){
						printf("%s\n", mysql_error(conn_ptr));
					}
					else{
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);

						memset(query, '\0', sizeof(query));
						if( 0 == atoi(row1[0])){
							sprintf(query, "insert into TB_clustChart(clustSeq, sentenceSeq, cnt) \
								values(%d, '%s', '%s')", clust, row[1], row[3]);
							if(mysql_query(conn_ptr, query)){
								printf("%s\n", mysql_error(conn_ptr));
							}
						}
						else{
							sprintf(query, "update TB_clustChart set cnt = cnt + '%s' \
								where clustSeq = %d AND sentenceSeq = '%s'", row[3], clust, row[1]);
							if(mysql_query(conn_ptr, query)){
								printf("%s\n", mysql_error(conn_ptr));
							}
						}
					}
				}
			}
		}
	}
}





















