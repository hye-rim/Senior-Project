package com.onpuri.Server;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-03-28.
 */
public class PacketUser extends Application {
    static public char USR_LOG = 3; //로그인
    static public char ACK_ULG = 4, ACK_MRY_LEN; //로그인 응답 (아이디, 이름, 가입일, 휴대전화, 현재 비밀번호)

    static final public char USR_OUT = 5, USR_OUT_LEN = 1; //로그아웃
    static final public char ACK_URO = 6, ACK_URO_LEN = 1; //로그아웃 응답
    static public char USR_CHK = 7, USR_CHK_LEN;
    static final public char ACK_UCK = 8, ACK_UCK_LEN = 1;
    static public char USR_REG = 9 , USR_REG_LEN;
    static final public char ACK_URG = 10, ACK_URG_LEN = 1;

    static final public char USR_MSL = 11, USR_MSL_LEN = 2; //홈 문장리스트 (user main sentence list) , 크기
    static final public char ACK_UMS = 12, ACK_UMS_LEN = 1; //홈 문자리스트 응답 (ack user main sentence)

    static final public char USR_SENTRNAS = 13; //3 translation
    static final public char ACK_SENTRNAS = 14; //translation ACK
    static final public char ACK_NTRANS = 15; // no translation
    static final public char USR_MTRANS = 16; //more translation
    static final public char USR_ATRANS = 17; //add translation
    static final public char ACK_ATRANS = 18; //add translation ACK

    static final public char USR_SENLISTEN = 20, USR_SENLISTEN_LEN = 2; //3 recoder
    static final public char ACK_SENLISTEN = 21; //recoder ACK
    static final public char ACK_NLISTEN = 22; // no recoder
    static final public char USR_MLISTEN = 23; //more recoder
    static final public char USR_ALISTEN = 24; //add recoder
    static final public char ACK_ALISTEN = 25; //add recoder ACK

    static final public char USR_SEARCH = 30; //검색 문장,단어 (search)
    static final public char ACK_WORSER = 31; //검색 단어 응답
    static final public char ACK_NWORSER = 32; //검색 단어 결과 없을 때
    static final public char ACK_SENSER = 33; //검색 문장 응답
    static final public char ACK_NSENSER = 34; //검색 문장 결과 없을 때

    static final public char USR_ASEN = 40; //add sentence
    static final public char ACK_ASEN = 41; //add sentence ACK

    static final public char TEST_CREATE_SER = 45; //시험출제 - 유저 리스트 받아오기 - 1담아서 보내기(문자1)
    static final public char ACK_TEST_CREATE_SER = 46; //시험출제 - 유저 리스트 받아오기 응답 - 아이디+이름
    static final public char ACK_TEST_CREATE_NSER = 47; //시험출제 - 유저 리스트 없을 때, 끝 (문자1)

    static final public char TEST_CREATE = 48; //시험출제 정보 보내기
    static final public char ACK_TEST_CREATE = 49; //시험출제 정보 응답 - 실패 0, 성공 1
    static final public char TEST_CREATE_USR = 50; //시험출제 - 제목 + 응시자아이디 (1명씩)
    static final public char ACK_TEST_CREATE_USR = 51; //시험출제 생성 응답 - 실패 0, 성공 1

    static final public char TEST_QUEST = 52; //패킷1 - 문제제목 + 문제, 패킷2 - 정답번호 + 보기리스트
    static final public char ACK_TEST_QUEST = 53; //출제 성공여부 - 실패 0, 성공 1

    static final public char USR_CHANGE = 58; //내정보 변경
    static final public char ACK_CHANGE = 59; //ACK 내정보 변경

    static final public char USR_NOTE = 60; //내노트 정보 요청
    static final public char ACK_NOTE = 61; //내노트 이름 (1+단어모음이름, 2+문장모음이름 - 내노트 이하 패킷 동일 구조)
    static final public char ACK_NNOTE = 62; //내노트 이름 끝날 때 (1, 2)

    static final public char USR_NOTE_ADD = 63; //내노트 이름 추가
    static final public char ACK_NOTE_ADD = 64; //ACK 내노트 이름 추가 - 실패 0, 성공 1
    static final public char USR_NOTE_EDIT = 65; //내노트 이름 수정
    static final public char ACK_NOTE_EDIT = 66; //ACK 내노트 이름 수정 - 실패 0, 성공 1
    static final public char USR_NOTE_DEL = 67; //내노트 이름 삭제
    static final public char ACK_NOTE_DEL = 68; //ACK 내노트 이름 삭제 - 실패 0, 성공 1

    static final public char USR_NOTE_ITEM_ADD = 69; //내노트에 문장,단어 추가
    static final public char ACK_NOTE_ITEM_ADD = 70; //ACK 내노트에 문장,단어 추가

    static final public char USR_NOTE_LOAD = 71; //해당 노트 문장or단어 리스트 받아오기
    static final public char ACK_NOTE_LOAD = 72; //ACK 해당 노트 문장or단어 리스트 받아오기
    static final public char ACK_NNOTE_LOAD = 73; //ACK 해당 노트 문장or단어 리스트 받아오기

    static final public char USR_ACT = 75; //내활동 요청
    static final public char ACK_ACTENRL = 76; //ACK 내활동 - 등록문장
    static final public char ACK_NACTENRL = 77; //ACK 내활동 - 등록문장 없을 때
    static final public char ACK_ACTREC = 78; //ACK 내활동 - 녹음문장
    static final public char ACK_NACTREC = 79; //ACK 내활동 - 녹음문장 없을 때
    static final public char ACK_ACTTRANS = 80; //ACK 내활동 - 해석문장
    static final public char ACK_NACTTRANS = 81; //ACK 내활동 - 해석문장 없을 때
    static final public char ACK_ACTTEST = 82; //ACK 내활동 - 출제한시험 ( 번호, 문제이름, 정답률)
    static final public char ACK_NACTTEST = 83; //ACK 내활동 - 출제한시험 없을 때

    static final public char USR_MYTEST = 84; //내활동 - 출제한 시험 목록 클릭 -> 시험번호 보내주기
    static final public char ACK_MYTEST = 85; //출제한 시험 목록 리스트 - 아이디, 정답률, 날짜
    static final public char ACK_NMYTEST = 86; //출제한 시험 목록 리스트 끝, 없을 때

    static final public char ACK_NSEN = 90; //홈 문장 리스트 없을 경우(no sentence ACK)
    static final public char USR_RECO = 91; //추천
    static final public char ACK_RECO = 92; //ACK 추천
    static final public char USR_DEL = 93; //삭제
    static final public char ACK_DEL = 94; //ACK 추천

    static final public char USR_LEV = 99; //회원 탈퇴(user leave)
    static final public char ACK_LEV = 100; //회원 탈퇴 응답(user leave ACK)

    static final public char USR_TRANS_DETAIL = 101; //해석 자세히보기
    static final public char ACK_TRANS_DETAIL = 102; //ACK 해석 자세히보기

    static final public char USR_TEST_AlllLIST = 104; //전체 시험 목록 요청
    static final public char USR_TEST_LIST = 105; //지정 시험 목록 요청
    static final public char ACK_TEST_LIST = 106; //ACK 시험 목록
    static final public char ACK_NTEST_LIST = 107; //ACK no 시험 목록
    static final public char USR_TEST = 108; //시험 요청
    static final public char ACK_TEST = 109; //ACK 시험 요청
    static final public char ACK_NTEST = 110; //ACK no시험 요청

    static final public char SELECT_SENTENCE = 111;
    static final public char ACK_SELECT_SENTENCE = 112;
    static final public char ACK_NSELECT_SENTENCE = 113;

    static final public char USR_TEST_FINISH = 114; // 시험 결과 전송
    static final public char ACK_TEST_FINISH = 115; //ACK 시험 결과 전송

    public static char SOF = 0xcc;//Decimal=204
    static public char CRC = 0x55;//Decimal=85

    public String userId = "", name = "", joinDate = "", phone = "", nowPass = "";

    public ArrayList<String> arrSentence = new ArrayList<String>();
    public ArrayList<String> arrSentenceNum = new ArrayList<String>();
    public ArrayList<String> arrSentenceTransNum = new ArrayList<String>();
    public ArrayList<String> arrSentenceListenNum = new ArrayList<String>();
    public ArrayList<String> arrSentenceId = new ArrayList<String>();
    public ArrayList<String> arrSentenceReco = new ArrayList<String>();


    public static int data_len;
    public static int sentence_len;

    private static int SEQ = 0;

    static public int getSEQ() {
        addSEQ();
        return SEQ;
    }

    static public void addSEQ() {
        if (SEQ == 255)
            SEQ = 0;
        else
            SEQ++;
    }

    public void setuserId(String str) {
        userId = str;
    }
    public void setSentence(String str) {
        arrSentence.add(str);
    }
    public void setSentenceNum(String num) {
        arrSentenceNum.add(num);
    }
    public void setSentenceTransNum(String num) {
        arrSentenceTransNum.add(num);
    }
    public void setSentenceListenNum(String num) {
        arrSentenceListenNum.add(num);
    }
    public void setsentenceId(String num) {arrSentenceId.add(num); }
    public void setsentenceReco(String reco) {arrSentenceReco.add(reco);}

    public String getuserId() {return userId;}
    public ArrayList<String> copyList() {
        return arrSentence;
    }


}

