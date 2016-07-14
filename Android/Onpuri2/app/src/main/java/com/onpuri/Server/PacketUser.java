package com.onpuri.Server;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

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
    static public char USR_MSL = 11, USR_MSL_LEN; //user main sentence list
    static public char ACK_UMS = 12, ACK_UMS_LEN = 1; //ack user main sentence
    public static char SOF = 0xcc;//Decimal=204
    static public char CRC = 0x55;//Decimal=85

    static public String userId = "", shell = "", problem = "", average = "",
            ranking = "", question = "", solving = "", attend = "",
            purchase = "", sale = "", declaration = "";

    static public ArrayList<String> arrSentence = new ArrayList<String>();
    static public ArrayList<String> arrSentenceNum = new ArrayList<String>();
    static public int data_len, sentence_len;

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

    static public void setSentence(String str) {
        arrSentence.add(str);
        System.out.println("string : " + arrSentence.get(0));
    }
    static public void setSentenceNum(String num) {
        arrSentenceNum.add(num);
        System.out.println("stringnum : " + arrSentenceNum.get(0));
    }

    public String getSentence(int i){
        return arrSentence.get(i);
    }
    public ArrayList<String> copyList() {
        return arrSentence;
    }

}
