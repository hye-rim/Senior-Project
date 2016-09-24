package com.onpuri.Activity.Home.Thread;

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
    byte[] transData = new byte[261];
    byte[] infoData = new byte[50];
    byte[] temp = new byte[261];

    String sentence_num;
    int count;

    List trans = new ArrayList();
    List userid = new ArrayList();
    List day = new ArrayList();
    List reco = new ArrayList();
    List transnum = new ArrayList();

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
    public List getTransnum() {
        return transnum;
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


            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, sentence_num.length()+5);
                dos.flush();
                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                int num = 0;
                while (num < 3) {
                    dis.read(temp, 0, 4);
                    for (index = 0; index < 4; index++) {
                        transData[index] = temp[index];
                    }

                    Log.d(TAG, "opc : " + transData[1]);
                    int trans_len = ((int) transData[3] <= 0 ? (int) transData[3] + 256 : (int) transData[3]);

                    if (transData[1] == PacketUser.ACK_SENTRNAS) {
                        //해석 읽어오기
                        dis.read(temp, 0, 1 + (trans_len));
                        for (index = 0; index <= trans_len; index++) {
                            transData[index + 4] = temp[index];
                        }

                        index = 0;
                        int i = 0;
                        byte[] transbyte = new byte[261];

                        while (true) {
                            if (index == trans_len)
                                break;
                            else {
                                transbyte[i] += transData[4 + index];
                                index++;
                                i++;
                            }
                        }
                        trans.add(new String(transbyte, 0, i)); //해석

                        //아이디-날짜-추천수-해석번호 읽어오기
                        dis.read(temp, 0, 4);
                        for (index = 0; index < 4; index++) {
                            infoData[index] = temp[index];
                        }
                        int len = (int) infoData[3];
                        dis.read(temp, 0, (1 + len));
                        for (index = 0; index <= len; index++) {
                            infoData[index + 4] = temp[index];
                        }

                        index = 0;
                        int j = 0;
                        byte[] transinfobyte = new byte[261];

                        while (true) {
                            if (index == len)
                                break;
                            else {
                                transinfobyte[j] += infoData[4 + index];
                                index++;
                                j++;
                            }
                        }
                        String transinfo = new String(transinfobyte, 0, j);
                        int plus = transinfo.indexOf('+');
                        userid.add(transinfo.substring(0, plus)); //아이디
                        day.add(transinfo.substring(plus + 1, plus + 11)); //날짜
                        transinfo = transinfo.substring(plus+12);
                        plus = transinfo.indexOf('+');
                        reco.add(transinfo.substring(0, plus)); //추천수
                        plus = transinfo.indexOf('+');
                        transnum.add(transinfo.substring(plus + 1, (transinfo.length()-1))); //해석번호

                        num++;
                        count=num;
                    }
                    else if (transData[1] == PacketUser.ACK_NTRANS) {
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