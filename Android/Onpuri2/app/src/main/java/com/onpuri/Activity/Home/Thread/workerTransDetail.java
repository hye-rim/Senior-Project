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
public class workerTransDetail extends Thread{
    private static final String TAG = "workerRecommend";
    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[30];
    byte[] inData = new byte[10];

    String num;
    String recommend;

    public workerTransDetail(boolean isPlay, String num) {
        this.isPlay = isPlay;
        this.num = num;

    }
    public String getRecommend() { return recommend; };

    public void run() {
        super.run();

        while(isPlay) {
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_TRANS_DETAIL;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) num.length();
            for (int i = 4; i < 4 + num.length(); i++) {
                outData[i] = (byte) num.charAt(i - 4);
            }

            outData[4 + num.length()] = (byte) PacketUser.CRC;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5);
                dos.flush();
                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                dis.read(inData);

                if(inData[1] == PacketUser.ACK_TRANS_DETAIL) {
                    recommend = new String(inData, 4, inData[3]);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }
}
