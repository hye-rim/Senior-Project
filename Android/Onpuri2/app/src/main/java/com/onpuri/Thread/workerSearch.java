package com.onpuri.Thread;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-02.
 */
public class workerSearch extends Thread {
    private static final String TAG = "Thread - WorkerSearch";
    //Socket
    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];
    byte[] numSentence = new byte[261];

    private boolean isPlay = false;

    private String toServerDataUser;
    private Boolean searchEnd = false;


    private PacketUser userSentence;

    public workerSearch(boolean isPlay, String searchStr) {
        this.isPlay = isPlay;
        toServerDataUser = searchStr;
    }

    public void stopThread() {
        isPlay = false;
    }

    public PacketUser getUserSentence() {
        return userSentence;
    }

    public Boolean getSearchEnd() {
        return searchEnd;
    }

    public void run() {
        super.run();
        while (isPlay) {
            int i, index;
            byte[] dataByte = toServerDataUser.getBytes();
            userSentence = new PacketUser();
            Log.d(TAG,"data : " + toServerDataUser);
            Log.d(TAG,"data : " + new String(dataByte));
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_SEARCH;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) dataByte.length;
            for (i = 4; i < 4 + dataByte.length ; i++) {
                outData[i] = (byte) dataByte[i-4];
            }
            outData[4 + dataByte.length] = (byte)PacketUser.CRC;
            Log.d(TAG,"out : " + new String(outData));
            try {
                i = 0;
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5); // packet transmission
                dos.flush();

                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                while (i < 10) {
                    //문장
                    dis.read(temp, 0, 4);
                    for (index = 0; index < 4; index++) {
                        inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }

                    if(inData[1] == PacketUser.ACK_NSEARCH){
                        searchEnd = true;
                        Log.d(TAG, "no more : " + String.valueOf(searchEnd));
                        i = 10;
                        isPlay = false;
                    }

                    int end = (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                    dis.read(temp, 0, 1 + end);
                    for (index = 0; index <= end; index++) {
                        inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                    }

                    if(!searchEnd) {
                        //문장번호
                        dis.read(temp, 0, 4);
                        for (index = 0; index < 4; index++) {
                            numSentence[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }

                        int numEnd = (numSentence[3] <= 0 ? (int) numSentence[3] + 256 : (int) numSentence[3]);
                        dis.read(temp, 0, 1 + numEnd );

                        for (index = 0; index <= numEnd; index++) {
                            numSentence[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                        }

                        PacketUser.sentence_len = end;

                        String str = new String (inData, 4, PacketUser.sentence_len); //문장
                        String num = Character.toString((char) numSentence[4])
                                + Character.toString((char) numSentence[5])
                                + Character.toString((char) numSentence[6]); //문장번호

                        userSentence.setSentence(str);
                        userSentence.setSentenceNum(num);

                        Log.d(TAG, "len : " + PacketUser.sentence_len);
                        Log.d(TAG, "lenNum : " + num);
                        Log.d(TAG,"search : " + str);

                        i++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }
}