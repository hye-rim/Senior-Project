package com.onpuri.Activity.Search;

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

    private int sentenceLen, sentenceInfoLen;

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
            int i;
            userSentence = new PacketUser();

            sendData( toServerDataUser, PacketUser.USR_SEARCH);
            try {

                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                while (!searchEnd) {

                    Log.d(TAG, "gogo");

                    try {
                        dis.read(temp, 0, 4);

                        for (int index = 0; index < 4; index++) {
                            inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }

                        sentenceLen = (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                        dis.read(temp, 0, 1 + sentenceLen);
                        for (int index = 0; index <= sentenceLen; index++) {
                            inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "receve data : " + new String(inData));

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
                            userSentence.sentence_len = sentenceLen;
                            String sentence = new String(inData, 4, userSentence.sentence_len); //문장
                            Log.d(TAG, "len : " + userSentence.sentence_len);
                            Log.d(TAG, "new search sentence : " + sentence);

                            switch (inData[1]){
                                case PacketUser.ACK_WORSER:
                                    userWord = sentence;
                                    break;

                                case PacketUser.ACK_SENSER:
                                    try {
                                        dis.read(temp, 0, 4);

                                        for (int index = 0; index < 4; index++) {
                                            numSentence[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                                        }

                                        sentenceInfoLen = (numSentence[3] <= 0 ? (int) numSentence[3] + 256 : (int) numSentence[3]);

                                        dis.read(temp, 0, 1 + sentenceInfoLen);
                                        for (int index = 0; index <= sentenceInfoLen; index++) {
                                            numSentence[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Log.d(TAG, "receve data : " + new String(numSentence));

                                    String senInfo = new String(numSentence, 4, sentenceInfoLen);
                                    Log.d(TAG, "sen num + id : " + senInfo);

                                    int plus = senInfo.indexOf('+');
                                    String sentenceNum = senInfo.substring(0,plus); //문장번호
                                    String sentenceId = senInfo.substring(plus+1, senInfo.length()); //아이디

                                    userSentence.setSentence(sentence);
                                    userSentence.setSentenceNum(sentenceNum);
                                    userSentence.setsentenceId(sentenceId);

                                    Log.d(TAG, "sentenceNum : " + sentenceNum);
                                    Log.d(TAG, "sentenceId : " + sentenceId);
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

    private void sendData(String sendingData, int opc) {
        byte[] dataByte = sendingData.getBytes();
        outData = new byte[dataByte.length + 6];


        outData[0] = (byte) PacketUser.SOF;
        outData[1] = (byte) opc;
        outData[2] = (byte) PacketUser.getSEQ();
        outData[3] = (byte) dataByte.length;
        for (int i = 4; i < 4 + dataByte.length ; i++) {
            outData[i] = (byte) dataByte[i-4];
        }
        outData[4 + dataByte.length] = (byte)PacketUser.CRC;
        Log.d(TAG,"out : " + new String(outData));

        try {
            dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
            dos.write(outData, 0, outData[3] + 5);
            dos.flush();
            Log.d(TAG, "after sending : " + new String(outData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}