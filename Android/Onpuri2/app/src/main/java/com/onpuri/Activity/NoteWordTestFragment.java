package com.onpuri.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.onpuri.R;

/**
 * Created by HYERIM on 2016-07-22.
 */
public class NoteWordTestFragment  extends Fragment{
    private static final String TAG = "NoteWordTestFragment";
    private static View view;

    private NumberPicker mTestCount;
    private Button mTestStart;

    protected RecyclerView.LayoutManager mLayoutManager;
    private Context context;

    private int wordCount;
    private int testCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_note_word_test, container, false);
        } catch (InflateException e) {
            //map is already there, just return view as it is
        }
        if (getArguments() != null)   //클릭한 단어장의 수 저장
            wordCount = getArguments().getInt("wordCount");

        mTestCount = (NumberPicker) view.findViewById(R.id.pick_word_test);
        mTestCount.setMinValue(5);
        mTestCount.setMaxValue(wordCount);
        mTestCount.setWrapSelectorWheel(false); //최대 도달시 더이상 올라가지 않도록 함
        mTestCount.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                testCount = newVal;
            }
        });

        mTestStart = (Button) view.findViewById(R.id.btn_word_test_start);
        mTestStart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), testCount + "개의 단어 시험을 시작합니다." , Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
