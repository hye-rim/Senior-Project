package com.onpuri.Activity.Note;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.onpuri.Activity.Note.Sentence.NoteSenAdapter;
import com.onpuri.Activity.Note.Word.NoteWordAdapter;
import com.onpuri.Data.NoteData;
import com.onpuri.Data.NoteWordData;
import com.onpuri.DividerItemDecoration;
import com.onpuri.R;
import com.onpuri.Activity.Note.workerNote;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_dark;

/**
 * Created by kutemsys on 2016-04-26.
 */
//내노트
public class NoteFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "NoteFragment";
    private static final int VIEW_NOTE_SEN = 0; //sentence item
    private static final int VIEW_NOTE_WORD = 1; //sentence add button
    private static View view;

    private Button btnSen, btnWord;
    private LinearLayout tabSen, tabWord;


    ArrayList<NoteData> listSentence;
    ArrayList<NoteWordData> listWord;
    ArrayList<String> listSentenceNum, listWordNum;

    private RecyclerView mRecyclerSen, mRecyclerWord;
    private RecyclerView.Adapter mSenAdapter, mWordAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;

    private FrameLayout mItemFrame;
    private FragmentManager mFragmentManager;

    private workerNote mworker_note;

    private Boolean isNullSen, isNullWord;
    private int noteKinds = 0;
    private Boolean first;

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
        btnSen = (Button)view.findViewById(R.id.btn_note_sen);
        btnWord = (Button)view.findViewById(R.id.btn_note_word);
        tabSen = (LinearLayout)view.findViewById(R.id.tab_sen);
        tabWord = (LinearLayout)view.findViewById(R.id.tab_word);

        btnSen.setOnClickListener(this);
        btnWord.setOnClickListener(this);

        initData(); //노트 데이터 서버로부터 받기
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_dark);

        first = true;
        //문장탭이 기본으로 오도록 한다.
        tabSen.setVisibility(LinearLayout.VISIBLE);
        tabWord.setVisibility(LinearLayout.INVISIBLE);

        //Set Sentence Adapter for Sentence RecyclerView (NoteTab)
        mRecyclerSen = (RecyclerView) view.findViewById(R.id.recycle_note_sen);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerSen.setLayoutManager(mLayoutManager);
        mSenAdapter = new NoteSenAdapter(listSentence, listSentenceNum, getContext(), getActivity().getSupportFragmentManager(), isNullSen, first);

        mRecyclerSen.setAdapter(mSenAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerSen.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerWord = (RecyclerView) view.findViewById(R.id.recycle_note_word);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerWord.setLayoutManager(mLayoutManager);
        mWordAdapter = new NoteWordAdapter(listWord, listWordNum, getContext(), getActivity().getSupportFragmentManager(), isNullWord, first);

        mRecyclerWord.setAdapter(mWordAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerWord.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_note_sen:
                noteKinds = VIEW_NOTE_SEN;
                tabSen.setVisibility(LinearLayout.VISIBLE);
                tabWord.setVisibility(LinearLayout.INVISIBLE);
                btnSen.setTextColor(Color.WHITE);
                btnWord.setTextColor(getResources().getColor(R.color.dark_gray));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btnSen.setBackground(getResources().getDrawable(R.color.fuzzy_peach));
                    btnWord.setBackground(getResources().getDrawable(R.drawable.btn_border));
                }else{
                    btnSen.setBackgroundResource((R.color.fuzzy_peach));
                    btnWord.setBackgroundResource((R.drawable.btn_border));
                }
                break;

            case R.id.btn_note_word:
                noteKinds = VIEW_NOTE_WORD;
                tabSen.setVisibility(LinearLayout.INVISIBLE);
                tabWord.setVisibility(LinearLayout.VISIBLE);
                btnSen.setTextColor(getResources().getColor(R.color.dark_gray));
                btnWord.setTextColor(Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btnSen.setBackground(getResources().getDrawable(R.drawable.btn_border));
                    btnWord.setBackground(getResources().getDrawable(R.color.fuzzy_peach));
                }else{
                    btnSen.setBackgroundResource((R.drawable.btn_border));
                    btnWord.setBackgroundColor(getResources().getColor(R.color.fuzzy_peach));
                }
                break;
            default:
                break;
        }

    }

    private void initData() {
        listSentence = new ArrayList<NoteData>();
        listSentenceNum = new ArrayList<String>();
        listWord = new ArrayList<NoteWordData>();
        listWordNum = new ArrayList<String>();

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
        listSentenceNum.clear();
        listWordNum.clear();

        //문장 모음 리스트
        int i = 0;
        if(mworker_note.getNoteSen() != null){
            isNullSen = false;
            while( i < mworker_note.getNoteSen().size()){
                listSentence.add(new NoteData( mworker_note.getNoteSen().get(i).toString() ));
                listSentenceNum.add(mworker_note.getNoteSenNum().get(i).toString());
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
                listWordNum.add(mworker_note.getNoteWordNum().get(i).toString());
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