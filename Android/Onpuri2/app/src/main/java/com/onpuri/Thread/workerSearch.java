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
    private Boolean sentenceSerEnd, wordSerEnd;
    private Boolean searchEnd = false;

    private PacketUser userSentence;
    private String userWord;

    public workerSearch(boolean isPlay, String searchStr) {
        this.isPlay = isPlay;
        toServerDataUser = searchStr;
        userWord = new String();
        sentenceSerEnd = false;
        wordSerEnd = false;
    }

    public void stopThread() {
        isPlay = false;
    }

    public PacketUser getUserSentence() {
        return userSentence;
    }
    public String getUserWord() {
        return userWord;
    }
    public Boolean getSentenceSerEnd() {
        return sentenceSerEnd;
    }
    public Boolean getWordSerEnd() {
        return wordSerEnd;
    }

    public void run() {
        super.run();
        while (isPlay) {
            int i, index;
            byte[] dataByte = toServerDataUser.getBytes();
            userSentence = new PacketUser();

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
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5);
                dos.flush();
                Log.d(TAG,"out : " + new String(outData));

                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                while (!searchEnd) {
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
                    if(inData[1] == PacketUser.ACK_NWORSER ||
                            inData[1] == PacketUser.ACK_NSENSER ){
                        switch (inData[1]){
                            case PacketUser.ACK_NWORSER:
                                wordSerEnd = true;
                                break;

                            case PacketUser.ACK_NSENSER:
                                sentenceSerEnd = true;
                                break;

                            default:  break;
                        }

                        if(wordSerEnd && sentenceSerEnd){
                            searchEnd = true;
                            isPlay = false;
                        }

                        Log.d(TAG, "no more search : " + String.valueOf(searchEnd));
                        Log.d(TAG, "no word : " + String.valueOf(wordSerEnd));
                        Log.d(TAG, "no sentence : " + String.valueOf(sentenceSerEnd));
                    }

                    else if(inData[1] == PacketUser.ACK_WORSER ||
                            inData[1] == PacketUser.ACK_SENSER ) {
                        if (!searchEnd) {
                            PacketUser.sentence_len = end;
                            String str = new String(inData, 4, PacketUser.sentence_len); //문장
                            Log.d(TAG, "len : " + PacketUser.sentence_len);
                            Log.d(TAG, "new search : " + str);

                            switch (inData[1]){
                                case PacketUser.ACK_WORSER:
                                    userWord = str;
                                    break;

                                case PacketUser.ACK_SENSER:
                                    //문장번호 정보
                                    dis.read(temp, 0, 4);
                                    for (index = 0; index < 4; index++) {
                                        numSentence[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                                    }

                                    int numEnd = (numSentence[3] <= 0 ? (int) numSentence[3] + 256 : (int) numSentence[3]);
                                    dis.read(temp, 0, 1 + numEnd);

                                    for (index = 0; index <= numEnd; index++) {
                                        numSentence[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                                    }
                                    String num = Character.toString((char) numSentence[4])
                                            + Character.toString((char) numSentence[5])
                                            + Character.toString((char) numSentence[6]); //문장번호

                                    userSentence.setSentence(str);
                                    userSentence.setSentenceNum(num);

                                    Log.d(TAG, "lenNum : " + num);
                                    break;
                                default:
                                    break;
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
}