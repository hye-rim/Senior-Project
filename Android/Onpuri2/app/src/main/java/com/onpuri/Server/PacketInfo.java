package com.onpuri.Server;

import android.app.Application;


/**
 * Created by kutemsys on 2016-03-21.
 */
public class PacketInfo extends Application{
    static public char MPC_RDY = 1, MPC_RDY_LEN = 0x14;//Decimal=20
    static public char  ACK_MRY = 2, ACK_MRY_LEN = 1;
    static public char SOF = 0xcc;//Decimal=204
    static public char CRC = 0x55;//Decimal=55

    private static int SEQ = 0;

    static public int getSEQ(){
        addSEQ();
        return SEQ;
    }

    static public void addSEQ(){
        if(SEQ == 255)
            SEQ = 0;
        else
            SEQ++;
    }
}
