package com.onpuri.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.Adapter.NoteSenItemAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.R;
import com.onpuri.RecyclerItemClickListener;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_light;

/**
 * Created by HYERIM on 2016-07-11.
 */
public class NoteSenFragment extends Fragment {
    private static final String TAG = "NoteSenFragment";
    private static View view;

    ArrayList<String> itemSentence;

    private RecyclerView mRecyclerSenItem;
    private RecyclerView.Adapter mSenAdapter;
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
            view = inflater.inflate(R.layout.fragment_note_sen, container, false);
        } catch (InflateException e) {
            //map is already there, just return view as it is
        }

        tvItemName = (TextView)view.findViewById(R.id.note_sen_name);
        if (getArguments() != null) {                       //클릭한 문장이름 저장
            itemName = getArguments().getString("senItemName");
            tvItemName.setText(itemName);
        }

        initData();
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), divider_light);

        //Set Sentence Adapter for Sentence RecyclerView (NoteTab)
        mRecyclerSenItem = (RecyclerView) view.findViewById(R.id.recycle_note_sen);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSenAdapter = new NoteSenItemAdapter(itemSentence);
        mRecyclerSenItem.setAdapter(mSenAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerSenItem.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        mRecyclerSenItem.addOnItemTouchListener(
                new RecyclerItemClickListener(context, mRecyclerSenItem, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.v(TAG,"sententce item : " + position);
                    }
/*
                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.v(TAG, "sententce item : " + position);
                    }*/
                }));

        return view;
    }

    private void initData() {
        itemSentence = new ArrayList<String>();

        for(int i = 0; i < 10; i++) {
            itemSentence.add("문장 " + i);
        }
    }
}
