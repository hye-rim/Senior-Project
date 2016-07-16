package com.onpuri.Activity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;

import java.io.IOException;

/**
 * Created by kutemsys on 2016-07-16.
 */

public class AddListenFragment extends Fragment implements View.OnClickListener {

    private static View view;
    private Toast toast;

    TextView item;
    String sentence="";

    static final String RECORDED_FILE = "/Docouments/recorded.mp4";
    MediaPlayer player;
    MediaRecorder recorder;
    boolean listen = true;
    boolean play = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_add_listen, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            item.setText(sentence);
        }
        Button btn_new_listen = (Button) view.findViewById(R.id.btn_new_listen);
        btn_new_listen.setOnClickListener(this);
        Button btn_new_play = (Button) view.findViewById(R.id.btn_new_play);
        btn_new_play.setOnClickListener(this);

        Button btn_new_trans = (Button) view.findViewById(R.id.btn_new_trans);
        btn_new_trans.setOnClickListener(this);
        Button btn_new_trans_back = (Button) view.findViewById(R.id.btn_new_trans_back);
        btn_new_trans_back.setOnClickListener(this);

        return view;
    }
    @Override
    public void onClick(View v) {
        /*
        switch (v.getId()) {
            case R.id.btn_new_listen:
                if (listen) {
                    if (recorder != null) {
                        recorder.stop();
                        recorder.release();
                        recorder=null;
                    }
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setOutputFile(RECORDED_FILE);
                    try {
                        Toast.makeText(getActivity(),"녹음시작", Toast.LENGTH_LONG).show();
                        recorder.prepare();
                        recorder.start();
                    } catch (Exception e) {
                        Log.e("error","Exception : ", e);
                    }
                   // listen = false;
                } else {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    Toast.makeText(getActivity(), "녹음중지", Toast.LENGTH_LONG).show();
                    listen = true;
                }
                break;
        }*/
    }
}
