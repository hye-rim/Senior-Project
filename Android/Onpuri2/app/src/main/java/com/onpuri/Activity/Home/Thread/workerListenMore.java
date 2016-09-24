package com.onpuri.Activity.Home.Thread;

import android.os.Environment;
import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kutemsys on 2016-08-30.
 */
public class workerListenMore extends Thread {
    private static final String TAG = "workerListenMore";
    private boolean isPlay = false;

    int index;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[20];
    byte[] recordbyte;

    String sentence_num;
    int count;

    List listen = new ArrayList();
    List userid = new ArrayList();
    List day = new ArrayList();
    List reco = new ArrayList();
    List listennum = new ArrayList();

    public int getCount() { return count;}
    public List getUserid() {
        return userid;
    }
    public List getDay() {
        return day;
    }
    public List getReco() {
        return reco;
    }
    public List getListennum() {
        return listennum;
    }


    public workerListenMore(boolean isPlay, String sentence_num) {
        this.isPlay = isPlay;
        this.sentence_num = sentence_num;
    }

    public void stopThread() {
        isPlay = !isPlay;
    }

    public void run() {
        super.run();
        while (isPlay) {
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_MLISTEN;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) sentence_num.length();
            for (int i = 4; i < 4 + sentence_num.length(); i++) {
                outData[i] = (byte) sentence_num.charAt(i - 4);
            }
            outData[4 + sentence_num.length()] = (byte) 85;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3]+5);
                dos.flush();
                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                int num=0;
                while (true) {
                    //패킷1
                    byte[] inData = new byte[10];
                    dis.read(inData, 0, 4);
                    int listen_lennum = (int) inData[3];

                    Log.d(TAG, "opc1 : " + inData[1]);
                    if (inData[1] == PacketUser.ACK_SENLISTEN) {
                        //음성파일 크기 읽어오기
                        dis.read(inData, 0, listen_lennum);
                        String listenLen = new String(inData, 0, listen_lennum);
                        int listen_len = Integer.parseInt(listenLen);
                        Log.d(TAG, "len : " + listen_len);

                        //음성파일 읽어오기
                        recordbyte = new byte[listen_len];
                        inData = new byte[(listen_len)+1];

                        Log.d(TAG, "read");
                        int readPacket = 0;
                        while(readPacket < (listen_len+1)) {
                            int readVal = dis.read(inData, readPacket, ((listen_len + 1) - readPacket));
                            readPacket += readVal;
                        }

                        for (index = 0; index < listen_len; index++) {
                            recordbyte[index] = inData[index];
                        }
                        listen.add(new String(recordbyte)); //듣기

                        //패킷2
                        byte[] infoData = new byte[10];
                        dis.read(infoData, 0, 4);
                        int len = (int) infoData[3];
                        Log.d(TAG, "opc2 : " + infoData[1]);

                        // 아이디-날짜-추천수-듣기번호 읽어오기
                        byte[] listeninfobyte = new byte[len];
                        infoData = new byte[len+1];

                        dis.read(infoData, 0, (len+1));
                        for (index = 0; index < len; index++) {
                            listeninfobyte[index] = infoData[index];
                        }

                        String listeninfo = new String(listeninfobyte, 0, len);
                        int plus = listeninfo.indexOf('+');
                        userid.add(listeninfo.substring(0, plus)); //아이디
                        day.add(listeninfo.substring(plus + 1, plus + 11)); //날짜
                        listeninfo = listeninfo.substring(plus+12);
                        plus = listeninfo.indexOf('+');
                        reco.add(listeninfo.substring(0, plus)); //추천수
                        plus = listeninfo.indexOf('+');
                        listennum.add(listeninfo.substring(plus+1, (listeninfo.length()-1))); //해석번호

                        FileOutputStream fos;
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Daily E";
                        File file = new File(path);

                        if(!file.exists())
                            file.mkdirs();

                        String filename = listennum.get(num)+"listen.mp3";
                        file = new File(path +"/"+ filename);

                        try {
                            fos = new FileOutputStream(file);
                            fos.write(recordbyte);
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        num++;
                        count=num;
                    }
                    else if (inData[1] == PacketUser.ACK_NLISTEN) {
                        dis.read(inData, 0, 2);
                        count=num;
                        break;
                    } else {
                        count=num;
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }
}