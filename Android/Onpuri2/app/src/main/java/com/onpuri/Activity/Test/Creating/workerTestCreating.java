package com.onpuri.Activity.Test.Creating;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-09-17.
 */
public class workerTestCreating extends Thread {
    private static final String TAG = "workerTestCreating";

    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[20];

    private Boolean isSuccess;
    private String toServerData;

    public workerTestCreating(boolean isPlay, String testInfo) {
        this.isPlay = isPlay;
        toServerData = testInfo;

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
            outData[1] = (byte) PacketUser.TEST_CREATE;
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

                if(inData[1] == PacketUser.ACK_TEST_CREATE) {
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