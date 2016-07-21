package com.onpuri.Server;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-03-28.
 */
public class PacketUser extends Application {
    static public char USR_LOG = 3, USR_LOG_LEN;
    static public char ACK_ULG = 4, ACK_MRY_LEN;
    static public char USR_OUT = 5, USR_OUT_LEN = 1;
    static public char ACK_URO = 6, ACK_URO_LEN = 1;
    public static char USR_CHK = 7;
    static public char USR_CHK_LEN;
    static public char ACK_UCK = 8, ACK_UCK_LEN = 1;
    public static char USR_REG = 9;
    static public char USR_REG_LEN;
    static public char ACK_URG = 10, ACK_URG_LEN = 1;
    static public char USR_MSL = 11, USR_MSL_LEN = 2; //user main sentence list
    static public char ACK_UMS = 12, ACK_UMS_LEN = 1; //ack user main sentence
    static public char USR_SEN = 13;
    static public char ACK_SEN = 14;
    static public char USR_LEV = 99;
    static public char ACK_LEV = 100;

    public static char SOF = 0xcc;//Decimal=204
    static public char CRC = 0x55;//Decimal=85

    public String userId = "", name = "", joinDate = "", phone = "",
            nowPass = "";
    public ArrayList<String> arrSentence = new ArrayList<String>();
    public ArrayList<String> arrSentenceNum = new ArrayList<String>();
    public int data_len;
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

    public void setSentence(String str) {
        arrSentence.add(str);
        System.out.println("string : " + arrSentence.get(0));
    }
    public void setSentenceNum(String num) {
        arrSentenceNum.add(num);
        System.out.println("string_num : " + arrSentenceNum.get(0));
    }

    public String getSentence(int i){
        return arrSentence.get(i);
    }
    public ArrayList<String> copyList() {
        return arrSentence;
    }

}
