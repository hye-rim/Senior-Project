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
import android.widget.TextView;

import com.onpuri.Adapter.NoteSenItemAdapter;
import com.onpuri.Adapter.NoteWordItemAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.R;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_light;

/**
 * Created by HYERIM on 2016-07-11.
 */
public class NoteWordFragment  extends Fragment {
    private static final String TAG = "NoteWordFragment";
    private static View view;

    ArrayList<String> itemWord;

    private RecyclerView mRecyclerWordItem;
    private RecyclerView.Adapter mWordAdapter;
    private TextView tvItemName;

    protected RecyclerView.LayoutManager mLayoutManager;
    private Context context;

    String itemName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_note_word, container, false);
        } catch (InflateException e) {
            //map is already there, just return view as it is
        }

        tvItemName = (TextView)view.findViewById(R.id.note_word_name);
        if (getArguments() != null) {   //클릭한 단어이름 저장
            itemName = getArguments().getString("wordItemName");
            tvItemName.setText(itemName);
        }

        initData();
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_light);

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerWordItem = (RecyclerView) view.findViewById(R.id.recycle_note_word);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mWordAdapter = new NoteWordItemAdapter(itemWord);
        mRecyclerWordItem.setAdapter(mWordAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerWordItem.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        return view;
    }

    private void initData() {
        itemWord = new ArrayList<String>();

        for(int i = 0; i < 10; i++) {
            itemWord.add("단어 " + i);
        }
    }
}
