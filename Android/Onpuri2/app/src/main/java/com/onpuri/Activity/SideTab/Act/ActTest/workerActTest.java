package com.onpuri.Activity.SideTab.Act.ActTest;

import android.util.Log;

import com.onpuri.Data.ActTestData;
import com.onpuri.Server.PacketInfo;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-20.
 */
public class workerActTest extends Thread {
    private static final String TAG = "workerActTest";
    private boolean isPlay = false;

    private DataOutputStream dos;
    private DataInputStream dis;

    byte[] outData;
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];
    private int dataLen;

    private Boolean testListEnd;
    private String testNum;

    private ArrayList<ActTestData> mTestList;
    private String testId, testDate, testCorrect;

    public workerActTest(boolean isPlay, String testNum) {
        this.isPlay = isPlay;
        this.testNum = testNum;

        mTestList = new ArrayList<ActTestData>();
        testListEnd = false;
    }

    public Boolean getTestListEnd() {
        return testListEnd;
    }
    public ArrayList getmTestList() {
        return mTestList;
    }

    public void stopThread() {
        isPlay = false;
    }

    public void run() {
        super.run();
        while (isPlay) {

            sendData(testNum, PacketUser.USR_MYTEST);
            try {
                dis = new DataInputStream(SocketConnection.socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(!testListEnd) {
                receiveData();
                Log.d(TAG, "receive opc : " + inData[1]);
                switch (inData[1]) {
                    //test user list 받아오기 끝
                    case PacketUser.ACK_NMYTEST:
                        testListEnd = true; //test 푼 사람 목록 끝
                        isPlay = false;
                        break;

                    //test user list 받기
                    case PacketUser.ACK_MYTEST:
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

        //ID + 이름 => ID, 이름
        int plus = str.indexOf('+');
        testId = str.substring(0,plus); //user id
        String temp = str.substring(plus+1, str.length()); //date + 정답률

        plus = temp.indexOf('+');
        testDate = temp.substring(0, plus); //푼 날짜
        testCorrect = temp.substring(plus+1, temp.length()) + "%"; //정답률

        if(testDate.compareTo("0000-00-00") == 0){
            testDate = new String("미응시");
        }
        Log.d(TAG, "ID : " + testId + ", date : " + testDate + ", correct : " + testCorrect);

        //ID + 날짜 + 정답률
        mTestList.add(new ActTestData(testId, testDate, testCorrect));
    }


    private void sendData(String sendingData, int opc) {
        byte[] dataByte = sendingData.getBytes();
        outData = new byte[dataByte.length + 6];

        outData[0] = (byte) PacketUser.SOF;
        outData[1] = (byte) opc;
        outData[2] = (byte) PacketInfo.getSEQ();
        outData[3] = (byte) dataByte.length;
        for (int i = 4; i < 4 + dataByte.length ; i++) {
            outData[i] = (byte) dataByte[i-4];
        }
        outData[4 + dataByte.length] = (byte)PacketUser.CRC;
        Log.d(TAG,"out : " + new String(outData));

        try {
            dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
            dos.write(outData, 0, outData[3] + 5);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"after sending : " + new String(outData));
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
