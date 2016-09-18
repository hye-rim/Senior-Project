package com.onpuri.Activity.Test.Creating;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
    private static final int SELECT_OBJECT_CODE = 1995;
    private static final int WORD = 1;
    private static final int SEN = 2;

    private HorizontalNumberPicker horizontalNumberPicker1;
    private RadioButton mEveryRadio, mSelectRadio;
    private TextView mSelectListTextView, mTestTitleTextView;
    private Button mCreatingButton, mCancelButton;

    private static View view;

    workerTestCreating mworker_test;
    FragmentManager fragmentManager;

    private boolean isFirst = true;

    private int kinds, problemNum, objectNum;
    private ArrayList<String> selectObjectList;

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
        mCreatingButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        if(!isFirst){
            String selectObjectString = "";
            for(int i = 0; i < selectObjectList.size(); i++){
                selectObjectString = selectObjectString.concat(selectObjectList.get(i));
                if( i < selectObjectList.size()-1)
                    selectObjectString = selectObjectString.concat(", ");
            }

            mSelectListTextView.setVisibility(View.VISIBLE);
            mSelectListTextView.setText(selectObjectString);
            Log.v(TAG, selectObjectString + ", objectNum : " + objectNum);
        }
        Log.v(TAG, "objectNum : " + objectNum);

        return view;
    }

    public void init(){
        fragmentManager = getActivity().getSupportFragmentManager();
        if(isFirst) {
            kinds = WORD;
            problemNum = 1;
            objectNum = 0;
            selectObjectList = new ArrayList<String>();
        }

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
        horizontalNumberPicker1.getButtonMinusView().setTextSize(13);
        horizontalNumberPicker1.getButtonPlusView().setTextSize(20);
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
                isFirst = false;

                mSelectListTextView.setVisibility(View.VISIBLE);
                final TestSelectObjectFragment testSelectObjectFragment = new TestSelectObjectFragment();
                testSelectObjectFragment.setTargetFragment(this, SELECT_OBJECT_CODE);
                fragmentManager.beginTransaction()
                        .replace(R.id.root_test, testSelectObjectFragment)
                        .addToBackStack(null)
                        .commit();

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
        selectObjectList.add( 0, mTestTitleTextView.getText().toString() );
        for(int i = 1; i < selectObjectList.size(); i++){
            String str = selectObjectList.get(i).toString();
            //ID + 이름 => ID, 이름
            int plus = str.indexOf('+');
            str = str.substring(0,plus); //ID
            Log.d(TAG, "ID : " + str);

            selectObjectList.set(i, str);
        }
        Log.d(TAG, "selectObjectList ===>" + selectObjectList);
        String testInfo = new String (selectObjectList.get(0) + "+" + kinds + "+" + problemNum + "+" + objectNum);
        Log.d(TAG, testInfo);

        toServer(testInfo);

        if(objectNum == 0){
            if (mworker_test.getSuccessInfo()) {
                moveMakingProblem();
            }
            else {
                Toast.makeText(getActivity(), "시험 출제를 실패하였습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            if (mworker_test.getSuccessInfo() && mworker_test.getSuccessUser()) {
                moveMakingProblem();
            }
            else {
                Toast.makeText(getActivity(), "시험 출제를 실패하였습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void moveMakingProblem() {
        switch (kinds){
            case WORD:
                final TestMakingWordFragment testMakingWordFragment = new TestMakingWordFragment();

                Bundle args = new Bundle();
                args.putString("test_title", selectObjectList.get(0));
                args.putInt("test_max_num", problemNum);
                testMakingWordFragment.setArguments(args);

                fragmentManager.beginTransaction()
                        .replace(R.id.root_test, testMakingWordFragment)
                        .addToBackStack(null)
                        .commit();

                break;

            case SEN:
                final TestMakingProblemFragment testMakingProblemFragment = new TestMakingProblemFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.root_test, testMakingProblemFragment)
                        .addToBackStack(null)
                        .commit();

                break;
        }

    }


    private void toServer(String data) {
        if (mworker_test != null && mworker_test.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_test.interrupt();
        }
        mworker_test = new workerTestCreating(true, data, objectNum, selectObjectList);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_OBJECT_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                selectObjectList.addAll( (ArrayList<String>) data.getSerializableExtra("select_object") );
                if(selectObjectList != null) {
                    Log.v(TAG, "Data passed from Child fragment = " + selectObjectList);
                    objectNum = selectObjectList.size();

                }
            }
        }
    }
}

