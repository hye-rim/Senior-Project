package com.onpuri.Activity.Test.Creating;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.MainActivity;
import com.onpuri.Data.CreatedTestData;
import com.onpuri.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by kutemsys on 2016-09-18.
 */
public class TestMakingWordFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TestMakingWordFragment";

    private static View view;

    private TextView mTitleTextView, mNowNumTextView, mMaxNumTextView;
    private Button mNextButton;
    private EditText mProblemEditText, mExampleEditText1, mExampleEditText2, mExampleEditText3, mExampleEditText4;
    private RadioButton mExampleRadio1, mExampleRadio2, mExampleRadio3, mExampleRadio4;
    private RadioGroup mExampleRadio;

    private  String title, problem, example1, example2, example3, example4;
    private int nowNum, maxNum, correctNum;

    private ArrayList<CreatedTestData> problemList;
    workerProblemCreating mworker_problem;

    private int listSize;

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
            view = inflater.inflate(R.layout.fragment_test_making_word, container, false);
        } catch (InflateException e) {
        }

        initView(); //view 아이디 찾기
        initData(); //데이터 기본 설정

        mNextButton.setOnClickListener(this);
        mExampleRadio1.setOnClickListener(this);
        mExampleRadio2.setOnClickListener(this);
        mExampleRadio3.setOnClickListener(this);
        mExampleRadio4.setOnClickListener(this);

        return view;
    }

    private void getArgumentsData() {
        if (getArguments() != null) { //클릭한 문장 출력
            title = getArguments().getString("test_title", "null");
            maxNum = getArguments().getInt("test_max_num", 1);
        }
    }

    private void initData() {
        getArgumentsData();

        nowNum = 1;
        problemList = new ArrayList<CreatedTestData>();
        correctNum = 1;

        mTitleTextView.setText(title);
        mNowNumTextView.setText(""+nowNum);
        mMaxNumTextView.setText(""+maxNum);

        ((MainActivity)getActivity()).Backkey = false;
    }


    private void initView() {
        mNextButton  = (Button)view.findViewById(R.id.btn_making_word_next);
        mTitleTextView = (TextView)view.findViewById(R.id.tv_making_word_title);
        mNowNumTextView  = (TextView)view.findViewById(R.id.tv_making_word_now);
        mMaxNumTextView  = (TextView)view.findViewById(R.id.tv_making_word_max);
        mProblemEditText = (EditText) view.findViewById(R.id.et_making_word_problem);
        mExampleEditText1 = (EditText)view.findViewById(R.id.et_making_word_example1);
        mExampleEditText2 = (EditText)view.findViewById(R.id.et_making_word_example2);
        mExampleEditText3 = (EditText)view.findViewById(R.id.et_making_word_example3);
        mExampleEditText4 = (EditText)view.findViewById(R.id.et_making_word_example4);
        mExampleRadio = (RadioGroup)view.findViewById(R.id.radio_example);
        mExampleRadio1 = (RadioButton)view.findViewById(R.id.radio_example1);
        mExampleRadio2 = (RadioButton)view.findViewById(R.id.radio_example2);
        mExampleRadio3 = (RadioButton)view.findViewById(R.id.radio_example3);
        mExampleRadio4 = (RadioButton)view.findViewById(R.id.radio_example4);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_making_word_next:
                if(!isNull(mProblemEditText) && !isNull(mExampleEditText1)
                        && !isNull(mExampleEditText2) && !isNull(mExampleEditText3)
                        && !isNull(mExampleEditText4)) {
                    getEnteredData();
                    listSize = problemList.size();

                    if (listSize == nowNum - 1)
                        problemList.add(new CreatedTestData(problem, example1, example2, example3, example4, correctNum));
                    else
                        problemList.set(nowNum - 1, new CreatedTestData(problem, example1, example2, example3, example4, correctNum));

                    Log.d(TAG,"problem :: " + problem);
                    if (nowNum == maxNum) {
                        checkExitDialog();
                        ((MainActivity) getActivity()).Backkey = true;
                    }
                    else {
                        changeToNextData();
                    }
                }
                else{
                    Toast.makeText(getActivity(), "빈칸이 존재합니다.", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.radio_example1:
                correctNum = 1;
                mExampleRadio1.setChecked(true);
                mExampleRadio2.setChecked(false);
                mExampleRadio3.setChecked(false);
                mExampleRadio4.setChecked(false);
                break;

            case R.id.radio_example2:
                correctNum = 2;
                mExampleRadio1.setChecked(false);
                mExampleRadio2.setChecked(true);
                mExampleRadio3.setChecked(false);
                mExampleRadio4.setChecked(false);
                break;

            case R.id.radio_example3:
                correctNum = 3;
                mExampleRadio1.setChecked(false);
                mExampleRadio2.setChecked(false);
                mExampleRadio3.setChecked(true);
                mExampleRadio4.setChecked(false);
                break;

            case R.id.radio_example4:
                correctNum = 4;
                mExampleRadio1.setChecked(false);
                mExampleRadio2.setChecked(false);
                mExampleRadio3.setChecked(false);
                mExampleRadio4.setChecked(true);
                break;
        }
    }

    private boolean isNull(TextView test) {
        if ( test.getText().toString().isEmpty() || test.getText().toString() == "")
            return true;
        return false;
    }

    private void changeToNextData() {
        nowNum++;
        mNowNumTextView.setText("" + nowNum);
        mProblemEditText.setText(null);
        mExampleEditText1.setText(null);
        mExampleEditText2.setText(null);
        mExampleEditText3.setText(null);
        mExampleEditText4.setText(null);

        problem = new String();
        example1 = new String();
        example2 = new String();
        example3 = new String();
        example4 = new String();
        correctNum = 1;

        mExampleRadio1.setChecked(true);
        mExampleRadio2.setChecked(false);
        mExampleRadio3.setChecked(false);
        mExampleRadio4.setChecked(false);
    }

    private void checkExitDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("출제를 완료했습니다.\n등록하시겠습니까?")
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK){
                            dialog.dismiss();
                            return true;
                        }
                        return false;
                    }
                })
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        sendingProblem();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                    }
                }).show();
    }

    private void sendingProblem() {
        toServer();

        if(mworker_problem.getSuccess()){
            Toast.makeText(getActivity(), "등록되었습니다.", Toast.LENGTH_SHORT).show();
            FragmentManager.BackStackEntry entry = getActivity().getSupportFragmentManager().getBackStackEntryAt(0);
            getActivity().getSupportFragmentManager().popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getActivity().getSupportFragmentManager().beginTransaction().commit();
        }
        else{
            Toast.makeText(getActivity(), "등록에 실패하였습니다. \n다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void toServer() {
        if (mworker_problem != null && mworker_problem.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_problem.interrupt();
        }
        mworker_problem = new workerProblemCreating(true, title, problemList);
        mworker_problem.start();
        try {
            mworker_problem.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getEnteredData() {
        problem = mProblemEditText.getText().toString();
        example1 = mExampleEditText1.getText().toString();
        example2 = mExampleEditText2.getText().toString();
        example3 = mExampleEditText3.getText().toString();
        example4 = mExampleEditText4.getText().toString();
        Log.d(TAG, "problem: " + problem);
    }
}
