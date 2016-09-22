package com.onpuri.Activity.Test.Creating;

import android.util.Log;
import android.widget.Switch;

import com.onpuri.Server.PacketInfo;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kutemsys on 2016-09-18.
 */
public class workerReceiveUserList extends Thread {
    private static final String TAG = "workerReceiveUserList";
    private boolean isPlay = false;

    private DataOutputStream dos;
    private DataInputStream dis;

    byte[] outData = new byte[20];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];
    private int dataLen;

    private Boolean userListEnd;

    private List<Map<String, String>> userList;
    private ArrayList mUserList;
    private String userId, userName;

    public workerReceiveUserList(boolean isPlay) {
        this.isPlay = isPlay;

        userList = new ArrayList<Map<String, String>>();
        mUserList = new ArrayList<String>();
        userListEnd = false;
    }

    public Boolean getUserListEnd() {
        return userListEnd;
    }
    public List<Map<String, String>> getUserList() {
        return userList;
    }
    public ArrayList getmUserList() {
        return mUserList;
    }

    public void stopThread() {
        isPlay = false;
    }

    public void run() {
        super.run();
        while (isPlay) {

            sendData((byte) '1' , PacketUser.TEST_CREATE_SER);
            try {
                dis = new DataInputStream(SocketConnection.socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(!userListEnd) {
                receiveData();
                Log.d(TAG, "receive opc : " + inData[1]);
                switch (inData[1]) {
                    //user list 받아오기 끝
                    case PacketUser.ACK_TEST_CREATE_NSER:
                        userListEnd = true; //실패 0, 성공 1
                        isPlay = false;
                        break;

                    //user list 받기
                    case PacketUser.ACK_TEST_CREATE_SER:
                        saveUser();
                        break;
                }
            }
            isPlay = false;
        }
    }

    private void saveUser() {
        String str = new String(inData, 4, dataLen); //byte 데이터 배열 string으로 변환
        Log.d(TAG, "data : " + str);
        str = str.replace("+", " / ");

        mUserList.add(str);
    }


    private void sendData(byte sendingData, int opc) {

        outData[0] = (byte) PacketUser.SOF;
        outData[1] = (byte) opc;
        outData[2] = (byte) PacketInfo.getSEQ();
        outData[3] = (byte) 1;
        outData[4] = sendingData; //내활동 내역 요청
        outData[5] = (byte) PacketUser.CRC;
        Log.d(TAG,"out : " + new String(outData));

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
            dis.read(temp, 0, 4);
            for (int index = 0; index < 4; index++) {
                inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
            }

            dataLen = (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

            dis.read(temp, 0, 1 + dataLen); //data길이 + crc -> end + 1
            for (int index = 0; index <= dataLen; index++) {
                inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
