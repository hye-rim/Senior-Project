#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <time.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <pthread.h>
#include <mysql.h>

MYSQL* conn_ptr;
time_t timer;

int main(){
	int a,s,d,f;
	char query[250];

	conn_ptr = mysql_init(NULL);
puts("q");
	conn_ptr = mysql_real_connect(conn_ptr, "127.0.0.1", "root", "kutemsys", "onpuri_eng", 0, NULL, 0);
puts("w");

	
	for(a = 1; a <= 100000; a++){
		memset(query, '\0', 250);
		sprintf(query, "insert into timeTest(seq) values(%d)", a);
		mysql_query(conn_ptr, query);
	}

	return 0;
}
