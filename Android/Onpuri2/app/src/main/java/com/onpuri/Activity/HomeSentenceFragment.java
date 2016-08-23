package com.onpuri.Activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Adapter.SenListenListAdapter;
import com.onpuri.Adapter.SenTransListAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.Listener.HomeItemClickListener;
import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.onpuri.R.drawable.divider_dark;


/**
 * Created by kutemsys on 2016-05-11.
 */
public class HomeSentenceFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {

    private static final String TAG = "HomeSentenceFragment";
    private WorkerTrans worker_sentence_trans;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] inData2 = new byte[261];
    byte[] temp = new byte[261];
    private static View view;
    private Toast toast;

    int count=-1;
    int num=0;
    int index;
    int j=0;

    ArrayList<String> list_trans;
    ArrayList<String> list_trans_userid;
    ArrayList<String> list_trans_day;
    ArrayList<String> list_trans_reco;
    List trans = new ArrayList();
    List tuserid = new ArrayList();
    List tday = new ArrayList();
    List treco = new ArrayList();

    ArrayList<String> list_listen;
    ArrayList<String> list_listen_userid;
    ArrayList<String> list_listen_day;
    ArrayList<String> list_listen_reco;
    List listen = new ArrayList();
    List ltuserid = new ArrayList();
    List ltday = new ArrayList();
    List ltreco = new ArrayList();

    TextView item;
    String sentence = "";
    String sentence_num = "";
    TextToSpeech tts;

    private RecyclerView TransRecyclerView;
    private SenTransListAdapter TransAdapter;
    protected RecyclerView.LayoutManager TransLayoutManager;
    private RecyclerView ListenRecyclerView;
    private SenListenListAdapter ListenAdapter;
    protected RecyclerView.LayoutManager ListenLayoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_home_sen, container, false);
        } catch (InflateException e) {}

        list_trans = new ArrayList<String>();
        list_trans_userid = new ArrayList<String>();
        list_trans_day = new ArrayList<String>();
        list_trans_reco = new ArrayList<String>();
        list_listen = new ArrayList<String>();

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num = getArguments().getString("sen_num");
            item.setText(sentence);
        }

        translation();
        listen();

        ImageButton tts_sen = (ImageButton) view.findViewById(R.id.tts);
        tts_sen.setOnClickListener(this);
        ImageButton del_sen = (ImageButton) view.findViewById(R.id.del_sen);
        del_sen.setOnClickListener(this);
        ImageButton add_note = (ImageButton) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        ImageButton add_trans = (ImageButton) view.findViewById(R.id.add_trans);
        add_trans.setOnClickListener(this);
        ImageButton add_listen = (ImageButton) view.findViewById(R.id.add_listen);
        add_listen.setOnClickListener(this);

        Button trans_more = (Button) view.findViewById(R.id.trans_more);
        trans_more.setOnClickListener(this);
        Button listen_more = (Button) view.findViewById(R.id.listen_more);
        listen_more.setOnClickListener(this);

        TransRecyclerView = (RecyclerView) view.findViewById(R.id.trans_list);
        TransLayoutManager = new LinearLayoutManager(getActivity());
        TransRecyclerView.setLayoutManager(TransLayoutManager);
        TransAdapter = new SenTransListAdapter(list_trans, TransRecyclerView);
        TransRecyclerView.setAdapter(TransAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        TransRecyclerView.addOnItemTouchListener(
                new HomeItemClickListener(getActivity().getApplicationContext(), TransRecyclerView ,new HomeItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(position < count) {
                            final TransDetailFragment tdf = new TransDetailFragment();
                            FragmentManager fm = getActivity().getSupportFragmentManager();

                            Bundle args = new Bundle();
                            args.putString("sen", sentence);
                            args.putString("sen_trans", list_trans.get(position));
                            args.putString("userid", list_trans_userid.get(position));
                            args.putString("day", list_trans_day.get(position));
                            args.putString("reco", list_trans_reco.get(position));
                            tdf.setArguments(args);

                            fm.beginTransaction()
                                    .replace(R.id.root_home, tdf)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_dark);
        TransRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        ListenRecyclerView = (RecyclerView) view.findViewById(R.id.listen_list);
        ListenLayoutManager = new LinearLayoutManager(getActivity());
        ListenRecyclerView.setLayoutManager(ListenLayoutManager);
        ListenAdapter = new SenListenListAdapter(list_listen, ListenRecyclerView);
        ListenRecyclerView.setAdapter(ListenAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        ListenRecyclerView.addOnItemTouchListener(
                new HomeItemClickListener(getActivity().getApplicationContext(), ListenRecyclerView ,new HomeItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getActivity(), position + "번째 클릭", Toast.LENGTH_SHORT).show();
                    }
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );
        ListenRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

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
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        final Bundle args = new Bundle();
        args.putString("sen", sentence);
        args.putString("sen_num", sentence_num);

        switch (v.getId()) {
            case R.id.del_sen:
                new AlertDialog.Builder(getActivity())
                        .setTitle("문장을 삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                fm.popBackStack();
                                ft.commit();
                               Toast.makeText(getActivity(), "삭제되었습니다(구현예정)", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                                Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT).show();
                            }

                        }).show();

                break;
            case R.id.add_note:
                final CharSequence[] items = {"노트1", "노트2", "노트3"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("노트를 선택해 주세요(구현 예정)")
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
                break;
            case R.id.listen_more:
                final ListenMoreFragment lmf = new ListenMoreFragment();
                lmf.setArguments(args);
                ft.replace(R.id.root_home, lmf);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.tts :
            tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
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
        worker_sentence_trans = new WorkerTrans(true);
        worker_sentence_trans.start();
        try {
            worker_sentence_trans.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < count; i++) {
            list_trans.add(trans.get(j).toString());
            list_trans_userid.add(tuserid.get(j).toString());
            list_trans_day.add(tday.get(j).toString());
            list_trans_reco.add(treco.get(j).toString());
            j++;

        }
    }
    private void listen() {
        list_listen.add("구현중");
    }

    class WorkerTrans extends Thread {

        private boolean isPlay = false;

        public WorkerTrans(boolean isPlay) {this.isPlay = isPlay;}

        public void run() {
            super.run();
            while (isPlay) {
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_SEN;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) sentence_num.length();
                for (int i = 4; i < 4 + sentence_num.length(); i++) {
                    outData[i] = (byte) sentence_num.charAt(i - 4);
                }
                outData[4 + sentence_num.length()] = (byte) 85;
                Log.d(TAG, "opc : " + outData[1]);

                try {
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, outData[3]+5); // packet transmission
                    dos.flush();
                    dis = new DataInputStream(SocketConnection.socket.getInputStream());

                    int num = 0;
                    while (num < 3) {
                        dis.read(temp, 0, 4);
                        Log.d(TAG, "read");
                        for (index = 0; index < 4; index++) {
                            inData[index] = temp[index];
                        }
                        Log.d(TAG, "opc : " + inData[1]);

                        if (inData[1] == PacketUser.ACK_SEN) {
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
                            Log.d(TAG,transinfo);
                            int plus = transinfo.indexOf('+');

                            trans.add(new String(transbyte, 0, i)); //해석
                            tuserid.add(transinfo.substring(0, plus)); //아이디
                            tday.add(transinfo.substring(plus + 1, plus + 11)); //날짜
                            treco.add(transinfo.substring(plus + 12, transinfo.length() - 1)); //추천수
                            num++;
                            count=num;
                        }
                        else if (inData[1] == PacketUser.ACK_NTRANS) {
                            count=num;
                            break;
                        } else {
                            count=num;
                            break;
                        }
                        Log.d(TAG, "while 끝"+count);
                    }
                    System.out.println("count : " + count);
                    dis.read(temp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlay = false;
            }
        }
    }
}