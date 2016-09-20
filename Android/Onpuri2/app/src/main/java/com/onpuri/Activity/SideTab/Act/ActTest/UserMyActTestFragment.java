package com.onpuri.Activity.SideTab.Act.ActTest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
    private ArrayAdapter testArrayAdapter;
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
        mPercentTextView.setText(percent + "%");
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
        testArrayAdapter = new ArrayAdapter<ActTestData>(this.getContext(), android.R.layout.simple_list_item_1, mTestUserList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView)view.findViewById(android.R.id.text1);
                textView.setTextSize(15);
                return view;
            }
        };
        mTestListView.setAdapter(testArrayAdapter);

        TestListUtils.setDynamicHeight(mTestListView);
    }

    private void receiveTestUserList() {
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
        mPercentTextView = (TextView)view.findViewById(R.id.tv_act_test_item_percent);
        mTestListView = (ListView)view.findViewById(R.id.listView1);

        mTestUserList = new ArrayList<ActTestData>();
    }

    public static class TestListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
}
