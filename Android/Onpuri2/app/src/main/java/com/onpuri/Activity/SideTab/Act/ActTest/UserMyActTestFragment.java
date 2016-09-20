package com.onpuri.Activity.SideTab.Act.ActTest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.onpuri.Data.ActTestData;
import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-19.
 */
public class UserMyActTestFragment  extends Fragment{
    private static final String TAG = "UserMyActTestFragment" ;
    private static View view;

    private ListView mTestListView;
    private TextView mTitleTextView, mPercentTextView;

    private String title, percent, num;

    private workerActTest mWorker_testList;
    private ArrayList<ActTestData> mTestUserList;
    private MyActTestItemAdapter testArrayAdapter;
    private android.support.v4.app.FragmentManager mFragmentManager;

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
            view = inflater.inflate(R.layout.fragment_my_act_test, container, false);
        } catch (InflateException e) {}

        init(); //뷰 아이디 설정

        receiveBundleData(); //프래그먼트 바뀔때 받은 title, percent, id 받기

        mTitleTextView.setText(title);
        mPercentTextView.setText( percent + "%");

        receiveTestUserList(); //시험을 푼 유저리스트 서버로부터 받아 저장

        initListView(); //리스트뷰 설정

        return view;
    }

    private void receiveBundleData() {
        Bundle extra = getArguments();
        title = extra.getString("test_title", "null");
        percent = extra.getString("test_percent", "0");
        num = extra.getString("test_num", "null");
    }

    private void initListView() {
        testArrayAdapter = new MyActTestItemAdapter();
        mTestListView.setAdapter(testArrayAdapter);

        if( !mTestUserList.isEmpty() ) {
            for (int i = 0; i < mTestUserList.size(); i++) {
                testArrayAdapter.addItem(mTestUserList.get(i).getTestId().toString() + "님",
                        mTestUserList.get(i).getTestDate().toString(),
                        mTestUserList.get(i).getTestCorrect().toString());
            }
        }else{
            testArrayAdapter.addItem("지정된 응시자가 없습니다", "","");
        }

        //TestListUtils.setDynamicHeight(mTestListView);
    }

    private void receiveTestUserList() {
        mTestUserList = new ArrayList<ActTestData>();

        if (mWorker_testList != null && mWorker_testList.isAlive()) {  //이미 동작하고 있을 경우 중지
            mWorker_testList.interrupt();
        }
        mWorker_testList = new workerActTest(true, num);
        mWorker_testList.start();
        try {
            mWorker_testList.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mTestUserList = mWorker_testList.getmTestList();

    }

    public void init(){
        mFragmentManager = getActivity().getSupportFragmentManager();

        mTitleTextView = (TextView)view.findViewById(R.id.tv_act_test_item_title);
        mPercentTextView = (TextView)view.findViewById(R.id.tv_act_test_item_total_percent);
        mTestListView = (ListView)view.findViewById(R.id.listView1);

    }
}
