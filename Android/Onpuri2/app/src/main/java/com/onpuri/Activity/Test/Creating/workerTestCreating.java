package com.onpuri.Activity.Test.Creating;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-17.
 */
public class workerTestCreating extends Thread {
    private static final String TAG = "workerTestCreating";
    private static final int ALLOBJECT = 0;

    private boolean isPlay = false;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData;
    byte[] inData = new byte[20];

    private Boolean isSuccessInfo, isSuccessUser;
    private Boolean isTitleOverlap;
    private String toServerData;
    private int object;

    private String title;
    private ArrayList<String> userList;

    public workerTestCreating(boolean isPlay, String testInfo, int object, ArrayList<String> userList) {
        this.isPlay = isPlay;
        toServerData = testInfo;
        this.object = object;

        title = userList.get(0);
        if(object != ALLOBJECT) {
            this.userList = new ArrayList<String>();
            for(int i = 0 ; i < userList.size()-1; i++)
                this.userList.add( userList.get(i+1) );
        }
        Log.d(TAG, "userlist : " + this.userList);
        isSuccessInfo = false;
        isSuccessUser = false;
        isTitleOverlap = false;
    }

    public Boolean getSuccessInfo() {
        return isSuccessInfo;
    }
    public Boolean getTitleOverlap() {
        return isTitleOverlap;
    }
    public Boolean getSuccessUser() {
        return isSuccessUser;
    }

    public void stopThread() {
        isPlay = false;
    }

    public void run() {
        super.run();
        while (isPlay) {

            sendData(toServerData, PacketUser.TEST_CREATE);
            receiveData();

            //시험에 대한 정보 보낸 후 성공 /실패 확인
            if(inData[1] == PacketUser.ACK_TEST_CREATE) {
                isSuccessInfo = inData[4] == '1' ? true : false; //실패 0, 성공 1
                isTitleOverlap = inData[4] == '2' ? true : false; //title 중복여부 2일 경우 중복됨
                isPlay = false;
                Log.d(TAG, "success? " + isSuccessInfo);
            }

            if(object != ALLOBJECT){ //지정된 응시자가 존재 할 경우
                isPlay = true;

                String titleUser;
                for(int i = 0; i < userList.size() ; i++){ //응시자 수 만큼 패킷 보내기(제목 + 아이디)
                    titleUser = title + "+" + userList.get(i);
                    Log.d(TAG,"title + userID : " + titleUser);
                    sendData(titleUser, PacketUser.TEST_CREATE_USR);

                    receiveData();

                    if(inData[1] == PacketUser.ACK_TEST_CREATE_USR) {
                        isSuccessUser = inData[4] == '1' ? true : false; //실패 0, 성공 1
                        Log.d(TAG, "success? " + isSuccessUser);
                        isPlay = false;
                    }

                }

            }

            isPlay = false;
        }
    }


    private void sendData(String sendingData, int opc) {
        byte[] dataByte = sendingData.getBytes();
        outData  = new byte[dataByte.length + 6];

        outData[0] = (byte) PacketUser.SOF;
        outData[1] = (byte) opc;
        outData[2] = (byte) PacketUser.getSEQ();
        outData[3] = (byte) dataByte.length;
        for (int i = 4; i < 4 + dataByte.length ; i++) {
            outData[i] = (byte) dataByte[i-4];
        }
        outData[4 + dataByte.length] = (byte)PacketUser.CRC;
        Log.d(TAG,"out : " + new String(dataByte)); //데이터부분
        Log.d(TAG,"out : " + new String(outData)); //나가는데이터


        try {
            dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
            dos.write(outData, 0, outData[3] + 5);
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