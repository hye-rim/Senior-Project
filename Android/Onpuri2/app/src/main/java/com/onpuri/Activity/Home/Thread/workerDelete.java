package com.onpuri.Activity.Home.Thread;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-09-19.
 */
public class workerDelete extends Thread{
    private static final String TAG = "workerRecommend";
    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[30];
    byte[] inData = new byte[30];

    String type;
    String num;

    public workerDelete(boolean isPlay, String type, String num) {
        this.isPlay = isPlay;
        this.type = type;
        this.num = num;

    }
    public void run() {
        super.run();
        Log.d(TAG, "num : " + num);

        String data = type+num;

        while(isPlay) {
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_DEL;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) data.length();
            for (int i = 4; i < 4 + data.length(); i++) {
                outData[i] = (byte) data.charAt(i - 4);
                Log.d(TAG, "data:"+outData[i]);

            }
            Log.d(TAG, "total data:"+data);

            outData[4 + data.length()] = (byte) PacketUser.CRC;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5);
                dos.flush();
                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                dis.read(inData);
                Log.d(TAG, "indata:"+inData[1]);

                if(inData[1] == PacketUser.ACK_DEL) {
                    Log.d(TAG,"삭제");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }
}
