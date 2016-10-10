package com.onpuri.Activity.Test.Creating;

import android.util.Log;

import com.onpuri.Data.CreatedTestData;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-19.
 */
public class workerProblemCreating extends Thread {
    private static final String TAG = "workerProblemCreating";
    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData;
    byte[] inData = new byte[20];

    private Boolean isSuccess;
    private String sendingDataProblem, sendingDataExample;

    private String title;
    private ArrayList<CreatedTestData> problemList;

    public workerProblemCreating(boolean isPlay, String title, ArrayList<CreatedTestData> problemList) {
        this.isPlay = isPlay;
        this.title = title;
        this.problemList = problemList;
        isSuccess = false;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void stopThread() {
        isPlay = false;
    }

    public void run() {
        super.run();
        while (isPlay) {
            String titleProblem, example;
            for(int i = 0; i < problemList.size() ; i++){
                title = title.replaceAll(" ","");
                title = title.replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");
                titleProblem = new String();
                titleProblem = title + "+" + problemList.get(i).getProblem();
                titleProblem = titleProblem.replace("______", "@");
                example = problemList.get(i).toStringExmaple();

                Log.d(TAG,"title + problem : " + titleProblem);
                Log.d(TAG,"example : " + example);

                sendData(titleProblem, PacketUser.TEST_QUEST);
                sendData(example, PacketUser.TEST_QUEST);

                receiveData();

                //시험에 대한 정보 보낸 후 성공 /실패 확인
                if(inData[1] == PacketUser.ACK_TEST_QUEST) {
                    isSuccess = inData[4] == '1' ? true : false; //실패 0, 성공 1
                    //isPlay = false;
                    Log.d(TAG, "success? " + isSuccess);
                }
            }
            isPlay = false;
        }
    }


    private void sendData(String sendingData, int opc) {
        byte[] dataByte = sendingData.getBytes();
        outData = new byte[dataByte.length + 5];

        outData[0] = (byte) PacketUser.SOF;
        outData[1] = (byte) opc;
        outData[2] = (byte) PacketUser.getSEQ();
        outData[3] = (byte) dataByte.length;
        for (int i = 4; i < 4 + dataByte.length ; i++) {
            outData[i] = (byte) dataByte[i-4];
        }
        outData[4 + dataByte.length] = (byte)PacketUser.CRC;
        Log.d(TAG,"out : " + new String(outData));
        for(int i = 0; i < outData.length; i++)
            System.out.println(outData[i]);

        try {
            dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
            dos.write(outData);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"send : " + new String(outData));
    }

    private void receiveData() {
        try{
            dis = new DataInputStream(SocketConnection.socket.getInputStream());
            dis.read(inData);
            Log.d(TAG, "opc : " + inData[1]);
            Log.d(TAG, "succes : " + inData[4]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
