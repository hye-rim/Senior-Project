package com.onpuri.Activity.Test.Creating;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.onpuri.R;
import com.onpuri.Server.PacketUser;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-21.
 */
public class TestSelectSentenceFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "TestSelectSenFragment" ;
    private static final int SELECT_BLANK_CODE = 1997;
    private static View view;

    private ListView mSentenceListView;

    private workerSelectSentenceList mWorker_sentenceList;
    private ArrayList<String> mSentenceList;
    private ArrayAdapter allSentenceArrayAdapter;
    private android.support.v4.app.FragmentManager mFragmentManager;

    private PacketUser sentence;
    private int sentenceNum;

    private String[] selectSentence;
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
            view = inflater.inflate(R.layout.fragment_test_select_sentence, container, false);
        } catch (InflateException e) {}

        init();

        receiveSentenceList(); //유저리스트 서버로부터 받아 저장
        initListView();

        return view;
    }

    public void init(){
        mFragmentManager = getActivity().getSupportFragmentManager();

        mSentenceListView = (ListView)view.findViewById(R.id.listView);

        mSentenceList = new ArrayList<String>();

        sentence = new PacketUser();
        sentenceNum = 0;

        selectSentence = new String[2];
    }

    private void initListView() {
        allSentenceArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_checked, mSentenceList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView)view.findViewById(android.R.id.text1);
                textView.setTextSize(15);
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);

                return view;
            }
        };
        mSentenceListView.setAdapter(allSentenceArrayAdapter);
        mSentenceListView.setOnItemClickListener(this);
        mSentenceListView.setChoiceMode(mSentenceListView.CHOICE_MODE_SINGLE);

        ListUtils.setDynamicHeight(mSentenceListView);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mSentenceListView == parent){
            CheckedTextView item = (CheckedTextView) view;

            selectSentence[0] = mSentenceList.get(position).toString();
            Log.d(TAG, mSentenceList.get(position));
            changeLayout();
        }
    }

    private void changeLayout() {
        final TestSelectBlankFragment testSelectBlankFragment = new TestSelectBlankFragment();
        testSelectBlankFragment.setTargetFragment(this, SELECT_BLANK_CODE);

        Bundle args = new Bundle();
        args.putString("select_sentence", selectSentence[0]);
        testSelectBlankFragment.setArguments(args);

        mFragmentManager.beginTransaction()
                .replace(R.id.root_test, testSelectBlankFragment)
                .addToBackStack("select_sen")
                .commit();

        Log.d(TAG, selectSentence[0]);
    }

    private void receiveSentenceList() {
        toServer();

        mSentenceList = mWorker_sentenceList.getUserSentence().arrSentence;
        sentenceNum = mWorker_sentenceList.getSentence_num();
    }

    private void toServer() {
        if (mWorker_sentenceList != null && mWorker_sentenceList.isAlive()) {  //이미 동작하고 있을 경우 중지
            mWorker_sentenceList.interrupt();
        }
        mWorker_sentenceList = new workerSelectSentenceList(true, sentence, sentenceNum );
        mWorker_sentenceList.start();
        try {
            mWorker_sentenceList.join();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_BLANK_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                selectSentence = new String[2];
                selectSentence = data.getStringArrayExtra("select_blank");
                if(selectSentence[1] != null) {
                    Log.v(TAG, "Data passed from Child fragment = " + selectSentence[1]);
                }
                moveProblem();
            }
        }
    }

    private void moveProblem(){
        Intent intent = new Intent();
        intent.putExtra("select_sentence", selectSentence);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        getFragmentManager().popBackStack();

        Log.d(TAG, selectSentence[1] + "," + selectSentence[0]);
    }

}
