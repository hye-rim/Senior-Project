package com.onpuri.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onpuri.Adapter.RecycleviewAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.EndlessRecyclerOnScrollListener;
import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_dark;

/**
 * Created by kutemsys on 2016-05-03.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private static View view;

    private worker_sentence_list mworker_sentence;

    ArrayList<String> listSentence;
    PacketUser userSentence;

    int i, index;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    private RecyclerView mRecyclerView;
    private RecycleviewAdapter mAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected Handler handler;

    // on scroll
    private static int current_page = 1;
    private int ival = 0;
    private int loadLimit = 10;
    private int total = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        userSentence = new PacketUser();
        listSentence = new ArrayList<String>();
        loadData(current_page);

        handler = new Handler();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_sentence);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecycleviewAdapter(listSentence,mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                loadMoreData(current_page);
            }
        });

        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), divider_dark);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    // By default, we add 10 objects for first time.
    private void loadData(int current_page) {

        // I have not used current page for showing demo, if u use a webservice
        // then it is useful for every call request

        if(mworker_sentence != null && mworker_sentence.isAlive()){  //이미 동작하고 있을 경우 중지
            mworker_sentence.interrupt();
        }
        mworker_sentence = new worker_sentence_list(true);
        mworker_sentence.start();
        try {
            mworker_sentence.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = ival; i < loadLimit; i++) {
            listSentence.add(userSentence.arrSentence.get(i));
            ival++;
        }

    }

    // adding 10 object creating dymically to arraylist and updating recyclerview when ever we reached last item
    private void loadMoreData(int current_page) {

        // I have not used current page for showing demo, if u use a webservice
        // then it is useful for every call request

        loadLimit = ival + 10;

        if(mworker_sentence != null && mworker_sentence.isAlive()){  //이미 동작하고 있을 경우 중지
            mworker_sentence.interrupt();
        }
        mworker_sentence = new worker_sentence_list(true);
        mworker_sentence.start();
        try {
            mworker_sentence.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = ival; i < loadLimit; i++) {
            listSentence.add(userSentence.arrSentence.get(i));
            ival++;
        }

        mAdapter.notifyDataSetChanged();

    }

    class worker_sentence_list extends Thread {
        private boolean isPlay = false;

        public worker_sentence_list(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {
                System.out.println("1");
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_MSL;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) PacketUser.USR_MSL_LEN;
                outData[4] = (byte) 10;
                outData[5] = (byte) 85;
                try {
                    i = 0;
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, outData[3] + 5); // packet transmission
                    dos.flush();

                    dis = new DataInputStream(SocketConnection.socket.getInputStream());
                    while (i < 10) {
                        dis.read(temp, 0, 4);
                        for (index = 0; index < 4; index++) {
                            inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }

                        dis.read(temp, 0, 1 + (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]));

                        for (index = 0; index <= (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]); index++) {
                            inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                        }

                        int SOF = inData[0];
                        System.out.println("0 : " + inData[0]);
                        System.out.println("1 : " + inData[1]);
                        System.out.println("2 : " + inData[2]);
                        System.out.println("3 : " + (int) inData[3]);
                        System.out.println("5 : " + (char) inData[5]); //sentence - second char
                        PacketUser.sentence_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);

                        index = 0;
                        String str = "";
                        System.out.println("len : " + PacketUser.sentence_len);

                        while (true) { //solving
                            System.out.print((char) inData[4 + index]);

                            if (index == PacketUser.sentence_len)
                                break;
                            else {
                                str += (char) inData[4 + index];
                                index++;
                            }
                        }

                        userSentence.setSentence(str);
                        System.out.println(total + "str :" + str);
                        total++;
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
