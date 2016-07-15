package com.onpuri.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.onpuri.R;

import java.util.Locale;


/**
 * Created by kutemsys on 2016-05-11.
 */
public class HomeSentenceFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {

    private static View view;
    private Toast toast;

    TextView item;
    String sentence="";
    String sentence_num="";

    TextToSpeech tts;
    public static HomeSentenceFragment newInstance() {
        HomeSentenceFragment hsf = new HomeSentenceFragment();
        return hsf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_home_sen, container, false);
        } catch (InflateException e) {
        }

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num = getArguments().getString("sen_num");
            item.setText(sentence);
        }
        TextView button1 = (TextView) view.findViewById(R.id.listen1);
        TextView button2 = (TextView) view.findViewById(R.id.listen2);
        TextView button3 = (TextView) view.findViewById(R.id.listen3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        tts = new TextToSpeech(getActivity(), this);

        return view;
    }
    @Override
    public void onDestroy() {
        if(tts!=null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listen1:
                tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.listen2:
                toast = Toast.makeText(getActivity(), "두번째 음성 파일이 존재하지 않습니다", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.listen3:
                toast = Toast.makeText(getActivity(), "세번째 음성 파일이 존재하지 않습니다", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }
    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
    }
}
