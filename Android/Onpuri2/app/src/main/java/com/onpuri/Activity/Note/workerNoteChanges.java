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
public class workerNoteChanges extends Thread {
    private static final String TAG = "workerNoteChanges";
    private static final char SEN = '1'; //문장모음
    private static final char WORD = '2'; //단어모음

    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[20];

    private Boolean isSuccess;

    private char noteFlag = '0'; //1 : 문장모음, 2 : 단어모음
    private char opcNote;
    private String toServerData;

    public workerNoteChanges(char opcNote, boolean isPlay, String nameStr) {
        this.opcNote = opcNote;
        this.isPlay = isPlay;
        toServerData = nameStr;

        isSuccess = false;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void stopThread() {
        isPlay = false;
    }

    public void run() {
        super.run();
        while (isPlay) {
            int i;

            byte[] dataByte = toServerData.getBytes();

            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) opcNote;
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
                dis.read(inData);
                Log.d(TAG, "opc : " + inData[1]);
                Log.d(TAG, "succes : " + inData[4]);

                if(inData[1] == PacketUser.ACK_NOTE_ADD ||
                        inData[1] == PacketUser.ACK_NOTE_EDIT ||
                        inData[1] == PacketUser.ACK_NOTE_DEL) {

                    isSuccess = inData[4] == '1' ? true : false;

                    isPlay = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }

}
