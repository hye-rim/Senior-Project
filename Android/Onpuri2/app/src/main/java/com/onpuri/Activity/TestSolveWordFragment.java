package com.onpuri.Activity;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onpuri.R;

import java.util.Locale;

public class TestSolveWordFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener{
    private static final String TAG = "TestSolveWordFragment";

    private static View view;

    TextView testname, next;
    Button ex1, ex2, ex3, ex4;

    String name="";
    String word="";
    TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            view = inflater.inflate(R.layout.fragment_test_solve_word, container, false);
        } catch (InflateException e) {
        }

        testname = (TextView) view.findViewById(R.id.testname);
        if (getArguments() != null) { //클릭한 문장 출력
            name = getArguments().getString("testname");
            testname.setText(name);
        }

        ex1 = (Button) view.findViewById(R.id.example1);
        ex1.setOnClickListener(this);
        ex2 = (Button) view.findViewById(R.id.example2);
        ex2.setOnClickListener(this);
        ex3 = (Button) view.findViewById(R.id.example3);
        ex3.setOnClickListener(this);
        ex4 = (Button) view.findViewById(R.id.example4);
        ex4.setOnClickListener(this);

        next = (TextView) view.findViewById(R.id.next);
        next.setOnClickListener(this);

        tts = new TextToSpeech(getActivity(), this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.example1 :
            case R.id.example2 :
            case R.id.example3 :
            case R.id.example4 :
                word="remember";
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null);

                ex2.setTextColor(getResources().getColor(R.color.white));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ex2.setBackground(getResources().getDrawable(R.color.fuzzy_peach));
                }else{
                    ex2.setBackgroundResource((R.color.fuzzy_peach));
                }
                next.setVisibility(LinearLayout.VISIBLE);

                break;

            case R.id.next :
                if (tts != null) {
                    tts.stop();
                    tts.shutdown();
                }

                FragmentManager fm = getActivity().getSupportFragmentManager();
                final TestSolveFinishFragment tsff = new TestSolveFinishFragment();

                fm.beginTransaction()
                        .replace(R.id.root_test, tsff)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
    }
}
