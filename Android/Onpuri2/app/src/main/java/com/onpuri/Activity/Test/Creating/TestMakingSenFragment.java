package com.onpuri.Activity.Test.Creating;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.onpuri.Data.CreatedTestData;
import com.onpuri.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-19.
 */
public class TestMakingSenFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TestMakingSenFragment";

    private static View view;

    private TextView mTitleTextView, mNowNumTextView, mMaxNumTextView;
    private Button mNextButton, mBackButton, mProblemSelectButton;
    private EditText mExampleEditText1, mExampleEditText2, mExampleEditText3, mExampleEditText4;
    private RadioButton mExampleRadio1, mExampleRadio2, mExampleRadio3, mExampleRadio4;
    private RadioGroup mExampleRadio;

    private RadioButton[] mExampleRadioList;
    private EditText[] mExampleEditTextList;

    private  String title, problem, example1, example2, example3, example4;
    private int nowNum, maxNum, correctNum;

    private ArrayList<CreatedTestData> problemList;
    workerProblemCreating mworker_problem;

    private int listSize;

    String[] tempProblem;
    String[] tempCorrect;

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
        mBackButton.setOnClickListener(this);

        for(int i = 0; i < 4; i++)
            mExampleRadioList[i].setEnabled(false);

        randomCorrect();

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

        tempProblem = new String[4];
        tempCorrect = new String[4];
        mExampleEditTextList = new EditText[4];
        mExampleRadioList = new RadioButton[4];

        tempProblem[0] = new String("An executive is someone who is employed by a business at a senior @.");
        tempProblem[1] = new String("Executives decide what the @ should do, and ensure that it is done.");
        tempProblem[2] = new String("Leaders also began a 10-day meeting in Bonn, Germany on Monday to follow up on the @ and to work out just how to make these targets achievable.");
        tempProblem[3] = new String("Scientists and leaders have agreed that global @ gas emissions will need to peak soon and be followed by quick reductions over the years ahead to contain temperature rises. ");

        tempCorrect[0] = new String("level");
        tempCorrect[1] = new String("business");
        tempCorrect[2] = new String("agreement");
        tempCorrect[3] = new String("greenhouse");

        mExampleEditTextList[0] = mExampleEditText1;
        mExampleEditTextList[1] = mExampleEditText2;
        mExampleEditTextList[2] = mExampleEditText3;
        mExampleEditTextList[3] = mExampleEditText4;

        mExampleRadioList[0] = mExampleRadio1;
        mExampleRadioList[1] = mExampleRadio2;
        mExampleRadioList[2] = mExampleRadio3;
        mExampleRadioList[3] = mExampleRadio4;
    }


    private void initView() {
        mNextButton  = (Button)view.findViewById(R.id.btn_making_sen_next);
        mBackButton  = (Button)view.findViewById(R.id.btn_making_sen_back);
        mTitleTextView = (TextView)view.findViewById(R.id.tv_making_sen_title);
        mNowNumTextView  = (TextView)view.findViewById(R.id.tv_making_sen_now);
        mMaxNumTextView  = (TextView)view.findViewById(R.id.tv_making_sen_max);

        mProblemSelectButton = (Button)view.findViewById(R.id.btn_making_sen_problem);

        mExampleEditText1 = (EditText)view.findViewById(R.id.et_making_sen_example1);
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
                Log.d(TAG,"여기?");
                if (/*!isNull(mProblemEditText) &&*/ !isNull(mExampleEditText1)
                        && !isNull(mExampleEditText2) && !isNull(mExampleEditText3)
                        && !isNull(mExampleEditText4)) {
                    getEnteredData();
                    Log.d(TAG,"여기?");
                    listSize = problemList.size();
                    Log.d(TAG,"여기?");
                    if (listSize == nowNum - 1)
                        problemList.add(new CreatedTestData(problem, example1, example2, example3, example4, correctNum));
                    else
                        problemList.set(nowNum - 1, new CreatedTestData(problem, example1, example2, example3, example4, correctNum));
                    Log.d(TAG,"여기?");
                    if (nowNum == maxNum)
                        checkExitDialog();
                    else {
                        changeToNextData();
                    }
                } else {
                    Toast.makeText(getActivity(), "빈칸이 존재합니다.", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_making_sen_back:
                if (nowNum != 1)
                    changeToBackData();
                break;
        }
    }

    private void exampleSet(int index) {
        for(int i = 0; i < 4; i ++){
            if(i == index) {
                mExampleEditTextList[i].setText(tempCorrect[i]);
                mExampleEditTextList[i].setEnabled(false);

                mExampleRadioList[i].setChecked(true);
            }
            else {
                mExampleEditTextList[i].setEnabled(true);
                mExampleRadioList[i].setChecked(false);
            }
        }
    }


    private boolean isNull(TextView test) {
        if ( test.getText().toString().isEmpty() || test.getText().toString().compareTo("") == 0)
            return true;
        return false;
    }

    private void changeToBackData() {
        nowNum--;
        mNowNumTextView.setText("" + nowNum);

        problem = problemList.get(nowNum-1).getProblem();
        example1 = problemList.get(nowNum-1).getExample1();
        example2 = problemList.get(nowNum-1).getExample2();
        example3 = problemList.get(nowNum-1).getExample3();
        example4 = problemList.get(nowNum-1).getExample4();
        correctNum = problemList.get(nowNum-1).getCorrectNum();

        //mProblemEditText.setText(problem);
        mExampleEditText1.setText(example1);
        mExampleEditText2.setText(example2);
        mExampleEditText3.setText(example3);
        mExampleEditText4.setText(example4);

        exampleSet(nowNum-1);

    }

    private void changeToNextData() {
        nowNum++;
        mNowNumTextView.setText("" + nowNum);

        //mProblemEditText.setText(null);

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

        randomCorrect(); //랜덤하게 답위치 지정
        /*
        mExampleRadio1.setChecked(true);
        mExampleRadio2.setChecked(false);
        mExampleRadio3.setChecked(false);
        mExampleRadio4.setChecked(false);
        */
    }

    private void randomCorrect() {
        double randomValue = Math.random();
        int random = (int)(randomValue * 3 ) + 0;
        Log.d(TAG, "random : " + random);

        exampleSet(random); //0~3
        correctNum = random+1; //1~4

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
        //problem = mProblemEditText.getText().toString();
        problem = tempProblem[nowNum-1];
        example1 = mExampleEditText1.getText().toString();
        example2 = mExampleEditText2.getText().toString();
        example3 = mExampleEditText3.getText().toString();
        example4 = mExampleEditText4.getText().toString();
    }
}
