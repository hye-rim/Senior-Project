package com.onpuri.Activity.Note;

import android.util.Log;

import com.onpuri.Data.NoteData;
import com.onpuri.Data.WordData;
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
public class workerNoteLoad extends Thread {
    private static final String TAG = "workerNoteLoad";
    private static final char SEN = '1';
    private static final char WORD = '2';

    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];
    byte[] numSentence = new byte[261];

    private String flag; //문장, 단어 구분
    private Boolean loadEnd = false;
    private int outKinds;
    private String toServerData;

    private PacketUser noteSentence;

    private ArrayList<WordData> noteWord;
    private int wordLen;

    public workerNoteLoad(boolean isPlay, String nameStr, int outKinds) {
        this.isPlay = isPlay;
        toServerData = nameStr;
        this.outKinds = outKinds;
    }

    public PacketUser getNoteSentence() {
        return noteSentence;
    }
    public ArrayList<WordData> getNoteWord() {
        return noteWord;
    }

    public void stopThread() {
        isPlay = false;
    }

    public void run() {
        super.run();
        while (isPlay) {
            int i, index;
            if(outKinds == 1)
                noteSentence = new PacketUser();
            else
                noteWord = new ArrayList<WordData>();

            byte[] dataByte = toServerData.getBytes();

            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_NOTE_LOAD;
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

                while (!loadEnd) {
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
                    if(inData[1] == PacketUser.ACK_NNOTE_LOAD ){
                        loadEnd = true;
                        isPlay = false;
                        Log.d(TAG, flag + " / load end : " + String.valueOf(loadEnd));
                    }

                    else if(inData[1] == PacketUser.ACK_NOTE_LOAD ) {
                        if (!loadEnd) {
                            if(outKinds == 1) {
                                noteSentence.sentence_len = end-2;
                                String str = new String(inData, 6, PacketUser.sentence_len); //문장
                                Log.d(TAG, "sentence len : " + PacketUser.sentence_len);
                                Log.d(TAG, "new search : " + str);

                                int plus = str.indexOf("+");
                                String num = str.substring(plus+1);
                                str = str.substring(0, plus);

                                noteSentence.setSentence(str);
                                noteSentence.setSentenceNum(num);

                                Log.d(TAG, "lenNum : " + num);

                                dis.read(temp, 0, 4);
                                for (index = 0; index < 4; index++) {
                                    inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                                }

                                int idEnd = (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                                dis.read(temp, 0, 1 + idEnd);
                                for (index = 0; index <= idEnd; index++) {
                                    inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                                }

                                String id = new String(inData, 4, idEnd);
                                noteSentence.setsentenceId(id);
                                Log.d(TAG, "id : " + id);
                            }
                            else if(outKinds == 2){
                                wordLen = end;
                                String data = new String(inData, 6, wordLen); //단어
                                Log.d(TAG, "new data : " + data);

                                int plus = data.indexOf("+");
                                Log.d(TAG, "plus ? " + plus);

                                String word = data.substring(0, plus-1);
                                String wordMean = data.substring(plus + 1, data.length()-2);

                                Log.d(TAG, "word data len : " + wordLen);
                                Log.d(TAG, "new word : " + word);
                                Log.d(TAG, "new word mean : " + wordMean);

                                noteWord.add(new WordData(word, wordMean));
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