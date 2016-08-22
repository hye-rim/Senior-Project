package com.onpuri.Thread;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-18.
 */
public class workerTransAdd extends Thread {
    private static final String TAG = "Thread - WorkerTransAdd";

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    private boolean isPlay = false;

    int sentence_num;
    String addtrans;
    int i;

    public workerTransAdd(boolean isPlay, int sentence_num, String addtrans) {
        this.isPlay = isPlay;
        this.sentence_num=sentence_num;
        this.addtrans=addtrans;
    }

    public void stopThread() {
        isPlay = !isPlay;
    }

    public void run() {
        super.run();
        while (isPlay) {
            byte[] dataByte = addtrans.getBytes();
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_ATRANS;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) dataByte.length;

            for (i = 4; i < 4+dataByte.length; i++) {
                outData[i] = (byte) dataByte[i-4];
            }
            outData[4 + dataByte.length] = (byte) (sentence_num / 255 + 1);
            outData[5 + dataByte.length] = (byte) (sentence_num % 255 + 1);
            outData[6 + dataByte.length] = (byte) PacketUser.CRC;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 7); // packet transmission
                dos.flush();

                dis = new DataInputStream(SocketConnection.socket.getInputStream());
                dis.read(temp, 0, 4);
                for (int index = 0; index < 4; index++) {
                    inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                }
                if (inData[1] == PacketUser.ACK_ATRANS) {

                }
                dis.read(temp);

            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = !isPlay;
        }
    }
}
