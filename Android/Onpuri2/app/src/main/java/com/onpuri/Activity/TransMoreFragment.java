package com.onpuri.Activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Adapter.TransListAdapter;
import com.onpuri.EndlessRecyclerOnScrollListener;
import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransMoreFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransMoreFragment";
    private static View view;
    private Toast toast;
    ArrayList<String> list_trans;
    private worker_sentence_trans worker_sentence_trans;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    private static int current_page = 1;
    private int ival = 0;
    private int loadLimit = 3;
    List trans = new ArrayList<>();

    TextView item;
    String sentence = "";
    String sentence_num = "";
    int i, index;

    private RecyclerView RecyclerView;
    private TransListAdapter Adapter;
    protected RecyclerView.LayoutManager LayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_trans_more, container, false);
        } catch (InflateException e) {}

        list_trans = new ArrayList<String>();

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num=getArguments().getString("sen_num");
            item.setText(sentence);
        }
        loadData(current_page);

        final TransDetailFragment tdf = new TransDetailFragment();

        Button add_note = (Button) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        Button add_trans = (Button) view.findViewById(R.id.add_trans);
        add_trans.setOnClickListener(this);

        RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_trans);
        LayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.setLayoutManager(LayoutManager);
        Adapter = new TransListAdapter(list_trans, RecyclerView);
        RecyclerView.setAdapter(Adapter);// Set CustomAdapter as the adapter for RecyclerView.
        RecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) LayoutManager) {
            @Override
            public void onLoadMore(int current_page){};
        });
        RecyclerView.addOnItemTouchListener(
                new HomeItemClickListener(getActivity().getApplicationContext(), RecyclerView ,new HomeItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Adapter.notifyDataSetChanged();
                        Bundle args = new Bundle();
                        args.putString("sen", sentence);
                        args.putString("sen_trans", list_trans.get(position));
                        tdf.setArguments(args);

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add(R.id.root_frame, tdf);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("선택한 해석을 삭제하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        toast = Toast.makeText(getActivity(), "삭제되었습니다(구현예정)", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dlg, int sumthin) {
                                        toast = Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                }).show();
                    }
                })
        );
        Adapter.notifyDataSetChanged();

        return view;
    }

    private void loadData(int current_page) {

        if (worker_sentence_trans != null && worker_sentence_trans.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_sentence_trans.interrupt();
        }
        worker_sentence_trans = new worker_sentence_trans(true);
        worker_sentence_trans.start();
        try {
            worker_sentence_trans.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < loadLimit; i++) {
            list_trans.add(trans.get(i).toString());
            System.out.println(list_trans.get(i));
        }

    }

/*
    private void loadMoreData(int current_page) {

        loadLimit = ival+3;

        if (worker_sentence_trans != null && worker_sentence_trans.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_sentence_trans.interrupt();
        }
        worker_sentence_trans = new worker_sentence_trans(true);
        worker_sentence_trans.start();
        try {
            worker_sentence_trans.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = ival; i < loadLimit; i++) {
            if (trans.get(i) == null) {
                list_trans.add("없음");
            } else
                list_trans.add(trans.get(i).toString());
            ival++;
        }

    }
*/
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void onClick(View v) {
        final Bundle args = new Bundle();
        args.putString("sen", sentence);
        args.putString("sen_num", sentence_num);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        switch (v.getId()) {
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
                ft.replace(R.id.root_frame, atf);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                break;
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

                    while (i < 3) {
                        dis.read(temp, 0, 4);
                        for (index = 0; index < 4; index++) {
                            inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }
                        dis.read(temp, 0, 1 + (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]));

                        for (index = 0; index <= (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]); index++) {
                            inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                        }

                        int SOF = inData[0];
                        byte[] tmp = new byte[261];
                        int trans_len;

                        trans_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                        index = 0;
                        int byteI = 0;
                        while (true) { //solving
                            if (index == trans_len)
                                break;
                            else {
                                tmp[byteI] += inData[4 + index];
                                index++;
                                byteI++;
                            }
                        }
                        trans.add(new String(tmp, 0, byteI));
                        i++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlay = !isPlay;
            }
        }
    }
}
