package com.onpuri.Activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.onpuri.R;
import com.onpuri.Thread.workerSearch;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
    private ImageButton btn_listen;
    private TextView tv_sen, tv_word, tv_word_title;
    private ListView list;

    String searchText;
    private workerSearch mworker_search;
    ArrayList<String> searchList = new ArrayList<String>();
    ArrayList<String> sentenceNumList = new ArrayList<String>();
    ArrayAdapter<String> Adapter;

    XmlPullParser xpp;
    String key="6VCLI3yWvTEYI8GqnDNO"; //Naver 개발자센터 검색 키
    String data;

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
        tv_word = (TextView)header.findViewById(R.id.tv_word_search);
        list = (ListView)view.findViewById(R.id.list_search_sen);
        btn_listen = (ImageButton)header.findViewById(R.id.btn_search_word_listen);
        tv_word_title = (TextView)header.findViewById(R.id.tv_search_word);
        loadData();

        tv_word_title.setText(searchText);

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
                String text = (String) tv_word_title.getText();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(text);
                } else {
                    ttsUnder20(text);
                }
            }
        });

        list.addHeaderView(header);
        list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, searchList) {
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

        searchWord();

        return view;
    }

    private void searchWord() {
        new Thread(){
            public void run(){

                // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
                String clientId = "6VCLI3yWvTEYI8GqnDNO";
                String clientSecret = "maNEdaKzXu";
                //encyc : 백과사전, book : 책
                try{
                    String text = URLEncoder.encode( searchText ,"UTF-8");
                    String apiURL = "https://openapi.naver.com/v1/search/encyc.xml?query="+ text +"&display=1&start=1";
                    URL url = new URL(apiURL);

                    URLConnection urlConn;
                    //url 연결
                    urlConn = url.openConnection();
                    urlConn.setRequestProperty("X-naver-Client-Id", clientId);
                    urlConn.setRequestProperty("X-naver-Client-Secret", clientSecret);

                    //파싱 - 팩토리 만들고 팩토리로 파서 생성 (풀 파서 사용)
                    XmlPullParserFactory factory;

                    factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput((new InputStreamReader(urlConn.getInputStream())));

                    int eventType = parser.getEventType();
                    String b = "";
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.END_DOCUMENT: // 문서의 끝
                                break;
                            case XmlPullParser.START_DOCUMENT:
                                b = b + " ";
                                Log.d(TAG, b);
                                break;
                            case XmlPullParser.START_TAG: {
                                Log.d(TAG, b);
                                String tag = parser.getName();
                                switch (tag) {
                                    case "description":
                                        if (b != null)
                                            b = b + (parser.nextText());

                                        break;
                                }
                                break;
                            }

                            case XmlPullParser.END_TAG: {
                                String tag = parser.getName();
                                if (tag.equals("item")) {
                                    b = b.replace("Naver Search Result", "");
                                    b = b.replace("." , "\n");

                                    final String finalB = b;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_word.setText(finalB);
                                        }
                                    });
                                    b = null;
                                }

                            }

                        }
                        eventType = parser.next();
                    }

                } catch (MalformedURLException e) {
                    Log.e(TAG, "MalformedURLException");
                    tv_word.setText("구현 예정입니다");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "UnsupportedEncodingException");
                    tv_word.setText("구현 예정입니다");
                } catch (IOException e) {
                    Log.e(TAG, "IOException");
                    tv_word.setText("구현 예정입니다");
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "XmlPullParserException");
                    tv_word.setText("구현 예정입니다");
                }
            }
        }.start();
    }

    public void loadData(){
        if (mworker_search != null && mworker_search.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_search.interrupt();
        }
        mworker_search = new workerSearch(true, searchText);
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
