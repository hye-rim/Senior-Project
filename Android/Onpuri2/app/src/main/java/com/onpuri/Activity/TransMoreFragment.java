package com.onpuri.Activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Adapter.TransListAdapter;
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

import static com.onpuri.R.drawable.divider_dark;

public class TransMoreFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransMoreFragment";
    private WorkerTransMore worker_trans_more;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] inData2 = new byte[261];
    byte[] temp = new byte[261];

    private static View view;

    ArrayList<String> list_trans;
    ArrayList<String> list_userid;
    ArrayList<String> list_day;
    ArrayList<String> list_reco;
    List trans = new ArrayList();
    List userid = new ArrayList();
    List day = new ArrayList();
    List reco = new ArrayList();

    TextView item;
    String sentence = "";
    String sentence_num = "";
    int i=0;
    int num=0;
    int j=0;
    int index;
    int count;

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
        list_userid = new ArrayList<String>();
        list_day = new ArrayList<String>();
        list_reco = new ArrayList<String>();

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num=getArguments().getString("sen_num");
            item.setText(sentence);
        }

        translation();

        ImageButton add_note = (ImageButton) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        ImageButton add_trans = (ImageButton) view.findViewById(R.id.add_trans);
        add_trans.setOnClickListener(this);

        RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_trans);
        LayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.setLayoutManager(LayoutManager);
        Adapter = new TransListAdapter(list_trans, list_day, list_reco, RecyclerView);
        RecyclerView.setAdapter(Adapter);
        RecyclerView.addOnItemTouchListener(
                new HomeItemClickListener(getActivity().getApplicationContext(), RecyclerView ,new HomeItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Adapter.notifyDataSetChanged();
                        final TransDetailFragment tdf = new TransDetailFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();

                        Bundle args = new Bundle();
                        args.putString("sen", sentence);
                        args.putString("sen_trans", list_trans.get(position));
                        args.putString("userid", list_userid.get(position));
                        args.putString("day", list_day.get(position));
                        args.putString("reco", list_reco.get(position));
                        tdf.setArguments(args);

                        ft.replace(R.id.root_home, tdf);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    public void onLongItemClick(View view, int position) {
                        Log.d(TAG,list_userid.get(position));
                        if (list_userid.get(position) == "Admin") {
                            Log.d(TAG, "true");
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("본인이 등록한 문장만 삭제가능합니다.")
                                    .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {}
                                    }).show();
                        }
                        else {
                            Log.d(TAG, "false");
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("선택한 해석을 삭제하시겠습니까?")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            final FragmentManager fm = getActivity().getSupportFragmentManager();
                                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
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
                        }
                    }
                })
        );

        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_dark);
        RecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        return view;
    }

    private void translation() {
        if (worker_trans_more != null && worker_trans_more.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_trans_more.interrupt();
        }
        worker_trans_more = new WorkerTransMore(true);
        worker_trans_more.start();
        try {
            worker_trans_more.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = j; i < count; i++) {
            list_trans.add(trans.get(i).toString());
            list_userid.add(userid.get(i).toString());
            list_day.add(day.get(i).toString());
            list_reco.add(reco.get(i).toString());
            j++;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onClick(View v) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        final Bundle args = new Bundle();
        args.putString("sen", sentence);
        args.putString("sen_num", sentence_num);

        switch (v.getId()) {
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
        }
    }
    class WorkerTransMore extends Thread {

        private boolean isPlay = false;

        public WorkerTransMore(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_MTRANS;
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

                    while (true) {
                        dis.read(temp, 0, 4);
                        System.out.println("read");
                        for (index = 0; index < 4; index++) {
                            inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }

                        if (inData[1] == PacketUser.ACK_SENTRNAS) {
                            //해석 읽어오기
                            dis.read(temp, 0, 1 + (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]));
                            for (index = 0; index <= (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]); index++) {
                                inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                            }

                            int trans_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                            index = 0;
                            int i = 0;
                            byte[] transbyte = new byte[261];

                            while (true) { //solving
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
                            int plus = transinfo.indexOf('+');

                            trans.add(new String(transbyte, 0, i)); //해석
                            userid.add(transinfo.substring(0, plus)); //아이디
                            day.add(transinfo.substring(plus + 1, plus + 11)); //날짜
                            reco.add(transinfo.substring(plus + 12, transinfo.length() - 1)); //추천수
                            num++;
                        }
                        else if(inData[1] == PacketUser.ACK_NTRANS) {
                            count=num;
                            break;
                        }
                        else {
                            count=num;
                            break;
                        }
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