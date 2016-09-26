package com.onpuri.Activity.Note.Sentence;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.Home.Fragment.HomeSentenceFragment;
import com.onpuri.DividerItemDecoration;
import com.onpuri.Listener.RecyclerItemClickListener;
import com.onpuri.R;
import com.onpuri.Activity.Note.workerNoteLoad;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_light;

/**
 * Created by HYERIM on 2016-07-11.
 */
public class NoteSenFragment extends Fragment {
    private static final String TAG = "NoteSenFragment";
    private static View view;

    ArrayList<String> itemSentence, itemSentenceNum, itemSentenceId;

    private RecyclerView mRecyclerSenItem;
    private TextView tvItemName;

    protected RecyclerView.LayoutManager mLayoutManager;
    private Context context;

    String itemName, itemNum;

    private FrameLayout mItemFrame;
    private FragmentManager mFragmentManager;
    private Boolean isEdit = false;
    private workerNoteLoad mworker_note;

    private Boolean isNullSen;
    private ViewPager viewPager;
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
        viewPager = (ViewPager)getActivity().findViewById(R.id.viewpager);
        isNullSen = false;

        mFragmentManager = getFragmentManager();

        tvItemName = (TextView)view.findViewById(R.id.note_sen_name);
        if (getArguments() != null) {                       //클릭한 문장이름 저장
            itemName = getArguments().getString("senItemName");
            itemNum = getArguments().getString("senItemNum");
            tvItemName.setText(itemName);
        }

        initData();
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_light);

        //Set Sentence Adapter for Sentence RecyclerView (NoteTab)
        mRecyclerSenItem = (RecyclerView) view.findViewById(R.id.recycle_note_sen);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerSenItem.setAdapter(new NoteSenItemAdapter(itemSentence));// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerSenItem.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        mRecyclerSenItem.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerSenItem, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(isNullSen) {
                            Toast.makeText(getActivity().getApplicationContext(), itemName + "에 문장을 추가해보세요.", Toast.LENGTH_SHORT).show();
                        }
                        else if( position >= itemSentenceNum.size()){

                        }else{
                            HomeSentenceFragment homeSentenceFragment = new HomeSentenceFragment();
                            FragmentManager fm = getActivity().getSupportFragmentManager();

                            Bundle args = new Bundle();
                            args.putString("sen", itemSentence.get(position));
                            args.putString("sen_num", itemSentenceNum.get(position));
                            args.putString("id", itemSentenceId.get(position));
                            homeSentenceFragment.setArguments(args);

                            fm.beginTransaction()
                                    .replace(R.id.root_home, homeSentenceFragment)
                                    .addToBackStack(null)
                                    .commit();
                            fm.executePendingTransactions();

                            viewPager.setCurrentItem(0);
                        }
                    }

                }));

        return view;
    }

    private void initData() {
        String nameData = new String ("1+" + itemNum);
        itemSentence = new ArrayList<String>();
        itemSentenceNum = new ArrayList<String>();
        itemSentenceId = new ArrayList<String>();

        if (mworker_note != null && mworker_note.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_note.interrupt();
        }
        mworker_note = new workerNoteLoad(true, nameData, 1);
        mworker_note.start();
        try {
            mworker_note.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int i = 0;

        if(mworker_note.getNoteSentence().arrSentence != null) {
            isNullSen = false;
            while (i < mworker_note.getNoteSentence().arrSentence.size()) {
                itemSentence.add(mworker_note.getNoteSentence().arrSentence.get(i));
                itemSentenceNum.add(mworker_note.getNoteSentence().arrSentenceNum.get(i));
                itemSentenceId.add(mworker_note.getNoteSentence().arrSentenceId.get(i));
                Log.d(TAG, mworker_note.getNoteSentence().arrSentence.get(i));
                i++;
            }
        }
        if(itemSentence.isEmpty()){
            isNullSen = true;
            itemSentence.add("추가된 문장이 없습니다.");
        }else{
            itemSentence.add("");
        }

    }

}
