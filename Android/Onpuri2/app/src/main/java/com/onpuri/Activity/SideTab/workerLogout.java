package com.onpuri.Activity.SideTab;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-02.
 */
public class workerLogout extends Thread {
    //Socket
    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];

    char check_out;
    char isOut = '0';

    private boolean isPlay = false;

    public workerLogout(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public void setThread() {
        isPlay = !isPlay;
    }

    public void run() {
        super.run();
        while (isPlay) {
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_OUT;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) PacketUser.USR_OUT_LEN;
            outData[4] = (byte) isOut;
            outData[5] = (byte) 85;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5); // packet transmission
                dos.flush();

                dis = new DataInputStream(SocketConnection.socket.getInputStream());
                dis.read(inData);
                int SOF = inData[0];

                check_out = (char) inData[4];

                if (check_out == '0' || check_out == '1')
                    isPlay = !isPlay;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
