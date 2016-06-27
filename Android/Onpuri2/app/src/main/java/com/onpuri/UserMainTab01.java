package com.onpuri;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-05-03.
 */
public class UserMainTab01 extends Activity  {
    private worker_sentence_list mworker_sentence;


    ArrayList<String> arrSentence;
    PacketUser userSentence;

    ListView listView;

    int i, index;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];
    boolean lastItemVisibleFlag = false;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mworker_sentence = new worker_sentence_list(true);
        mworker_sentence.start();

        try {
            mworker_sentence.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listView = (ListView) this.findViewById(R.id.list_sentence);
        arrSentence = userSentence.copyList();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                    mworker_sentence = new worker_sentence_list(true);
                    mworker_sentence.start();

                    try {
                        mworker_sentence.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    arrSentence = userSentence.copyList();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firtVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemVisibleFlag = (totalItemCount > 0) && (firtVisibleItem + visibleItemCount >= totalItemCount);
            }
        });


        //ArrayAdapter adapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrSentence);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrSentence) {
            final float scale = getResources().getDisplayMetrics().density;

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView textView = ((TextView) view.findViewById(android.R.id.text1));
                if (position % 2 == 0) {
                    textView.setBackgroundColor(Color.parseColor("#FEE098"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#faf5b3"));
                }
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                textView.setHeight((int) (24 * scale)); // Height
                return view;
            }

        });




        listView.setOnItemClickListener(listener);

    }


    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Intent intent = new Intent(UserMainTab01.this, UserMainSentence.class);

            intent.putExtra("Sentence", arrSentence.get(position));

            //view = UserMain.MainGroup.getLocalActivityManager().startActivity("UserMainSentence", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
            //UserMain.MainGroup.replaceView(view);
        }
    };

    class worker_sentence_list extends Thread{
        private boolean isPlay = false;
        public worker_sentence_list(boolean isPlay){
            this.isPlay = isPlay;
        }

        public void stopThread(){
            isPlay = !isPlay;
        }
        public void run() {
            super.run();
            while (isPlay) {
                System.out.println("1");
                String toServerDataUser;
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_MSL;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) PacketUser.USR_MSL_LEN;
                outData[4] = (byte) 10;
                outData[5] = (byte) 85;
                System.out.println("1");
                try {
                    i = 0;
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData,0,outData[3]+5); // packet transmission
                    dos.flush();
                    System.out.println("1");

                    dis = new DataInputStream(SocketConnection.socket.getInputStream());
                    while( i < 10){
                        dis.read(temp, 0, 4);
                        for(index = 0; index < 4; index++){
                            inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }

                        dis.read(temp, 0, 1+(inData[3] <= 0 ? (int)inData[3] + 256 : (int)inData[3]));

                        for(index = 0; index <= (inData[3] <= 0 ? (int)inData[3] + 256 : (int)inData[3]); index++){
                            inData[index+4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                        }

                        int SOF = inData[0];
                        System.out.println("0 : "+inData[0]);
                        System.out.println("1 : "+inData[1]);
                        System.out.println("2 : "+inData[2]);
                        System.out.println("3 : "+(int)inData[3]);
                        System.out.println("5 : "+(char) inData[5]); //sentence - second char
                        PacketUser.sentence_len = ( (int)inData[3] <= 0 ? (int)inData[3] + 256 : (int)inData[3]);

                        System.out.println("str_start");
                        index = 0;
                        String str = "";
                        System.out.println("len : "+PacketUser.sentence_len);

                        while (true) { //solving
                            System.out.print( (char)inData[4+index]);

                            if( index == PacketUser.sentence_len)
                                break;
                            else {
                                str += (char) inData[4 + index];
                                index++;
                            }
                        }
                        System.out.println("\n last : " + inData[4 + index]);

                        userSentence.setSentence(str);
                        System.out.println("str :" + str);
                        i++;
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                isPlay = !isPlay;

            }
        }
    }
}
