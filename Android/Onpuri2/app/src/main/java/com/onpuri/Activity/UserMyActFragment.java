package com.onpuri.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Adapter.MyActNewAdapter;
import com.onpuri.Adapter.MyActRecordAdapter;
import com.onpuri.Adapter.MyActTranslateAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.Listener.RecyclerItemClickListener;
import com.onpuri.R;
import com.onpuri.Thread.workerAct;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_dark;
import static com.onpuri.R.drawable.divider_light;

/**
 * Created by kutemsys on 2016-05-26.
 */
public class UserMyActFragment extends Fragment {
    private static final String TAG = "UserMyActFragment";
    private static View view;
    private TabHost mTabHost;

    ArrayList<String> listNew, listRecord, listTrans;
    ArrayList<String> listNewNum, listRecordNum, listTransNum;

    private RecyclerView mRecyclerNew, mRecyclerRecord, mRecyclerTrans;
    private RecyclerView.Adapter mNewAdapter, mRecordAdapter, mTransAdapter;

    private TextView tv_userId, tv_userInfo;

    protected RecyclerView.LayoutManager mLayoutManager;

    private FrameLayout mItemFrame;
    private FragmentManager mFragmentManager;

    private Boolean isNullNew, isNullRecord, isNullTrans;

    private workerAct mworker_act;

    private int cntNew, cntRecord, cntTrans;

    public static UserMyActFragment newInstance() {
        UserMyActFragment fragment = new UserMyActFragment();
        return fragment;
    }

    public UserMyActFragment() {

        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_my_act, container, false);
        } catch (InflateException e) {
            //map is already there, just return view as it is
        }
        isNullNew = false;
        isNullRecord = false;
        isNullTrans = false;

        Bundle extra = getArguments();
        String userId = null;
        userId = extra.getString("ActId");

        tv_userId = (TextView)view.findViewById(R.id.tv_act_name);
        tv_userInfo = (TextView)view.findViewById(R.id.tv_act_info);

        tv_userId.setText("     " + userId + " 님");

        mFragmentManager = getFragmentManager();
        mItemFrame = (FrameLayout)view.findViewById(R.id.my_act_item);
        mTabHost = (TabHost) view.findViewById(R.id.my_act_tab);

        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec("tab_1")
                .setIndicator("등록문장")
                .setContent(R.id.tab_act_new));

        mTabHost.addTab(mTabHost.newTabSpec("tab_2")
                .setIndicator("녹음문장")
                .setContent(R.id.tab_act_record));

        mTabHost.addTab(mTabHost.newTabSpec("tab_3")
                .setIndicator("해석문장")
                .setContent(R.id.tab_act_translate));

        setTabColor(); //탭 색상 지정
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() { //탭 색상 변경
            @Override
            public void onTabChanged(String tabId) {
                setTabColor();
            }
        });

        loadActData();

        tv_userInfo.setText("등록 : " + cntNew + "\n녹음 : " + cntRecord + "\n해석 : " + cntTrans );

        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_dark);

        //Set Sentence Adapter for Sentence RecyclerView (NoteTab)
        mRecyclerNew = (RecyclerView) view.findViewById(R.id.recycle_act_new);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNewAdapter = new MyActNewAdapter(getActivity().getApplicationContext(),listNew,mRecyclerNew);
        mRecyclerNew.setAdapter(mNewAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerNew.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mRecyclerNew.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerNew, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(isNullNew)
                            Toast.makeText(getActivity().getApplicationContext(), "문장을 등록해보세요.", Toast.LENGTH_SHORT).show();
                        else{
                            HomeSentenceFragment homeSentenceFragment = new HomeSentenceFragment();
                            FragmentManager fm = getActivity().getSupportFragmentManager();

                            Bundle args = new Bundle();
                            args.putString("sen", listNew.get(position));
                            args.putString("sen_num", listNewNum.get(position));
                            homeSentenceFragment.setArguments(args);

                            fm.beginTransaction()
                                    .replace(R.id.root_home, homeSentenceFragment)
                                    .addToBackStack(null)
                                    .commit();
                            fm.executePendingTransactions();
                        }
                    }

                }));

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerRecord = (RecyclerView) view.findViewById(R.id.recycle_act_record);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecordAdapter = new MyActRecordAdapter(listRecord,mRecyclerRecord);
        mRecyclerRecord.setAdapter(mRecordAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerRecord.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mRecyclerRecord.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerRecord, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(isNullRecord)
                            Toast.makeText(getActivity().getApplicationContext(), "문장 녹음을 등록해보세요.", Toast.LENGTH_SHORT).show();
                        else{
                            HomeSentenceFragment homeSentenceFragment = new HomeSentenceFragment();
                            FragmentManager fm = getActivity().getSupportFragmentManager();

                            Bundle args = new Bundle();
                            args.putString("sen", listRecord.get(position));
                            args.putString("sen_num", listRecordNum.get(position));
                            homeSentenceFragment.setArguments(args);

                            fm.beginTransaction()
                                    .replace(R.id.root_home, homeSentenceFragment)
                                    .addToBackStack(null)
                                    .commit();
                            fm.executePendingTransactions();
                        }
                    }

                }));

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerTrans = (RecyclerView) view.findViewById(R.id.recycle_act_translate);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTransAdapter = new MyActTranslateAdapter(listTrans,mRecyclerTrans);
        mRecyclerTrans.setAdapter(mTransAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerTrans.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mRecyclerTrans.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerTrans, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(isNullTrans)
                            Toast.makeText(getActivity().getApplicationContext(), "문장 번역을 등록해보세요.", Toast.LENGTH_SHORT).show();
                        else{
                            HomeSentenceFragment homeSentenceFragment = new HomeSentenceFragment();
                            FragmentManager fm = getActivity().getSupportFragmentManager();

                            Bundle args = new Bundle();
                            args.putString("sen", listTrans.get(position));
                            args.putString("sen_num", listTransNum.get(position));
                            homeSentenceFragment.setArguments(args);

                            fm.beginTransaction()
                                    .replace(R.id.root_home, homeSentenceFragment)
                                    .addToBackStack(null)
                                    .commit();
                            fm.executePendingTransactions();
                        }
                    }

                }));


        return view;
    }

    public void loadActData(){
        listNew = new ArrayList<String>();
        listNewNum = new ArrayList<String>();
        listRecord = new ArrayList<String>();
        listRecordNum = new ArrayList<String>();
        listTrans = new ArrayList<String>();
        listTransNum = new ArrayList<String>();

        if (mworker_act != null && mworker_act.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_act.interrupt();
        }
        mworker_act = new workerAct(true);
        mworker_act.start();
        try {
            mworker_act.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int i = 0;

        if(mworker_act.getActNewSentence().arrSentence != null) {
            isNullNew = false;
            while (i < mworker_act.getActNewSentence().arrSentence.size()) {
                listNew.add(mworker_act.getActNewSentence().arrSentence.get(i));
                listNewNum.add(mworker_act.getActNewSentence().arrSentenceNum.get(i));
                Log.d(TAG, mworker_act.getActNewSentence().arrSentence.get(i));
                i++;
            }
        }
        if(listNew.isEmpty()){
            listNew.add("문장을 등록해보세요.");
            isNullNew = true;
        }

        i = 0;
        if(mworker_act.getActRecSentence().arrSentence != null) {
            isNullRecord = false;
            while (i < mworker_act.getActRecSentence().arrSentence.size()) {
                listRecord.add(mworker_act.getActRecSentence().arrSentence.get(i));
                listRecordNum.add(mworker_act.getActRecSentence().arrSentenceNum.get(i));
                Log.d(TAG, mworker_act.getActRecSentence().arrSentence.get(i));
                i++;
            }
        }
        if(listRecord.isEmpty()){
            isNullRecord = true;
            listRecord.add("문장 녹음을 해보세요.");
        }

        i = 0;
        if(mworker_act.getActTransSentence().arrSentence != null) {
            isNullTrans = false;
            while (i < mworker_act.getActTransSentence().arrSentence.size()) {
                listTrans.add(mworker_act.getActTransSentence().arrSentence.get(i));
                listTransNum.add(mworker_act.getActTransSentence().arrSentenceNum.get(i));
                Log.d(TAG, mworker_act.getActTransSentence().arrSentence.get(i));
                i++;
            }
        }
        if(listTrans.isEmpty()){
            isNullTrans = true;
            listTrans.add("문장 번역을 해보세요.");
        }

        cntNew = ( isNullNew ? 0 : listNew.size() );
        cntRecord = ( isNullRecord ? 0 : listRecord.size() );
        cntTrans = ( isNullTrans ? 0 : listTrans.size() ) ;
    }

    public void setTabColor() {
        for(int i=0;i< mTabHost.getTabWidget().getChildCount();i++) {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.china_ivory)); //선택되지 않은 탭
        }
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.pale_gold)); //선택된 탭
    }
}