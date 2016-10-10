package com.onpuri.Activity.Test.Creating;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.MainActivity;
import com.onpuri.Data.CreatedTestData;
import com.onpuri.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-19.
 */
public class TestMakingSenFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TestMakingSenFragment";
    private static final int SELECT_SENTENCE_CODE = 1996;

    private static View view;
    private FragmentManager fragmentManager;

    private TextView mTitleTextView, mNowNumTextView, mMaxNumTextView, mTempSelectSentence;
    private Button mNextButton, mProblemSelectButton, mCorrectCheckButton;
    private EditText mExampleEditText1, mExampleEditText2, mExampleEditText3, mExampleEditText4;
    private RadioButton mExampleRadio1, mExampleRadio2, mExampleRadio3, mExampleRadio4;
    private RadioGroup mExampleRadio;

    private RadioButton[] mExampleRadioList;
    private EditText[] mExampleEditTextList;

    private  String title, problem;
    private String example[];
    private int nowNum, maxNum, correctNum;

    private ArrayList<CreatedTestData> problemList;
    workerProblemCreating mworker_problem;

    private int listSize;

    private String[] selectSentence;
    private boolean isFirst = true;
    private String correct;

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
            view = inflater.inflate(R.layout.fragment_test_making_sen, container, false);
        } catch (InflateException e) {
        }

        initView(); //view 아이디 찾기
        initData(); //데이터 기본 설정

        mNextButton.setOnClickListener(this);
        mProblemSelectButton.setOnClickListener(this);
        mCorrectCheckButton.setOnClickListener(this);

        return view;
    }

    private void getArgumentsData() {
        if (getArguments() != null) { //클릭한 문장 출력
            title = getArguments().getString("test_title", "null");
            maxNum = getArguments().getInt("test_max_num", 1);
        }
    }



    private void initData() {
        fragmentManager = getActivity().getSupportFragmentManager();
        getArgumentsData();

        mTitleTextView.setText(title);
        mNowNumTextView.setText(""+nowNum);
        mMaxNumTextView.setText(""+maxNum);

        mExampleEditTextList = new EditText[4];
        mExampleRadioList = new RadioButton[4];

        mExampleEditTextList[0] = mExampleEditText1;
        mExampleEditTextList[1] = mExampleEditText2;
        mExampleEditTextList[2] = mExampleEditText3;
        mExampleEditTextList[3] = mExampleEditText4;

        mExampleRadioList[0] = mExampleRadio1;
        mExampleRadioList[1] = mExampleRadio2;
        mExampleRadioList[2] = mExampleRadio3;
        mExampleRadioList[3] = mExampleRadio4;

        for(int i = 0; i < 4; i++)
            mExampleRadioList[i].setEnabled(false);

        example = new String[4];

        if(isFirst) {
            nowNum = 1;
            problemList = new ArrayList<CreatedTestData>();
            correctNum = 1;

            correct = new String();
            selectSentence = new String[2];
            mProblemSelectButton.setVisibility(View.VISIBLE);
            mTempSelectSentence.setVisibility(View.GONE);

            isFirst = false;

        }else if(!isFirst){
            mProblemSelectButton.setVisibility(View.GONE);
            mTempSelectSentence.setVisibility(View.VISIBLE);
            mTempSelectSentence.setText(selectSentence[0]);
            correct = selectSentence[1];
            Log.d(TAG, "correct isFirst : " + correct);

            randomCorrect();

        }

        ((MainActivity)getActivity()).Backkey = false;
    }


    private void initView() {
        mCorrectCheckButton = (Button)view.findViewById(R.id.btn_correct_check);
        mNextButton  = (Button)view.findViewById(R.id.btn_making_sen_next);
        mTitleTextView = (TextView)view.findViewById(R.id.tv_making_sen_title);
        mNowNumTextView  = (TextView)view.findViewById(R.id.tv_making_sen_now);
        mMaxNumTextView  = (TextView)view.findViewById(R.id.tv_making_sen_max);

        mProblemSelectButton = (Button)view.findViewById(R.id.btn_making_sen_problem);
        mTempSelectSentence = (TextView)view.findViewById(R.id.tv_select_sentence);

        mExampleEditText1 = (EditText)view.findViewById(R.id.et_making_sen_example);
        mExampleEditText2 = (EditText)view.findViewById(R.id.et_making_sen_example2);
        mExampleEditText3 = (EditText)view.findViewById(R.id.et_making_sen_example3);
        mExampleEditText4 = (EditText)view.findViewById(R.id.et_making_sen_example4);
        mExampleRadio = (RadioGroup)view.findViewById(R.id.radio_sen_example);

        mExampleRadio1 = (RadioButton)view.findViewById(R.id.radio_sen_example1);
        mExampleRadio2 = (RadioButton)view.findViewById(R.id.radio_sen_example2);
        mExampleRadio3 = (RadioButton)view.findViewById(R.id.radio_sen_example3);
        mExampleRadio4 = (RadioButton)view.findViewById(R.id.radio_sen_example4);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_making_sen_next:
                exampleSet(correctNum-1);
                mExampleEditTextList[correctNum-1].setText(correct);

                Log.d(TAG, "mExampleEditTextList set : " + mExampleEditTextList[correctNum-1].getText().toString());
                if (!isNull(mTempSelectSentence) && !isNull(mExampleEditText1)
                        && !isNull(mExampleEditText2) && !isNull(mExampleEditText3)
                        && !isNull(mExampleEditText4)) {
                    getEnteredData(correctNum-1);

                    listSize = problemList.size();

                    example[correctNum-1] = correct;
                    Log.d(TAG, "mExampleEditTextList set : " + example[correctNum-1].toString());

                    if (listSize == nowNum - 1)
                        problemList.add(new CreatedTestData(problem, example[0], example[1], example[2], example[3], correctNum));
                    else
                        problemList.set(nowNum - 1, new CreatedTestData(problem, example[0], example[1], example[2], example[3], correctNum));

                    if (nowNum == maxNum)
                        checkExitDialog();
                    else {
                        changeToNextData();
                    }
                } else {
                    Toast.makeText(getActivity(), "빈칸이 존재합니다.", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_making_sen_problem:
                isFirst = false;

                final TestSelectSentenceFragment testSelectSentenceFragment = new TestSelectSentenceFragment();
                testSelectSentenceFragment.setTargetFragment(this, SELECT_SENTENCE_CODE);
                fragmentManager.beginTransaction()
                        .replace(R.id.root_test, testSelectSentenceFragment)
                        .addToBackStack("select_sen")
                        .commit();

                break;

            case R.id.btn_correct_check:
                if(!correct.isEmpty()) {
                    randomCorrect();
                    exampleSet(correctNum - 1);
                    mExampleEditTextList[correctNum - 1].setText(correct);
                }else{
                    Toast.makeText(getActivity(), "문제를 선택하세요.", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    private void exampleSet(int index) {
        for(int i = 0; i < 4; i ++){
            if(i == index) {
                Log.d(TAG, "correct set : " + correct);
                mExampleEditTextList[i].setText(correct);
                mExampleEditTextList[i].setEnabled(false);
                mExampleRadioList[i].setChecked(true);
                mExampleRadioList[i].setEnabled(false);
            }
            else {
                mExampleEditTextList[i].setEnabled(true);
                mExampleRadioList[i].setChecked(false);
                mExampleRadioList[i].setEnabled(false);
            }
        }

        Log.d(TAG, "mExampleEditTextList set : " + mExampleEditTextList[index].getText().toString());
    }


    private boolean isNull(TextView test) {
        if ( test.getText().toString().isEmpty() || test.getText().toString().compareTo("") == 0)
            return true;
        return false;
    }

    private void changeToNextData() {
        nowNum++;
        mNowNumTextView.setText("" + nowNum);
        correct = new String();

        //mProblemEditText.setText(null);

        mExampleEditText1.setText(null);
        mExampleEditText2.setText(null);
        mExampleEditText3.setText(null);
        mExampleEditText4.setText(null);

        problem = new String();
        for(int i = 0; i< 4; i++)
            example[i] = new String();
        correctNum = 1;

        mExampleRadio1.setChecked(true);
        mExampleRadio2.setChecked(false);
        mExampleRadio3.setChecked(false);
        mExampleRadio4.setChecked(false);

        selectSentence = new String[2];
        mProblemSelectButton.setVisibility(View.VISIBLE);
        mTempSelectSentence.setVisibility(View.GONE);
        mTempSelectSentence.setText("");

        for(int i = 0; i < 4; i ++){
            mExampleEditTextList[i].setEnabled(true);
            mExampleRadioList[i].setEnabled(true);
        }
    }

    private void randomCorrect() {
        double randomValue = Math.random();
        int random = (int)(randomValue * 3 ) + 0;
        Log.d(TAG, "random : " + random);

        correctNum = random+1;

        exampleSet(random); //0~3
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
        ((MainActivity)getActivity()).Backkey = true;


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

    private void getEnteredData(int correctNum) {
        problem = mTempSelectSentence.getText().toString();
        for(int i = 0; i < 4; i++ ){
            if(correctNum == i){

            }
            else{
                example[i] = mExampleEditTextList[i].getText().toString();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_SENTENCE_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                selectSentence = data.getStringArrayExtra("select_sentence");
                if(selectSentence != null) {
                    Log.v(TAG, "Data passed " + selectSentence[1] + ", " + selectSentence[0]);
                }

                mProblemSelectButton.setVisibility(View.GONE);
                mTempSelectSentence.setVisibility(View.VISIBLE);

                selectSentence[0] = selectSentence[0].replace(selectSentence[1], "______");
                correct = selectSentence[1];
                mTempSelectSentence.setText(""+selectSentence);

            }else{
                mProblemSelectButton.setVisibility(View.VISIBLE);
                mTempSelectSentence.setVisibility(View.GONE);
            }

            Log.d(TAG, "correct passed : " + correct);
        }
    }
}
