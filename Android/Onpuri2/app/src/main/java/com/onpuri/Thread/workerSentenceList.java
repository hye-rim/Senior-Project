package com.onpuri.Thread;

import android.util.Log;

import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-08-18.
 */
public class workerSentenceList extends Thread {//홈 문장 10개씩 서버에서 불러오는 쓰레드
    private static final String TAG = "workerSentenceList";
    private boolean isPlay = false; //플래그

    int i, j;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261]; //나가는 데이터
    byte[] sen = new byte[261];
    byte[] info = new byte[20];

    PacketUser userSentence;
    int sentence_num;
    int count, num; //서버로부터 받은 문장의 수
    private Boolean sentenceEnd = false; //문장의 끝인지 여부

    public workerSentenceList(boolean isPlay, PacketUser userSentence, int sentence_num) { //생성자 함수
        this.isPlay = isPlay; //true여야 시작한다.
        this.userSentence = userSentence; //문장 정보
        this.sentence_num = sentence_num; //받은 문장 수
    }

    public int getSentence_num() {
        return sentence_num;
    }

    public PacketUser getUserSentence() {
        return userSentence;
    }

    public Boolean getSentenceEnd() {
        return sentenceEnd;
    }

    public int getCount() {
        return count;
    }

    public void stopThread() {
        isPlay = false;
    }

    public void run() {
        super.run();
        while (isPlay) {
            int sNum = sentence_num/255 + 1;
            int sNumN = sentence_num%255 + 1;

            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_MSL;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) PacketUser.USR_MSL_LEN;
            outData[4] = (byte) sNum; //255이하일 때 1
            outData[5] = (byte) sNumN; //255이하일 때 몫 + 1
            outData[6] = (byte) PacketUser.CRC;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write(outData, 0, outData[3] + 5); // packet transmission
                dos.flush();
                dis = new DataInputStream(SocketConnection.socket.getInputStream());

                num = 0;
                while (num < 10) {
                    byte[] inData = new byte[261];
                    byte[] senData = new byte[20];

                    for (i = 0; i < 261; i++)
                        inData[i] = 0;
                    for( i = 0; i< 20; i++)
                        senData[i] = 0;

                    //문장
                    dis.read(sen, 0, 4);
                    for (i = 0; i < 4; i++) {
                        inData[i] = sen[i];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        System.out.println(sen[i] + "/");
                    }
                    Log.d(TAG, "num : " + num);

                    if(inData[1] == PacketUser.ACK_UMS){
                        //문장 데이터
                        PacketUser.sentence_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                        dis.read(sen, 0, (1 + PacketUser.sentence_len));
                        for (j = 0; j <  PacketUser.sentence_len; j++) { //문장내용
                            inData[j + 4] = sen[j];
                        }
                        Log.d(TAG, "inData" + "  " +inData[0] + "  " + inData[1] + "  " + PacketUser.sentence_len + "  " + sen[PacketUser.sentence_len]);
                        String sen = new String (inData, 4, PacketUser.sentence_len); //문장
                        Log.d(TAG, "sen : " + sen);

                        //문장번호+해석수+듣기수
                        dis.read(info, 0, 4);
                        for (j = 0; j < 4; j++) {
                            senData[j] = info[j];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }
                        Log.d(TAG, "info" +  "  " +info[0] + "  " + info[1] + "  " +info[3]);

                        //문장번호+해석수+듣기수 데이터
                        int len = (int) senData[3];

                        dis.read(info, 0, (1 + len));
                        for (j = 0; j <= len; j++) {
                            senData[j+4] = info[j];
                        }
                        Log.d(TAG, "senData" + "  " +senData[0] + "  " + senData[1] + "  " +senData[3] + "  " + senData[len+4]);
                        String seninfo = new String(senData, 4, len);
                        Log.d(TAG, "seninfo : " + seninfo);

                        int plus = seninfo.indexOf('+');
                        String senNum = seninfo.substring(0,plus); //문장번호
                        seninfo = seninfo.substring(plus+1,seninfo.length());
                        plus = seninfo.indexOf('+');
                        String transNum = seninfo.substring(0, plus); //해석수
                        String ListenNum = seninfo.substring(plus+1, seninfo.length()-1); //듣기수

                        userSentence.setSentence(sen);
                        userSentence.setSentenceNum(senNum);
                        userSentence.setSentenceTransNum(transNum);
                        userSentence.setSentenceListenNum(ListenNum);

                        sentence_num++;
                        num++;
                    }
                    else if(inData[1] == PacketUser.ACK_NSEN){ //더이상 문장이 없을 경우
                        count = num; //현재까지 서버에서 받은 문장 수를 count에 저장
                        sentenceEnd = true;  //문장의 끝임을 표시
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