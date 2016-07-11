package com.onpuri.Activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.onpuri.RecyclerItemClickListener;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_light;

/**
 * Created by kutemsys on 2016-04-26.
 */
//내노트
public class NoteFragment extends Fragment {
    private static final String TAG = "NoteFragment";
    //public static UserProfile ProfileGroup;
    // private ArrayList<View> history;
    private static View view;
    private TabHost mTabHost;

    ArrayList<NoteData> listSentence, listWord;

    private RecyclerView mRecyclerSen, mRecyclerWord;
    private RecyclerView.Adapter mSenAdapter, mWordAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;
    private Context context;

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
        mRecyclerSen.addOnItemTouchListener(
                new RecyclerItemClickListener(context, mRecyclerSen, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.v(TAG, "click : " + position);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Log.v(TAG, "click : " + position);
            }
        }));

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
