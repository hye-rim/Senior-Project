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

        initData();
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_light);

        //Set Sentence Adapter for Sentence RecyclerView (NoteTab)
        mRecyclerSen = (RecyclerView) view.findViewById(R.id.recycle_note_sen);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSenAdapter = new NoteSenAdapter(listSentence);
        mRecyclerSen.setAdapter(mSenAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerSen.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerWord = (RecyclerView) view.findViewById(R.id.recycle_note_word);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mWordAdapter = new NoteWordAdapter(listWord);
        mRecyclerWord.setAdapter(mWordAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerWord.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mSenAdapter.notifyDataSetChanged();
        mWordAdapter.notifyDataSetChanged();

        mRecyclerSen.addOnItemTouchListener(
                new RecycleItemClickListener(getActivity().getApplicationContext(), mRecyclerSen, new RecycleItemClickListener.OnItemClickListener() {
                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.v(TAG,"sententce item : " + position);
                        if( view.getId() != R.id.ll_sen_more&& view.getId() != R.id.btn_sen_more && mRecyclerWord.getAdapter().getItemViewType(position) == VIEW_TYPE_CELL  ) {
                            NoteSenFragment noteSenItem = new NoteSenFragment();
                            FragmentManager fm = getActivity().getSupportFragmentManager();

                            Bundle args = new Bundle();
                            args.putString("senItemName", "문장 모음" );
                            noteSenItem.setArguments(args);
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.root_note, noteSenItem)
                                    .addToBackStack(null)
                                    .commit();
                            fm.executePendingTransactions();
                        }

                    }
                }));

        mRecyclerWord.addOnItemTouchListener(
                new RecycleItemClickListener(getActivity().getApplicationContext(), mRecyclerWord, new RecycleItemClickListener.OnItemClickListener() {
                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.v(TAG, "word item : " + position);
                        if( view.getId() != R.id.ll_word_more&& view.getId() != R.id.btn_word_more &&mRecyclerWord.getAdapter().getItemViewType(position) == VIEW_TYPE_CELL  ) {
                            NoteWordFragment noteWordItem = new NoteWordFragment();
                            FragmentManager fm = getActivity().getSupportFragmentManager();

                            Bundle args = new Bundle();
                            args.putString("wordItemName", "단어 모음" );
                            noteWordItem.setArguments(args);

                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.root_note, noteWordItem)
                                    .addToBackStack(null)
                                    .commit();
                            fm.executePendingTransactions();
                        }
                    }
                }));
        return view;
    }

    private void setTabColor() {
        for(int i=0;i< mTabHost.getTabWidget().getChildCount();i++) {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.never_forgotten)); //선택되지 않은 탭
        }
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.pale_gold)); //선택된 탭
    }

    public void onBackPressed(){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initData() {
        listSentence = new ArrayList<NoteData>();
        listWord = new ArrayList<NoteWordData>();

        for(int i = 0; i < 3; i++) {
            listSentence.add(new NoteData("문장모음 " + i));
            listWord.add(new NoteWordData("단어모음 " + i));
/*
            for(int j = 0; j < 20; i++) {
                listWord.get(i).getData().add(new WordData("word" + j, "뜻" + j));
                Log.d( TAG, String.valueOf(listWord.get(i).getData().get(j)) );
            }*/
        }

    }

}