package com.onpuri.Activity.Join;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-02.
 */
public class workerIdCheck extends Thread {
    private boolean isPlay = false;
    private String checkId;
    private char check = '5';

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];

    public workerIdCheck(boolean isPlay, String id) {
        this.isPlay = isPlay;
        checkId = id;
    }

    public char getCheck() {
        return check;
    }

    public void stopThread () {
        isPlay = !isPlay;
    }

    public void run () {
        super.run ();
        while (isPlay) {

            byte[] dataByte = checkId.getBytes();

            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_CHK;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) dataByte.length;
            for (int i = 4; i < 4 + dataByte.length; i++) {
                outData[i] = (byte) dataByte[i - 4];
            }
            outData[4 +  dataByte.length] = (byte) PacketUser.CRC;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream ());
                dos.write (outData,0,outData[3]+5); // packet transmission
                dos.flush();

                //in data
                dis = new DataInputStream(SocketConnection.socket.getInputStream ());
                dis.read (inData);

                check = (char) inData[4];
                System.out.println(check+"\n");
                if( check == '0' || check == '1')
                    isPlay = !isPlay;

            } catch (IOException e) {
                e.printStackTrace ();
            }

        }
    }
}
