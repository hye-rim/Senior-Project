package com.onpuri.Activity.Test.Solving;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-09-22.
 */
public class workerTestFinish extends Thread{
    private static final String TAG = "workerTestFinish";
    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[30];
    byte[] inData = new byte[30];

    String num;
    String score;
    String percent;

    public workerTestFinish(boolean isPlay, String num, String score, String percent) {
        this.isPlay = isPlay;
        this.num = num;
        this.score = score;
        this.percent = percent;

    }
    public void run() {
        super.run();
        Log.d(TAG, "num : " + num);
        Log.d(TAG, "score : " + score);
        Log.d(TAG, "percent : " + percent);

        String data = num+"+"+score+"+"+percent;
        Log.d(TAG, "data : " + data);


        while(isPlay) {
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_TEST_FINISH;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) data.length();
            for (int i = 4; i < 4 + data.length(); i++) {
                outData[i] = (byte) data.charAt(i - 4);
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

                if(inData[1] == PacketUser.ACK_TEST_FINISH) {
                    Log.d(TAG,"시험종료");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }
}

