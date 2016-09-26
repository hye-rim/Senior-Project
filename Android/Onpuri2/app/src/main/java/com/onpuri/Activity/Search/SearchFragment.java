package com.onpuri.Activity.Search;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.Home.Fragment.HomeSentenceFragment;
import com.onpuri.R;
import com.onpuri.Activity.Note.workerNote;
import com.onpuri.Activity.Note.workerNoteItemAdd;

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
    private ImageButton btn_listen, btn_note_add;
    private TextView tv_word, tv_word_title;
    private ListView list;

    String searchText, wordMean;
    private workerSearch mworker_search;
    private workerNote mworker_note;
    private workerNoteItemAdd mworker_item_add;

    ArrayList<String> searchList;
    ArrayList<String> sentenceNumList, sentenceIdList;
    private ArrayList<String> listNote, listNoteNum;
    private ViewPager viewPager;
    private int pos;

    private boolean isNullSen;
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
        viewPager = (ViewPager)getActivity().findViewById(R.id.viewpager);
        Bundle extra = getArguments();
        searchText = null;
        searchText = extra.getString("searchText");
        Log.e(TAG, "data : " + searchText);

        View header = inflater.inflate(R.layout.search_header, null, false);
        tv_word_title = (TextView)header.findViewById(R.id.tv_search_word);
        btn_listen = (ImageButton)header.findViewById(R.id.btn_search_word_listen);
        btn_note_add = (ImageButton)header.findViewById(R.id.btn_search_word_add);
        tv_word = (TextView)header.findViewById(R.id.tv_word_search);
        list = (ListView)view.findViewById(R.id.list_search_sen);

        loadSearchData();
        noteLoad();

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
        btn_note_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = listNote.toArray(new String[listNote.size()]);
                AlertDialog.Builder noteSelectDialog = new AlertDialog.Builder(getActivity());

                noteSelectDialog.setTitle("노트를 선택해 주세요")
                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                pos = index;

                                selectNote(listNoteNum.get(index));

                                dialog.cancel();
                            }
                        })
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if(keyCode == KeyEvent.KEYCODE_BACK){
                                    dialog.dismiss();
                                    return true;
                                }
                                return false;
                            }
                        });

                AlertDialog alert_dialog = noteSelectDialog.create();
                alert_dialog.show();

                // set defult select value
                alert_dialog.getListView().setItemChecked(pos, true);
            }
        });

        list.addHeaderView(header);
        list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, searchList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View viewList = super.getView(position, convertView, parent);
                if(!isNullSen) {
                    TextView textView = ((TextView) viewList.findViewById(android.R.id.text1));
                    Spannable str = new SpannableString(textView.getText());
                    int i = str.toString().indexOf(searchText);
                    str.setSpan(new ForegroundColorSpan(Color.parseColor("#E36153")), i, i + searchText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(str, TextView.BufferType.SPANNABLE);
                    textView.setHeight(140); // Height
                    textView.setMaxLines(2);
                }
                return viewList;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isNullSen) {
                    HomeSentenceFragment homeSentenceFragment = new HomeSentenceFragment();
                    FragmentManager fm = getActivity().getSupportFragmentManager();

                    int realPosition = position - 1;
                    Bundle args = new Bundle();
                    args.putString("sen", searchList.get(realPosition));
                    args.putString("sen_num", sentenceNumList.get(realPosition));
                    args.putString("id", sentenceIdList.get(realPosition));

                    homeSentenceFragment.setArguments(args);

                    fm.beginTransaction()
                            .replace(R.id.root_home, homeSentenceFragment)
                            .addToBackStack(null)
                            .commit();
                    fm.executePendingTransactions();

                    viewPager.setCurrentItem(0);
                }
            }
        });


        return view;
    }

    public void loadSearchData(){
        searchList = new ArrayList<String>();
        sentenceNumList = new ArrayList<String>();
        sentenceIdList = new ArrayList<String>();

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

        //단어
        if( mworker_search.getUserWord().isEmpty() == false ){
            wordMean = mworker_search.getUserWord();
            wordMean = wordMean.replaceAll("[?]", "\n"); //?일 때 줄바꿈
            tv_word.setText(wordMean);

        }else{
            tv_word.setText("  검색 결과가 없습니다..");
        }

        //문장
        int i = 0;
        if( mworker_search.getUserSentence().arrSentence != null) {
            isNullSen = false;
            while (i < mworker_search.getUserSentence().arrSentence.size()) {
                searchList.add(mworker_search.getUserSentence().arrSentence.get(i));
                sentenceNumList.add(mworker_search.getUserSentence().arrSentenceNum.get(i));
                sentenceIdList.add(mworker_search.getUserSentence().arrSentenceId.get(i));
                Log.d(TAG, mworker_search.getUserSentence().arrSentence.get(i));
                i++;
            }
        }else{
            isNullSen = true;
            searchList.add("검색결과가 없습니다..");
        }
    }

    private void noteLoad() {
        listNote = new ArrayList<String>();
        listNoteNum = new ArrayList<String>();

        if (mworker_note != null && mworker_note.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_note.interrupt();
        }
        mworker_note = new workerNote(true);
        mworker_note.start();
        try {
            mworker_note.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //문장 모음 리스트
        int i = 0;
        if(mworker_note.getNoteWord() != null){
            while( i < mworker_note.getNoteWord().size()){
                listNote.add( mworker_note.getNoteWord().get(i).toString() );
                Log.d(TAG, mworker_note.getNoteWord().get(i).toString());
                i++;
            }
            listNoteNum.addAll(mworker_note.getNoteWordNum());
        }

    }

    private void selectNote(String item) {
        String nameData = new String ("2+" + item + "+" + searchText);
        Log.d(TAG, "search : " + searchText);
        if (mworker_item_add != null && mworker_item_add.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_item_add.interrupt();
        }
        mworker_item_add = new workerNoteItemAdd(true, nameData, -2);
        mworker_item_add.start();
        try {
            mworker_item_add.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(mworker_item_add.getResult() == 1) {
            Toast.makeText(getActivity(), "추가되었습니다.", Toast.LENGTH_LONG).show();
        }else if( mworker_item_add.getResult() == 2){
            Toast.makeText(getActivity(), "목록에 이미 있습니다.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), "추가에 실패하였습니다.", Toast.LENGTH_LONG).show();
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
