package com.onpuri.Activity.SideTab.Setting;

import com.onpuri.Server.PacketInfo;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-02.
 */
//회원탈퇴 Thread
public class workerLeave extends Thread {
    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    private char check_out;

    public workerLeave(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public void setThread() {
        isPlay = !isPlay;
    }

    public void run() {
        super.run();
        while (isPlay) {
            byte leave = 1;

            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_LEV;
            outData[2] = (byte) PacketInfo.getSEQ();
            outData[3] = leave;
            outData[4] = leave;
            outData[5] = (byte) 85;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5);
                dos.flush();


                dis = new DataInputStream(SocketConnection.socket.getInputStream());
                dis.read(inData);

                check_out = (char) inData[4];

                if (check_out == '0' || check_out == '1')
                    isPlay = !isPlay;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}