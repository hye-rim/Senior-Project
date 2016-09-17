package com.onpuri.Activity.Login;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-18.
 */
public class workerLogin extends Thread{
    private static final String TAG = "workerLogin";

    private DataOutputStream dos;
    private DataInputStream dis;
    private byte[] outData;
    private byte[] inData;

    private boolean isPlay = false;
    private PacketUser mPacketUser;
    private String toServerDataUser;

    private boolean isFail = false;


    private char check, checkLength;

    public workerLogin(boolean isPlay, String toServerData, char check, char checkLength){
        this.isPlay = isPlay;
        this.toServerDataUser = toServerData;
        this.check = check;
        this.checkLength = checkLength;
    }

    public boolean isFail() {
        return isFail;
    }

    public PacketUser getmPacketUser() {
        return mPacketUser;
    }

    public char getCheck() {
        return check;
    }

    public char getCheckLength() {
        return checkLength;
    }

    public void stopThread(){
        isPlay = !isPlay;
    }
    public void run() {
        super.run();

        outData = new byte[261];
        inData = new byte[261];

        while (isPlay) {
            mPacketUser = new PacketUser();

            byte[] dataByte = toServerDataUser.getBytes();

            outData[0] = (byte) mPacketUser.SOF;
            outData[1] = (byte) mPacketUser.USR_LOG;
            outData[2] = (byte) mPacketUser.getSEQ();
            outData[3] = (byte) dataByte.length;
            for (int i = 4; i < 4 + dataByte.length; i++) {
                outData[i] = (byte) dataByte[i - 4];
            }
            outData[4 + dataByte.length] = (byte) 85;
            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData,0,outData[3]+5); // packet transmission
                dos.flush();


                dis = new DataInputStream(SocketConnection.socket.getInputStream());
                dis.read(inData);

                int SOF = inData[0];
                System.out.println(inData[0]);
                System.out.println(inData[1]);
                System.out.println(inData[2]);
                System.out.println(inData[3]);
                System.out.println((char) inData[4]);
                System.out.println(inData[5]);

                check = (char) inData[4];
                checkLength = (char) inData[3];

                isFail = ( check == '0' ) ? true : false;

                mPacketUser.data_len = (int) inData[3];
                byte[] nameByte = new byte[221];
                int byteI = 0;
                int index = 0;
                if (inData[4] != '0' && inData[1] == mPacketUser.ACK_ULG) { //ID, PW가 틀렸을 경우 실행하지 않도록 한다. && ID

                    while (true) { //아이디
                        if ((char) (inData[4 + index]) == '+') {
                            index++;
                            break;
                        } else {
                            mPacketUser.userId = mPacketUser.userId + (char) inData[4 + index];
                            index++;
                        }
                    }

                    while (true) { //이름
                        if ((char) (inData[4 + index]) == '+') {
                            index++;
                            break;
                        } else {
                            nameByte[byteI] = inData[4 + index];
                            index++;
                            byteI++;
                        }
                    }
                    mPacketUser.name = new String(nameByte, 0, byteI);

                    while (true) { //가입일
                        if ((char) (inData[4 + index]) == '+') {
                            index++;
                            break;
                        } else {
                            mPacketUser.joinDate = mPacketUser.joinDate + (char) inData[4 + index];
                            index++;
                        }
                    }

                    while (true) { //휴대전화
                        if ((char) (inData[4 + index]) == '+') {
                            index++;
                            break;
                        } else {
                            mPacketUser.phone = mPacketUser.phone + (char) inData[4 + index];
                            index++;
                        }
                    }

                    while (true) { //현재비밀번호
                        if ((char) (inData[4 + index]) == '+') {
                            index++;
                            break;
                        } else {
                            mPacketUser.nowPass = mPacketUser.nowPass + (char) inData[4 + index];
                            index++;
                        }
                    }

                    //등록문장 + 녹음문장 + 해석문장 번호만 받고! 탭에서 누를 때 문장내용 받기
                }

                Log.d(TAG, "id : " + mPacketUser.userId);
                Log.d(TAG,"name : " + mPacketUser.name);
                Log.d(TAG,"joinDate : " + mPacketUser.joinDate);
                Log.d(TAG,"phone : " + mPacketUser.phone);
                Log.d(TAG,"nowPass : " + mPacketUser.nowPass);


                if( isFail || inData[4 + index] == PacketUser.CRC){
                    isPlay = false;
                }

            }catch(IOException e){
                e.printStackTrace();
            }
            isPlay = false;
        }
    }
}
