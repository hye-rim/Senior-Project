package com.onpuri.Activity.Test.Creating;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestSelectObjectFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "TestSelectObject" ;
    private static View view;

    private ListView mListView1, mListView2;
    private Button mOkButton, mCancelButton;

    private workerReceiveUserList mWorker_userList;
    private List<Map<String, String>> userList;
    private ArrayList<String> mSelectUserList, mUserList;
    private ArrayAdapter selectArrayAdapter, allArrayAdapter;
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
            view = inflater.inflate(R.layout.fragment_test_select_object, container, false);
        } catch (InflateException e) {}

        init();

        mOkButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        receiveUserList(); //유저리스트 서버로부터 받아 저장
        initListView();

        return view;
    }

    public void init(){
        mFragmentManager = getActivity().getSupportFragmentManager();

        mListView1 = (ListView)view.findViewById(R.id.listView1);
        mListView2 = (ListView)view.findViewById(R.id.listView2);
        mOkButton = (Button)view.findViewById(R.id.btn_test_select_ok);
        mCancelButton = (Button)view.findViewById(R.id.btn_test_select_cancel);

        userList = new ArrayList<Map<String, String>>();
        mSelectUserList = new ArrayList<String>();
        mUserList = new ArrayList<String>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_test_select_ok:
                if(checkNull()){
                    Toast.makeText(getActivity(), "지정된 응시자가 없습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Log.d(TAG, "select object list : " + mSelectUserList);
                    Intent intent = new Intent();
                    intent.putExtra("select_object", mSelectUserList);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    getFragmentManager().popBackStack();
                }
                break;

            case R.id.btn_test_select_cancel:
                mFragmentManager.popBackStack();
                mFragmentManager.beginTransaction()
                        .commit();
                break;
        }

    }

    private boolean checkNull() {
        if(mSelectUserList.isEmpty()){
            return true;
        }
        return false;
    }

    private void initListView() {
        selectArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_checked, mSelectUserList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView)view.findViewById(android.R.id.text1);
                textView.setTextSize(15);
                return view;
            }
        };
        mListView1.setAdapter(selectArrayAdapter);
        mListView1.setOnItemClickListener(this);
        mListView1.setChoiceMode(mListView1.CHOICE_MODE_SINGLE);


        allArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_checked, mUserList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView)view.findViewById(android.R.id.text1);
                textView.setTextSize(15);
                return view;
            }
        };
        mListView2.setAdapter(allArrayAdapter);
        mListView2.setOnItemClickListener(this);
        mListView2.setChoiceMode(mListView2.CHOICE_MODE_MULTIPLE);

        ListUtils.setDynamicHeight(mListView1);
        ListUtils.setDynamicHeight(mListView2);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mListView1 == parent){
            CheckedTextView item = (CheckedTextView) view;

            if(item.isChecked()) {
                mSelectUserList.remove(position);
                selectArrayAdapter.notifyDataSetChanged();
                ListUtils.setDynamicHeight(mListView1);
                item.setChecked(false);
            }
        }
        else if(mListView2 == parent){
            CheckedTextView item = (CheckedTextView) view;

            if(item.isChecked()) {
                String selectUser =  mUserList.get(position).toString();
                if( !mSelectUserList.contains(selectUser)){
                    mSelectUserList.add( mUserList.get(position).toString() );
                    selectArrayAdapter.notifyDataSetChanged();
                    ListUtils.setDynamicHeight(mListView1);
                }else{
                    Toast.makeText(getActivity(), "이미 추가한 응시자 입니다.", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }


    private void receiveUserList() {
        toServer();

        userList = mWorker_userList.getUserList();
        mUserList = mWorker_userList.getmUserList();

    }

    private void toServer() {
        if (mWorker_userList != null && mWorker_userList.isAlive()) {  //이미 동작하고 있을 경우 중지
            mWorker_userList.interrupt();
        }
        mWorker_userList = new workerReceiveUserList(true);
        mWorker_userList.start();
        try {
            mWorker_userList.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static class ListUtils {
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
