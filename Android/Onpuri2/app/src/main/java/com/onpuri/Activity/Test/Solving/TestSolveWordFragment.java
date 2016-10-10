package com.onpuri.Activity.Test.Solving;

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

import com.onpuri.Activity.MainActivity;
import com.onpuri.R;

import java.util.Locale;

public class TestSolveWordFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener{
    private static final String TAG = "TestSolveWordFragment";
    private static View view;

    private workerTestSolve worker_test_solve;
    private workerTestFinish worker_test_finish;

    TextView testname, quizWord, next, totaltext, scoretext;
    Button ex1, ex2, ex3, ex4;

    String name, num, quiz;
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

        ((MainActivity)getActivity()).Backkey = false;

        testname = (TextView) view.findViewById(R.id.testname);
        if (getArguments() != null) { //클릭한 문장 출력
            name = getArguments().getString("testname");
            num = getArguments().getString("testnum");
            quiz = getArguments().getString("testquiz");
            testname.setText(name);
        }

        testlist(num);

        totaltext = (TextView) view.findViewById(R.id.total);
        scoretext = (TextView) view.findViewById(R.id.score);
        totaltext.setText(String.valueOf(worker_test_solve.count));

        quizWord = (TextView) view.findViewById(R.id.word);
        quizWord.setText(worker_test_solve.getQuiz().get(worker_test_solve.tmp).toString());

        ex1 = (Button) view.findViewById(R.id.example1);
        ex1.setOnClickListener(this);
        ex1.setText(worker_test_solve.getEx1().get(worker_test_solve.tmp).toString());
        ex2 = (Button) view.findViewById(R.id.example2);
        ex2.setOnClickListener(this);
        ex2.setText(worker_test_solve.getEx2().get(worker_test_solve.tmp).toString());
        ex3 = (Button) view.findViewById(R.id.example3);
        ex3.setOnClickListener(this);
        ex3.setText(worker_test_solve.getEx3().get(worker_test_solve.tmp).toString());
        ex4 = (Button) view.findViewById(R.id.example4);
        ex4.setOnClickListener(this);
        ex4.setText(worker_test_solve.getEx4().get(worker_test_solve.tmp).toString());

        next = (TextView) view.findViewById(R.id.next);
        next.setOnClickListener(this);

        tts = new TextToSpeech(getActivity(), this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.example1 :
                if(worker_test_solve.getSol().get(worker_test_solve.tmp).toString().equals("1")) { worker_test_solve.score++;}
                ex2.setEnabled(false);
                ex3.setEnabled(false);
                ex4.setEnabled(false);
                check();
                break;
            case R.id.example2 :
                if(worker_test_solve.getSol().get(worker_test_solve.tmp).toString().equals("2")) { worker_test_solve.score++;}
                ex1.setEnabled(false);
                ex3.setEnabled(false);
                ex4.setEnabled(false);
                check();
                break;
            case R.id.example3 :
                if(worker_test_solve.getSol().get(worker_test_solve.tmp).toString().equals("3")) { worker_test_solve.score++;}
                ex1.setEnabled(false);
                ex2.setEnabled(false);
                ex4.setEnabled(false);
                check();
                break;
            case R.id.example4 :
                if(worker_test_solve.getSol().get(worker_test_solve.tmp).toString().equals("4")) { worker_test_solve.score++;}
                ex1.setEnabled(false);
                ex2.setEnabled(false);
                ex3.setEnabled(false);
                check();
                break;

            case R.id.next :
                if (tts != null) {
                    tts.stop();
                    tts.shutdown();
                }

                ex1.setEnabled(true);
                ex2.setEnabled(true);
                ex3.setEnabled(true);
                ex4.setEnabled(true);

                if((worker_test_solve.tmp)+1 < worker_test_solve.count) {nextQuiz();}
                else {finishQuiz();}

                break;
        }
    }

    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
    }

    private void testlist(String num) {
        if(worker_test_solve != null && worker_test_solve.isAlive()){
            worker_test_solve.interrupt();
        }
        worker_test_solve = new workerTestSolve(true, num);
        worker_test_solve.start();
        try {
            worker_test_solve.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void testfinish(String num, String score, String percent) {
        if(worker_test_finish != null && worker_test_finish.isAlive()){
            worker_test_finish.interrupt();
        }
        worker_test_finish = new workerTestFinish(true, num, score, percent);
        worker_test_finish.start();
        try {
            worker_test_finish.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void check() {
        if(worker_test_solve.getSol().get(worker_test_solve.tmp).toString().equals("1")) {
            ex1.setTextColor(getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ex1.setBackground(getResources().getDrawable(R.color.fuzzy_peach));
            } else {
                ex1.setBackgroundResource((R.color.fuzzy_peach));
            }
        }
        if(worker_test_solve.getSol().get(worker_test_solve.tmp).toString().equals("2")) {
            ex2.setTextColor(getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ex2.setBackground(getResources().getDrawable(R.color.fuzzy_peach));
            } else {
                ex2.setBackgroundResource((R.color.fuzzy_peach));
            }
        }
        if(worker_test_solve.getSol().get(worker_test_solve.tmp).toString().equals("3")) {
            ex3.setTextColor(getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ex3.setBackground(getResources().getDrawable(R.color.fuzzy_peach));
            } else {
                ex3.setBackgroundResource((R.color.fuzzy_peach));
            }
        }
        if(worker_test_solve.getSol().get(worker_test_solve.tmp).toString().equals("4")) {
            ex4.setTextColor(getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ex4.setBackground(getResources().getDrawable(R.color.fuzzy_peach));
            } else {
                ex4.setBackgroundResource((R.color.fuzzy_peach));
            }
        }

        next.setVisibility(LinearLayout.VISIBLE);
        scoretext.setText(String.valueOf((worker_test_solve.score)));

        if((worker_test_solve.tmp)+1 >= worker_test_solve.count) {
            next.setText("종료");
        }

    }
    void nextQuiz() {
        worker_test_solve.tmp++;

        quizWord.setText(worker_test_solve.getQuiz().get(worker_test_solve.tmp).toString());

        ex1.setText(worker_test_solve.getEx1().get(worker_test_solve.tmp).toString());
        ex2.setText(worker_test_solve.getEx2().get(worker_test_solve.tmp).toString());
        ex3.setText(worker_test_solve.getEx3().get(worker_test_solve.tmp).toString());
        ex4.setText(worker_test_solve.getEx4().get(worker_test_solve.tmp).toString());

        ex1.setTextColor(getResources().getColor(R.color.black));
        ex2.setTextColor(getResources().getColor(R.color.black));
        ex3.setTextColor(getResources().getColor(R.color.black));
        ex4.setTextColor(getResources().getColor(R.color.black));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ex1.setBackground(getResources().getDrawable(R.drawable.btn_click));
            ex2.setBackground(getResources().getDrawable(R.drawable.btn_click));
            ex3.setBackground(getResources().getDrawable(R.drawable.btn_click));
            ex4.setBackground(getResources().getDrawable(R.drawable.btn_click));

        }else{
            ex1.setBackgroundResource((R.drawable.btn_click));
            ex2.setBackgroundResource((R.drawable.btn_click));
            ex3.setBackgroundResource((R.drawable.btn_click));
            ex4.setBackgroundResource((R.drawable.btn_click));

        }
        next.setVisibility(LinearLayout.INVISIBLE);
    }
    void finishQuiz() {
        int percent = (worker_test_solve.score) * 100 / (worker_test_solve.count);
        ((MainActivity)getActivity()).Backkey = true;
        testfinish(num, String.valueOf(worker_test_solve.score), String.valueOf(percent));

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(0);
        getFragmentManager().popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction().commit();
    }
}
