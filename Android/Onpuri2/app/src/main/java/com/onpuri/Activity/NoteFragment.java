package com.onpuri.Activity;

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
import android.widget.FrameLayout;
import android.widget.TabHost;

import com.onpuri.Adapter.NoteSenAdapter;
import com.onpuri.Adapter.NoteWordAdapter;
import com.onpuri.Data.NoteData;
import com.onpuri.Data.NoteWordData;
import com.onpuri.DividerItemDecoration;
import com.onpuri.Listener.RecycleItemClickListener;
import com.onpuri.R;
import com.onpuri.Thread.workerNote;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_light;

/**
 * Created by kutemsys on 2016-04-26.
 */
//내노트
public class NoteFragment extends Fragment {
    private static final String TAG = "NoteFragment";
    private static final int VIEW_TYPE_CELL = 0; //sentence item
    private static final int VIEW_TYPE_FOOTER = 1; //sentence add button
    private static View view;
    private TabHost mTabHost;

    ArrayList<NoteData> listSentence;
    ArrayList<NoteWordData> listWord;

    private RecyclerView mRecyclerSen, mRecyclerWord;
    private RecyclerView.Adapter mSenAdapter, mWordAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;

    private FrameLayout mItemFrame;
    private FragmentManager mFragmentManager;

    private workerNote mworker_note;

    private Boolean isNullSen, isNullWord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_note, container, false);
        } catch (InflateException e) {
            //map is already there, just return view as it is
        }
        isNullSen = false;
        isNullWord = false;

        mFragmentManager = getFragmentManager();
        mItemFrame = (FrameLayout)view.findViewById(R.id.note_item);
        mTabHost = (TabHost) view.findViewById(R.id.note_tab);

        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec("tab_1")
                .setIndicator("문장")
                .setContent(R.id.tab_sen));

        mTabHost.addTab(mTabHost.newTabSpec("tab_2")
                .setIndicator("단어")
                .setContent(R.id.tab_word));

        setTabColor(); //탭 색상 지정
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() { //탭 색상 변경
            @Override
            public void onTabChanged(String tabId) {
                setTabColor();
            }
        });

        initData(); //노트 데이터 서버로부터 받기
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_light);

        //Set Sentence Adapter for Sentence RecyclerView (NoteTab)
        mRecyclerSen = (RecyclerView) view.findViewById(R.id.recycle_note_sen);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSenAdapter = new NoteSenAdapter(listSentence, getContext(), getActivity().getSupportFragmentManager(), isNullSen, mRecyclerSen);
        mRecyclerSen.setAdapter(mSenAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerSen.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerWord = (RecyclerView) view.findViewById(R.id.recycle_note_word);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mWordAdapter = new NoteWordAdapter(listWord, getContext(), getActivity().getSupportFragmentManager(), isNullWord);
        mRecyclerWord.setAdapter(mWordAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerWord.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mSenAdapter.notifyDataSetChanged();
        mWordAdapter.notifyDataSetChanged();

        return view;
    }

    private void setTabColor() {
        for(int i=0;i< mTabHost.getTabWidget().getChildCount();i++) {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.china_ivory)); //선택되지 않은 탭
        }
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.pale_gold)); //선택된 탭
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initData() {
        listSentence = new ArrayList<NoteData>();
        listWord = new ArrayList<NoteWordData>();

        if (mworker_note != null && mworker_note.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_note.interrupt();
        }
        mworker_note = new workerNote(true);
        mworker_note.start();
        try {
            mworker_note.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listSentence.clear();
        listWord.clear();

        //문장 모음 리스트
        int i = 0;
        if(mworker_note.getNoteSen() != null){
            isNullSen = false;
            while( i < mworker_note.getNoteSen().size()){
                listSentence.add(new NoteData( mworker_note.getNoteSen().get(i).toString() ));
                Log.d(TAG, mworker_note.getNoteSen().get(i).toString());
                i++;
            }
        }
        if(listSentence.isEmpty()){
            isNullSen = true;
            listSentence.add(new NoteData("새로운 문장 모음을 등록해보세요."));
        }

        //단어 모음 리스트
        i = 0;
        if(mworker_note.getNoteWord() != null){
            isNullWord = false;
            while( i < mworker_note.getNoteWord().size()){
                listWord.add(new NoteWordData( mworker_note.getNoteWord().get(i).toString() ));
                Log.d(TAG, mworker_note.getNoteWord().get(i).toString());
                i++;
            }
        }
        if(listWord.isEmpty()){
            isNullWord = true;
            listWord.add(new NoteWordData("새로운 단어 모음을 등록해보세요."));
        }
    }
}