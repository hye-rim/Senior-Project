package com.onpuri.Thread;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kutemsys on 2016-08-24.
 */
public class workerListen extends Thread {
    private static final String TAG = "workerListen";
    private boolean isPlay = false;

    int index;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] info = new byte[261];
    byte[] temp = new byte[261];

    String sentence_num;
    int count;
    List listen = new ArrayList();
    List userid = new ArrayList();
    List day = new ArrayList();
    List reco = new ArrayList();

    public int getCount() { return count;}
    public List getListen() { return listen;}
    public List getUserid() {
        return userid;
    }
    public List getDay() {
        return day;
    }
    public List getReco() {
        return reco;
    }

    public workerListen(boolean isPlay, String sentence_num) {
        this.isPlay = isPlay;
        this.sentence_num = sentence_num;
    }

    public void run() {
        super.run();
        while (isPlay) {
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_SENLISTEN;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) sentence_num.length();
            for (int i = 4; i < 4 + sentence_num.length(); i++) {
                outData[i] = (byte) sentence_num.charAt(i - 4);
            }
            outData[4 + sentence_num.length()] = (byte) 85;
            Log.d(TAG, "opc : " + outData[1]);

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3]+5);
                dos.flush();
                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                int num = 0;
                while (num < 3) {
                    dis.read(temp, 0, 4);
                    for (index = 0; index < 4; index++) {
                        inData[index] = temp[index];
                    }
                    int listen_len = (int) inData[3];
                    Log.d(TAG, "opc : " + inData[1]);

                    if (inData[1] == PacketUser.ACK_SENLISTEN) {
                        //음성 읽어오기
                        dis.read(temp, 0, 1+(listen_len));
                        for (index = 0; index <= 1+(listen_len); index++) {
                            inData[index + 4] = temp[index];
                        }

                        index = 0;
                        int i = 0;
                        byte[] listenbyte = new byte[261];

                        while (true) {
                            if (index == listen_len)
                                break;
                            else {
                                listenbyte[i] += inData[4 + index];
                                index++;
                                i++;
                            }
                        }
                        listen.add(new String(listenbyte, 0, i)); //해석

                        //아이디-날짜-추천수 읽어오기
                        dis.read(temp, 0, 4);
                        for (index = 0; index < 4; index++) {
                            info[index] = temp[index];
                        }
                        int len = (int) info[3];
                        dis.read(temp, 0, (1 + len));
                        for (index = 0; index <= len; index++) {
                            info[index + 4] = temp[index];
                        }

                        index = 0;
                        int j = 0;
                        byte[] listeninfobyte = new byte[261];

                        while (true) {
                            if (index == len)
                                break;
                            else {
                                listeninfobyte[j] += info[4 + index];
                                index++;
                                j++;
                            }
                        }
                        String listeninfo = new String(listeninfobyte, 0, j);
                        Log.d(TAG,listeninfo);
                        int plus = listeninfo.indexOf('+');
                        userid.add(listeninfo.substring(0, plus)); //아이디
                        day.add(listeninfo.substring(plus + 1, plus + 11)); //날짜
                        reco.add(listeninfo.substring(plus + 12, listeninfo.length() - 1)); //추천수
                        num++;
                        count=num;
                    }
                    else if (inData[1] == PacketUser.ACK_NTRANS) {
                        count=num;
                        break;
                    } else {
                        count=num;
                        break;
                    }
                }
                dis.read(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = false;
        }
    }
}