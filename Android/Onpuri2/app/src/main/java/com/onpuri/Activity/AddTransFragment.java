package com.onpuri.Activity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;

/**
 * Created by kutemsys on 2016-07-16.
 */

public class AddTransFragment extends Fragment implements View.OnClickListener {

    private static View view;

    TextView item;
    String sentence="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_add_trans, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            item.setText(sentence);
        }/*
        Button del_sen = (Button) view.findViewById(R.id.del_sen);
        del_sen.setOnClickListener(this);
        Button add_note = (Button) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
*/
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_new_trans:
                break;
            case R.id.btn_new_trans_back:
                break;
        }
    }
}
