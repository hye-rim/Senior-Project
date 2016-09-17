package com.onpuri.Activity.Test.Creating;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.hrules.horizontalnumberpicker.HorizontalNumberPickerListener;
import com.onpuri.Activity.Note.workerNoteChanges;
import com.onpuri.Activity.Test.Solving.TestSolveStartFrgment;
import com.onpuri.Adapter.TestListAdapter;
import com.onpuri.Listener.HomeItemClickListener;
import com.onpuri.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-11.
 */
public class TestCreateFragment extends Fragment implements View.OnClickListener, HorizontalNumberPickerListener {
    private static final String TAG = "TestCreateFragment" ;
    private static final int SEN = 2;
    private static final int WORD = 1;

    private HorizontalNumberPicker horizontalNumberPicker1;
    private RadioButton mEveryRadio, mSelectRadio;
    private TextView mSelectListTextView, mTestTitleTextView;
    private Button mCreatingButton, mCancelButton;

    private static View view;

    workerTestCreating mworker_test;
    FragmentManager fragmentManager;

    private int kinds, problemNum, objectNum;
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
            view = inflater.inflate(R.layout.fragment_test_create, container, false);
        } catch (InflateException e) {}

        init();

        //click listener 설정
        mEveryRadio.setOnClickListener(this);
        mSelectRadio.setOnClickListener(this);
        horizontalNumberPicker1.setListener(this);

        return view;
    }

    public void init(){
        fragmentManager = getActivity().getSupportFragmentManager();

        //view 아이디
        mTestTitleTextView = (TextView)view.findViewById(R.id.et_test_title);
        mEveryRadio = (RadioButton)view. findViewById(R.id.radio_every);
        mSelectRadio = (RadioButton)view. findViewById(R.id.radio_select);
        mSelectListTextView = (TextView)view. findViewById(R.id.select_list);
        horizontalNumberPicker1 = (HorizontalNumberPicker)view.findViewById(R.id.horizontal_number_picker1);
        mCreatingButton = (Button)view.findViewById(R.id.btn_creating);
        mCancelButton = (Button)view.findViewById(R.id.btn_creating_cancel);

        //응시자 수 250으로 제한
        //horizontal number picker 기본값, 디자인 설정
        horizontalNumberPicker1.setMinValue(1);
        horizontalNumberPicker1.setMaxValue(20);
        horizontalNumberPicker1.getTextValueView()
                .setTextColor(getResources().getColor(android.R.color.black));
        horizontalNumberPicker1.getButtonMinusView()
                .setTextColor(getResources().getColor(android.R.color.black));
        horizontalNumberPicker1.getButtonPlusView()
                .setTextColor(getResources().getColor(android.R.color.black));

        horizontalNumberPicker1.getTextValueView().setTextSize(20);
        horizontalNumberPicker1.getButtonMinusView().setTextSize(16);
        horizontalNumberPicker1.getButtonPlusView().setTextSize(20);

        mCreatingButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_word:
                kinds = WORD;
                break;

            case R.id.radio_sen:
                kinds = SEN;
                break;

            case R.id.radio_every:
                mSelectListTextView.setVisibility(View.GONE);
                objectNum = 0;
                break;

            case R.id.radio_select:
                mSelectListTextView.setVisibility(View.VISIBLE);
                mSelectListTextView.setText("지정 하기");
                final TestSelectObjectFragment testSelectObjectFragment = new TestSelectObjectFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.root_test, testSelectObjectFragment)
                        .addToBackStack(null)
                        .commit();

                objectNum = 0; //임시값
                break;

            case R.id.btn_creating:
                createTest();
                break;

            case R.id.btn_creating_cancel:
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().commit();
                break;

        }
    }

    private void createTest() {
        String testInfo = new String (mTestTitleTextView.getText().toString() + "+" + kinds + "+" + problemNum + "+" + objectNum);
        Log.d(TAG, testInfo);

        toServer(testInfo);

        if(mworker_test.getSuccess()) {
            moveMakingProblem();
        }else{
            Toast.makeText(getActivity(), "추가에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }

    }

    private void moveMakingProblem() {
        final TestMakingProblemFragment testMakingProblemFragment = new TestMakingProblemFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.root_test, testMakingProblemFragment)
                .addToBackStack(null)
                .commit();

    }


    private void toServer(String data) {
        if (mworker_test != null && mworker_test.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_test.interrupt();
        }
        mworker_test = new workerTestCreating(true, data);
        mworker_test.start();
        try {
            mworker_test.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker, int value) {
        if (horizontalNumberPicker.getId() == R.id.horizontal_number_picker1) {
            Log.d(TAG, "problem num current value => " + value);
            problemNum = value;
        }
    }
}

