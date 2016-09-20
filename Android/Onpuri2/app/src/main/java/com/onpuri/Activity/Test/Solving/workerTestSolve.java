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
public class workerTestSolve extends Thread{
    private static final String TAG = "workerTestSolve";
    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[20];
    byte[] quizData = new byte[261];
    byte[] exampleData = new byte[200];


    String num;
    int count;

    List quiz = new ArrayList();
    List ex1 = new ArrayList();
    List ex2 = new ArrayList();
    List ex3 = new ArrayList();
    List ex4 = new ArrayList();
    List sol = new ArrayList();

    public int getCount() { return count;}
    public List getQuiz() {return quiz;}
    public List getEx1() {return ex1;}
    public List getEx2() {
        return ex2;
    }
    public List getEx3() {
        return ex3;
    }
    public List getEx4() {
        return ex4;
    }
    public List getSol() {
        return sol;
    }

    public workerTestSolve(boolean isPlay, String num) {
        this.isPlay = isPlay;
        this.num = num;
    }

    public void run() {
        super.run();
        Log.d(TAG, "num : " + num);

        while(isPlay) {
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_TEST;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) num.length();
            for (int i = 4; i < 4 + num.length(); i++) {
                outData[4] = (byte) num.charAt(i - 4);
                Log.d(TAG, "data:"+outData[i]);

            }
            outData[4 + num.length()] = (byte) PacketUser.CRC;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5);
                dos.flush();
                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                int n = 0;
                while(true) {
                    //패킷1
                    dis.read(quizData, 0, 4);
                    Log.d(TAG, "indata:" + quizData[0]);
                    Log.d(TAG, "indata:" + quizData[1]);

                    int q_len = quizData[3];

                    if (quizData[1] == PacketUser.ACK_TEST) {
                        dis.read(quizData, 0, 1+(q_len));

                        byte[] quizbyte = new byte[(q_len)+1];
                        for(int i=0; i<q_len; i++) {
                            quizbyte[i] += quizData[4 + i];
                        }

                        quiz.add(new String(quizbyte, 0, q_len));

                        //패킷2
                        dis.read(exampleData, 0, 4);
                        int ex_len = exampleData[3];

                        dis.read(exampleData, 0, 1+(ex_len));
                        byte[] examplebyte = new byte[(ex_len)+1];
                        for(int i=0; i<q_len; i++) {
                            examplebyte[i] += exampleData[4 + i];
                        }
                        String example = new String(examplebyte, 0, q_len);
                        int plus = example.indexOf('+');
                        ex1.add(example.substring(0, plus));
                        example = example.substring(plus+1);
                        plus = example.indexOf('+');
                        ex2.add(example.substring(0, plus));
                        example = example.substring(plus+1);
                        plus = example.indexOf('+');
                        ex3.add(example.substring(0, plus));
                        example = example.substring(plus+1);
                        plus = example.indexOf('+');
                        ex4.add(example.substring(0, plus));
                        sol.add(example.substring(plus+1, (ex_len)-1));

                        Log.d(TAG,"1"+ex1);
                        Log.d(TAG,"2"+ex2);
                        Log.d(TAG,"3"+ex3);
                        Log.d(TAG,"4"+ex4);
                        Log.d(TAG,"5"+sol);

                        n++;
                    } else if (quizData[1] == PacketUser.ACK_NTEST_LIST) {
                        dis.read(quizData);
                        count = n;
                        break;
                    } else {
                        dis.read(quizData);
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
