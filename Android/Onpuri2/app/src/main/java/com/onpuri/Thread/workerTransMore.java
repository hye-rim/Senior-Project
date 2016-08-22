package com.onpuri.Thread;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kutemsys on 2016-08-18.
 */
public class workerTransMore extends Thread {
    private static final String TAG = "Thread - WorkerTransMore";

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] inData2 = new byte[261];
    byte[] temp = new byte[261];
    private boolean isPlay = false;

    String sentence_num;
    int index;
    int count, num=0;

    List trans = new ArrayList();
    List userid = new ArrayList();
    List day = new ArrayList();
    List reco = new ArrayList();

    public workerTransMore(boolean isPlay, String sentence_num)
    {
        this.isPlay = isPlay;
        this.sentence_num=sentence_num;
    }

    public int getCount() {
        return count;
    }
    public int getNum() {
        return num;
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

    public void stopThread() {
        isPlay = !isPlay;
    }

    public void run() {
        super.run();
        while (isPlay) {
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_MTRANS;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) sentence_num.length();
            for (int i = 4; i < 4 + sentence_num.length(); i++) {
                outData[i] = (byte) sentence_num.charAt(i - 4);
            }
            outData[4 + sentence_num.length()] = (byte) 85;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5); // packet transmission
                dos.flush();
                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                while (true) {
                    dis.read(temp, 0, 4);
                    System.out.println("read");
                    for (index = 0; index < 4; index++) {
                        inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }

                    if (inData[1] == PacketUser.ACK_SEN) {
                        //해석 읽어오기
                        dis.read(temp, 0, 1 + (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]));
                        for (index = 0; index <= (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]); index++) {
                            inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                        }

                        int trans_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                        index = 0;
                        int i = 0;
                        byte[] transbyte = new byte[261];

                        while (true) { //solving
                            if (index == trans_len)
                                break;
                            else {
                                transbyte[i] += inData[4 + index];
                                index++;
                                i++;
                            }
                        }

                        //아이디-날짜-추천수 읽어오기
                        dis.read(temp, 0, 4);
                        for (index = 0; index < 4; index++) {
                            inData2[index] = temp[index];
                        }
                        dis.read(temp, 0, 1 + (inData2[3] <= 0 ? (int) inData2[3] + 256 : (int) inData2[3]));
                        for (index = 0; index <= (inData2[3] <= 0 ? (int) inData2[3] + 256 : (int) inData2[3]); index++) {
                            inData2[index + 4] = temp[index];
                        }

                        int len = ((int) inData2[3] <= 0 ? (int) inData2[3] + 256 : (int) inData2[3]);

                        index = 0;
                        int j = 0;
                        byte[] transinfobyte = new byte[261];

                        while (true) {
                            if (index == len)
                                break;
                            else {
                                transinfobyte[j] += inData2[4 + index];
                                index++;
                                j++;
                            }
                        }

                        String transinfo = new String(transinfobyte, 0, j);
                        int plus = transinfo.indexOf('+');

                        trans.add(new String(transbyte, 0, i)); //해석
                        userid.add(transinfo.substring(0, plus)); //아이디
                        day.add(transinfo.substring(plus + 1, plus + 11)); //날짜
                        reco.add(transinfo.substring(plus + 12, transinfo.length() - 1)); //추천수
                        num++;
                    }
                    else if(inData[1] == PacketUser.ACK_NTRANS) {
                        count=num;
                        break;
                    }
                    else {
                        count=num;
                        break;
                    }
                }
                dis.read(temp);

            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlay = !isPlay;
        }
    }
}