package com.onpuri.Thread;

import android.util.Log;

import com.onpuri.Server.PacketInfo;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-24.
 */
public class workerAct extends Thread {
    private static final String TAG = "workerAct";
    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];
    byte[] numSentence = new byte[261];

    private Boolean actEnd = false;
    private Boolean enrlEnd, recEnd, transEnd;

    private PacketUser actEnrlSentence;
    private PacketUser actRecSentence;
    private PacketUser actTransSentence;

    public workerAct(boolean isPlay) {
        this.isPlay = isPlay;

        actEnrlSentence = new PacketUser();
        actRecSentence = new PacketUser();
        actTransSentence = new PacketUser();

        enrlEnd = false;
        recEnd = false;
        transEnd = false;
    }

    public void stopThread() {
        isPlay = false;
    }

    public PacketUser getActNewSentence() {
        return actEnrlSentence;
    }
    public PacketUser getActRecSentence() {
        return actRecSentence;
    }
    public PacketUser getActTransSentence() {
        return actTransSentence;
    }

    public void run() {
        super.run();
        while (isPlay) {
            byte act = 1;
            int index;

            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_ACT;
            outData[2] = (byte) PacketInfo.getSEQ();
            outData[3] = act;
            outData[4] = act; //내활동 내역 요청
            outData[5] = (byte) 85;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5);
                dos.flush();
                Log.d(TAG,"out : " + new String(outData));

                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                while (!actEnd) {
                    //문장
                    Log.d(TAG, "gogo");

                    dis.read(temp, 0, 4);
                    for (index = 0; index < 4; index++) {
                        inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }

                    int end = (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                    dis.read(temp, 0, 1 + end);
                    for (index = 0; index <= end; index++) {
                        inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                    }

                    Log.d(TAG, "opc : " + inData[1]);
                    if(inData[1] == PacketUser.ACK_NACTENRL ||
                            inData[1] == PacketUser.ACK_NACTREC ||
                            inData[1] == PacketUser.ACK_NACTTRANS){


                        switch (inData[1]){
                            case PacketUser.ACK_NACTENRL:
                                enrlEnd = true; break;

                            case PacketUser.ACK_NACTREC:
                                recEnd = true; break;

                            case PacketUser.ACK_NACTTRANS:
                                transEnd = true; break;

                            default:  break;
                        }

                        if(enrlEnd && recEnd && transEnd){
                            actEnd = true;
                            isPlay = false;
                        }

                        Log.d(TAG, "no more : " + String.valueOf(actEnd));
                        Log.d(TAG, "no enrl : " + String.valueOf(enrlEnd));
                        Log.d(TAG, "no rec : " + String.valueOf(recEnd));
                        Log.d(TAG, "no trans : " + String.valueOf(transEnd));
                    }

                    else if(inData[1] == PacketUser.ACK_ACTENRL ||
                            inData[1] == PacketUser.ACK_ACTREC ||
                            inData[1] == PacketUser.ACK_ACTTRANS) {

                        if (!actEnd) {
                            //문장번호
                            dis.read(temp, 0, 4);
                            for (index = 0; index < 4; index++) {
                                numSentence[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                            }

                            int numEnd = (numSentence[3] <= 0 ? (int) numSentence[3] + 256 : (int) numSentence[3]);
                            dis.read(temp, 0, 1 + numEnd);

                            for (index = 0; index <= numEnd; index++) {
                                numSentence[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                            }

                            switch (inData[1]){
                                case PacketUser.ACK_ACTENRL:
                                    saveSen(actEnrlSentence, end); break;

                                case PacketUser.ACK_ACTREC:
                                    saveSen(actRecSentence, end); break;

                                case PacketUser.ACK_ACTTRANS:
                                    saveSen(actTransSentence, end); break;

                                default: break;
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }

    private void saveSen(PacketUser kinds, int end) {
        kinds.sentence_len = end;

        String str = new String(inData, 4, kinds.sentence_len); //문장
        String num = Character.toString((char) numSentence[4])
                + Character.toString((char) numSentence[5])
                + Character.toString((char) numSentence[6]); //문장번호

        kinds.setSentence(str);
        kinds.setSentenceNum(num);

        Log.d(TAG, "len : " + kinds.sentence_len);
        Log.d(TAG, "lenNum : " + num);
        Log.d(TAG, "new act : " + str);
    }
}