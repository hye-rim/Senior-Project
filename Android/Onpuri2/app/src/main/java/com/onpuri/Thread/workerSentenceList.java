package com.onpuri.Thread;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-18.
 */
public class workerSentenceList extends Thread {
    private static final String TAG = "workerSentenceList";
    private boolean isPlay = false;
    int i, j;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] senData = new byte[20];
    byte[] sen = new byte[261];
    byte[] info = new byte[20];

    PacketUser userSentence;
    int sentence_num;
    int count, num=0;
    private Boolean sentenceEnd = false;

    public workerSentenceList(boolean isPlay,PacketUser userSentence, int sentence_num) {
        this.isPlay = isPlay;
        this.userSentence = userSentence;
        this.sentence_num = sentence_num;
    }

    public int getSentence_num() {
        return sentence_num;
    }

    public PacketUser getUserSentence() {
        return userSentence;
    }

    public Boolean getSentenceEnd() {
        return sentenceEnd;
    }

    public int getCount() {
        return count;
    }

    public void stopThread() {
        isPlay = false;
    }

    public void run() {
        super.run();
        while (isPlay) {
            int sNum = sentence_num/255 + 1;
            int sNumN = sentence_num%255 + 1;

            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_MSL;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) PacketUser.USR_MSL_LEN;
            outData[4] = (byte) sNum; //255이하일 때 1
            outData[5] = (byte) sNumN; //255이하일 때 몫 + 1\
            outData[6] = (byte) PacketUser.CRC;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5); // packet transmission
                dos.flush();
                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                num = 0;
                while (num < 10) {
                    //문장
                    dis.read(sen, 0, 4);
                    for (i = 0; i < 4; i++) {
                        inData[i] = sen[i];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }
                    Log.d(TAG, "num : " + num);
                    Log.d(TAG, "len : " + inData[3]);

                    if(inData[1] == PacketUser.ACK_UMS){
                        //문장 데이터
                        PacketUser.sentence_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);
                        dis.read(sen, 0, (1 + PacketUser.sentence_len));
                        for (j = 0; j <  PacketUser.sentence_len; j++) {
                            inData[j + 4] = sen[j];
                        }
                        //문장번호+해석수+듣기수
                        dis.read(info, 0, 4);
                        for (j = 0; j < 4; j++) {
                            senData[j] = info[j];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }
                        Log.d(TAG, "info : " + info[0]);
                        Log.d(TAG, "info : " + info[1]);
                        Log.d(TAG, "info : " + info[2]);
                        Log.d(TAG, "info : " + info[3]);
                        Log.d(TAG, "infosof : " + senData[0]);
                        Log.d(TAG, "infoopc : " + senData[1]);
                        Log.d(TAG, "infoseq : " + senData[2]);
                        Log.d(TAG, "infolen : " + senData[3]);

                        //문장번호+해석수+듣기수 데이터
                        int len = (int) senData[3];
                        dis.read(info, 0, (1 + len));
                        for (j = 0; j <= len; j++) {
                            senData[j+4] = info[j];
                        }

                        String sen = new String (inData, 4, PacketUser.sentence_len); //문장
                        Log.d(TAG, "sen : " + sen);

                        String seninfo = new String(senData, 4, len);
                        Log.d(TAG, "seninfo : " + seninfo);

                        int plus = seninfo.indexOf('+');
                        String senNum = seninfo.substring(0,plus); //문장번호
                        seninfo = seninfo.substring(plus+1,seninfo.length());
                        plus = seninfo.indexOf('+');
                        String transNum = seninfo.substring(0, plus); //해석수
                        String ListenNum = seninfo.substring(plus+1, seninfo.length()-1); //듣기수

                        userSentence.setSentence(sen);
                        userSentence.setSentenceNum(senNum);
                        userSentence.setSentenceTransNum(transNum);
                        userSentence.setSentenceListenNum(ListenNum);

                        sentence_num++;
                        num++;
                    }
                    else if(inData[1] == PacketUser.ACK_NSEN){
                        count=num;
                        sentenceEnd = true;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }
}