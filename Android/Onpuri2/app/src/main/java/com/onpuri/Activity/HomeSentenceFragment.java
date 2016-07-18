package com.onpuri.Activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;


/**
 * Created by kutemsys on 2016-05-11.
 */
public class HomeSentenceFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {

    private static final String TAG = "HomeSentenceFragment";
    private worker_sentence_trans worker_sentence_trans;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    int i, index;

    private static View view;
    private Toast toast;

    TextView item;
    String sentence = "";
    String sentence_num = "";
    TextToSpeech tts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_home_sen, container, false);
        } catch (InflateException e) {
        }

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num = getArguments().getString("sen_num");
            item.setText(sentence);
        }
/*
        if(worker_sentence_trans != null && worker_sentence_trans.isAlive()){  //이미 동작하고 있을 경우 중지
            worker_sentence_trans.interrupt();
        }
        worker_sentence_trans = new worker_sentence_trans(true);
        worker_sentence_trans.start();
        try {
            worker_sentence_trans.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
        Button del_sen = (Button) view.findViewById(R.id.del_sen);
        del_sen.setOnClickListener(this);
        Button add_note = (Button) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        Button add_trans = (Button) view.findViewById(R.id.add_trans);
        add_trans.setOnClickListener(this);
        Button add_listen = (Button) view.findViewById(R.id.add_listen);
        add_listen.setOnClickListener(this);

        TextView trans1 = (TextView) view.findViewById(R.id.trans1);
        TextView trans2 = (TextView) view.findViewById(R.id.trans2);
        TextView trans3 = (TextView) view.findViewById(R.id.trans3);
        Button btn_trans_more = (Button) view.findViewById(R.id.btn_trans_more);
        trans1.setOnClickListener(this);
        trans2.setOnClickListener(this);
        trans3.setOnClickListener(this);
        btn_trans_more.setOnClickListener(this);

        TextView listen1 = (TextView) view.findViewById(R.id.listen1);
        TextView listen2 = (TextView) view.findViewById(R.id.listen2);
        TextView listen3 = (TextView) view.findViewById(R.id.listen3);
        Button btn_listen_more = (Button) view.findViewById(R.id.btn_listen_more);
        listen1.setOnClickListener(this);
        listen2.setOnClickListener(this);
        listen3.setOnClickListener(this);
        btn_listen_more.setOnClickListener(this);

        tts = new TextToSpeech(getActivity(), this);

        return view;
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        final Bundle args = new Bundle();
        args.putString("sen", sentence);
        args.putString("sen_num", sentence_num);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        switch (v.getId()) {
            case R.id.del_sen:
                new AlertDialog.Builder(getActivity())
                        .setTitle("문장을 삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final HomeFragment hf = new HomeFragment();
                                ft.replace(R.id.root_frame, hf);
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                ft.commit();
                                toast = Toast.makeText(getActivity(), "삭제되었습니다(구현예정)", Toast.LENGTH_SHORT);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                                toast = Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT);
                            }

                        }).show();
                break;
            case R.id.add_note:
                final CharSequence[] items = {"노트1", "노트2", "노트3"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("노트를 선택해 주세요(노트 연동은 구현 예정)")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                Toast.makeText(getActivity(), items[index] + "선택", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                            }
                        }).show();
                break;
            case R.id.add_trans:
                final AddTransFragment atf = new AddTransFragment();
                atf.setArguments(args);
                atf.setArguments(args);
                ft.replace(R.id.root_frame, atf);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                break;
            case R.id.btn_trans_more:
                final TransMoreFragment tmf = new TransMoreFragment();
                tmf.setArguments(args);
                tmf.setArguments(args);
                ft.replace(R.id.root_frame, tmf);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                break;
            case R.id.add_listen:
                final AddListenFragment alf = new AddListenFragment();
                alf.setArguments(args);
                alf.setArguments(args);
                ft.replace(R.id.root_frame, alf);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.btn_listen_more:
             /*   final ListenMoreFragment lmf = new ListenMoreFragment();
                lmf.setArguments(args);
                lmf.setArguments(args);
                ft.replace(R.id.root_frame, lmf);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();*/
                break;
            case R.id.listen1:
                tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);

                break;
            case R.id.listen2:
                toast = Toast.makeText(getActivity(), "두번째 음성 파일이 존재하지 않습니다", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.listen3:
                toast = Toast.makeText(getActivity(), "세번째 음성 파일이 존재하지 않습니다", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
    }

    class worker_sentence_trans extends Thread {
        private boolean isPlay = false;

        public worker_sentence_trans(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {
                Log.d(TAG, "worker trans start");

                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_SEN;
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

                    while( i < 3) {
                        dis.read(temp, 0, 4);
                        for (index = 0; index < 4; index++) {
                            inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }
                        dis.read(temp, 0, 1 + (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]));
                        for (index = 0; index <= (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]); index++) {
                            inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                        }

                        int SOF = inData[0];
                        System.out.println("000 : " + inData[0]);
                        System.out.println("111 : " + inData[1]);
                        System.out.println("222 : " + inData[2]);
                        System.out.println("333 : " + inData[3]);
                        System.out.println("444 : " + inData[4]);

                        String trans = "";
                        int trans_len;

                        trans_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                        index = 0;
                        while (true) { //solving
                            if (index == trans_len)
                                break;
                            else {
                                trans += (char) inData[4 + index];
                                index++;
                            }
                        }
                        System.out.println(trans);
                        i++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}