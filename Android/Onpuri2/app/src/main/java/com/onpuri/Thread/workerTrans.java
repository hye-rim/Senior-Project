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
public class workerTrans extends Thread {
    private static final String TAG = "workerTrans";
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

    List trans = new ArrayList();
    List userid = new ArrayList();
    List day = new ArrayList();
    List reco = new ArrayList();

    public int getCount() {
        return count;
    }
    public List getTrans() {
        return trans;
    }
    public List getUserid() {
        return userid;
    }
    public List getDay() {
        return day;
    }
    public List getReco() {
        return reco;
    }

    public workerTrans(boolean isPlay, String sentence_num) {
        this.isPlay = isPlay;
        this.sentence_num = sentence_num;
    }

    public void run() {
        super.run();
        while (isPlay) {
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_SENTRNAS;
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
                    int trans_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);
                    Log.d(TAG, "opc : " + inData[1]);

                    if (inData[1] == PacketUser.ACK_SENTRNAS) {
                        //해석 읽어오기
                        dis.read(temp, 0, 1 + (trans_len));
                        for (index = 0; index <= (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]); index++) {
                            inData[index + 4] = temp[index];
                        }

                        index = 0;
                        int i = 0;
                        byte[] transbyte = new byte[261];

                        while (true) {
                            if (index == trans_len)
                                break;
                            else {
                                transbyte[i] += inData[4 + index];
                                index++;
                                i++;
                            }
                        }
                        trans.add(new String(transbyte, 0, i)); //해석

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
                        byte[] transinfobyte = new byte[261];

                        while (true) {
                            if (index == len)
                                break;
                            else {
                                transinfobyte[j] += info[4 + index];
                                index++;
                                j++;
                            }
                        }
                        String transinfo = new String(transinfobyte, 0, j);
                        Log.d(TAG,transinfo);
                        int plus = transinfo.indexOf('+');
                        userid.add(transinfo.substring(0, plus)); //아이디
                        day.add(transinfo.substring(plus + 1, plus + 11)); //날짜
                        reco.add(transinfo.substring(plus + 12, transinfo.length() - 1)); //추천수
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