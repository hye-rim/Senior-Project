package com.onpuri.Thread;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-18.
 */
public class workerListenAdd  extends Thread {
    private static final String TAG = "Thread - WorkerListenAdd";

    DataOutputStream dos;
    DataInputStream dis;
    FileInputStream fis;

    byte[] outData;
    byte[] temp = new byte[261];
    byte[] inData = new byte[261];
    byte[] reData = new byte[261];

    private boolean isPlay = false;

    int sentence_num;
    String mFileName;

    public workerListenAdd(boolean isPlay, int sentence_num, String filename) {
        this.isPlay = isPlay;
        this.sentence_num=sentence_num;
        mFileName=filename;
    }

    public void stopThread() {
        isPlay = !isPlay;
    }

    public void run() {
        super.run();
        while (isPlay) {
            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                fis = new FileInputStream(new File(mFileName));
                byte[] buffer = new byte[50000];

                int fileSize = 0;
                int n;
                while((n = fis.read(buffer))!=-1) {
                    fileSize += n;
                }
                String filesize = Integer.toString(fileSize);

                outData = new byte[filesize.length()+fileSize+7];
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_ALISTEN;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) filesize.length(); //파일크기의 길이
                outData[4] = (byte) (sentence_num/255 +1) ;
                outData[5] = (byte) (sentence_num%255 +1) ;

                for(int i=0; i<filesize.length(); i++) {
                    outData[6+i] = (byte) filesize.charAt(i);
                }

                for(int j=0; j< fileSize; j++) {
                    outData[(6+filesize.length())+j]=buffer[j];
                }
                outData[(6+filesize.length())+fileSize]= (byte) PacketUser.CRC;

                dos.write(outData, 0, (7+filesize.length())+fileSize);
                for(int z=0; z<10; z++) {
                    System.out.println(outData[z]);
                }
                dos.flush();
                fis.close();

                dis = new DataInputStream(SocketConnection.socket.getInputStream());
                dis.read(temp, 0, 4);
                for (int index = 0; index < 1; index++) {
                    inData[index] = temp[index];
                }
                if(inData[1] == PacketUser.ACK_ALISTEN) {
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