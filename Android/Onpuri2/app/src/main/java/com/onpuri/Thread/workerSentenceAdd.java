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
public class workerSentenceAdd extends Thread {
    private static final String TAG = "Thread - WorkerSentenceAdd";

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    private boolean isPlay = false;

    String addsen;

    public workerSentenceAdd(boolean isPlay, String addsen) {
        this.isPlay = isPlay;
        this.addsen=addsen;
    }

    public void stopThread() {
        isPlay = !isPlay;
    }

    public void run() {
        super.run();
        while (isPlay) {
            Log.d(TAG, "worker add trans start");
            byte[] dataByte = addsen.getBytes();
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_ASEN;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) dataByte.length;
            for (int i = 4; i < 4+dataByte.length; i++) {
                outData[i] = (byte) dataByte[i-4];
            }
            outData[6 + dataByte.length] = (byte) PacketUser.CRC;
            Log.d(TAG, addsen);

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3]+5); // packet transmission
                dos.flush();

                dis = new DataInputStream(SocketConnection.socket.getInputStream());
                dis.read(temp, 0, 4);
                for (int index = 0; index < 4; index++) {
                    inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                }
                if(inData[1] == PacketUser.ACK_ASEN) {
                    Log.d(TAG, "등록완료");
                }
                dis.read(temp);

            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = !isPlay;
        }
    }
}
