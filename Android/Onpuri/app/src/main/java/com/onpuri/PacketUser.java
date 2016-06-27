package com.onpuri;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-03-28.
 */
public class PacketUser extends Application {
    static char USR_LOG = 3, USR_LOG_LEN;
    static char ACK_ULG = 4, ACK_MRY_LEN;
    static char USR_OUT = 5, USR_OUT_LEN = 1;
    static char ACK_URO = 6, ACK_URO_LEN = 1;
    static char USR_CHK = 7, USR_CHK_LEN;
    static char ACK_UCK = 8, ACK_UCK_LEN = 1;
    static char USR_REG = 9, USR_REG_LEN;
    static char ACK_URG = 10, ACK_URG_LEN = 1;
    static char USR_MSL = 11, USR_MSL_LEN; //user main sentence list
    static char ACK_UMS = 12, ACK_UMS_LEN = 1; //ack user main sentence
    static char SOF = 0xcc;//Decimal=204
    static char CRC = 0x55;//Decimal=85

    static String userId = "", shell = "", problem = "", average = "",
            ranking = "", question = "", solving = "", attend = "",
            purchase = "", sale = "", declaration = "";

    static ArrayList<String> arrSentence = new ArrayList<String>();
    static int data_len, sentence_len;

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

    static public ArrayList<String> copyList() {
        return arrSentence;
    }
}
