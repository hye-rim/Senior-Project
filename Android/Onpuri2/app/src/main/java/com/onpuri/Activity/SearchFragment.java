package com.onpuri.Activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;
import com.onpuri.Thread.WorkerSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by kutemsys on 2016-08-01.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment" ;
    private static View view;
    private TextToSpeech tts;
    private Button btn_listen;
    private TextView tv_sen;
    private ListView list;

    String searchText;
    private WorkerSearch mworker_search;
    ArrayList<String> searchList = new ArrayList<String>();
    ArrayList<String> sentenceNumList = new ArrayList<String>();
    ArrayAdapter<String> Adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_search, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        Bundle extra = getArguments();
        searchText = null;
        searchText = extra.getString("searchText");
        Log.e(TAG, "data : " + searchText);

        View header = inflater.inflate(R.layout.search_header, null, false);
        list = (ListView)view.findViewById(R.id.list_search_sen);
        btn_listen = (Button)header.findViewById(R.id.btn_search_word_listen);
        loadData();

        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        btn_listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "word";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(text);
                } else {
                    ttsUnder20(text);
                }
            }
        });

        list.addHeaderView(header);
        list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, searchList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View viewList = super.getView(position, convertView, parent);
                TextView textView = ((TextView) viewList.findViewById(android.R.id.text1));
                textView.setHeight(140); // Height
                textView.setMaxLines(2);
                return viewList;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomeSentenceFragment homeSentenceFragment = new HomeSentenceFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();

                Bundle args = new Bundle();
                args.putString("sen", searchList.get(position));
                args.putString("sen_num", sentenceNumList.get(position));
                homeSentenceFragment.setArguments(args);

                fm.beginTransaction()
                        .replace(R.id.root_home, homeSentenceFragment)
                        .addToBackStack(null)
                        .commit();
                fm.executePendingTransactions();
            }
        });


        return view;
    }

    public void loadData(){
        if (mworker_search != null && mworker_search.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_search.interrupt();
        }
        mworker_search = new WorkerSearch(true, searchText);
        mworker_search.start();
        try {
            mworker_search.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int i = 0;
        while( i < mworker_search.getUserSentence().arrSentence.size()) {
            searchList.add(mworker_search.getUserSentence().arrSentence.get(i));
            sentenceNumList.add(mworker_search.getUserSentence().arrSentenceNum.get(i));
            Log.d(TAG, mworker_search.getUserSentence().arrSentence.get(i));
            i++;
        }
    }

    public void onPause(){
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}
