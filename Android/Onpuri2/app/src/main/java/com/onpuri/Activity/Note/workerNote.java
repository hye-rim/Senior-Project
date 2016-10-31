package com.onpuri.Activity.Note;

import android.util.Log;

import com.onpuri.Server.PacketInfo;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kutemsys on 2016-08-28.
 */
public class workerNote extends Thread {
    private static final String TAG = "workerNote";
    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    private Boolean noteEnd = false;
    private Boolean senEnd, wordEnd;

    private char noteFlag = '0'; //1 : 문장모음, 2 : 단어모음
    private List noteSen, noteSenNum;
    private List noteWord, noteWordNum;

    public workerNote(boolean isPlay) {
        this.isPlay = isPlay;

        noteSen = new ArrayList<String>();
        noteWord = new ArrayList<String>();
        noteSenNum = new ArrayList<String>();
        noteWordNum = new ArrayList<String>();

        senEnd = false;
        wordEnd = false;
    }

    public List getNoteSen() {
        return noteSen;
    }
    public List getNoteWord() {
        return noteWord;
    }
    public List getNoteSenNum() {
        return noteSenNum;
    }
    public List getNoteWordNum() {
        return noteWordNum;
    }

    public void stopThread() {
        isPlay = false;
    }

    public void run() {
        super.run();
        while (isPlay) {
            byte note = 1;
            int index;

            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_NOTE;
            outData[2] = (byte) PacketInfo.getSEQ();
            outData[3] = note;
            outData[4] = note; //내노트 내역 요청
            outData[5] = (byte) PacketUser.CRC;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5);
                dos.flush();
                Log.d(TAG,"out : " + new String(outData));

                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                while (!noteEnd) {
                    //이름
                    Log.d(TAG, "gogo");

                    dis.read(temp, 0, 4);
                    for (index = 0; index < 4; index++) {
                        inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }

                    int end = (inData[3] <= 0 ? (int)
                            inData[3] + 256 : (int) inData[3]);

                    dis.read(temp, 0, 1 + end);
                    for (index = 0; index <= end; index++) {
                        inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                    }

                    Log.d(TAG, "opc : " + inData[1]);
                    Log.d(TAG, "what : " + inData[4]);
                    noteFlag = (char) inData[4]; //1 : 문장, 2 : 단어

                    if(inData[1] == PacketUser.ACK_NNOTE ){
                        switch (noteFlag){
                            case '1':
                                senEnd = true;
                                break;

                            case '2':
                                wordEnd = true;
                                break;
                        }

                        if(senEnd && wordEnd){
                            noteEnd = true;
                            isPlay = false;
                        }

                        Log.d(TAG, "no more : " + String.valueOf(noteEnd));
                        Log.d(TAG, "no sen : " + String.valueOf(senEnd));
                        Log.d(TAG, "no word : " + String.valueOf(wordEnd));
                    }

                    else if(inData[1] == PacketUser.ACK_NOTE) {
                        if (!noteEnd) {
                            int nameEnd = end - 2;

                            //inData[4] : 문장,단어 구분 / inData[5] : 구분자 '+'
                            String name = new String(inData, 6, nameEnd); //문장
                            int plus = name.indexOf("+");
                            String nameNum = name.substring(plus+1, name.length());
                            name = name.substring(0, plus);

                            switch (noteFlag){
                                case '1':
                                    noteSen.add(name);
                                    noteSenNum.add(nameNum);
                                    break;

                                case '2':
                                    noteWord.add(name);
                                    noteWordNum.add(nameNum);
                                    break;

                                default:
                                    break;
                            }

                            Log.d(TAG, "len : " + nameEnd);
                            Log.d(TAG, noteFlag + " new name : " + name);
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