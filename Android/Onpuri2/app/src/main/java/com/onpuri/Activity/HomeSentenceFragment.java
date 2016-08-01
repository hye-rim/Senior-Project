package com.onpuri.Activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import android.widget.Toast;

import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    byte[] inData2 = new byte[261];
    byte[] temp = new byte[261];

    int num=0;
    int index;
    List trans = new ArrayList();
    List userid = new ArrayList();
    List day = new ArrayList();
    List reco = new ArrayList();

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

        translation();

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
        trans1.setText(trans.get(0).toString());
        trans2.setText(trans.get(1).toString());
        trans3.setText(trans.get(2).toString());
        Button trans_more = (Button) view.findViewById(R.id.trans_more);
        trans1.setOnClickListener(this);
        trans2.setOnClickListener(this);
        trans3.setOnClickListener(this);
        trans_more.setOnClickListener(this);


        TextView listen1 = (TextView) view.findViewById(R.id.listen1);
        TextView listen2 = (TextView) view.findViewById(R.id.listen2);
        TextView listen3 = (TextView) view.findViewById(R.id.listen3);
        listen1.setText("listen1");
        Button listen_more = (Button) view.findViewById(R.id.listen_more);
        listen1.setOnClickListener(this);
        listen2.setOnClickListener(this);
        listen3.setOnClickListener(this);
        listen_more.setOnClickListener(this);

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
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        final Bundle args = new Bundle();
        args.putString("sen", sentence);
        args.putString("sen_num", sentence_num);

        switch (v.getId()) {
            case R.id.del_sen:
                Toast.makeText(getActivity(), "구현예정입니다.", Toast.LENGTH_SHORT).show();

              /*  new AlertDialog.Builder(getActivity())
                        .setTitle("문장을 삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final HomeFragment hf = new HomeFragment();
                                ft.replace(R.id.root_home, hf);
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                ft.commit();
                                toast = Toast.makeText(getActivity(), "삭제되었습니다(구현예정)", Toast.LENGTH_SHORT);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                                toast = Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT);
                            }

                        }).show();*/

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
                final TransAddFragment atf = new TransAddFragment();
                atf.setArguments(args);
                ft.replace(R.id.root_home, atf);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.trans_more:
                final TransMoreFragment tmf = new TransMoreFragment();
                tmf.setArguments(args);
                ft.replace(R.id.root_home, tmf);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.add_listen:

                final ListenAddFragment alf = new ListenAddFragment();
                alf.setArguments(args);
                ft.replace(R.id.root_home, alf);
                ft.addToBackStack(null);
                ft.commit();
           /*     toast = Toast.makeText(getActivity(), "구현중", Toast.LENGTH_SHORT);
                toast.show();*/
                break;
            case R.id.listen_more:
                final ListenMoreFragment lmf = new ListenMoreFragment();
                lmf.setArguments(args);
                ft.replace(R.id.root_home, lmf);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.listen1:
                tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.listen2:
                break;
            case R.id.listen3:
                break;
            case R.id.trans1:
                final TransDetailFragment tdf1 = new TransDetailFragment();
                args.putString("sen_trans", trans.get(0).toString());
                args.putString("userid", userid.get(0).toString());
                args.putString("day", day.get(0).toString());
                args.putString("reco", reco.get(0).toString());
                tdf1.setArguments(args);
                ft.replace(R.id.root_home, tdf1);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.trans2:
                final TransDetailFragment tdf2 = new TransDetailFragment();
                args.putString("sen_trans", trans.get(1).toString());
                args.putString("userid", userid.get(1).toString());
                args.putString("day", day.get(1).toString());
                args.putString("reco", reco.get(1).toString());
                tdf2.setArguments(args);
                ft.replace(R.id.root_home, tdf2);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.trans3:
                final TransDetailFragment tdf3 = new TransDetailFragment();
                args.putString("sen_trans", trans.get(2).toString());
                args.putString("userid", userid.get(2).toString());
                args.putString("day", day.get(2).toString());
                args.putString("reco", reco.get(2).toString());
                tdf3.setArguments(args);
                ft.replace(R.id.root_home, tdf3);
                ft.addToBackStack(null);
                ft.commit();
                break;
        }
    }

    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
    }

    private void translation() {
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
                    num=0;
                    while (num < 3) {
                        Log.d(TAG, "while" + num);
                        dis.read(temp, 0, 4);
                        System.out.println("read");
                        for (index = 0; index < 4; index++) {
                            inData[index] = temp[index];
                        }
                        System.out.println("opc : " + inData[1]);
                        if (inData[1] == PacketUser.ACK_SEN) {
                            Log.d(TAG, "해석있음" + num);
                            //해석 읽어오기
                            dis.read(temp, 0, 1 + (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]));
                            for (index = 0; index <= (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]); index++) {
                                inData[index + 4] = temp[index];
                            }

                            int trans_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                            index = 0;
                            int i = 0;
                            byte[] transbyte = new byte[261];

                            while (true) {
                                if (index == trans_len)
                                    break;
                                else {
                                    transbyte[i] += inData[4 + index];
                                    index++;
                                    i++;
                                }
                            }

                            //아이디-날짜-추천수 읽어오기
                            dis.read(temp, 0, 4);
                            System.out.println("info read");
                            for (index = 0; index < 4; index++) {
                                inData2[index] = temp[index];
                            }
                            dis.read(temp, 0, 1 + (inData2[3] <= 0 ? (int) inData2[3] + 256 : (int) inData2[3]));
                            for (index = 0; index <= (inData2[3] <= 0 ? (int) inData2[3] + 256 : (int) inData2[3]); index++) {
                                inData2[index + 4] = temp[index];
                            }

                            int len = ((int) inData2[3] <= 0 ? (int) inData2[3] + 256 : (int) inData2[3]);

                            index = 0;
                            int j = 0;
                            byte[] transinfobyte = new byte[261];

                            while (true) {
                                if (index == len)
                                    break;
                                else {
                                    transinfobyte[j] += inData2[4 + index];
                                    index++;
                                    j++;
                                }
                            }
                            String transinfo = new String(transinfobyte, 0, j);
                            int plus = transinfo.indexOf('+');
                            System.out.println(transinfo);

                            trans.add(new String(transbyte, 0, i)); //해석
                            userid.add(transinfo.substring(0,plus)); //아이디
                            day.add(transinfo.substring(plus+1,plus+11)); //날짜
                            reco.add(transinfo.substring(plus+12,transinfo.length()-1)); //추천수

                            num++;
                        }
                        else if (inData[1] == PacketUser.ACK_NTRANS) {
                            Log.d(TAG, "해석없음" + num);
                            for (int j = 0; j < 3 - num; j++) {
                                trans.add("none");
                            }
                            num++;
                            break;
                        } else {
                            trans.add("error");
                            num++;
                        }
                        Log.d(TAG, "while 끝" + num);
                    }
                    dis.read(temp);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlay = !isPlay;
            }
        }
    }
}