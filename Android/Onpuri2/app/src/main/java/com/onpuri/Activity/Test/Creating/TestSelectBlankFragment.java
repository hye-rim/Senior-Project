package com.onpuri.Activity.Test.Creating;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kutemsys on 2016-09-21.
 */
public class TestSelectBlankFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "TestSelectBlankFragment" ;
    private static View view;

    private ListView mSentenceListView;
    private TextView mSelectSentenceTextView;

    private ArrayList<String> mSelectBlankList;
    private ArrayAdapter allSentenceArrayAdapter;
    private android.support.v4.app.FragmentManager mFragmentManager;

    private String select;
    private String sentence;

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
            view = inflater.inflate(R.layout.fragment_test_select_blank, container, false);
        } catch (InflateException e) {}

        init();

        changeSentenceList(); //string 변환하여 저장
        initListView();

        return view;
    }

    public void init(){
        mFragmentManager = getActivity().getSupportFragmentManager();
        getArgumentsData();

        mSentenceListView = (ListView)view.findViewById(R.id.listView);
        mSelectSentenceTextView = (TextView)view.findViewById(R.id.tv_blank_select_sentence);

        mSelectBlankList = new ArrayList<String>();

        mSelectSentenceTextView.setText(sentence);

    }

    private void getArgumentsData() {
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("select_sentence", "null");
        }
    }

    private void initListView() {
        allSentenceArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_checked, mSelectBlankList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView)view.findViewById(android.R.id.text1);
                textView.setTextSize(15);
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
            select = mSelectBlankList.get(position).toString();
            Log.d(TAG, mSelectBlankList.get(position));
            changeLayout();
        }
    }

    private void changeLayout() {
        selectSentence = new String[2];
        selectSentence[0] = sentence;
        selectSentence[1] = select;

        Intent intent = new Intent();
        intent.putExtra("select_blank", selectSentence);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        getFragmentManager().popBackStack();

        Log.d(TAG, select);
    }


    private void changeSentenceList() {
        String[] arStrRegexMultiSpace = sentence.split("\\s+");
        Collections.addAll(mSelectBlankList, arStrRegexMultiSpace);
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
