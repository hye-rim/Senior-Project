package com.onpuri.Server;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-03-28.
 */
public class PacketUser extends Application {
    static public char USR_LOG = 3, USR_LOG_LEN; //로그인
    static public char ACK_ULG = 4, ACK_MRY_LEN; //로그인 응답 (아이디, 이름, 가입일, 휴대전화, 현재 비밀번호)
    static public char USR_OUT = 5, USR_OUT_LEN = 1; //로그아웃
    static public char ACK_URO = 6, ACK_URO_LEN = 1; //로그아웃 응답
    public static char USR_CHK = 7, USR_CHK_LEN;
    static public char ACK_UCK = 8, ACK_UCK_LEN = 1;
    public static char USR_REG = 9 , USR_REG_LEN;
    static public char ACK_URG = 10, ACK_URG_LEN = 1;

    static public char USR_MSL = 11, USR_MSL_LEN = 2; //홈 문장리스트 (user main sentence list) , 크기
    static public char ACK_UMS = 12, ACK_UMS_LEN = 1; //홈 문자리스트 응답 (ack user main sentence)

    static public char USR_SENTRNAS = 13; //3 translation
    static public char ACK_SENTRNAS = 14; //translation ACK
    static public char ACK_NTRANS = 15; // no translation
    static public char USR_MTRANS = 16; //more translation
    static public char USR_ATRANS = 17; //add translation
    static public char ACK_ATRANS = 18; //add translation ACK

    static public char USR_SENLISTEN = 20; //3 recoder
    static public char ACK_SENLISTEN = 21; //3 recoder ACK
    static public char ACK_NLISTEN = 22; // no recoder
    static public char USR_MLISTEN = 23; //more recoder
    static public char USR_ALISTEN = 24; //add recoder
    static public char ACK_ALISTEN = 25; //add recoder ACK


    static public char USR_SEARCH = 30; //검색 문장,단어 (search)
    static public char ACK_SEARCH = 31; //검색 문장 응답(search ACK)
    static public char ACK_NSEARCH = 32; //검색 문장 결과 없을 때 (no search ACK)

    static public char USR_ASEN = 40; //add sentence
    static public char ACK_ASEN = 41; //add sentence ACK

    static public char USR_CHANGE = 50; //내정보 변경
    static public char ACK_CHANGE = 51; //ACK 내정보 변경

    static public char ACK_NSEN = 90; //홈 문장 리스트 없을 경우(no sentence ACK)
    static public char USR_LEV = 99; //회원 탈퇴(user leave)
    static public char ACK_LEV = 100; //회원 탈퇴 응답(user leave ACK)

    public static char SOF = 0xcc;//Decimal=204
    static public char CRC = 0x55;//Decimal=85

    public String userId = "", name = "", joinDate = "", phone = "", nowPass = "";

    public ArrayList<String> arrSentence = new ArrayList<String>();
    public ArrayList<String> arrSentenceNum = new ArrayList<String>();
    public ArrayList<String> arrSentenceTransNum = new ArrayList<String>();
    public ArrayList<String> arrSentenceListenNum = new ArrayList<String>();

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

    public ArrayList<String> copyList() {
        return arrSentence;
    }

}
