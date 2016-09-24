package com.onpuri.Activity.Test.Solving;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kutemsys on 2016-09-19.
 */
public class workerTestAllList extends Thread{
    private static final String TAG = "workerTestList";
    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[20];
    byte[] inData = new byte[50];

    String type;
    int count;

    List title = new ArrayList();
    List userid = new ArrayList();
    List part = new ArrayList();
    List quiz = new ArrayList();
    List num = new ArrayList();

    public int getCount() { return count;}
    public List getTitle() {return title;}
    public List getUserid() {return userid;}
    public List getPart() {
        return part;
    }
    public List getQuiz() {
        return quiz;
    }
    public List getNum() {
        return num;
    }


    public workerTestAllList(boolean isPlay, String type) {
        this.isPlay = isPlay;
        this.type = type;
    }

    public void run() {
        super.run();
        Log.d(TAG, "type : " + type);

        while(isPlay) {
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_TEST_AlllLIST;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) type.length();
            for (int i = 4; i < 4 + type.length(); i++) {
                outData[4] = (byte) type.charAt(i - 4);
            }
            outData[4 + type.length()] = (byte) PacketUser.CRC;
            Log.d(TAG, "outData[1] : " + outData[1]);

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5);
                dos.flush();
                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                int n = 0;
                while(true) {

                    dis.read(inData, 0, 4);
                    int len = inData[3];
                    Log.d(TAG, "inData[1] : " + inData[1]);

                    if (inData[1] == PacketUser.ACK_TEST_LIST) {
                        dis.read(inData, 0, 1+(len));
                        String info = new String(inData, 0, len);
                        Log.d(TAG,info);
                        int plus = info.indexOf('+');
                        num.add(info.substring(0, plus));
                        info = info.substring(plus+1);
                        plus = info.indexOf('+');
                        userid.add(info.substring(0, plus));
                        info = info.substring(plus+1);
                        plus = info.indexOf('+');
                        String Title = info.substring(0, plus);
                        info = info.substring(plus+1);
                        plus = info.indexOf('+');
                        quiz.add(info.substring(0,plus));
                        info = info.substring(plus+1);
                        plus = info.indexOf('+');
                        part.add(info.substring(0,plus));
                        String Per = info.substring(plus+1,(info.length()));
                        title.add(Per + "% " + Title);

                        n++;
                    } else if (inData[1] == PacketUser.ACK_NTEST_LIST) {
                        dis.read(inData);
                        count = n;
                        break;
                    } else {
                        dis.read(inData);
                        count = n;
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
