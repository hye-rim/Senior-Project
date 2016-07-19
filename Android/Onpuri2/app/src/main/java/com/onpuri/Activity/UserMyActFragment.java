package com.onpuri.Activity;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Adapter.MyActNewAdapter;
import com.onpuri.Adapter.MyActRecordAdapter;
import com.onpuri.Adapter.MyActTranslateAdapter;
import com.onpuri.Adapter.NoteSenAdapter;
import com.onpuri.Adapter.NoteWordAdapter;
import com.onpuri.Adapter.RecycleviewAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.NoteData;
import com.onpuri.R;
import com.onpuri.RecycleItemClickListener;
import com.onpuri.RecyclerItemClickListener;
import com.onpuri.Server.ActivityList;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_light;

/**
 * Created by kutemsys on 2016-05-26.
 */
public class UserMyActFragment extends Fragment {
    private static final String TAG = "UserMyActFragment";
    private static View view;
    private TabHost mTabHost;

    ArrayList<String> listNew, listRecord, listTrans;

    private RecyclerView mRecyclerNew, mRecyclerRecord, mRecyclerTrans;
    private RecyclerView.Adapter mNewAdapter, mRecordAdapter, mTransAdapter;

    private TextView tv_userId;

    protected RecyclerView.LayoutManager mLayoutManager;

    private FrameLayout mItemFrame;
    private FragmentManager mFragmentManager;

    private CustomOnClickListener customLister;
    public interface CustomOnClickListener {
        public void onClicked(int id);
    }


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
        Bundle extra = getArguments();
        String userId = null;
        userId = extra.getString("ActId");

        tv_userId = (TextView)view.findViewById(R.id.tv_act_name);
        tv_userId.setText(userId + " 님");

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

        for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++){
            mTabHost.getTabWidget().getChildAt(i)
                    .setBackgroundColor(Color.parseColor("#FFAA78"));
        }

        initData();

        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_light);

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
                        Toast.makeText(getActivity().getApplicationContext(), "선택한 문장으로의 이동은 구현예정입니다.", Toast.LENGTH_SHORT).show();
                    }

                }));

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerRecord = (RecyclerView) view.findViewById(R.id.recycle_act_record);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecordAdapter = new MyActRecordAdapter(listRecord,mRecyclerRecord);
        mRecyclerRecord.setAdapter(mRecordAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerRecord.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mRecyclerRecord.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerNew, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getActivity().getApplicationContext(), "선택한 문장으로의 이동은 구현예정입니다.", Toast.LENGTH_SHORT).show();
                    }

                }));

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerTrans = (RecyclerView) view.findViewById(R.id.recycle_act_translate);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTransAdapter = new MyActTranslateAdapter(listTrans,mRecyclerTrans);
        mRecyclerTrans.setAdapter(mTransAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerTrans.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mRecyclerTrans.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerNew, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getActivity().getApplicationContext(), "선택한 문장으로의 이동은 구현예정입니다.", Toast.LENGTH_SHORT).show();
                    }

                }));


        return view;
    }

    private void initData() {
        listNew = new ArrayList<String>();
        listRecord  = new ArrayList<String>();
        listTrans = new ArrayList<String>();

        for(int i = 0; i < 3; i++) {
            listNew.add("등록문장 " + i);
            listRecord.add("녹음문장 " + i);
            listTrans.add("해석문장 " + i);
        }
    }
}
