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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Adapter.NoteSenItemAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.R;

import java.util.ArrayList;

import static com.onpuri.R.drawable.default_profile;
import static com.onpuri.R.drawable.divider_light;

/**
 * Created by HYERIM on 2016-07-11.
 */
public class NoteSenFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "NoteSenFragment";
    private static View view;

    ArrayList<String> itemSentence;

    private RecyclerView mRecyclerSenItem;
    private RecyclerView.Adapter mSenAdapter;
    private TextView tvItemName;
    private Button btn_listen, btn_test, btn_edit;

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
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_light);

        //Set Sentence Adapter for Sentence RecyclerView (NoteTab)
        mRecyclerSenItem = (RecyclerView) view.findViewById(R.id.recycle_note_sen);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSenAdapter = new NoteSenItemAdapter(itemSentence);
        mRecyclerSenItem.setAdapter(mSenAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerSenItem.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        btn_listen = (Button)view.findViewById(R.id.note_sen_listen);
        btn_test = (Button)view.findViewById(R.id.note_sen_test);
        btn_edit = (Button)view.findViewById(R.id.note_sen_edit);

        btn_listen.setOnClickListener(this);
        btn_test.setOnClickListener(this);
        btn_edit.setOnClickListener(this);

        return view;
    }

    private void initData() {
        itemSentence = new ArrayList<String>();

        for(int i = 0; i < 10; i++) {
            itemSentence.add("문장 " + i);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.note_sen_listen:
                Toast.makeText(getActivity(),"내일 화면 추가 예정입니다.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.note_sen_test:
                Toast.makeText(getActivity(),"내일 화면 추가 예정입니다.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.note_sen_edit:
                Toast.makeText(getActivity(),"내일 화면 추가 예정입니다.",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
