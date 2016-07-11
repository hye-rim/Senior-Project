package com.onpuri.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.onpuri.Adapter.NoteSenAdapter;
import com.onpuri.Adapter.NoteWordAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.NoteData;
import com.onpuri.R;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_light;

/**
 * Created by kutemsys on 2016-04-26.
 */
//내노트
public class NoteFragment extends Fragment {
    //public static UserProfile ProfileGroup;
    // private ArrayList<View> history;
    private static View view;
    private TabHost mTabHost;

    ArrayList<NoteData> listSentence, listWord;

    private RecyclerView mRecyclerSen, mRecyclerWord;
    private RecyclerView.Adapter mSenAdapter, mWordAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;
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
        mTabHost = (TabHost) view.findViewById(R.id.note_tab);

        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec("tab_1")
                .setIndicator("문장")
                .setContent(R.id.tab_sen));

        mTabHost.addTab(mTabHost.newTabSpec("tab_2")
                .setIndicator("단어")
                .setContent(R.id.tab_word));

        initData();
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), divider_light);

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

        return view;
    }

    private void initData() {
        listSentence = new ArrayList<NoteData>();
        listWord = new ArrayList<NoteData>();

        for(int i = 0; i < 3; i++) {
            listSentence.add(new NoteData("문장모음 " + i));
            listWord.add(new NoteData("단어모음 " + i));
        }
    }
}
