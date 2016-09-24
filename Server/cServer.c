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
#include "parse.h"

#define DB_HOST "127.0.0.1"
#define DB_USER "root"
#define DB_PASS "kutemsys"
#define DB_NAME "onpuri_eng"

#define SOF 0xcc
#define CRC 0x55
#define MPC_RDY 1
#define ACK_MRY 2
#define USR_LOG 3
#define ACK_ULG 4
#define USR_OUT 5
#define ACK_OUT 6
#define USR_CHK 7
#define ACK_UCK 8
#define USR_REG 9
#define ACK_URG 10
#define SEN_REQ 11	// request from client("SEND MAIN all sentence")
#define SEN_SEND 12	// send sentence to client
#define SEN_CLICK 13	// SEQ of sentence that client cliecked
#define SEN_TRANS 14	//
#define SEN_NOTRANS 15	// there's no translation
#define SEN_SENDALL 16
#define SEN_USERTRANS 17
#define SEN_TRANSREG 18

#define AUDIO_REQUEST_THREE 20
#define AUDIO_REQUEST_THREE_ACK 21
#define AUDIO_REQUEST_THREE_NOMORE 22
#define AUDIO_REQUEST_ALL 23 
#define ADD_RECORD 24	// regist audio
#define ADD_RECORD_ACK 25	//24 respond

#define SEN_SEARCH 30
#define WORD_RESULT 31
#define WORD_NORESULT 32
#define SEN_SEARCHRESULT 33
#define SEN_NORESULT 34

#define SEN_REGISTER 40
#define SEN_REGIACK 41

#define USER_SEARCH 45	// user info search
#define USER_INFO 46
#define USER_INFO_END 47
#define TEST_REGISTER 48
#define TEST_REGISTER_END 49
#define TEST_EXAMINEE 50
#define TEST_EXAMINEE_END 51
#define QUESTION_REGISTER 52
#define QUESTION_REGISTER_END 53

#define INFO_CHANGE 58
#define INFO_CHACK 59

#define NOTE_REQUEST 60
#define NOTE_TRANS_NAME 61
#define NOTE_NONAME 62
#define NOTE_ADD 63
#define NOTE_ADD_ACK 64
#define NOTE_RENAME 65
#define NOTE_RENAME_ACK 66
#define NOTE_DELETE 67
#define NOTE_DELETE_ACK 68

#define NOTE_SEN_REG 69
#define NOTE_SEN_REG_ACK 70

#define NOTE_CONTENT_REQ 71
#define NOTE_CONTENT_SEN 72
#define NOTE_CONTENT_END 73

#define MY_REQUEST 75
#define MY_SEN 76
#define MY_SEN_END 77
#define MY_RECORD 78
#define MY_RECORD_END 79
#define MY_TRANS 80
#define MY_TRANS_END 81
#define MY_TEST 82
#define MY_TEST_END 83
#define TEST_CLICK 84
#define TEST_CLICK_ACK 85
#define TEST_CLICK_END 86


#define SEN_NOMORE 90
#define RECOMMEND 91
#define RECOMMEND_ACK 92
#define DELETE 93
#define DELETE_ACK 94
#define USR_LEAVE 99 //
#define USR_BYE 100 // 회원 탈퇴

#define TRANS_RECOMMEND 101
#define TRANS_REC_ACK 102

#define USER_TESTLIST_REQ_ALL 104
#define USER_TESTLIST_REQ 105
#define USER_TESTLIST_ACK 106
#define USER_TESTLIST_END 107

#define TEST_REQ 108
#define TEST_REQ_ACK 109
#define TEST_REQ_END 110

#define SEN_SEND_ALL 111
#define SEN_SEND_ALL_ACK 112
#define SEN_SEND_ALL_END 113

#define TEST_RESULT 114
#define TEST_RESULT_ACK 115

#define LEN_USER_ID 40
#define LEN_PASS 40
#define LEN_DEVICE_ID 20
#define LEN_MODEL_ID 10
#define LEN_DATA 256
#define LEN_NAME 40
#define LEN_QUERY 500
#define LEN_PHONE 16
#define LEN_DIR 40
#define LEN_QUESTION 60
#define LEN_ANSWER 20

#define DATA_SIZE 256
#define HEAD_SIZE 5
#define PCK_HEAD 4

void* client_session	(void*);
void deviceCheck	(unsigned char*, int);
void TD_ID_Check	(unsigned char*, int);
void TD_USER_REG	(unsigned char*, int);
void table_Check	(unsigned char*, int);
void searchSentence	(unsigned char*, int);
void searchWord		(unsigned char*, int);
void user_logout	(unsigned char*, int);
void userSearch		(unsigned char*, int);
void testExaminee 	(unsigned char*, int);
void recommendation 	(unsigned char*, int);
void deleteResource 	(unsigned char*, int);
void testClick	 	(unsigned char*, int);
void transRecommend	(unsigned char*, int);
void testReqAck		(unsigned char*, int);
void testResult		(unsigned char*, int, int);
void questionRegister   (unsigned char*, unsigned char*, int);

void audioRequest	(unsigned char*, int, int);

void clicked_sentence	(unsigned char*, int, int);
void registTranslation	(unsigned char*, int, int);
void send_trans_all	(unsigned char*, int, int);
void audioRequest	(unsigned char*, int, int);
void userTestlistReq 	(unsigned char*, int, int);
void userTestlistReqAll	(unsigned char*, int, int);

void Make_Packet	(unsigned char*, unsigned char*, int);
void Send_Sentence	(unsigned char*, unsigned char*, int);
void sendSentenceAll	(unsigned char*, unsigned char*, int);
void userInfoChange	(unsigned char*, unsigned char*, int);
void sentenceRegister	(unsigned char*, unsigned char*, int);
void userWithdrawal	(unsigned char*, unsigned char*, int);
void noteNameSend	(unsigned char*, unsigned char*, int);
void noteAdd		(unsigned char*, unsigned char*, int);
void noteRename		(unsigned char*, unsigned char*, int);
void noteDelete		(unsigned char*, unsigned char*, int);
void noteSentenceRegist (unsigned char*, unsigned char*, int);
void noteContentReq 	(unsigned char*, unsigned char*, int);

void testRegister 	(unsigned char*, unsigned char*, int, int);
void sendMyActivity	(unsigned char*, unsigned char*, int, int);
void addRecorder	(unsigned char*, unsigned char*, int, int);

int TD_LOGIN_Check	(unsigned char*, unsigned char*, int, int*);


MYSQL	*conn_ptr;	// 디비 연결 관리


int main( void)
{	
	int	server_socket;
	int	bValid = 1;
	struct sockaddr_in server_addr;

	server_socket  = socket(PF_INET, SOCK_STREAM, 0);

	if(server_socket == -1)
	{
		printf("server socket 생성 실패\n");
		exit(1);
	}
	// 서버소켓 설정
	memset(&server_addr, 0, sizeof( server_addr));
	server_addr.sin_family     = AF_INET;
	server_addr.sin_port       = htons(2040);
	server_addr.sin_addr.s_addr= htonl(INADDR_ANY);	// INADDR_ANY: 서버의 주소를 자동으로 찾아서 대입

	//setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, (const char *)&bValid, sizeof(bValid));
	if (setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, &bValid, sizeof(int)) < 0)
	    error("setsockopt(SO_REUSEADDR) failed");	
	
	if(bind(server_socket, (struct sockaddr*)&server_addr, sizeof(server_addr)) == -1 )
	{
		printf("bind() 실행 에러\n");
		exit(1);
	}

	if(!listen(server_socket, 5) == -1)
	{
		printf("대기상태 모드 설정 실패\n");
		exit(1);
	}

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

	printf("connection success\n");

	// 클라이언트 대기 & 쓰레드 생성
	while(1)
	{	
		int	client_addr_size = 0;
		int temp_fd ;
		struct sockaddr_in client_addr;
		pthread_t thread;

		printf("waiting for data from client.....\n");

		memset(&client_addr, 0, sizeof(client_addr));		
		client_addr_size = sizeof(client_addr);
		temp_fd = accept(server_socket,(struct sockaddr*)&client_addr, &client_addr_size);	// 클라이언트 대기

		if(temp_fd != -1){	// 클라이언트 접속 성공한 경우
			pthread_create(&thread, NULL, client_session, (void *)&temp_fd);			
			pthread_detach(thread);
		}
		else // temp_fd == -1
		{
			printf("클라이언트 연결 수락 실패\n");
			exit(1);
		}      		
	}
}

// 클라이언트 세션`
void* client_session(void* cSocket){		
	int client_fd = *((int*)cSocket);		// 클라이언트 소켓 파일 디스크립터
	int isLogin = 0;	// 로그인 안된 상태
	int readByte;	// read의 상태 -1, 0, 1을 파악하기 위해 -1: 연결종료, 0: 비정상 종료, 1: 데이터 수신Byte
	int userSeq;
	int audioLength, i, j;

	unsigned char   buff_temp[HEAD_SIZE+DATA_SIZE];	// 버퍼로부터 읽은 데이터 일단 헤더 4개만 받아온다.
	unsigned char   buff_rcv[HEAD_SIZE+DATA_SIZE];	// temp로 부터 받은 데이터(버퍼에는 데이터에 대한 구분이 없기 때문에 버퍼에 다른 내용이 쓰일 수 도 있어서 이런식으로 나눠서 받음)
	unsigned char	buff_audio[1000000];
	unsigned char 	userId[LEN_USER_ID];
	
	time_t timer;

	memset(userId, '\0', sizeof(userId));
	
	time(&timer);

	printf("\n\nClient connected! \n");
	printf("%s", ctime(&timer));

	while(1){
		memset(buff_rcv, '\0', sizeof(buff_rcv));		// 기존 데이터 지움
		memset(buff_temp, '\0', sizeof(buff_temp));		// 기존 데이터 지움

		if(readByte = read(client_fd, buff_temp, PCK_HEAD) > 0){		// 클라이언트로부터 패킷 헤더만 받아오기			
			strcat(buff_rcv, buff_temp);	// 일단 헤더까지만 읽어서 buff_temp에 저장			
			memset(buff_temp, '\0', sizeof(buff_temp));	// 머저 받은 패킷의 헤더 지움
puts("=============");
printf("OPC: %d\n", buff_rcv[1]);			
puts("=============");
			if(buff_rcv[1] == ADD_RECORD){
printf("sssss  %d\n", buff_rcv[3]);
				read(client_fd, buff_temp, buff_rcv[3] + 2);
				strcat(buff_rcv, buff_temp);
				memset(buff_temp, '\0', sizeof(buff_temp));

				audioLength = 0;
printf("audio pck: %d %d %d %d %d %d %d\n", buff_rcv[4], buff_rcv[5], buff_rcv[6], buff_rcv[7], buff_rcv[8], buff_rcv[9], buff_rcv[10]);

				for(i = 0; i < buff_rcv[3]; i++){
					audioLength = audioLength * 10 + buff_rcv[6+i] - 48;
					printf("%d ", buff_rcv[6+i]);
				}
puts("");
printf("auauauau lengTH: %d \n", audioLength);
				addRecorder(buff_rcv, userId, client_fd, audioLength);
			}	
			else{
				if(readByte = read(client_fd, buff_temp, buff_rcv[3]+1) > 0){	// 데이터의 길이 + CRC
					strcat(buff_rcv, buff_temp);	// buff_temp[3]을 통해 데이터의 길이를 알고 +1하여 CRC까지 읽어서 buff_rcv에 붙인다.
					switch(buff_rcv[1]){
					case MPC_RDY:{	
						deviceCheck(buff_rcv, client_fd);		// device id, model을 디비 조회해서 옳은지 조사, 없으면 추가 후 ack
					//	clusteringStart(buff_rcv, conn_ptr);
					//	countInClust(buff_rcv, conn_ptr);
						break;
						}
					case USR_LOG:{	// 로그인 id, pass 검사
						if(TD_LOGIN_Check(buff_rcv, userId, client_fd, &userSeq)){
							isLogin = 1;
							printf("UserName: \"%s\" %d   Login!\n", userId, userSeq);
						}			
						break;
						}
					case USR_OUT:{	// 로그아웃 처리
						if(isLogin == 1){	// 로그인이 되어있는 상태일 경우에 로그아웃을 위해 isLogin = 0, userId를 '\0'으로 초기화 한다.
							printf("UserName: \"%s\" Logout!\n", userId);
							isLogin = 0; 
							memset(userId, '\0', strlen(userId));						
							user_logout(buff_rcv, client_fd);
						}
						break;
						}
					case USR_CHK:{				// id 중복체크
						TD_ID_Check(buff_rcv, client_fd);						
						break;
						}
					case USR_REG:{				// 회원가입정보 db저장
						TD_USER_REG(buff_rcv, client_fd);
						break;
						}
					case 77:{
						table_Check(buff_rcv, client_fd);
						break;
						}
					case SEN_CLICK:{
						clicked_sentence(buff_rcv, client_fd, userSeq);
						break;
						}
					case SEN_REQ:{
						Send_Sentence(buff_rcv, userId, client_fd);
						break;
						}
					case SEN_SEND_ALL:{
						sendSentenceAll(buff_rcv, userId, client_fd);
						break;
						}
					case SEN_REGISTER:{
						sentenceRegister(buff_rcv, userId, client_fd);
						break;
						}
					case SEN_SEARCH:{
						searchSentence(buff_rcv, client_fd);	
						break;
						}
					case SEN_SENDALL:{
						send_trans_all(buff_rcv, client_fd, userSeq);
						break;
						}
					case SEN_USERTRANS:{
						memset(buff_temp, '\0', sizeof(buff_temp));
	
						if(readByte = read(client_fd, buff_temp, 2) > 0){
							strcat(buff_rcv, buff_temp);
						}
						registTranslation(buff_rcv, client_fd, userSeq);
	
						break;
						}
					case AUDIO_REQUEST_THREE:{
						audioRequest(buff_rcv, client_fd, 3);
						break;
						}
					case AUDIO_REQUEST_ALL:{
puts("audio all");
						audioRequest(buff_rcv, client_fd, 0);
						break;
						}
					case MY_REQUEST:{
						sendMyActivity(buff_rcv, userId, client_fd, userSeq);	
						break;
						}
					case INFO_CHANGE:{
						userInfoChange(buff_rcv, userId, client_fd);	
						break;
						}
					case NOTE_REQUEST:{
						noteNameSend(buff_rcv, userId, client_fd);
						break;
						}
					case NOTE_ADD:{
						noteAdd(buff_rcv, userId, client_fd);
						break;
						}
					case NOTE_RENAME:{
						noteRename(buff_rcv, userId, client_fd);
						break;
						}						
					case NOTE_DELETE:{
						noteDelete(buff_rcv, userId, client_fd);
						break;
						}
					case NOTE_SEN_REG:{
						noteSentenceRegist(buff_rcv, userId, client_fd);
						break;
						}
					case USER_SEARCH:{
						userSearch(buff_rcv, client_fd);
						break;
						}
					case NOTE_CONTENT_REQ:{
						noteContentReq(buff_rcv, userId, client_fd);
						break;
						}
					case TEST_REGISTER:{
						testRegister(buff_rcv, userId, client_fd, userSeq);
						break;
						}
					case TEST_EXAMINEE:{
						testExaminee(buff_rcv, client_fd);
						break;
						}
					case QUESTION_REGISTER:{
						questionRegister(buff_rcv, userId, client_fd);
						break;
						}
					case RECOMMEND:{
						recommendation(buff_rcv, client_fd);
						break;
						}
					case TEST_CLICK:{
						testClick(buff_rcv, client_fd);
						break;
						}
					case DELETE:{
						deleteResource(buff_rcv, client_fd);
						break;
						}
					case TRANS_RECOMMEND:{
						transRecommend(buff_rcv, client_fd);
						break;
						}
					case USER_TESTLIST_REQ:{
						userTestlistReq(buff_rcv, userSeq, client_fd);
						break;
						}
					case USER_TESTLIST_REQ_ALL:{
						userTestlistReqAll(buff_rcv, userSeq, client_fd);
						break;
						}
					case TEST_REQ:{
						testReqAck(buff_rcv, client_fd);
						break;
						}
					case TEST_RESULT:{
						testResult(buff_rcv, userSeq, client_fd);
						break;
						}
					case USR_LEAVE:{
						userWithdrawal(buff_rcv, userId, client_fd);
	
						if(isLogin == 1){	// 로그인이 되어있는 상태일 경우에 로그아웃을 위해 isLogin = 0, userId를 '\0'으로 초기화 한다.
							isLogin = 0; 
							memset(userId, '\0', strlen(userId));						
							user_logout(buff_rcv, client_fd);
						}
						break;
						}
					}
				}
			}
		}	// 클라이언트로부터 패킷 헤더만 받아오기 종료
		else if(readByte == 0){	// 0인경우
			printf("User end !\n");
			break; 
		}
		else{
			printf("Data receving failed!\n");
			break; 
		}
	}		
	close(client_fd);
}

// DeviceId, Model을 데이터베이스에 조회
void deviceCheck(unsigned char* buff_rcv, int client_fd){
	MYSQL_RES	*res_ptr;	// 쿼리문 결과
	MYSQL_ROW	row;		// 값 빼오기

	char 	query[LEN_QUERY];
	char	toClientData[LEN_DATA];	// client에게 보낼 데이터
	char	D_ID[LEN_DEVICE_ID];
	char	D_MODEL[LEN_MODEL_ID];	
	int	index = 0;	// 데이터 시작 위치
	int	i = 0;
	int	data_len;

	data_len = buff_rcv[3];	

	memset(query, '\0', sizeof(query));
	memset(toClientData, '\0', sizeof(toClientData));
	memset(D_ID, '\0', sizeof(D_ID));
	memset(D_MODEL, '\0', sizeof(D_MODEL));

	while(1){	// deviceId 저장
		if((char)buff_rcv[index + 4] == '+'){
			index++;	
			break;
		}
		else{
			D_ID[i++] = buff_rcv[index + 4];
			index++;
		}		
	}

	i = 0;		
	while(1){	// model 저장
		if(index == data_len){
			break;
		}
		else{
			D_MODEL[i++] = buff_rcv[index+4];
			index++;
		}
	}

	sprintf(query, "SELECT COUNT(*) FROM TB_DEVICE WHERE D_ID='%s'", D_ID);

	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
	}else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		if(row){				//row==false이면 더 이상의 행이 없는상태
			if(row[0][0] == '0'){
				sprintf(query, "INSERT INTO TB_DEVICE(D_ID, D_MODEL) VALUES('%s', '%s')", D_ID, D_MODEL);
				if(mysql_query(conn_ptr, query)){
					printf("%s\n", mysql_error(conn_ptr));
				}
				else{
					printf("Device ID, Model added!\n");
				}
			}
		}
		toClientData[0] = '1';	// MPC에게 ack를 보낸다.

		// 데이터 포장, 패킷에 akc 실어서 보내기
		Make_Packet(buff_rcv, toClientData, client_fd);	
		mysql_free_result(res_ptr);
	}
}

int TD_LOGIN_Check(unsigned char* buff_rcv, unsigned char* userId, int client_fd, int* userSeq){
	MYSQL_RES	*res_ptr;	// 쿼리문 결과
	MYSQL_ROW	row;		// 값 빼오기

	char query[LEN_QUERY];
	char UID[LEN_USER_ID];
	char PASS[LEN_PASS];
	char toClientData[LEN_DATA];	// client에게 보낼 데이터
	int index = 0;	
	int i = 0;
	int data_len;

	data_len = buff_rcv[3];	

	memset(UID, '\0', sizeof(UID));
	memset(PASS, '\0', sizeof(PASS));
	memset(query, '\0', sizeof(query));
	memset(toClientData, '\0', sizeof(toClientData));

	while(1){	// 아이디 저장
		if((char)buff_rcv[index+4] == '+'){
			index++;	
			i = 0;		
			break;
		}
		else{
			UID[i++] = buff_rcv[index+4];
			index++;
		}
	}

	while(1){	// PASS 저장
		if(index == data_len){
			break;
		}
		else{
			PASS[i++] = buff_rcv[index+4];
			index++;
		}
	}

	strcpy(userId, UID);

	// printf("UID: %s\n", UID);
	// printf("PASS: %s\n", PASS);

	sprintf(query, "SELECT COUNT(*) FROM TB_USER WHERE UID='%s' AND PASSWD='%s'", UID, PASS);	// id, pass를 동시에 만족하는 데이터의 수를 찾는다

	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
	}

	res_ptr = mysql_store_result(conn_ptr); // 결과 추출
	row = mysql_fetch_row(res_ptr);

	if(row){
		if((char)*row[0] == '0'){	// 일치하는 데이터의 수 0인 경우
			toClientData[0] = '0';		//login 실패 패킷 보내
			Make_Packet(buff_rcv, toClientData, client_fd);		// 데이터 포장, 패킷에 nack 실어서 보내기
			mysql_free_result(res_ptr);
			return 0;
		}
		else{
			//login 성공 패킷 보내
			sprintf(query, "SELECT UID, R_NAME, REG_DAY, PHONE, PASSWD FROM TB_USER WHERE UID='%s' AND PASSWD='%s'", UID, PASS);
			if(mysql_query(conn_ptr, query)){
				printf("%s\n", mysql_error(conn_ptr));
			}

			res_ptr = mysql_store_result(conn_ptr);
			row = mysql_fetch_row(res_ptr);

			for(i = 0 ; i < 5 ; i++){	// 쿼리를 통해 얻은 사용자 정보로 패킷을 만든다.
				strcat(toClientData, row[i]);
				strcat(toClientData, "+");
			} 

			mysql_free_result(res_ptr);

			Make_Packet(buff_rcv, toClientData, client_fd);

			memset(query, '\0', sizeof(query));

			sprintf(query, "SELECT SEQ FROM TB_USER WHERE UID='%s'", UID);

			if(mysql_query(conn_ptr, query)){
				printf("%s\n", mysql_error(conn_ptr));
			}

			res_ptr = mysql_store_result(conn_ptr);
			row = mysql_fetch_row(res_ptr);
			
			// userSEQ 저장
			(*userSeq) = atoi(row[0]);

			mysql_free_result(res_ptr);
			return 1;
		}
	}
	mysql_free_result(res_ptr);
}

void TD_ID_Check(unsigned char* buff_rcv, int client_fd){
	MYSQL_RES	*res_ptr;	// 쿼리문 결과
	MYSQL_ROW	row;		// 값 빼오기

	char query[LEN_QUERY];
	char UID[LEN_USER_ID];
	char toClientData[LEN_DATA];	// client에게 보낼 데이터
	int index = 0;	
	int	i = 0;
	int	data_len = buff_rcv[3];	

	memset(UID, '\0', sizeof(UID));
	memset(query, '\0', sizeof(query));
	memset(toClientData, '\0', sizeof(toClientData));

	while(1){	// UID 저장
		if(index == data_len){
			break;
		}
		else{
			UID[i++] = buff_rcv[index+4];
			index++;
		}
	}

	sprintf(query, "SELECT COUNT(*) FROM TB_USER WHERE UID='%s'", UID);	// id 중복 확인

	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
	}else{
		//값 빼오기 
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		if(row){	// 데이터가 있을경우
			if((char)*row[0] == '1'){
				// 아이디 중복
				toClientData[0] = '0';
			}
			else{
				// 아이디 사용가능
				toClientData[0] = '1';
			}
		}
		// 데이터 포장, 패킷에 ack 실어서 보내기
		Make_Packet(buff_rcv, toClientData, client_fd);
		mysql_free_result(res_ptr);
	}
}

void TD_USER_REG(unsigned char* buff_rcv, int client_fd){
	MYSQL_ROW row;		// 값 빼오기

	char query[LEN_QUERY];
	char UID[LEN_USER_ID];
	char PASS[LEN_PASS];
	char NAME[LEN_NAME];
	char PHONE[LEN_PHONE];
	char fileDir[LEN_DIR];
	char toClientData[LEN_DATA];	// client에게 보낼 데이터
	int	index = 0;	
	int	i = 0;
	int	data_len;

	data_len = buff_rcv[3];	

	memset(fileDir, '\0', LEN_DIR);
	memset(UID, '\0', sizeof(UID));
	memset(PASS, '\0', sizeof(PASS));
	memset(NAME, '\0', sizeof(NAME));
	memset(PHONE, '\0', sizeof(PHONE));
	memset(query, '\0', sizeof(query));
	memset(toClientData, '\0', sizeof(toClientData));

	while(1){	// 아이디 저장
		if((char)buff_rcv[index+4] == '+'){
			index++;	
			i = 0;		
			break;
		}
		else{
			UID[i++] = buff_rcv[index+4];
			index++;
		}
	}
	while(1){	// 패스워드
		if((char)buff_rcv[index+4] == '+'){
			index++;	
			i = 0;		
			break;
		}
		else{
			PASS[i++] = buff_rcv[index+4];
			index++;
		}
	}
	while(1){	// 이름
		if((char)buff_rcv[index+4] == '+'){
			index++;	
			i = 0;		
			break;
		}
		else{
		//	printf("%d\n", buff_rcv[index+4]);
			NAME[i++] = buff_rcv[index+4];
			index++;
		}
	}
	while(1){	// 전화번호
		if(index == data_len){
			break;
		}
		else{
			PHONE[i++] = buff_rcv[index+4];
			index++;
		}
	}


	sprintf(fileDir, "mkdir storage/%s", UID);
	system(fileDir);

	sprintf(fileDir, "mkdir storage/%s/audio", UID);
	system(fileDir);


	sprintf(query, "INSERT INTO TB_USER(UID, PASSWD, R_NAME, PHONE, REG_DAY) VALUES('%s', '%s', '%s', '%s', CURDATE())", UID, PASS, NAME, PHONE);

	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
		toClientData[0] = '0';
	}
	else{
		toClientData[0] = '1';
	}

	memset(query, '\0', sizeof(query));
	sprintf(query, "insert into TB_note(id, senOrWord, noteName) values('%s', 1, '문장모음')", UID);

	if(mysql_query(conn_ptr, query)){
		printf("TD_USER_REG 1%s\n", mysql_error(conn_ptr));
	}
	
	memset(query, '\0', sizeof(query));
	sprintf(query, "insert into TB_note(id, senOrWord, noteName) values('%s', 2, '단어모음')", UID);

	if(mysql_query(conn_ptr, query)){
		printf("TD_USER_REG 1%s\n", mysql_error(conn_ptr));
	}

	// 데이터 포장, 패킷에 ack 실어서 보내기
	Make_Packet(buff_rcv, toClientData, client_fd);
}

// app main에 문장 전송
void Send_Sentence(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	MYSQL_RES *res_ptr;	// 쿼리문 결과
	MYSQL_RES *res_ptr1;
	MYSQL_ROW row;		// 값 빼오기
	MYSQL_ROW row1;	

	int portion, senNum;
	char query[LEN_QUERY];
	char toClientData[LEN_DATA];	// client에게 보낼 데이터
	int data_len = buff_rcv[3];
	int i, numOfSentence, index = 0, clustSeq;
	char transCount[100], audioCount[100];

	portion = buff_rcv[4] - 1; 
	senNum = buff_rcv[5] - 1; 

	numOfSentence = (portion) * 255 + (senNum);

	memset(query, '\0', sizeof(query));
	sprintf(query, "select clust from TB_USER where UID = '%s'", userId);
	
	if(mysql_query(conn_ptr, query)){
		printf("Send_Sentence1\n%s", mysql_error(conn_ptr));
	}else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);
		clustSeq = atoi(row[0]);

		if(strcmp(row[0], "-1") == 0){	
			for(i = 0 ; i < 10; i++){	// 쿼리를 통해 얻은 사용자 정보로 패킷을 만든다.	
				memset(query, '\0', sizeof(query));
				memset(toClientData, '\0', sizeof(toClientData));
		
				sprintf(query, "SELECT SEN, SEQ, userSeq, recommend FROM TB_SENTENCE LIMIT %d,%d", numOfSentence++,1);
		
				if(mysql_query(conn_ptr, query)){
					printf("%s\n", mysql_error(conn_ptr));
				}
				else{
					res_ptr = mysql_store_result(conn_ptr);
					row = mysql_fetch_row(res_ptr);
		
					if(row == NULL){
						buff_rcv[1] = SEN_NOMORE;
						toClientData[0] = 1;

						Make_Packet(buff_rcv, toClientData, client_fd);
						break;
					}
					else{
						memset(transCount, '\0', sizeof(transCount));
						memset(audioCount, '\0', sizeof(audioCount));
						memset(query, '\0', sizeof(query));
		
						sprintf(query, "select count(*) from TB_Translation where sentenceSeq = '%s'", row[1]);

						mysql_query(conn_ptr, query);
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);

						strcat(transCount, row1[0]);
		
						memset(query, '\0', sizeof(query));

						sprintf(query, "select count(*) from TB_audioDir where sentenceSeq = '%s'", row[1]);
		
						mysql_query(conn_ptr, query);
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);

						strcat(audioCount, row1[0]);
						strcpy(toClientData, row[0]);

						Make_Packet(buff_rcv, toClientData, client_fd);

						memset(query, '\0', sizeof(query));

						sprintf(query, "select UID from TB_USER where seq = '%s'", row[2]);
					
						mysql_query(conn_ptr, query);
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);
			
						memset(toClientData, '\0', sizeof(toClientData));

						strcpy(toClientData, row[1]);
						strcat(toClientData, "+");
						strcat(toClientData, transCount);
						strcat(toClientData, "+");
						strcat(toClientData, audioCount);
						strcat(toClientData, "+");

						if(row1 == NULL)
							strcat(toClientData, "admin");
						else						
							strcat(toClientData, row1[0]);
						
						strcat(toClientData, "+");
						strcat(toClientData, row[3]);
						Make_Packet(buff_rcv, toClientData, client_fd);
					}
				}
			} 
		}
		else{
puts("new method=\n");
			for(i = 0 ; i < 10; i++){	// 쿼리를 통해 얻은 사용자 정보로 패킷을 만든다.	
			memset(query, '\0', sizeof(query));
			memset(toClientData, '\0', sizeof(toClientData));	
	
			sprintf(query, "select SEN, SEQ, userSeq, recommend from TB_SENTENCE where seq = (select sentenceSeq from TB_clustChart where clustSeq = %d order by cnt DESC limit %d, %d)", clustSeq, numOfSentence++, 1);
		
				if(mysql_query(conn_ptr, query)){
					printf("%s\n", mysql_error(conn_ptr));

					buff_rcv[1] = SEN_NOMORE;
					toClientData[0] = 1;

					Make_Packet(buff_rcv, toClientData, client_fd);
					break;
				}
				else{
					res_ptr = mysql_store_result(conn_ptr);
					row = mysql_fetch_row(res_ptr);
		
					if(row == NULL){
						buff_rcv[1] = SEN_NOMORE;
						toClientData[0] = 1;

						Make_Packet(buff_rcv, toClientData, client_fd);
						break;
					}
					else{
						memset(transCount, '\0', sizeof(transCount));
						memset(audioCount, '\0', sizeof(audioCount));
						memset(query, '\0', sizeof(query));
		
						sprintf(query, "select count(*) from TB_Translation where sentenceSeq = '%s'", row[1]);

						mysql_query(conn_ptr, query);
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);

						strcat(transCount, row1[0]);
		
						memset(query, '\0', sizeof(query));

						sprintf(query, "select count(*) from TB_audioDir where sentenceSeq = '%s'", row[1]);
		
						if(mysql_query(conn_ptr, query))
							printf("new method failed \n%s", mysql_error(conn_ptr));
								
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);

						strcat(audioCount, row1[0]);
						strcpy(toClientData, row[0]);

						Make_Packet(buff_rcv, toClientData, client_fd);
		
						memset(query, '\0', sizeof(query));

						sprintf(query, "select UID from TB_USER where seq = '%s'", row[2]);

						if(mysql_query(conn_ptr, query))
							printf("new method failed \n%s", mysql_error(conn_ptr));

						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);
puts(toClientData);
						memset(toClientData, '\0', sizeof(toClientData));

						strcpy(toClientData, row[1]);
						strcat(toClientData, "+");
						strcat(toClientData, transCount);
						strcat(toClientData, "+");
						strcat(toClientData, audioCount);
						strcat(toClientData, "+");

						if(row1 == NULL)
							strcat(toClientData, "admin");
						else
							strcat(toClientData, row1[0]);

						strcat(toClientData, "+");
						strcat(toClientData, row[3]);
puts(toClientData);

						Make_Packet(buff_rcv, toClientData, client_fd);
					}
				}
			} 
		}
	}

	if(res_ptr != NULL) mysql_free_result(res_ptr);
}

void sendSentenceAll(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	MYSQL_RES *res_ptr;	// 쿼리문 결과
	MYSQL_RES *res_ptr1;
	MYSQL_ROW row;		// 값 빼오기
	MYSQL_ROW row1;	

	int portion, senNum;
	char query[LEN_QUERY];
	char toClientData[LEN_DATA];	// client에게 보낼 데이터
	int data_len = buff_rcv[3];
	int i, numOfSentence, index = 0, clustSeq;
	char transCount[100], audioCount[100];

	portion = buff_rcv[4] - 1; 
	senNum = buff_rcv[5] - 1; 

	numOfSentence = (portion) * 255 + (senNum);

	memset(query, '\0', sizeof(query));
	sprintf(query, "select clust from TB_USER where UID = '%s'", userId);
	
	if(mysql_query(conn_ptr, query)){
		printf("Send_Sentence1\n%s", mysql_error(conn_ptr));
	}else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);
		clustSeq = atoi(row[0]);

		if(strcmp(row[0], "-1") == 0){	
			for(i = 0 ; i < 100000; i++){	// 쿼리를 통해 얻은 사용자 정보로 패킷을 만든다.	
				memset(query, '\0', sizeof(query));
				memset(toClientData, '\0', sizeof(toClientData));
		
				sprintf(query, "SELECT SEN, SEQ, userSeq, recommend FROM TB_SENTENCE LIMIT %d,%d", numOfSentence++,1);
		
				if(mysql_query(conn_ptr, query)){
					printf("%s\n", mysql_error(conn_ptr));
				}
				else{
					res_ptr = mysql_store_result(conn_ptr);
					row = mysql_fetch_row(res_ptr);
		
					if(row == NULL){
						buff_rcv[1] = SEN_SEND_ALL_END;
						toClientData[0] = 1;

						Make_Packet(buff_rcv, toClientData, client_fd);
						break;
					}
					else{
						memset(transCount, '\0', sizeof(transCount));
						memset(audioCount, '\0', sizeof(audioCount));
						memset(query, '\0', sizeof(query));
		
						sprintf(query, "select count(*) from TB_Translation where sentenceSeq = '%s'", row[1]);

						mysql_query(conn_ptr, query);
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);

						strcat(transCount, row1[0]);
		
						memset(query, '\0', sizeof(query));

						sprintf(query, "select count(*) from TB_audioDir where sentenceSeq = '%s'", row[1]);
		
						mysql_query(conn_ptr, query);
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);

						strcat(audioCount, row1[0]);
						strcpy(toClientData, row[0]);

						Make_Packet(buff_rcv, toClientData, client_fd);

						memset(query, '\0', sizeof(query));

						sprintf(query, "select UID from TB_USER where seq = '%s'", row[2]);
					
						mysql_query(conn_ptr, query);
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);
			
						memset(toClientData, '\0', sizeof(toClientData));

						strcpy(toClientData, row[1]);
						strcat(toClientData, "+");
						strcat(toClientData, transCount);
						strcat(toClientData, "+");
						strcat(toClientData, audioCount);
						strcat(toClientData, "+");

						if(row1 == NULL)
							strcat(toClientData, "admin");
						else						
							strcat(toClientData, row1[0]);
						
						strcat(toClientData, "+");
						strcat(toClientData, row[3]);
						Make_Packet(buff_rcv, toClientData, client_fd);
					}
				}
			} 
		}
		else{
puts("new method=\n");
			for(i = 0 ; i < 100000; i++){	// 쿼리를 통해 얻은 사용자 정보로 패킷을 만든다.	
			memset(query, '\0', sizeof(query));
			memset(toClientData, '\0', sizeof(toClientData));	
	
			sprintf(query, "select SEN, SEQ, userSeq, recommend from TB_SENTENCE where seq = (select sentenceSeq from TB_clustChart where clustSeq = %d order by cnt DESC limit %d, %d)", clustSeq, numOfSentence++, 1);
		
				if(mysql_query(conn_ptr, query)){
					printf("%s\n", mysql_error(conn_ptr));

					buff_rcv[1] = SEN_SEND_ALL_END;
					toClientData[0] = 1;

					Make_Packet(buff_rcv, toClientData, client_fd);
					break;
				}
				else{
					res_ptr = mysql_store_result(conn_ptr);
					row = mysql_fetch_row(res_ptr);
		
					if(row == NULL){
						buff_rcv[1] = SEN_SEND_ALL_END;
						toClientData[0] = 1;

						Make_Packet(buff_rcv, toClientData, client_fd);
						break;
					}
					else{
						memset(transCount, '\0', sizeof(transCount));
						memset(audioCount, '\0', sizeof(audioCount));
						memset(query, '\0', sizeof(query));
		
						sprintf(query, "select count(*) from TB_Translation where sentenceSeq = '%s'", row[1]);

						mysql_query(conn_ptr, query);
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);

						strcat(transCount, row1[0]);
		
						memset(query, '\0', sizeof(query));

						sprintf(query, "select count(*) from TB_audioDir where sentenceSeq = '%s'", row[1]);
		
						if(mysql_query(conn_ptr, query))
							printf("new method failed \n%s", mysql_error(conn_ptr));
								
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);

						strcat(audioCount, row1[0]);
						strcpy(toClientData, row[0]);

						Make_Packet(buff_rcv, toClientData, client_fd);
		
						memset(query, '\0', sizeof(query));

						sprintf(query, "select UID from TB_USER where seq = '%s'", row[2]);

						if(mysql_query(conn_ptr, query))
							printf("new method failed \n%s", mysql_error(conn_ptr));

						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);

						memset(toClientData, '\0', sizeof(toClientData));

						strcpy(toClientData, row[1]);
						strcat(toClientData, "+");
						strcat(toClientData, transCount);
						strcat(toClientData, "+");
						strcat(toClientData, audioCount);
						strcat(toClientData, "+");

						if(row1 == NULL)
							strcat(toClientData, "admin");
						else
							strcat(toClientData, row1[0]);

						strcat(toClientData, "+");
						strcat(toClientData, row[3]);

						Make_Packet(buff_rcv, toClientData, client_fd);
					}
				}
			} 
		}
	}

	if(res_ptr != NULL) mysql_free_result(res_ptr);
}

void user_logout(unsigned char* buff_rcv, int client_fd){	// MPC에게 
	char toClientData[LEN_DATA];	// client에게 보낼 데이터
	
	memset(toClientData, '\0', sizeof(toClientData));
	toClientData[0] = '1';	// 로그아웃이 됬음을 알리기위해 ack로 응답

	Make_Packet(buff_rcv, toClientData, client_fd);
}

void registTranslation(unsigned char* buff_rcv, int client_fd, int userSeq){
	MYSQL_RES* res_ptr;
	MYSQL_ROW row;

	char query[LEN_QUERY];
	char toClientData[LEN_DATA];
	char translation[LEN_DATA];
	int data_len = buff_rcv[3];
	int sentenceSeq, portion, rest;
	int i = 0, index = 0;
	
	memset(query, '\0', sizeof(query));
	memset(translation, '\0', sizeof(translation));

	while(1){
		if(index == data_len){
			break;
		}
		else{
			translation[i++] = buff_rcv[index + 4];
			index++;
		}
	}
	portion = buff_rcv[4 + index++] - 1;
	rest = buff_rcv[4 + index++] -1;

	sentenceSeq = (portion * 255) + rest;
printf("SENTENCE NUMBER %d\n", sentenceSeq);
	sprintf(query, "SELECT * FROM TB_SENTENCE WHERE SEQ = %d", sentenceSeq);
	
	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);
		if(row != NULL){
			memset(query, '\0', sizeof(query));
			sprintf(query, "INSERT INTO TB_Translation(sentenceSeq, trans, transBy, regDay, recommend)\
					VALUES(%d, '%s', %d, CURDATE(), 0)", sentenceSeq, translation, userSeq);
		printf("@@ %d %s %d", sentenceSeq, translation, userSeq);
			if(mysql_query(conn_ptr, query)){
				printf("%s\n", mysql_error(conn_ptr));
				puts("Insert error\n");
			}
			else{
				toClientData[0] = 1;
				Make_Packet(buff_rcv, toClientData, client_fd);
			}
		}
		mysql_free_result(res_ptr);
	}	
}


void clicked_sentence(unsigned char* buff_rcv, int client_fd, int userSeq){
	MYSQL_RES *res_ptr;	// 쿼리문 결과
	MYSQL_RES *res_ptr1;	// 쿼리문 결과
	MYSQL_ROW row;		// 값 빼오기
	MYSQL_ROW row1;		// 값 빼오기

	char query[LEN_QUERY];
	char toClientData[LEN_DATA];	// client에게 보낼 데이터
	int data_len = buff_rcv[3];
	int i = 0, sentenceSeq, numOfSentence;
	int index = 0;


	memset(query, '\0', sizeof(query));
	memset(toClientData, '\0', sizeof(toClientData));

	while(1){	
		if(index == data_len){
			break;
		}
		else{
			printf("seq: %c ", buff_rcv[index+4]);
			toClientData[i++] = buff_rcv[index+4];
			index++;
		}
	}

	sentenceSeq = atoi(toClientData);

	printf("문장 번호:%d\n", sentenceSeq);

	sprintf(query, "SELECT COUNT(*) FROM TB_SentenceClick WHERE sentenceSeq = %d AND userSeq = %d", sentenceSeq, userSeq);

	if(mysql_query(conn_ptr, query)){
			printf("clicked_sentence 1 \n %s", mysql_error(conn_ptr));
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);
		if(strcmp("0", row[0]) == 0){
			sprintf(query, "INSERT INTO TB_SentenceClick(sentenceSeq, userSeq, cnt) VALUES(%d, %d, 1)", sentenceSeq, userSeq);
			if(mysql_query(conn_ptr, query)){
				printf("%s\n%s\n", query, mysql_error(conn_ptr));
				puts("??????????????\n");
			}	
		}
		else{
			sprintf(query, "UPDATE TB_SentenceClick set cnt = cnt + 1 where sentenceSeq = %d and userSeq = %d", sentenceSeq, userSeq);
			if(mysql_query(conn_ptr, query)){
				printf("%s\n%s\n", query, mysql_error(conn_ptr));
				puts("!!!!!!!!!!!!!!\n");
			}
		}
		mysql_free_result(res_ptr);
	}

	memset(query, '\0', sizeof(query));
	
	sprintf(query, "SELECT trans, transBy, regDay, recommend, seq FROM TB_Translation WHERE sentenceSeq = %d order by recommend desc LIMIT 3", sentenceSeq);

//	sprintf(query, "SELECT trans,\
			(select UID from TB_USER where seq = (select transBy from TB_Translation where sentenceSeq = %d)) ,\
			regDay, recommend FROM TB_Translation WHERE sentenceSeq = %d LIMIT 3", sentenceSeq, sentenceSeq);
	/*Do i need 'ANY' ??*/

	if(mysql_query(conn_ptr, query)){
		printf("clicked_sentnece failed 2 \n %s\n", mysql_error(conn_ptr));
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		do{   
			row = mysql_fetch_row(res_ptr);
			if(row == NULL){
				memset(toClientData, '\0', sizeof(toClientData));
				buff_rcv[1] = SEN_NOTRANS;
				toClientData[0] = '1';
				Make_Packet(buff_rcv, toClientData, client_fd);
				break;
			}
			else{
				memset(toClientData, '\0', sizeof(toClientData));
				strcpy(toClientData, row[0]);

				Make_Packet(buff_rcv, toClientData, client_fd);

				memset(toClientData, '\0', sizeof(toClientData));
				memset(query, '\0', sizeof(query));

				sprintf(query, "select UID from TB_USER where seq = %d", atoi(row[1]));	
				
				if(mysql_query(conn_ptr, query))
					printf("%s\n", mysql_error(conn_ptr));
				else{
					res_ptr1 = mysql_store_result(conn_ptr);
					row1 = mysql_fetch_row(res_ptr1);

					memset(toClientData, '\0', sizeof(toClientData));

					if(row1 == NULL){
						strcat(toClientData, "Admin");
						strcat(toClientData, "+");
						strcat(toClientData, row[2]);
						strcat(toClientData, "+");
						strcat(toClientData, row[3]);
						strcat(toClientData, "+");
						strcat(toClientData, row[4]);
						strcat(toClientData, "+");
					}
					else{		
						strcat(toClientData, row1[0]);
						strcat(toClientData, "+");
						strcat(toClientData, row[2]);
						strcat(toClientData, "+");
						strcat(toClientData, row[3]);
						strcat(toClientData, "+");
						strcat(toClientData, row[4]);
						strcat(toClientData, "+");
					}
					Make_Packet(buff_rcv, toClientData, client_fd);
				}
			}
		}while(row != NULL);
		mysql_free_result(res_ptr);
	}
}

void send_trans_all(unsigned char* buff_rcv, int client_fd, int userSeq){
	MYSQL_RES *res_ptr;	// 쿼리문 결과
	MYSQL_RES *res_ptr1;	// 쿼리문 결과
	MYSQL_ROW row;		// 값 빼오기
	MYSQL_ROW row1;		// 값 빼오기

	char query[LEN_QUERY];
	char toClientData[LEN_DATA];	// client에게 보낼 데이터
	int data_len = buff_rcv[3];
	int i = 0, sentenceSeq, numOfSentence;
	int index = 0;


	memset(query, '\0', sizeof(query));
	memset(toClientData, '\0', sizeof(toClientData));

	while(1){	
		if(index == data_len){
			break;
		}
		else{
			toClientData[i++] = buff_rcv[index+4];
			index++;
		}
	}

	sentenceSeq = atoi(toClientData);

	sprintf(query, "SELECT COUNT(*) FROM TB_SentenceClick WHERE sentenceSeq = %d AND userSeq = %d", sentenceSeq, userSeq);

	if(mysql_query(conn_ptr, query)){
			printf("%s\n", mysql_error(conn_ptr));
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);
		if(strcmp("0", row[0]) == 0){
			sprintf(query, "INSERT INTO TB_SentenceClick(sentenceSeq, userSeq) VALUES(%d, %d)", sentenceSeq, userSeq);
			if(mysql_query(conn_ptr, query)){
				printf("%s\n", mysql_error(conn_ptr));
			}	
		}
		mysql_free_result(res_ptr);
	}

	memset(query, '\0', sizeof(query));
	
	sprintf(query, "SELECT trans, transBy, regDay, recommend, seq FROM TB_Translation WHERE sentenceSeq = %d order by recommend desc", sentenceSeq);

	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		do{   
			row = mysql_fetch_row(res_ptr);
			if(row == NULL){
				memset(toClientData, '\0', sizeof(toClientData));
				buff_rcv[1] = SEN_NOTRANS;
				toClientData[0] = '1';

				Make_Packet(buff_rcv, toClientData, client_fd);
			}
			else{
				memset(toClientData, '\0', sizeof(toClientData));
				strcpy(toClientData, row[0]);

				Make_Packet(buff_rcv, toClientData, client_fd);

				memset(toClientData, '\0', sizeof(toClientData));
				sprintf(query, "select UID from TB_USER where seq = %d", atoi(row[1]));	
				
				if(mysql_query(conn_ptr, query))
					printf("%s\n", mysql_error(conn_ptr));
				else{
					res_ptr1 = mysql_store_result(conn_ptr);
					row1 = mysql_fetch_row(res_ptr1);

					memset(toClientData, '\0', sizeof(toClientData));

					if(row1 == NULL){
						strcat(toClientData, "Admin");
						strcat(toClientData, "+");
						strcat(toClientData, row[2]);
						strcat(toClientData, "+");
						strcat(toClientData, row[3]);
						strcat(toClientData, "+");
						strcat(toClientData, row[4]);
						strcat(toClientData, "+");
					}
					else{		
						strcat(toClientData, row1[0]);
						strcat(toClientData, "+");
						strcat(toClientData, row[2]);
						strcat(toClientData, "+");
						strcat(toClientData, row[3]);
						strcat(toClientData, "+");
						strcat(toClientData, row[4]);
						strcat(toClientData, "+");
					}
					Make_Packet(buff_rcv, toClientData, client_fd);
				}
			}
		}while(row != NULL);
		mysql_free_result(res_ptr);
	}
}

/*사용자 회원 탈퇴
일단 TB_USER에 등록된 내용만 삭제한다. 2016.07.18*/
void userWithdrawal(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	MYSQL_RES *res_ptr;	// 쿼리문 결과
	MYSQL_ROW row;		// 값 빼오기

	char toClientData[LEN_DATA];	// client에게 보낼 데이터
	char query[LEN_QUERY];
	char rmDir[LEN_QUERY];

	memset(query, '\0', sizeof(query));
	memset(rmDir, '\0', LEN_QUERY);
	memset(toClientData, '\0', sizeof(toClientData));

	sprintf(query, "DELETE FROM TB_USER WHERE UID = '%s'", userId);
	sprintf(rmDir, "rm -r %s", userId);

	system(rmDir);
	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
		toClientData[0] = '0';
	}else{
		toClientData[0] = '1';
	}	

	Make_Packet(buff_rcv, toClientData, client_fd);
}

void addRecorder(unsigned char* buff_rcv, unsigned char* userId, int client_fd, int audioLength){
	unsigned char audioTemp;
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char currtime[LEN_DATA];
	char fileName[LEN_QUERY], fileDir[LEN_QUERY];
	int dataLength, i;
	int sentenceSeq;

	FILE* fp;
	time_t regiTime;

	time(&regiTime);
	
	memset(query, '\0', sizeof(query));
	memset(fileName, '\0', sizeof(fileName));
	memset(fileDir, '\0', sizeof(fileDir));
	memset(toClientData, '\0', sizeof(toClientData));
	memset(currtime, '\0', sizeof(currtime));


	sprintf(fileDir, "storage/%s/audio", userId);

	i = 0;
	dataLength = buff_rcv[3];	// audio data length's length
	sentenceSeq = (buff_rcv[4]-1) * 255 + buff_rcv[5]-1;	// sentence number
	printf("%d  %d\n", buff_rcv[4], buff_rcv[5]);

	strcat(currtime, ctime(&regiTime));
	currtime[strlen(currtime)-1] = '\0';
	sprintf(fileName, "%s/%d_%s.mp3", fileDir, sentenceSeq, currtime);
	
	fp = fopen(fileName,"wb");
//printf("audio pck: %d %d %d %d %d %d\n", buff_rcv[0], buff_rcv[1], buff_rcv[2], buff_rcv[3], buff_rcv[4], buff_rcv[5]);

	for(i = 0; i < audioLength; i++){
		read(client_fd, &audioTemp, 1);
//		printf("%d/", audioTemp);
		fputc(audioTemp, fp);
//		printf("%d  ", audioTemp);
	}
	
//while(1){
	read(client_fd, &audioTemp, 1);
printf(" Last data: %d\n", audioTemp);
//}
	fclose(fp);

//	strcat(currtime, ctime(&regiTime));

//	i = 0;

//	while(currtime[i] != '\0'){
//		if(currtime[i] == '\n')
//			currtime[strlen(currtime)-4] = '\0';
//		i++;
//	}

	sprintf(query,"insert into TB_audioDir(userSeq, sentenceSeq, audioDir, size, regDay) \
		values((select seq from TB_USER where UID = '%s'), %d, '%s/%d_%s.mp3', %d, CURDATE())", userId, sentenceSeq, fileDir, sentenceSeq, currtime, audioLength);
puts("zzzzzzzzzz\n");
	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
	}

	toClientData[0] = 1;
	Make_Packet(buff_rcv, toClientData, client_fd);
}

void sentenceRegister(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_ROW row;
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	unsigned char sentence[LEN_DATA];
	int dataLength, i;

	memset(query, '\0', sizeof(query));
	memset(toClientData, '\0', sizeof(toClientData));
	memset(sentence, '\0', sizeof(sentence));
	
	i = 0;
	dataLength = buff_rcv[3];
	while(1){
		if(dataLength == i){
			break;
		}
		else{
			sentence[i] = buff_rcv[4+i];
			i++;
		}
	}

	sprintf(query,"insert into TB_SENTENCE(SEN, userSeq) values('%s', (select seq from TB_USER where UID = '%s'))", sentence, userId);

	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
	}

	toClientData[0] = 1;
	Make_Packet(buff_rcv, toClientData, client_fd);

	memset(query, '\0', LEN_QUERY);
	sprintf(query, "select seq from TB_SENTENCE where SEN = '%s'", sentence);
	printf("select seq from TB_SENTENCE where SEN = '%s'", sentence);
	
	mysql_query(conn_ptr, query);
	res_ptr = mysql_store_result(conn_ptr);
	row = mysql_fetch_row(res_ptr);
	
	newSenClustRegister(conn_ptr, atoi(row[0]));
}
/*
void searchWord(unsigned char* buff_rcv, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_ROW row;

	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	unsigned char search[LEN_DATA];

	int data_len =

}*/
void searchSentence(unsigned char* buff_rcv, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_RES* res_ptr1;
	MYSQL_ROW row;
	MYSQL_ROW row1;

	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	unsigned char search[LEN_DATA];
	
	int data_len = buff_rcv[3];
	int i = 0, sentenceSeq, numOfSentence;
	int tempIndex;

	// word extraction
	memset(search, '\0', sizeof(search));
	while(1){
		if(i == data_len){
			break;
		}
		else{
			if(buff_rcv[4+i] != ' '){
				search[i] = buff_rcv[4 + i];
			}
			else{
				search[i] = '%';
			}
			i++;
		}
	}
printf("EXTRACTED WORD : %s\n", search);
	//search word's meaning
	tempIndex = 0;
	memset(query, '\0', sizeof(query));
	memset(toClientData, '\0', sizeof(toClientData));

	sprintf(query, "select mean from TB_dictionary where word like '%s%%' limit 1", search);	
	
	if(mysql_query(conn_ptr, query)){
		printf("searchSentence 1 failed\n%s", mysql_error(conn_ptr));
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		while(1){
			row = mysql_fetch_row(res_ptr);
			if(row == NULL || tempIndex == 1){
				toClientData[0] = '1';
				buff_rcv[1] = WORD_NORESULT;
				Make_Packet(buff_rcv, toClientData, client_fd);
				break;
			}
			//else if(tempIndex == 1)
			//	break;
			else{
				memset(toClientData, '\0', sizeof(toClientData));
				strcat(toClientData, row[0]);	

				buff_rcv[1] = WORD_RESULT;
				Make_Packet(buff_rcv, toClientData, client_fd);

				tempIndex++;
			}
		}
	}

	// search sentence/ including word
	tempIndex = 0;
	memset(query, '\0', sizeof(query));
	memset(toClientData, '\0', sizeof(toClientData));
	sprintf(query, "select SEN, SEQ, userSeq from TB_SENTENCE where SEN like '%%%s%%' limit 10", search);

	if(mysql_query(conn_ptr, query)){
		printf("%s\n", mysql_error(conn_ptr));
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		memset(query, '\0', sizeof(query));
		if(row != NULL){
			sprintf(query, "select UID from TB_USER where seq = '%s'", row[2]);
			printf("select UID from TB_USER where seq = '%s'", row[2]);
		}

		if(mysql_query(conn_ptr, query)){
			printf("%s\n", mysql_error(conn_ptr));
					toClientData[0] = '1';
					buff_rcv[1] = SEN_NORESULT; 
					Make_Packet(buff_rcv, toClientData, client_fd);
		}
		else{
			//res_ptr1 = mysql_store_result(conn_ptr);

			while(1){
				if(row == NULL || tempIndex == 10){
					toClientData[0] = '1';
					buff_rcv[1] = SEN_NORESULT; 
					Make_Packet(buff_rcv, toClientData, client_fd);
					break;
				}
			//	else if(tempIndex == 10)
			//		break;
				else{
			res_ptr1 = mysql_store_result(conn_ptr);
				row1 = mysql_fetch_row(res_ptr1);
					buff_rcv[1] = SEN_SEARCH;
	
					memset(toClientData, '\0', sizeof(toClientData));
					strcat(toClientData, row[0]);
					Make_Packet(buff_rcv, toClientData, client_fd);
	//printf("data from client:: %s\n", toClientData);
					
					memset(toClientData, '\0', sizeof(toClientData));
				
					strcat(toClientData, row[1]);
					strcat(toClientData, "+");
puts("212121212");
					if(row1 == NULL){
						strcat(toClientData, "admin");
puts("mdkm");
					}
					else{
puts("ssssssm");
						strcat(toClientData, row1[0]);
puts("mfkdsfmsdlakfm");
					}

					Make_Packet(buff_rcv, toClientData, client_fd);
	printf("data from client:: %s\n", toClientData);
				
					tempIndex++;
				}
				row = mysql_fetch_row(res_ptr);

				if(row != NULL){
puts("zzzzzzz");
					memset(query, '\0', sizeof(query));
					sprintf(query, "select UID from TB_USER where seq = '%s'", row[2]);
puts("ggggggg");
					printf("select UID from TB_USER where seq = '%s'", row[2]);
					if(mysql_query(conn_ptr, query)){
					}
				}
				else{
					toClientData[0] = '1';
					buff_rcv[1] = SEN_NORESULT; 
					Make_Packet(buff_rcv, toClientData, client_fd);
					break;
				}
			}
			mysql_free_result(res_ptr);
		}
	}
}

void userInfoChange(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char phone[LEN_PHONE];
	char pass[LEN_PASS];
	int i, j, dataLength;
puts(buff_rcv);
	memset(toClientData, '\0', sizeof(toClientData));
	memset(query, '\0', sizeof(query));
	memset(phone, '\0', sizeof(phone));
	memset(pass, '\0', sizeof(pass));

	i = j = 0;
	dataLength = buff_rcv[3];

	while(1){
		if(buff_rcv[4+i] == '+'){
			i++;
			break;
		}
		else{
			phone[j++] = buff_rcv[4+i];
			i++;
		}
	}
	j = 0;
	while(1){
		if(dataLength == i+1){
			break;
		}
		else{
			pass[j++] = buff_rcv[4+i];
			i++;
		}
	}

	sprintf(query, "update TB_USER set PHONE = '%s', PASSWD = '%s' where UID = '%s'", phone, pass, userId);
	printf( "update TB_USER set PHONE = '%s', PASSWD = '%s' where UID = '%s'", phone, pass, userId);
	
	if(mysql_query(conn_ptr, query)){
		printf("userInfoChange failed\n%s", mysql_error(conn_ptr));
	}

	toClientData[0] = 1;
	Make_Packet(buff_rcv, toClientData, client_fd);	
}	

void sendMyActivity(unsigned char* buff_rcv, unsigned char* userId, int client_fd, int userSeq){
	MYSQL_RES* res_ptr;
	MYSQL_RES* res_ptr1;
	MYSQL_RES* res_ptr2;
	MYSQL_ROW row;
	MYSQL_ROW row1;
	MYSQL_ROW row2;
	
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	
	memset(toClientData, '\0', sizeof(toClientData));
	memset(query, '\0', sizeof(query));

	sprintf(query, "select SEN, SEQ, userSeq from TB_SENTENCE where userSeq = %d", userSeq);
	printf("select SEN, SEQ, userSeq from TB_SENTENCE where userSeq = %d", userSeq);
	
	if(mysql_query(conn_ptr, query)){
		printf("sendMyactivity 1 failed!\n%s", mysql_error(conn_ptr));
	}	
	
	res_ptr = mysql_store_result(conn_ptr);
puts("start!!!");
	while(1){
		row = mysql_fetch_row(res_ptr);
		memset(toClientData, '\0', sizeof(toClientData));
		
		if(row == NULL){
puts("000");
			toClientData[0] = '1';
			buff_rcv[1] = MY_SEN_END;
			Make_Packet(buff_rcv, toClientData, client_fd);
			break;
		}
		else{
puts("111");
			strcpy(toClientData, row[0]);	

			buff_rcv[1] = MY_SEN;

			Make_Packet(buff_rcv, toClientData, client_fd);
	
			// sentence owner
			memset(query, '\0', sizeof(query));

			memset(toClientData, '\0', sizeof(toClientData));

			strcpy(toClientData, row[1]);	
			strcat(toClientData, "+");
	
			sprintf(query, "select UID from TB_USER where seq = '%s'", row[2]);

			if(strcmp(row[2], "-1") == 0){
				strcat(toClientData, "admin");	
			}
			else{
				if(mysql_query(conn_ptr, query))
					printf("sendMyActivity 1.5 failed!\n%s", mysql_error(conn_ptr));

				res_ptr1 = mysql_store_result(conn_ptr);
				row1 = mysql_fetch_row(res_ptr1);

				strcat(toClientData, row1[0]);	
			}

			buff_rcv[1] = MY_SEN;
puts(toClientData);
			Make_Packet(buff_rcv, toClientData, client_fd);
		}
	}


	
	memset(query, '\0', sizeof(query));
	sprintf(query, "select sentenceSeq from TB_audioDir where userSeq = %d", userSeq);
	
	if(mysql_query(conn_ptr, query)){
		printf("sendMyActivity 2 failed\n%s", mysql_error(conn_ptr));
	}
	res_ptr1 = mysql_store_result(conn_ptr);
	row1 = mysql_fetch_row(res_ptr1);
	
	while(1){
		memset(toClientData, '\0', sizeof(toClientData));
		
		if(row1 == NULL){
			toClientData[0] = '1';
			buff_rcv[1] = MY_RECORD_END;	
			Make_Packet(buff_rcv, toClientData, client_fd);
puts(" !!!!!!!!!!!!!!!!1\n");
			break;
		}
		else{
			memset(query, '\0', sizeof(query));
			sprintf(query, "select SEN, SEQ, userSeq from TB_SENTENCE where SEQ = '%s'", row1[0]);

			if(mysql_query(conn_ptr, query)){
				printf("sendMyActivity 3 failed\n%s", mysql_error(conn_ptr));
				break;
			}
			
			res_ptr = mysql_store_result(conn_ptr);
			row = mysql_fetch_row(res_ptr);

			strcpy(toClientData, row[0]);
			buff_rcv[1] = MY_RECORD;	

			Make_Packet(buff_rcv, toClientData, client_fd);	

			memset(toClientData, '\0', sizeof(toClientData));

			strcpy(toClientData, row[1]);	
			strcat(toClientData, "+");	

			sprintf(query, "select UID from TB_USER where seq = '%s'", row[2]);

			if(strcmp(row[2], "-1") == 0){
				strcat(toClientData, "admin");	
			}
			else{
				if(mysql_query(conn_ptr, query))
					printf("sendMyActivity 2.5 failed!\n%s", mysql_error(conn_ptr));

				res_ptr2 = mysql_store_result(conn_ptr);
				row2 = mysql_fetch_row(res_ptr2);

				strcat(toClientData, row2[0]);	
			}

			buff_rcv[1] = MY_RECORD;

			Make_Packet(buff_rcv, toClientData, client_fd);
		}
		
		row1 = mysql_fetch_row(res_ptr1);
	}
	


	memset(query, '\0', sizeof(query));
	sprintf(query, "select sentenceSeq from TB_Translation where transBy = %d", userSeq);

	if(mysql_query(conn_ptr, query)){
		printf("sendMyActivity 4 failed\n%s", mysql_error(conn_ptr));
	}
	
	res_ptr1 = mysql_store_result(conn_ptr);
	row1 = mysql_fetch_row(res_ptr1);
		
	while(1){
		memset(toClientData, '\0', sizeof(toClientData));
		
		if(row1 == NULL){
puts("3333");
			toClientData[0] = '1';
			buff_rcv[1] = MY_TRANS_END;	
			Make_Packet(buff_rcv, toClientData, client_fd);
			break;
		}
		else{
puts("4444");
			memset(query, '\0', sizeof(query));
			sprintf(query, "select SEN, SEQ, userSeq from TB_SENTENCE where SEQ = '%s'", row1[0]);

			if(mysql_query(conn_ptr, query)){
				printf("sendMyActivity 5 failed\n%s", mysql_error(conn_ptr));
			}

			res_ptr = mysql_store_result(conn_ptr);
			row = mysql_fetch_row(res_ptr);

			strcpy(toClientData, row[0]);

			buff_rcv[1] = MY_TRANS;	
puts(toClientData);
			Make_Packet(buff_rcv, toClientData, client_fd);

			memset(toClientData, '\0', sizeof(toClientData));

			strcpy(toClientData, row[1]);	
			strcat(toClientData, "+");	

			sprintf(query, "select UID from TB_USER where seq = '%s'", row[2]);

			if(strcmp(row[2], "-1") == 0){
				strcat(toClientData, "admin");	
			}
			else{
				if(mysql_query(conn_ptr, query))
					printf("sendMyActivity 2.5 failed!\n%s", mysql_error(conn_ptr));

				res_ptr2 = mysql_store_result(conn_ptr);
				row2 = mysql_fetch_row(res_ptr2);

				strcat(toClientData, row2[0]);	
			}
			

			buff_rcv[1] = MY_TRANS;
puts(toClientData);

			Make_Packet(buff_rcv, toClientData, client_fd);
		}
		row1 = mysql_fetch_row(res_ptr1);
	}
	
	// test information
	
	memset(query, '\0', sizeof(query));
	sprintf(query, "select title, ratio, seq, examinee from TB_test where userSeq = %d", userSeq);	
	
	if(mysql_query(conn_ptr, query)){
		printf("sendMyActivity 5 failed\n%s", mysql_error(conn_ptr));

		memset(toClientData, '\0', sizeof(toClientData));

		toClientData[0] = '1';
		buff_rcv[1] = MY_TEST_END;

		Make_Packet(buff_rcv, toClientData, client_fd);
	}else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		while(row != NULL){
			//send title, ratio
			memset(toClientData, '\0', sizeof(toClientData));

			strcat(toClientData, row[0]);
			strcat(toClientData, "+");
			strcat(toClientData, row[1]);
			strcat(toClientData, "+");
			strcat(toClientData, row[3]);

			buff_rcv[1] = MY_TEST;
puts(toClientData);
			Make_Packet(buff_rcv, toClientData, client_fd);
			//send testSeq
			memset(toClientData, '\0', sizeof(toClientData));

			strcat(toClientData, row[2]);
			Make_Packet(buff_rcv, toClientData, client_fd);
puts(toClientData);

			buff_rcv[1] = MY_TEST;
			row = mysql_fetch_row(res_ptr);
		}
		if(row == NULL){
			memset(toClientData, '\0', sizeof(toClientData));

			toClientData[0] = '1';
			buff_rcv[1] = MY_TEST_END;

			Make_Packet(buff_rcv, toClientData, client_fd);
		}
	}
}


void audioRequest(unsigned char* buff_rcv, int client_fd, int numOfAudio){
	MYSQL_RES* res_ptr;
	MYSQL_RES* res_ptr1;
	MYSQL_ROW row;
	MYSQL_ROW row1;

	unsigned char buff_snd[HEAD_SIZE + DATA_SIZE];
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	int sentenceNumber, temp, i, readSize;

	FILE* fp;
	sentenceNumber = (buff_rcv[4]-48)*100 + (buff_rcv[5]-48)*10 + buff_rcv[6] - 48;
printf("numOnAudio: %d\n", numOfAudio);
	if(numOfAudio == 0)
		numOfAudio = 2000;

	temp = 0;

printf("numOnAudio: %d\n", numOfAudio);
	memset(query, '\0', sizeof(query));
	sprintf(query, "select audioDir, size, userSeq, recommend, regDay, seq from TB_audioDir where sentenceSeq = %d order by recommend desc limit %d", sentenceNumber, numOfAudio);

printf("sentenceNumber: %d\n", sentenceNumber);
	if(mysql_query(conn_ptr, query)){
		printf("audioRequest 1 \n%s", mysql_error(conn_ptr));
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		while(1){
			row = mysql_fetch_row(res_ptr);
			if(row == NULL){
puts("Audio NULL!!\n");
				memset(toClientData, '\0', sizeof(toClientData));
				buff_rcv[1] = AUDIO_REQUEST_THREE_NOMORE;
				toClientData[0] = '1';
				Make_Packet(buff_rcv, toClientData, client_fd);
				break;
			}
			else if(temp == numOfAudio){
				puts("Audio temp 3\n");
				break;

				}
			else{
				sprintf(query, "select UID from TB_USER where seq = '%s'", row[2]);
				if(mysql_query(conn_ptr, query)){
					printf("audioRequest 2 failed\n%s", mysql_error(conn_ptr));
				}
				else{
					res_ptr1 = mysql_store_result(conn_ptr);
					row1 = mysql_fetch_row(res_ptr1);

					fp = fopen(row[0], "rb");
					memset(buff_snd, '\0', sizeof(buff_snd));
					// send Head of packet
					buff_snd[0] = SOF;
					buff_snd[1] = AUDIO_REQUEST_THREE_ACK;
					buff_snd[2] = buff_rcv[2];
					buff_snd[3] = strlen(row[1]);

					write(client_fd, buff_snd, strlen(buff_snd));
	
					// send size of data length
					memset(buff_snd, '\0', sizeof(buff_snd));
					strcat(buff_snd, row[1]);
					write(client_fd, buff_snd, strlen(buff_snd));
				puts(buff_snd);	
					// send audio binary file
					while(!feof(fp)){
//puts("00");
						memset(buff_snd, '\0', sizeof(buff_snd));
//puts("10");
						readSize = fread(buff_snd, sizeof(char), sizeof(buff_snd), fp);
						//fgets(buff_snd, sizeof(buff_snd), fp);
//puts("11");
						write(client_fd, buff_snd, readSize);
//puts("12");					
	//					for( i = 0; i < readSize; i++)
	//						printf("%d ", buff_snd[i]);
//						puts("");
					}
					temp++;
					fclose(fp);

					//CRC	
					memset(toClientData, '\0', LEN_DATA);
					toClientData[0] = CRC;
					write(client_fd, toClientData, strlen(toClientData));
			
				
					// Id, recommend, regist day	
					memset(toClientData, '\0', LEN_DATA);
					strcat(toClientData, row1[0]);
					strcat(toClientData, "+");
					strcat(toClientData, row[4]);
					strcat(toClientData, "+");
					strcat(toClientData, row[3]);
					strcat(toClientData, "+");
					strcat(toClientData, row[5]);
					strcat(toClientData, "+");

	///				for( i = 0; i < sizeof(toClientData); i++)
	//					printf("%d ", toClientData[i]);
	//	puts(buff_snd);	
					Make_Packet(buff_rcv, toClientData, client_fd);
				}
			}
		}
	}
}

void noteNameSend(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_ROW row;

	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	int senOrWord,  i, j;
printf("noteName send!!!\n");
puts(buff_rcv);
	// send sentence note name
	memset(query, '\0', sizeof(query));

	sprintf(query, "select noteName, seq from TB_note where id = '%s' and senOrWord = 1", userId);

	if(mysql_query(conn_ptr, query))
		printf("noteNameSend 1\n%s", mysql_error(conn_ptr));
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);
		while(1){
			if(row == NULL){
				memset(toClientData, '\0', sizeof(toClientData));

				toClientData[0] = '1';
				buff_rcv[1] = NOTE_NONAME;

				Make_Packet(buff_rcv, toClientData, client_fd);
				break;
			}
			else{
				memset(toClientData, '\0', sizeof(toClientData));

				toClientData[0] = '1';
				toClientData[1] = '+';
				strcat(toClientData, row[0]);
				strcat(toClientData, "+");
				strcat(toClientData, row[1]);

				buff_rcv[1] = NOTE_TRANS_NAME;
		
				Make_Packet(buff_rcv, toClientData, client_fd);
			}
			row = mysql_fetch_row(res_ptr);
		}
	}


	// send word note name
	memset(query, '\0', sizeof(query));

	sprintf(query, "select noteName, seq from TB_note where id = '%s' and senOrWord = 2", userId);

	if(mysql_query(conn_ptr, query))
		printf("noteNameSend 2\n%s", mysql_error(conn_ptr));
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);
		while(1){
			if(row == NULL){
				memset(toClientData, '\0', sizeof(toClientData));

				toClientData[0] = '2';
				buff_rcv[1] = NOTE_NONAME;

				Make_Packet(buff_rcv, toClientData, client_fd);
				break;
			}
			else{
				memset(toClientData, '\0', sizeof(toClientData));

				toClientData[0] = '2';
				toClientData[1] = '+';
				strcat(toClientData, row[0]);
				strcat(toClientData, "+");
				strcat(toClientData, row[1]);

				buff_rcv[1] = NOTE_TRANS_NAME;
		
				Make_Packet(buff_rcv, toClientData, client_fd);
			}
			row = mysql_fetch_row(res_ptr);
		}
	}
	mysql_free_result(res_ptr);
}

void noteAdd(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	char query[LEN_QUERY];
	char toClientData[LEN_DATA];
	char noteName[LEN_DATA];
	int senOrWord, dataLength, i;

	dataLength = buff_rcv[3];
	senOrWord = buff_rcv[4] - 48;
	i = 0;

	memset(noteName, '\0', LEN_NAME);

	while(1){
		if(dataLength - 2 == i)
			break;
		else{
			noteName[i] = buff_rcv[6 + i];
			i++;
		}
	}

	memset(query, '\0', LEN_QUERY);

	if(senOrWord == 1 || senOrWord == 2){
		sprintf(query, "insert into TB_note(id, senOrWord, noteName) values('%s', %d, '%s')", userId, senOrWord, noteName);
	
		if(mysql_query(conn_ptr, query)){
			printf("noteAdd 1\n%s", mysql_error(conn_ptr));
	
			memset(toClientData, '\0', LEN_DATA);
			toClientData[0] = '0';
	
			Make_Packet(buff_rcv, toClientData, client_fd);
		}
		else{
			memset(toClientData, '\0', LEN_DATA);
			toClientData[0] = '1';
	
			Make_Packet(buff_rcv, toClientData, client_fd);
		}
	}
	else{
		printf("sendOrWord invalid value\n");

		memset(toClientData, '\0', LEN_DATA);
		toClientData[0] = '0';

		Make_Packet(buff_rcv, toClientData, client_fd);
	}
}


void noteRename(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char noteSeq[LEN_DATA];
	char newName[LEN_NAME];
	int dataLength, senOrWord, i, j;

	dataLength = buff_rcv[3];
	senOrWord = buff_rcv[4] - 48;
	i = j = 0;

	memset(noteSeq, '\0', LEN_NAME);
	memset(newName, '\0', LEN_NAME);

	while(1){
		if(buff_rcv[6 + j] == '+'){
			j++;
			break;
		}
		else{
			noteSeq[i] = buff_rcv[6 + j];
			i++;
			j++;
		}
	}

	i = 0;
	while(1){
		if(dataLength - 2 == j)
			break;
		else{
			newName[i] = buff_rcv[6 + j];
			i++;
			j++;
		}
	}

	memset(query, '\0', LEN_QUERY);

	if(senOrWord == 1 || senOrWord == 2){
		sprintf(query,"update TB_note set noteName = '%s' where \
			id = '%s' and seq = '%s' and senOrWord = %d", newName, userId, noteSeq, senOrWord);	

		if(mysql_query(conn_ptr, query)){
			printf("noteRename 1\n%s", mysql_error(conn_ptr));

			memset(toClientData, '\0', LEN_DATA);
			toClientData[0] = '0';

			Make_Packet(buff_rcv, toClientData, client_fd);
		}
		else{
			memset(toClientData, '\0', LEN_DATA);
			toClientData[0] = '1';

			Make_Packet(buff_rcv, toClientData, client_fd);
		}
	}
	else{
		printf("noteRename: senOrWord invalid value\n");
		memset(toClientData, '\0', LEN_DATA);
		toClientData[0] = '0';
		Make_Packet(buff_rcv, toClientData, client_fd);
	}
	
}

void noteDelete(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char noteSeq[LEN_NAME];
	int dataLength, senOrWord, i;

	dataLength = buff_rcv[3];
	senOrWord = buff_rcv[4] - 48;
	i = 0;

	memset(noteSeq, '\0', LEN_NAME);
	
	while(1){
		if(dataLength - 2 == i)
			break;
		else{
			noteSeq[i] = buff_rcv[6 + i];
			i++;
		}
	}

	if(senOrWord == 1 || senOrWord == 2){
		sprintf(query, "delete from TB_note where id = '%s' and senOrWord = %d and seq = '%s'", userId, senOrWord, noteSeq);
		if(mysql_query(conn_ptr, query)){
			printf("noteDelete 1\n %s", mysql_error(conn_ptr));
			
			memset(toClientData, '\0', LEN_DATA);
			toClientData[0] = '0';

			Make_Packet(buff_rcv, toClientData, client_fd);
		}
		else{
			memset(toClientData, '\0', LEN_DATA);
			toClientData[0] = '1';

			Make_Packet(buff_rcv, toClientData, client_fd);
		}
	}
	else{
		printf("noteDelete: senOrWord invlid value\n");
		
		memset(toClientData, '\0', LEN_DATA);
		toClientData[0] = '0';

		Make_Packet(buff_rcv, toClientData, client_fd);
	}
	
}

void noteSentenceRegist(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_ROW row;
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char noteSeq[LEN_NAME], word[LEN_DATA];
	int i, j, senOrWord, sentenceNumber, dataLength;

	i = j = 0;
	senOrWord = buff_rcv[4] - 48;
	dataLength = buff_rcv[3];
	memset(noteSeq, '\0', LEN_NAME);
	memset(word, '\0', LEN_DATA);

	while(1){
		if(buff_rcv[6 + i] == '+'){
			i++;
			break;
		}
		else{
			noteSeq[j] = buff_rcv[6 + i];
			i++;
			j++;	
		}
	}

	
	if(senOrWord == 1){
		sentenceNumber = (buff_rcv[6 + i] - 48 - 1) * 255 + (buff_rcv[7 + i] - 48 - 1);
		printf(" @@ i: %d %d   %d", i, buff_rcv[i-1], buff_rcv[i]);
		i += 2;
	}
	else{
		j = 0;
		while(1){
			if(dataLength - 2 == i)
				break;
			else{
				word[j] = buff_rcv[6 + i];
				j++;
				i++;
			}
		}
	}
	
	memset(query, '\0', LEN_QUERY);
	if(senOrWord == 1)
		sprintf(query, "select count(*) from TB_noteContents where senOrWord = %d and noteSeq = '%s' and sentenceSeq = %d and\
				id = (select UID from TB_USER where UID = '%s')", senOrWord, noteSeq, sentenceNumber, userId);
	else
		sprintf(query, "select count(*) from TB_noteContents where senOrWord = %d and noteSeq = '%s' and\
				sentenceSeq = (select seq from TB_dictionary where word like '%s%%' limit 1) and\
				id = (select UID from TB_USER where UID = '%s')", senOrWord, noteSeq, word, userId);

	if(mysql_query(conn_ptr, query)){
		printf("noteSentenceRegist 1\n%s", mysql_error(conn_ptr));
		
		memset(toClientData, '\0', LEN_DATA);
		toClientData[0] = '0';

		Make_Packet(buff_rcv, toClientData, client_fd);	
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		if(strcmp(row[0], "0") == 0){
			if(senOrWord == 1)
				sprintf(query, "insert into TB_noteContents(senOrWord, noteSeq, sentenceSeq, id)\
						values(%d, '%s', %d, (select UID from TB_USER where UID = '%s'))", senOrWord, noteSeq, sentenceNumber, userId);
			else
				sprintf(query, "insert into TB_noteContents(senOrWord, noteSeq, sentenceSeq, id)\
						values(%d, '%s', (select seq from TB_dictionary where word like '%s%%' limit 1),\
						(select UID from TB_USER where UID = '%s'))", senOrWord, noteSeq, word, userId);
		
		
			if(mysql_query(conn_ptr, query)){
				printf("noteSentenceRegist 1\n%s", mysql_error(conn_ptr));
				
				memset(toClientData, '\0', LEN_DATA);
				toClientData[0] = '0';
		
				Make_Packet(buff_rcv, toClientData, client_fd);	
			}
			else{
				memset(toClientData, '\0', LEN_DATA);
				toClientData[0] = '1';
			
				Make_Packet(buff_rcv, toClientData, client_fd);
			}
		}
		else{
			printf("note insert duplication\n");
			
			memset(toClientData, '\0', LEN_DATA);
			toClientData[0] = '2';
	
			Make_Packet(buff_rcv, toClientData, client_fd);	
		}
	}
}

void noteContentReq(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_RES* res_ptr1;
	MYSQL_RES* res_ptr2;
	MYSQL_ROW row;
	MYSQL_ROW row1;
	MYSQL_ROW row2;

	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char noteSeq[LEN_NAME];
	int i, dataLength, senOrWord;

	memset(noteSeq, '\0', LEN_NAME);
	dataLength = buff_rcv[3];
	senOrWord = buff_rcv[4] - 48;
	i = 0;

	while(1){
		if(dataLength - 2 == i)
			break;
		else{
			noteSeq[i] = buff_rcv[6 + i];
			i++;
		}
	}
	
	memset(query, '\0', LEN_QUERY);

	sprintf(query, "select sentenceSeq from TB_noteContents where senOrWord = %d and noteSeq = '%s' and id = '%s'", senOrWord, noteSeq, userId);

puts(query);
	if(mysql_query(conn_ptr, query)){
		printf("noteContentReq failed!\n%s", mysql_error(conn_ptr));
		
		memset(toClientData, '\0', LEN_DATA);
		toClientData[0] = '1';
		buff_rcv[1] = NOTE_CONTENT_END;

		Make_Packet(buff_rcv, toClientData, client_fd);
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		if(row == NULL){
			printf("noteContentReq ''row == NULL''\n");

			memset(toClientData, '\0', LEN_DATA);
			toClientData[0] = '1';
			buff_rcv[1] = NOTE_CONTENT_END;
	
			Make_Packet(buff_rcv, toClientData, client_fd);
		}
		else{
//			memset(query, '\0', LEN_QUERY);
//			if(senOrWord == 1)
//				sprintf(query, "select SEN, SEQ from TB_SENTENCE where SEQ = '%s'", row[0]);
//			else
//				sprintf(query, "select word, mean from TB_dictionary where seq = '%s'", row[0]);

			while(1){
	//			memset(query, '\0', LEN_QUERY);
	//			if(senOrWord == 1)
	//				sprintf(query, "select SEN, SEQ from TB_SENTENCE where SEQ = '%s'", row[0]);
	//			else
	//				sprintf(query, "select word, mean from TB_dictionary where seq = '%s'", row[0]);

				if(row == NULL){
					memset(toClientData, '\0', LEN_DATA);
					toClientData[0] = '1';
					buff_rcv[1] = NOTE_CONTENT_END;
		puts("note content end call\n");	
					Make_Packet(buff_rcv, toClientData, client_fd);
					break;
				}
				else{
					memset(query, '\0', LEN_QUERY);
					if(senOrWord == 1)
						sprintf(query, "select SEN, SEQ, userSeq from TB_SENTENCE where SEQ = '%s'", row[0]);
					else
						sprintf(query, "select word, mean from TB_dictionary where seq = '%s'", row[0]);

					if(mysql_query(conn_ptr, query)){
						printf("noteContentReq failed!\n%s", mysql_error(conn_ptr));						

						memset(toClientData, '\0', LEN_DATA);

						toClientData[0] = '1';
						buff_rcv[1] = NOTE_CONTENT_END;
	puts("12345\n");
						Make_Packet(buff_rcv, toClientData, client_fd);
						break;
					}
					else{
						res_ptr1 = mysql_store_result(conn_ptr);
						row1 = mysql_fetch_row(res_ptr1);
				
				printf("select SEN, SEQ from TB_SENTENCE where SEQ = '%s'", row[0]);
						if(senOrWord == 1){
							memset(toClientData, '\0', LEN_DATA);
							strcat(toClientData, "1");
							strcat(toClientData, "+");
							strcat(toClientData, row1[0]);
							strcat(toClientData, "+");
							strcat(toClientData, row1[1]);
							
							Make_Packet(buff_rcv, toClientData, client_fd);

							memset(query, '\0', LEN_QUERY);
							
							sprintf(query, "select UID from TB_USER where seq = '%s'", row1[2]);

							if(mysql_query(conn_ptr, query)){
								//memset(toClientData, '\0', LEN_DATA);
//								strcat(toClientData, "admin");
								
//								Make_Packet(buff_rcv, toClientData, client_fd);
							}
							else{
								res_ptr2 = mysql_store_result(conn_ptr);
								row2 = mysql_fetch_row(res_ptr2);
			
								memset(toClientData, '\0', LEN_DATA);
								if(row2 != NULL){
									strcat(toClientData, row2[0]);
								}
								else{
									strcat(toClientData, "admin");
								}

								Make_Packet(buff_rcv, toClientData, client_fd);
							}

							
						}
						else{
							memset(toClientData, '\0', LEN_DATA);
							strcat(toClientData, "1");
							strcat(toClientData, "+");
							strcat(toClientData, row1[0]);
							strcat(toClientData, "+");
							strcat(toClientData, row1[1]);


							Make_Packet(buff_rcv, toClientData, client_fd);
						}
					}
				}
				row = mysql_fetch_row(res_ptr);
//				printf("row value %s\n", row[0]);
			}
		}
	}
puts("by by by by by by");
}

void userSearch (unsigned char* buff_rcv, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_ROW row;

	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	
	if(buff_rcv[4] == '1'){	// send user search result all
puts("1111");
		memset(query, '\0', LEN_QUERY);

		sprintf(query, "select UID, R_NAME from TB_USER");

		if(mysql_query(conn_ptr, query)){
			printf("userSearch failed! \n%s", mysql_error(conn_ptr));

			memset(toClientData, '\0', LEN_DATA);
			
			toClientData[0] = '1';
			buff_rcv[1] = USER_INFO_END;

			Make_Packet(buff_rcv, toClientData, client_fd);
		}
		else{
puts("2222");
			res_ptr = mysql_store_result(conn_ptr);
			row = mysql_fetch_row(res_ptr);
			
			while(row != NULL){
				memset(toClientData, '\0', LEN_DATA);

				strcat(toClientData, row[0]);
				strcat(toClientData, "+");
				strcat(toClientData, row[1]);

				buff_rcv[1] = USER_INFO;
							
				Make_Packet(buff_rcv, toClientData, client_fd);
puts(toClientData);
				row = mysql_fetch_row(res_ptr);
			}	
			mysql_free_result(res_ptr);
	
			memset(toClientData, '\0', LEN_DATA);

			toClientData[0] = '1';
			buff_rcv[1] = USER_INFO_END;

			Make_Packet(buff_rcv, toClientData, client_fd);
		}
	}
	else{	// send opc 47
puts("3333");
		memset(toClientData, '\0', LEN_DATA);

		toClientData[0] = '1';
		buff_rcv[1] = USER_INFO_END;
		
		Make_Packet(buff_rcv, toClientData, client_fd);
	}
}

void testRegister(unsigned char* buff_rcv, unsigned char* userId, int client_fd, int userSeq){
	MYSQL_RES* res_ptr;
	MYSQL_ROW row;
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char testName[LEN_NAME];
	int testType, numOfQuestion, examinee;
	int i,j;
	
puts("==========");
puts("testRegister");
puts(buff_rcv);
puts("==========");
	memset(testName, '\0', LEN_NAME);
	i = 0;

	while(buff_rcv[4 + i] != '+'){
		testName[i] = buff_rcv[4 + i];
		i++;
	}

	i++;
	testType = buff_rcv[4 + (i++)] - 48;

	i++;
	numOfQuestion = buff_rcv[4 + (i++)] - 48;

	i++;
	examinee = buff_rcv[4 + (i++)] - 48;
/////
	puts(buff_rcv);
for(i = 0 ; i < strlen(buff_rcv); i++)
	printf("@ %d @", buff_rcv[i]);
////
/////

	memset(query, '\0', LEN_QUERY);
	sprintf(query, "select count(*) from TB_test where userSeq = %d and title = '%s'", userSeq, testName);

	if(!mysql_query(conn_ptr, query)){
		printf("testRegister 2 fafa\n%s", mysql_error(conn_ptr));
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		if(strcmp(row[0], "0") == 0){
/////
			memset(query, '\0', LEN_QUERY);
	
			sprintf(query, "insert into TB_test(userSeq, title, type, num, examinee, date)\
					values(%d, '%s', %d, %d, %d, CURDATE())"\
					, userSeq, testName, testType, numOfQuestion, examinee);

			if(mysql_query(conn_ptr, query)){
				memset(toClientData, '\0', LEN_DATA);

				toClientData[0] = '0';

				Make_Packet(buff_rcv, toClientData, client_fd);
			}
			else{
				memset(toClientData, '\0', LEN_DATA);

				toClientData[0] = '1';

				Make_Packet(buff_rcv, toClientData, client_fd);
			}
		}
		else{
			memset(toClientData, '\0', LEN_DATA);	

			toClientData[0] = '2';

			Make_Packet(buff_rcv, toClientData, client_fd);	
		}
	}
	else{
		memset(toClientData, '\0', LEN_DATA);

		toClientData[0] = '0';

		Make_Packet(buff_rcv, toClientData, client_fd);	
	}

}

void testExaminee(unsigned char* buff_rcv, int client_fd){
	char query[LEN_QUERY];
	char testTitle[LEN_NAME];
	char examineeId[LEN_NAME];	
	char toClientData[LEN_DATA];
	int i, j, dataLength;
puts("==========");
puts("testExaminee");
puts(buff_rcv);
puts("==========");
	i = j = 0;
	dataLength = buff_rcv[3]; 

	memset(testTitle, '\0', LEN_NAME);
	memset(examineeId, '\0', LEN_NAME);

for(i = 0 ; i < strlen(buff_rcv); i++)
	printf("@ %d @", buff_rcv[i]);
i=0;
	while(1){
		if(buff_rcv[4 + i] == '+'){
			j++;
			break;
		}
		else{
			testTitle[i] = buff_rcv[4 + j];
			i++;
			j++;
		}
	}	

	i = 0;
	while(1){
		if(dataLength == j)
			break;
		else{
			examineeId[i] = buff_rcv[4 + j];
			i++;
			j++;
		}
	}
puts(buff_rcv);
for(i = 0 ; i < strlen(buff_rcv); i++)
	printf("# %d @", buff_rcv[i]);
puts("");
for(i = 0 ; i < strlen(buff_rcv); i++)
	printf("# %c @", buff_rcv[i]);
	memset(query, '\0', LEN_QUERY);

	sprintf(query, "insert into TB_examinee(testSeq, userSeq, date) values(\
			(select seq from TB_test where title = '%s')\
			,(select seq from TB_USER where UID = '%s'), CURDATE())", testTitle, examineeId);
	
	printf("insert into TB_examinee(testSeq, userSeq) values(\
			(select seq from TB_test where title = '%s')\
			,(select seq from TB_USER where UID = '%s'))", testTitle, examineeId);
	if(mysql_query(conn_ptr, query)){
		printf("testExaminee failed! \n%s", mysql_error(conn_ptr));
		
		memset(toClientData, '\0', LEN_DATA);

		toClientData[0] = '0';

		Make_Packet(buff_rcv, toClientData, client_fd);
	}else{
		memset(toClientData, '\0', LEN_DATA);

		toClientData[0] = '1';
		
		Make_Packet(buff_rcv, toClientData, client_fd);
	}
}

void questionRegister(unsigned char* buff_rcv, unsigned char* userId, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_ROW row;
	char toClientData[LEN_DATA];
	char temp_rcv[LEN_DATA], head_rcv[LEN_DATA];
	char query[LEN_QUERY];
	char testTitle[LEN_NAME], question[LEN_QUESTION];
	char answer[LEN_ANSWER], ans1[LEN_ANSWER], ans2[LEN_ANSWER], ans3[LEN_ANSWER], ans4[LEN_ANSWER];
	int i, j, dataLength, questionSeq;

puts("==========");
puts("==========");
puts("==========");
puts("==========");
puts("questionRegister");
puts(buff_rcv);
puts("==========");
	memset(testTitle, '\0', LEN_NAME);
	memset(question, '\0', LEN_NAME);
	
	i = j = 0;
	dataLength = buff_rcv[3];

	while(1){
		if(buff_rcv[4 + i] == '+'){
			j++;
			break;
		}
		else{
			testTitle[i] = buff_rcv[4 + j];
			i++;
			j++;
		}
	}
		
	i = 0;
	while(1){
		if(j == dataLength)
			break;
		else{
			question[i] = buff_rcv[4 + j];
			i++;
			j++;
		}
	}
puts("***********");
puts(buff_rcv);
puts(testTitle);
puts(question);
puts("***********");
//for(i = 0 ; i < strlen(buff_rcv); i++)
//	printf("@ %d @", buff_rcv[i]);

	memset(query, '\0', LEN_QUERY);
	sprintf(query, "insert into TB_question(testSeq, question) values(\
			(select seq from TB_test where title = '%s' and userSeq = (select seq from TB_USER where UID = '%s')), '%s')", testTitle, userId, question);
puts("###########");
puts(query);
puts("###########");
puts("1111111");
	if(!mysql_query(conn_ptr, query)){
		memset(query, '\0', LEN_QUERY);
		sprintf(query, "select seq from TB_question where testSeq = (select seq from TB_test where title = '%s') and question = '%s'", testTitle, question);
		printf("select seq from TB_question where testSeq = (select seq from TB_test where title = '%s') and question = '%s'", testTitle, question);
		
puts("222222");
		mysql_query(conn_ptr, query);
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);
	
		questionSeq = atoi(row[0]);
	
puts("33333");
		// read question & answer frim client buffer
		memset(temp_rcv, '\0', LEN_DATA);
		memset(head_rcv, '\0', LEN_DATA);

		read(client_fd, head_rcv, HEAD_SIZE);
		strcat(temp_rcv, head_rcv);

		read(client_fd, head_rcv, temp_rcv[3] + 1);
		strcat(temp_rcv, head_rcv);

puts("44444");
		// answer extract
		memset(answer, '\0', LEN_ANSWER);
		memset(ans1, '\0', LEN_ANSWER);
		memset(ans2, '\0', LEN_ANSWER);
		memset(ans3, '\0', LEN_ANSWER);
		memset(ans4, '\0', LEN_ANSWER);

		answer[0] = temp_rcv[4];	
		
		i = j = 0;
		dataLength = temp_rcv[3] - 2;

puts("555555");
		while(1){
			if(temp_rcv[6 + j] == '+'){
				j++;
				break;
			}
			else{
				ans1[i] = temp_rcv[6 + j];
				i++;
				j++;
			}
		}

		i = 0;
		while(1){
			if(temp_rcv[6 + j] == '+'){
				j++;
				break;
			}
			else{
				ans2[i] = temp_rcv[6 + j];
				i++;
				j++;
			}
		}
		
		i = 0;
		while(1){
			if(temp_rcv[6 + j] == '+'){
				j++;
				break;
			}
			else{
				ans3[i] = temp_rcv[6 + j];
				i++;
				j++;
			}
		}

		i = 0;
		while(1){
			if(dataLength == j)
				break;
			else{
				ans4[i] = temp_rcv[6 + j];
				i++;
				j++;
			}
		}
puts("HERE");
		memset(query, '\0', LEN_QUERY);
		sprintf(query, "insert into TB_answer(questionSeq, ans1, ans2, ans3, ans4, answer)\
				values(%d, '%s', '%s', '%s', '%s', '%s')", questionSeq, ans1, ans2, ans3, ans4, answer);

		if(mysql_query(conn_ptr, query)){
			printf("%s  failed!!\n%s", query, mysql_error(conn_ptr));
			
			memset(toClientData, '\0', LEN_DATA);

			toClientData[0] = '0';
			buff_rcv[1] = QUESTION_REGISTER_END;

			Make_Packet(buff_rcv, toClientData, client_fd);
for(i = 0 ; i < strlen(toClientData); i++)
	printf("###$$$$ %d", toClientData[i]);
		}
		else{
			memset(toClientData, '\0', LEN_DATA);

			toClientData[0] = '1';
			buff_rcv[1] = QUESTION_REGISTER_END;

			Make_Packet(buff_rcv, toClientData, client_fd);
for(i = 0 ; i < strlen(toClientData); i++)
	printf("& %d", toClientData[i]);
		}

	}
	else
	{
puts("wheehehehe");
puts(mysql_error(conn_ptr));
		memset(toClientData, '\0', LEN_DATA);

		toClientData[0] = '0';
		buff_rcv[1] = QUESTION_REGISTER_END;

		Make_Packet(buff_rcv, toClientData, client_fd);
for(i = 0 ; i < strlen(buff_rcv); i++)
	printf("^ %d", buff_rcv[i]);
	}
}

void recommendation(unsigned char* buff_rcv, int client_fd){	
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char targetNum[LEN_NAME];
	int type, num;
	int i, dataLength;
	
	i = 0;
	dataLength = buff_rcv[3] - 2;
	type = buff_rcv[4] - 48;

	memset(targetNum, '\0', LEN_NAME);

	while(1){
		if(i == dataLength)
			break;
		else{
			targetNum[i] = buff_rcv[6 + i];
			i++;
		}
	}
for(i = 0 ; i < strlen(buff_rcv); i++)
	printf(" !! %d", buff_rcv[i]);

	num = atoi(targetNum);
puts(targetNum);
puts(buff_rcv);
printf("recommend :: %d\n", num);
	memset(query, '\0', LEN_QUERY);

	switch(type){
		case 1:{sprintf(query, "update TB_SENTENCE set recommend = recommend + 1 where seq = %d", num); break;}
		case 2:{sprintf(query, "update TB_Translation set recommend = recommend + 1 where seq = %d", num); break;}
		case 3:{sprintf(query, "update TB_audioDir set recommend = recommend + 1 where seq = %d", num); break;}
	}	
	
	if(mysql_query(conn_ptr, query)){
		printf("recommdation failed!\n%s", mysql_error(conn_ptr));	
		
		memset(toClientData, '\0', LEN_DATA);

		toClientData[0] = '0';
		
		Make_Packet(buff_rcv, toClientData, client_fd);
	}
	else{
		memset(toClientData, '\0', LEN_DATA);

		toClientData[0] = '1';
		
		Make_Packet(buff_rcv, toClientData, client_fd);
	}
}

void testClick(unsigned char* buff_rcv, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_RES* res_ptr1;
	MYSQL_ROW row;
	MYSQL_ROW row1;
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char num[LEN_NAME];
	int i, dataLength;

	dataLength = buff_rcv[3];
	i = 0;
	memset(num, '\0', LEN_NAME);

	while(1){
		if(dataLength == i)
			break;
		else{
			num[i] = buff_rcv[4 + i];
			i++;
		}	
	}
	
	memset(query, '\0', LEN_QUERY);

	sprintf(query, "select userSeq, date, ratio from TB_examinee where testSeq = '%s'", num);

	if(mysql_query(conn_ptr, query)){
		printf("testClick1\n%s", mysql_error(conn_ptr));
		memset(toClientData, '\0', LEN_DATA);
		buff_rcv[1] = TEST_CLICK_END;
		toClientData[0] = '1';
		Make_Packet(buff_rcv, toClientData, client_fd);
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		while(row != NULL){

			memset(query, '\0', LEN_QUERY);
			sprintf(query, "select UID from TB_USER where seq = '%s'", row[0]);
	
			if(mysql_query(conn_ptr, query)){
				printf("testClick1\n%s", mysql_error(conn_ptr));
				memset(toClientData, '\0', LEN_DATA);
				buff_rcv[1] = TEST_CLICK_END;
				toClientData[0] = '1';
				Make_Packet(buff_rcv, toClientData, client_fd);
				break;
			}
			else{
				res_ptr1 = mysql_store_result(conn_ptr);
				row1 = mysql_fetch_row(res_ptr1);
			
				memset(toClientData, '\0', LEN_DATA);
	
				strcat(toClientData, row1[0]);
				strcat(toClientData, "+");
				strcat(toClientData, row[1]);
				strcat(toClientData, "+");
				strcat(toClientData, row[2]);

				Make_Packet(buff_rcv, toClientData, client_fd);
			}	
			row = mysql_fetch_row(res_ptr);
		}
		if(row == NULL){
				printf("testClick1\n%s", mysql_error(conn_ptr));
				memset(toClientData, '\0', LEN_DATA);
				buff_rcv[1] = TEST_CLICK_END;
				toClientData[0] = '1';
				Make_Packet(buff_rcv, toClientData, client_fd);
		}
	}
}

void deleteResource(unsigned char* buff_rcv, int client_fd){
	char toClientData[LEN_DATA];
	char num[LEN_NAME];
	char query[LEN_QUERY];
	int type, dataLength;
	int i;
		
	memset(num, '\0', LEN_NAME);

	i = 0;
	dataLength = buff_rcv[3] - 2;
	type = buff_rcv[4] - 48;

	while(1){
		if(dataLength == i)
			break;
		else{
			num[i] = buff_rcv[6 + i];
			i++;
		}
	}

	memset(query, '\0', LEN_QUERY);

	switch(type){
		case 1: sprintf(query, "delete from TB_SENTENCE where seq = '%s'", num); break;
		case 2: sprintf(query, "delete from TB_Translation where seq = '%s'", num); break;
		case 3: sprintf(query, "delete from TB_audioDir where seq = '%s'", num); break;
	}
	
	memset(toClientData, '\0', LEN_DATA);
printf("delete function::  %s\n", query);
	if(mysql_query(conn_ptr, query)){
		printf("deleteResource 1\n%s", mysql_error(conn_ptr));
		toClientData[0] = '0';
		Make_Packet(buff_rcv, toClientData, client_fd);			
	}
	else{
		toClientData[0] = '1';
		Make_Packet(buff_rcv, toClientData, client_fd);			
	}
}

void transRecommend(unsigned char* buff_rcv, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_ROW row;
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char senNum[LEN_NAME];
	char i, dataLength;

	i = 0;
	dataLength = buff_rcv[3];
	memset(senNum, '\0', LEN_NAME);

	while(i != dataLength){
		senNum[i] = buff_rcv[4 + i];
		i++;
	}

	memset(query, '\0', LEN_QUERY);
	sprintf(query, "select recommend from TB_Translation where seq = '%s'", senNum);

	if(mysql_query(conn_ptr, query)){
		printf("transRecommend1 \n%s", mysql_error(conn_ptr));
	}	
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		memset(toClientData, '\0', LEN_DATA);
		strcat(toClientData, row[0]);

		Make_Packet(buff_rcv, toClientData, client_fd);
	}	
}

void userTestlistReq(unsigned char* buff_rcv, int userSeq, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_RES* res_ptr1;
	MYSQL_RES* res_ptr2;
	MYSQL_ROW row;
	MYSQL_ROW row1;
	MYSQL_ROW row2;
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	int type;

	type = buff_rcv[4] - 48;
puts("=================");

puts("=================");


	memset(query, '\0', LEN_QUERY);

	sprintf(query, "select testSeq from TB_examinee where userSeq = %d", userSeq);
	printf("select testSeq from TB_examinee where userSeq = %d", userSeq);

	if(mysql_query(conn_ptr, query)){
		printf("userTestlistReq1 \n%s", mysql_error(conn_ptr));
		memset(toClientData, '\0', LEN_DATA);
		buff_rcv[1] = USER_TESTLIST_END;
		toClientData[0] = '0';
		Make_Packet(buff_rcv, toClientData, client_fd);
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		if(row == NULL){
			memset(toClientData, '\0', LEN_DATA);
			toClientData[0] = '0';
		buff_rcv[1] = USER_TESTLIST_END;
			Make_Packet(buff_rcv, toClientData, client_fd);
		}
		else{
			while(row != NULL){
				memset(query, '\0', LEN_QUERY);
				sprintf(query, "select seq, userSeq, title, num, examinee, ratio from TB_test where seq = '%s' and type = %d order by desc date", row[0], type);
				printf("select seq, userSeq, title, num, examinee, ratio from TB_test where seq = '%s' and type = %d", row[0], type);
				if(mysql_query(conn_ptr, query)){
					memset(toClientData, '\0', LEN_DATA);
					toClientData[0] = '0';
					buff_rcv[1] = USER_TESTLIST_END;
					Make_Packet(buff_rcv, toClientData, client_fd);
					break;
				}
				else{
					res_ptr1 = mysql_store_result(conn_ptr);
					row1 = mysql_fetch_row(res_ptr1);
					
					if(row1 != NULL){
						memset(query, '\0', LEN_QUERY);
						sprintf(query, "select UID from TB_USER where seq = '%s'", row1[1]);
	
						if(mysql_query(conn_ptr, query)){
							memset(toClientData, '\0', LEN_DATA);
							toClientData[0] = '0';
							buff_rcv[1] = USER_TESTLIST_END;
							Make_Packet(buff_rcv, toClientData, client_fd);
							break;
						}
						else{
							res_ptr2 = mysql_store_result(conn_ptr);
							row2 = mysql_fetch_row(res_ptr2);
	
							memset(toClientData, '\0', LEN_DATA);
							strcat(toClientData, row1[0]);
							strcat(toClientData, "+");
							strcat(toClientData, row2[0]);
							strcat(toClientData, "+");
							strcat(toClientData, row1[2]);
							strcat(toClientData, "+");
							strcat(toClientData, row1[3]);
							strcat(toClientData, "+");
							strcat(toClientData, row1[4]);
							strcat(toClientData, "+");
							strcat(toClientData, row1[5]);
	puts("-----------");
	puts(toClientData);
	puts("-----------");
							Make_Packet(buff_rcv, toClientData, client_fd);
						}
					}
				}
				row = mysql_fetch_row(res_ptr);
			}
			if(row == NULL){
puts("im 107 hahahah");
				memset(toClientData, '\0', LEN_DATA);	
				toClientData[0] = '0';
				buff_rcv[1] = USER_TESTLIST_END;
				Make_Packet(buff_rcv, toClientData, client_fd);
			}
		}
	}
}


void userTestlistReqAll(unsigned char* buff_rcv, int userSeq, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_RES* res_ptr1;
	MYSQL_RES* res_ptr2;
	MYSQL_ROW row;
	MYSQL_ROW row1;
	MYSQL_ROW row2;
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	int type;

	type = buff_rcv[4] - 48;

	memset(query, '\0', LEN_QUERY);

	sprintf(query, "select seq, userSeq, title, num, examinee, ratio from TB_test where examinee = 0 and type = %d order by desc date", type);
	puts(query);

	if(mysql_query(conn_ptr, query)){
		memset(toClientData, '\0', LEN_DATA);
		toClientData[0] = '1';
		buff_rcv[1] = USER_TESTLIST_END;
		Make_Packet(buff_rcv, toClientData, client_fd);
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);
					
		while(row != NULL){
			memset(query, '\0', LEN_QUERY);
			sprintf(query, "select UID from TB_USER where seq = '%s'", row[1]);

			if(mysql_query(conn_ptr, query)){
 				memset(toClientData, '\0', LEN_DATA);
				toClientData[0] = '1';
				buff_rcv[1] = USER_TESTLIST_END;
				Make_Packet(buff_rcv, toClientData, client_fd);
				break;
			}
			else{
				res_ptr1 = mysql_store_result(conn_ptr);
				row1 = mysql_fetch_row(res_ptr1);
	
				memset(toClientData, '\0', LEN_DATA);
				strcat(toClientData, row[0]);
				strcat(toClientData, "+");
				strcat(toClientData, row1[0]);
				strcat(toClientData, "+");
				strcat(toClientData, row[2]);
				strcat(toClientData, "+");
				strcat(toClientData, row[3]);
				strcat(toClientData, "+");
				strcat(toClientData, row[4]);
				strcat(toClientData, "+");
				strcat(toClientData, row[5]);
	puts("-----------");
	puts(toClientData);
	puts("-----------");
				Make_Packet(buff_rcv, toClientData, client_fd);
			}
			row = mysql_fetch_row(res_ptr);
		}
		if(row == NULL){
puts("im 107 hahahah");
			memset(toClientData, '\0', LEN_DATA);	
			toClientData[0] = '0';
			buff_rcv[1] = USER_TESTLIST_END;
			Make_Packet(buff_rcv, toClientData, client_fd);
		}
	}
}

void testReqAck(unsigned char* buff_rcv, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_RES* res_ptr1;
	MYSQL_ROW row;
	MYSQL_ROW row1;
	char toClientData[LEN_DATA];
	char query[LEN_QUERY];
	char senNum[LEN_NAME];
	int i, dataLength;

	i = 0;
	dataLength = buff_rcv[3];
	memset(senNum, '\0', LEN_NAME);

	while(i != dataLength){
		senNum[i] = buff_rcv[4 + i];
		i++;
	}

	memset(query, '\0', LEN_QUERY);
puts(buff_rcv);
	sprintf(query, "select seq, question from TB_question where testSeq = '%s'", senNum);
	printf("select seq, question from TB_question where testSeq = '%s'", senNum);
puts(query);

	if(mysql_query(conn_ptr, query)){
		printf("testReqAck1\n%s", mysql_error(conn_ptr));
		memset(toClientData, '\0', LEN_DATA);
		toClientData[0] = '1';
		buff_rcv[1] = TEST_REQ_END;
		Make_Packet(buff_rcv, toClientData, client_fd);
	}
	else{
		res_ptr = mysql_store_result(conn_ptr);
		row = mysql_fetch_row(res_ptr);

		while(1){
			if(row == NULL){
		printf("testReqAck2\n%s", mysql_error(conn_ptr));
				memset(toClientData, '\0', LEN_DATA);
				toClientData[0] = '1';	
				buff_rcv[1] = TEST_REQ_END;
				Make_Packet(buff_rcv, toClientData, client_fd);
				break;
			}
			else{
				memset(query, '\0', LEN_QUERY);
				sprintf(query, "select ans1, ans2, ans3, ans4, answer from TB_answer where questionSeq = '%s'", row[0]);
	puts(query);	
				if(mysql_query(conn_ptr, query)){
		printf("testReqAck3\n%s", mysql_error(conn_ptr));
					memset(toClientData, '\0', LEN_DATA);
					toClientData[0] = '1';
					buff_rcv[1] = TEST_REQ_END;
					Make_Packet(buff_rcv, toClientData, client_fd);
					break;
				}
				else{
		printf("testReqAck4\n%s", mysql_error(conn_ptr));
					buff_rcv[1] = TEST_REQ;
					res_ptr1 = mysql_store_result(conn_ptr);
					row1 = mysql_fetch_row(res_ptr1);
			
					memset(toClientData, '\0', LEN_DATA);
					strcat(toClientData, row[1]);
	
					Make_Packet(buff_rcv, toClientData, client_fd);
	
					memset(toClientData, '\0', LEN_DATA);
					strcat(toClientData, row1[0]);
					strcat(toClientData, "+");
					strcat(toClientData, row1[1]);
					strcat(toClientData, "+");
					strcat(toClientData, row1[2]);
					strcat(toClientData, "+");
					strcat(toClientData, row1[3]);
					strcat(toClientData, "+");
					strcat(toClientData, row1[4]);
		puts(toClientData);			
					Make_Packet(buff_rcv, toClientData, client_fd);
				}
			}
			row = mysql_fetch_row(res_ptr);
		}
	}
}

void testResult(unsigned char* buff_rcv, int userSeq, int client_fd){
	MYSQL_RES* res_ptr;
	MYSQL_ROW row;
	char toClientData[LEN_DATA], query[LEN_QUERY];
	char testSeq[LEN_NAME], correctNum[LEN_NAME], ratio[LEN_NAME];
	int i, j, dataLength;

	i = j = 0;
	dataLength = buff_rcv[3];
puts(buff_rcv);
	memset(testSeq, '\0', LEN_NAME);
	memset(correctNum, '\0', LEN_NAME);
	memset(ratio, '\0', LEN_NAME);
puts("zzzzzzzzz");
	while(1){
		if(buff_rcv[4 + j] == '+'){
			j++;
			break;
		}
		else{
			testSeq[i] = buff_rcv[4 + j];
			i++;
			j++;
		}
puts(testSeq);
	}
puts("zxzxzxzxzx");
	i = 0;
	while(1){
		if(buff_rcv[4 + j] == '+'){
			j++;
			break;
		}
		else{
			correctNum[i] = buff_rcv[4 + j];
			i++;
			j++;
		}
	}
printf("%s", correctNum);
puts("vbvbvbvbvb");
	i = 0;
	while(1){
		if(j == dataLength)
			break;
		else{
			ratio[i] = buff_rcv[4 + j];
			i++;
			j++;
		}
	}
	puts("mkmkmkmk");
	memset(query, '\0', LEN_QUERY);
	sprintf(query, "update TB_examinee set correct = '%s',  date = CURDATE(),  ratio = '%s' where testSeq = '%s' and userSeq = %d", correctNum, ratio, testSeq, userSeq);
puts(query);
puts("klklklkllk");
	if(mysql_query(conn_ptr, query)){
		printf("testResult 1 \n%s", mysql_error(conn_ptr));
		memset(toClientData, '\0', LEN_DATA);
		toClientData[0] = '0';
		Make_Packet(buff_rcv, toClientData, client_fd);
	}
	else{
		memset(query, '\0', LEN_QUERY);
		sprintf(query, "select num, numOfTry, numOfCorrect from TB_test where seq = '%s'", testSeq);
puts("hhjhjhjjh");
		if(mysql_query(conn_ptr, query)){
			printf("testResult 2 \n%s", mysql_error(conn_ptr));
			memset(toClientData, '\0', LEN_DATA);
			toClientData[0] = '0';
			Make_Packet(buff_rcv, toClientData, client_fd);
		}
		else{
puts("111121212121212121");
			res_ptr = mysql_store_result(conn_ptr);
			row = mysql_fetch_row(res_ptr);
puts("fgkldfmgklalkgmakl");
			if(row == NULL){
				printf("%s  IS NULL\n", query);
				memset(toClientData, '\0', LEN_DATA);
				toClientData[0] = '1';
				Make_Packet(buff_rcv, toClientData, client_fd);
			}
			else{
				memset(query, '\0', LEN_QUERY);
				sprintf(query, "update TB_test set \
						numOfTry = numOfTry + 1,\
						numOfCorrect = numOfCorrect + '%s',\
						ratio = %d where seq = '%s'",\
						correctNum, 100*(atoi(correctNum) + atoi(row[2])) / (atoi(row[0]) * (atoi(row[1]) + 1)), testSeq);

						printf("$$$$$$    %d %d \n", 100*(atoi(correctNum)+ atoi(row[2])), atoi(row[0])*( atoi(row[1])+1));
						printf("$$$$$$    %d %d %d %d \n",atoi(correctNum), atoi(row[2]), atoi(row[0]), atoi(row[1]));
				if(mysql_query(conn_ptr, query)){
					printf("testResult 2 \n%s", mysql_error(conn_ptr));
					memset(toClientData, '\0', LEN_DATA);
					toClientData[0] = '1';
					Make_Packet(buff_rcv, toClientData, client_fd);
				}
				else{
					memset(toClientData, '\0', LEN_DATA);
					toClientData[0] = '1';
puts(toClientData);
					Make_Packet(buff_rcv, toClientData, client_fd);
				}
			}
		}
	}
}

// client에게 패킷을 만들어서 전송
// 패킷은 헤더와 toClientData, CRC를 추가하여 전송한다.
void Make_Packet(unsigned char* buff_rcv, unsigned char* toClientData, int client_fd){
	int i, j;
	unsigned char buff_snd[HEAD_SIZE+DATA_SIZE];

	memset(buff_snd, '\0', sizeof(buff_snd));

	buff_snd[0] = SOF;

	switch(buff_rcv[1]){
	case MPC_RDY: buff_snd[1] = ACK_MRY; break;
	case USR_LOG: buff_snd[1] = ACK_ULG; break;
	case USR_OUT: buff_snd[1] = ACK_OUT; break;
	case USR_CHK: buff_snd[1] = ACK_UCK; break;
	case SEN_REQ: buff_snd[1] = SEN_SEND; break;
	case SEN_CLICK: buff_snd[1] = SEN_TRANS; break;
	case USR_LEAVE: buff_snd[1] = USR_BYE; break;
	case SEN_NOTRANS: buff_snd[1] = SEN_NOTRANS; break;
	case SEN_NOMORE: buff_snd[1] = SEN_NOMORE; break;
	case SEN_SENDALL: buff_snd[1] = SEN_TRANS; break;
	case SEN_USERTRANS: buff_snd[1] = SEN_TRANSREG; break;
	case SEN_SEARCH: buff_snd[1] = SEN_SEARCHRESULT; break;
	case SEN_NORESULT: buff_snd[1] = SEN_NORESULT; break;
	case ADD_RECORD: buff_snd[1] = ADD_RECORD_ACK; break;
	case SEN_REGISTER: buff_snd[1] = SEN_REGIACK; break;
	case INFO_CHANGE: buff_snd[1] = INFO_CHACK; break;
	case MY_SEN_END: buff_snd[1] = MY_SEN_END; break;
	case MY_SEN: buff_snd[1] = MY_SEN; break;
	case MY_RECORD: buff_snd[1] = MY_RECORD; break;
	case MY_RECORD_END: buff_snd[1] = MY_RECORD_END; break;
	case MY_TRANS: buff_snd[1] = MY_TRANS; break;
	case MY_TRANS_END: buff_snd[1] = MY_TRANS_END; break;
	case WORD_RESULT: buff_snd[1] = WORD_RESULT; break;
	case WORD_NORESULT: buff_snd[1] = WORD_NORESULT; break;
	case AUDIO_REQUEST_THREE: buff_snd[1] = AUDIO_REQUEST_THREE_ACK; break;
	case AUDIO_REQUEST_ALL: buff_snd[1] = AUDIO_REQUEST_THREE_ACK; break;
	case AUDIO_REQUEST_THREE_NOMORE: buff_snd[1] = AUDIO_REQUEST_THREE_NOMORE; break;
	case NOTE_TRANS_NAME: buff_snd[1] = NOTE_TRANS_NAME; break;
	case NOTE_NONAME: buff_snd[1] = NOTE_NONAME; break;
	case NOTE_ADD: buff_snd[1] = NOTE_ADD_ACK; break;
	case NOTE_RENAME: buff_snd[1] = NOTE_RENAME_ACK; break;
	case NOTE_DELETE: buff_snd[1] = NOTE_DELETE_ACK; break;
	case NOTE_SEN_REG: buff_snd[1] = NOTE_SEN_REG_ACK; break;
	case NOTE_CONTENT_END: buff_snd[1] = NOTE_CONTENT_END; break;
	case NOTE_CONTENT_REQ: buff_snd[1] = NOTE_CONTENT_SEN; break;
	case MY_TEST: buff_snd[1] = MY_TEST; break;
	case MY_TEST_END: buff_snd[1] = MY_TEST_END; break;
	case USER_INFO: buff_snd[1] = USER_INFO; break;
	case USER_INFO_END: buff_snd[1] = USER_INFO_END; break;
	case TEST_REGISTER: buff_snd[1] = TEST_REGISTER_END; break;
	case TEST_EXAMINEE: buff_snd[1] = TEST_EXAMINEE_END; break;
	case QUESTION_REGISTER_END: buff_snd[1] = QUESTION_REGISTER_END; break;
	case RECOMMEND: buff_snd[1] = RECOMMEND_ACK; break;
	case TEST_CLICK: buff_snd[1] = TEST_CLICK_ACK; break;
	case TEST_CLICK_END: buff_snd[1] = TEST_CLICK_END; break;
	case DELETE: buff_snd[1] = DELETE_ACK; break;
	case TRANS_RECOMMEND: buff_snd[1] = TRANS_REC_ACK; break;
	case USER_TESTLIST_REQ: buff_snd[1] = USER_TESTLIST_ACK; break;
	case USER_TESTLIST_REQ_ALL: buff_snd[1] = USER_TESTLIST_ACK; break;
	case USER_TESTLIST_END: buff_snd[1] = USER_TESTLIST_END; break;
	case TEST_REQ: buff_snd[1] = TEST_REQ_ACK; break;
	case TEST_REQ_END: buff_snd[1] = TEST_REQ_END; break;
	case SEN_SEND_ALL: buff_snd[1] = SEN_SEND_ALL_ACK; break;
	case SEN_SEND_ALL_END: buff_snd[1] = SEN_SEND_ALL_END; break;
	case TEST_RESULT: buff_snd[1] = TEST_RESULT_ACK; break;
	}

	buff_snd[2] = buff_rcv[2];	// 받은 패킷과 같은 시퀀스를 붙여서 보낸다.(클라이언트는 이값을 확인하여 같은 경우에 작업 처리)
	buff_snd[3] = strlen(toClientData);	

	for(i = 0 ; i < strlen(toClientData) ; i++){ // 데이터 길이만큼 데이터 복사
		buff_snd[i+4] = toClientData[i]; 
	}
//puts(buff_snd);
	buff_snd[strlen(toClientData)+4] = CRC;
i =	write(client_fd, buff_snd, strlen(buff_snd));      
	if(buff_snd[1] == USER_TESTLIST_END || buff_snd[1] == TEST_RESULT_ACK){
		printf(" i wroted@!!!!!  %d\n", i);
		for(i = 0; i < strlen(buff_snd); i++)
			printf("$%d", buff_snd[i]);
	}
}

void table_Check(unsigned char* buff_rcv, int client_fd){	// 테이블에 문장 추가
	char toClientData[LEN_DATA];	// client에게 보낼 데이터

	memset(toClientData, '\0', sizeof(toClientData));	
	sentencRegist(buff_rcv, conn_ptr);	// 문장 파싱, 문장&단어 등록	
}
