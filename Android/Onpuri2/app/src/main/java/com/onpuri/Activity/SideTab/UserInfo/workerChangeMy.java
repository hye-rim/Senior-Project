package com.onpuri.Activity.SideTab.UserInfo;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-22.
 */
public class workerChangeMy extends Thread {
    private boolean isPlay = false;
    private String changeData;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];

    public workerChangeMy(boolean isPlay, String data) {
        this.isPlay = isPlay;
        changeData = data;
    }

    public void stopThread () {
        isPlay = !isPlay;
    }

    public void run () {
        super.run ();
        while (isPlay) {

            byte[] dataByte = changeData.getBytes();

            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_CHANGE;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) dataByte.length;
            for (int i = 4; i < 4 + dataByte.length ; i++) {
                outData[i] = dataByte[i-4];
            }
            outData[4 + dataByte.length] = (byte) PacketUser.CRC;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write (outData,0,outData[3]+5); // packet transmission
                dos.flush();

                dis = new DataInputStream(SocketConnection.socket.getInputStream());
                dis.read(inData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }
}
