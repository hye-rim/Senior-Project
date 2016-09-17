package com.onpuri.Activity.Note;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-28.
 */
public class workerNoteItemAdd  extends Thread {
    private static final String TAG = "workerNoteItemAdd";

    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[20];

    private int result;
    private String toServerData;
    private int sentenceNum;

    public workerNoteItemAdd(boolean isPlay, String nameStr, int sentenceNum) {
        this.isPlay = isPlay;
        toServerData = nameStr;
        this.sentenceNum = sentenceNum;
        result = 0;
    }

    public int getResult() {
        return result;
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
            outData[1] = (byte) PacketUser.USR_NOTE_ITEM_ADD;
            outData[2] = (byte) PacketUser.getSEQ();

            if(sentenceNum != -2) {
                outData[3] = (byte) ((byte) dataByte.length + 2);
                for (i = 4; i < 4 + dataByte.length; i++) {
                    outData[i] = (byte) dataByte[i - 4];
                }
                outData[4 + dataByte.length] = (byte) (sentenceNum / 255 + 1 + 48);
                outData[4 + dataByte.length + 1] = (byte) (sentenceNum % 255 + 1 + 48);
                outData[4 + dataByte.length + 2] = (byte) PacketUser.CRC;
                Log.d(TAG, "senNum : " + (sentenceNum / 255 + 1) + "+" + (sentenceNum % 255 + 1));
            }
            else if(sentenceNum == -2){
                outData[3] = (byte) ((byte) dataByte.length);
                for (i = 4; i < 4 + dataByte.length; i++) {
                    outData[i] = (byte) dataByte[i - 4];
                }
                outData[4 + dataByte.length] = (byte) PacketUser.CRC;
            }
            Log.d(TAG,"out : " + new String(outData));

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5);
                dos.flush();
                Log.d(TAG,"out : " + new String(outData));

                dis = new DataInputStream(SocketConnection.socket.getInputStream());
                dis.read(inData);
                Log.d(TAG, "opc : " + inData[1]);
                Log.d(TAG, "succes : " + (char)inData[4]);

                if(inData[1] == PacketUser.ACK_NOTE_ITEM_ADD) {
                    switch (inData[4]){
                        case '1': result = 1; break;
                        case '2': result = 2; break;
                        default: result = 0; break;
                    }
                    isPlay = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }

}