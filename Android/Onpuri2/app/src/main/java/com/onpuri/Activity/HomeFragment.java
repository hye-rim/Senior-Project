package com.onpuri.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.onpuri.Adapter.RecycleviewAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.Listener.EndlessRecyclerOnScrollListener;
import com.onpuri.Listener.HomeItemClickListener;
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

    ArrayList<String> listSentence = new ArrayList<String>();
    ArrayList<String> listSentenceNum = new ArrayList<String>();
    PacketUser userSentence;

    int i, index;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] senData = new byte[261];
    byte[] temp = new byte[261];

    private RecyclerView mRecyclerView;
    private RecycleviewAdapter mAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected Handler handler;

    // on scroll
    private static int current_page = 1;
    private int ival = 0;
    private int loadLimit = 10;
    private int sentence_num;
    private Boolean sentenceEnd = false;

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
        sentence_num = 0;
        userSentence = new PacketUser();

        listSentence = new ArrayList<String>();
        listSentenceNum = new ArrayList<String>();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if(!sentenceEnd)
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
                if(sentenceEnd)
                    Toast.makeText(getActivity(),"불러올 문장이 더이상 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(
                new HomeItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new HomeItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        HomeSentenceFragment hsf = new HomeSentenceFragment();
                        FragmentManager fm = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putString("sen", listSentence.get(position));
                        args.putString("sen_num", listSentenceNum.get(position));
                        hsf.setArguments(args);

                        fm.beginTransaction()
                                .replace(R.id.root_home, hsf)
                                .addToBackStack(null)
                                .commit();
                   //     fm.executePendingTransactions();
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_dark);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    // By default, we add 10 objects for first time.
    private void loadData(int current_page) {
        if (mworker_sentence != null && mworker_sentence.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_sentence.interrupt();
        }
        mworker_sentence = new worker_sentence_list(true);
        mworker_sentence.start();
        try {
            mworker_sentence.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < loadLimit; i++) {
            listSentence.add(userSentence.arrSentence.get(i));
            listSentenceNum.add(userSentence.arrSentenceNum.get(i));
            ival++;
        }
    }

    // adding 10 object creating dymically to arraylist and updating recyclerview when ever we reached last item
    private void loadMoreData(int current_page) {
        if (mworker_sentence != null && mworker_sentence.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_sentence.interrupt();
        }
        mworker_sentence = new worker_sentence_list(true);
        mworker_sentence.start();
        try {
            mworker_sentence.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!sentenceEnd) {
            loadLimit = ival + 10;

            for (int i = ival; i < loadLimit; i++) {
                listSentence.add(userSentence.arrSentence.get(i));
                listSentenceNum.add(userSentence.arrSentenceNum.get(i));
                ival++;
            }
            mAdapter.notifyDataSetChanged();
        }

        else{
            if (mworker_sentence != null && mworker_sentence.isAlive()) {  //이미 동작하고 있을 경우 중지
                mworker_sentence.interrupt();
            }
            Toast.makeText(getActivity(), "불러올 문장이 더이상 없습니다.", Toast.LENGTH_SHORT).show();

        }
    }

    class worker_sentence_list extends Thread {
        private boolean isPlay = false;

        public worker_sentence_list(boolean isPlay) {
            this.isPlay = isPlay;
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
                outData[5] = (byte) sNumN; //255이하일 때 몫 + 1\
                outData[6] = (byte) PacketUser.CRC;

                try {
                    i = 0;
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, outData[3] + 5); // packet transmission
                    dos.flush();


                    dis = new DataInputStream(SocketConnection.socket.getInputStream());

                    while (i < 10) {
                        //문장
                        dis.read(temp, 0, 4);
                        for (index = 0; index < 4; index++) {
                            inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                        }

                        if(inData[1] == PacketUser.ACK_NSEN){
                            sentenceEnd = true;
                            Log.d(TAG, "no more : " + String.valueOf(sentenceEnd));
                            i = 10;
                            isPlay = false;
                        }

                        dis.read(temp, 0, 1 + (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]));

                        for (index = 0; index <= (inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]); index++) {
                            inData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                        }

                        if(!sentenceEnd) {
                            //문장번호
                            dis.read(temp, 0, 4);
                            for (index = 0; index < 4; index++) {
                                senData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                            }

                            dis.read(temp, 0, 1 + (senData[3] <= 0 ? (int) senData[3] + 256 : (int) senData[3]));

                            for (index = 0; index <= (senData[3] <= 0 ? (int) senData[3] + 256 : (int) senData[3]); index++) {
                                senData[index + 4] = temp[index];    // 패킷의 Data부분을 inData에 추가해준다.
                            }

                            PacketUser.sentence_len = ((int) inData[3] <= 0 ? (int) inData[3] + 256 : (int) inData[3]);
                            int len = ((int) senData[3] <= 0 ? (int) senData[3] + 256 : (int) senData[3]);

                        /*    String seninfo = new String(senData, 4, len);
                            int plus = seninfo.indexOf('+');
                            String senNum = seninfo.substring(0,plus); //문장번호
                            seninfo = seninfo.substring(plus+1,seninfo.length());
                            plus = seninfo.indexOf('+');
                            String transNum = seninfo.substring(0, plus); //해석수
                            String ListenNum = seninfo.substring(plus+1, seninfo.length()); //듣기수
                         */
                            String sen = new String (inData, 4, PacketUser.sentence_len); //문장
                            String senNum = Character.toString((char) senData[4])
                                    + Character.toString((char) senData[5])
                                    + Character.toString((char) senData[6]); //문장번호

                            userSentence.setSentence(sen);
                            userSentence.setSentenceNum(senNum);
                   /*         userSentence.setSentenceTransNum(transNum);
                            userSentence.setSentenceListenNum(ListenNum);*/

                            i++;
                            sentence_num++;



                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlay = false;

            }
        }
    }
}
