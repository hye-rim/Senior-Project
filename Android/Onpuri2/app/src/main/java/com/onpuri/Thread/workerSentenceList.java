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
    int i, index;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] senData = new byte[261];
    byte[] temp = new byte[261];

    PacketUser userSentence;
    int sentence_num;
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

                i = 0;
                while (i < 10) {
                    //문장
                    dis.read(temp, 0, 4);
                    for (index = 0; index < 4; index++) {
                        inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }
                    Log.d(TAG, "opc : " + inData[1]);

                    if(inData[1] == PacketUser.ACK_UMS){
                        //문장 데이터
                        dis.read(temp, 0, 1 + (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]));
                        for (index = 0; index <= (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]); index++) {
                            inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                        }

                        //문장번호+해석수+듣기수
                        dis.read(temp, 0, 4);
                        for (index = 0; index < 4; index++) {
                            senData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }

                        //문장번호+해석수+듣기수 데이터
                        dis.read(temp, 0, 1 + (senData[3] <= 0 ? (int) senData[3] + 256 : (int) senData[3]));
                        for (index = 0; index <= (senData[3] <= 0 ? (int) senData[3] + 256 : (int) senData[3]); index++) {
                            senData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                        }

                        PacketUser.sentence_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);
                        int len = ((int) senData[3] <= 0 ? (int) senData[3] + 256 : (int) senData[3]);

                        String sen = new String (inData, 4, PacketUser.sentence_len); //문장
                        Log.d(TAG, "" + i);
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

                        i++;
                        sentence_num++;
                    }
                    else if(inData[1] == PacketUser.ACK_NSEN){
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