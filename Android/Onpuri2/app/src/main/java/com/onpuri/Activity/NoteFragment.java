package com.onpuri.Activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.onpuri.DividerItemDecoration;
import com.onpuri.NoteData;
import com.onpuri.R;
import com.onpuri.RecycleItemClickListener;

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

    ArrayList<NoteData> listSentence, listWord;

    private RecyclerView mRecyclerSen, mRecyclerWord;
    private RecyclerView.Adapter mSenAdapter, mWordAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;

    private FrameLayout mItemFrame;
    private FragmentManager mFragmentManager;

    private CustomOnClickListener customLister;
    public interface CustomOnClickListener {
        public void onClicked(int id);
    }

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

        initData();
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_light);
        final NoteSenFragment noteSenItem = new NoteSenFragment();


        //Set Sentence Adapter for Sentence RecyclerView (NoteTab)
        mRecyclerSen = (RecyclerView) view.findViewById(R.id.recycle_note_sen);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSenAdapter = new NoteSenAdapter(listSentence);
        mRecyclerSen.setAdapter(mSenAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerSen.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mRecyclerSen.addOnItemTouchListener(
                new RecycleItemClickListener(getActivity().getApplicationContext(), mRecyclerSen, new RecycleItemClickListener.OnItemClickListener() {
                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.v(TAG,"sententce item : " + position);
                        if( view.getId() != R.id.ll_sen_more&& view.getId() != R.id.btn_sen_more && mRecyclerWord.getAdapter().getItemViewType(position) == VIEW_TYPE_CELL  ) {
                            Bundle args = new Bundle();
                            args.putString("senItemName", "단어 모음" );
                            noteSenItem.setArguments(args);

                            mTabHost.setVisibility(View.GONE);
                            mItemFrame.setVisibility(View.VISIBLE);
                            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.note_item, noteSenItem)
                                    .commit();
                        }

                    }
                }));


        final NoteWordFragment noteWordItem = new NoteWordFragment();
        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerWord = (RecyclerView) view.findViewById(R.id.recycle_note_word);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mWordAdapter = new NoteWordAdapter(listWord);
        mRecyclerWord.setAdapter(mWordAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerWord.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        mRecyclerWord.addOnItemTouchListener(
                new RecycleItemClickListener(getActivity().getApplicationContext(), mRecyclerWord, new RecycleItemClickListener.OnItemClickListener() {
                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.v(TAG, "word item : " + position);
                        if( view.getId() != R.id.ll_word_more&& view.getId() != R.id.btn_word_more &&mRecyclerWord.getAdapter().getItemViewType(position) == VIEW_TYPE_CELL  ) {
                            Bundle args = new Bundle();
                            args.putString("wordItemName", "단어 모음" );
                            noteWordItem.setArguments(args);

                            mTabHost.setVisibility(View.GONE);
                            mItemFrame.setVisibility(View.VISIBLE);
                            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.note_item, noteWordItem)
                                    .commit();
                        }
                    }
                }));

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

    void setVisible() {
        mTabHost.setVisibility(View.GONE);
        mItemFrame.setVisibility(View.VISIBLE);
    }
}
