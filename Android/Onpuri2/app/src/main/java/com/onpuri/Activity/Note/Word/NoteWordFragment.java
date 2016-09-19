package com.onpuri.Activity.Note.Word;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.Search.SearchFragment;
import com.onpuri.Data.WordData;
import com.onpuri.DividerItemDecoration;
import com.onpuri.Listener.RecyclerItemClickListener;
import com.onpuri.R;
import com.onpuri.Activity.Note.workerNoteLoad;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_light;

/**
 * Created by HYERIM on 2016-07-11.
 */
public class NoteWordFragment  extends Fragment implements View.OnClickListener {
    private static final String TAG = "NoteWordFragment";
    private static View view;

    ArrayList<WordData> itemWord;

    private RecyclerView mRecyclerWordItem;
    private TextView tvItemName;
    private Button btn_listen,btn_edit;

    protected RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWordAdapter;
    private Context context;

    String itemName;

    private FrameLayout mItemFrame;
    private FragmentManager mFragmentManager;
    private Boolean isEdit = false;
    private workerNoteLoad mworker_note;
    private boolean isNullWord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_note_word, container, false);
        } catch (InflateException e) {
            //map is already there, just return view as it is
        }
        isNullWord = false;
        mFragmentManager = getFragmentManager();

        tvItemName = (TextView)view.findViewById(R.id.note_word_name);
        if (getArguments() != null) {   //클릭한 단어이름 저장
            itemName = getArguments().getString("wordItemName");
            tvItemName.setText(itemName);
        }


        initData();
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_light);

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerWordItem = (RecyclerView) view.findViewById(R.id.recycle_note_word);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mWordAdapter = new NoteWordItemAdapter(itemWord, isEdit, context);
        mRecyclerWordItem.setAdapter(mWordAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerWordItem.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        mRecyclerWordItem.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerWordItem, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(isNullWord)
                            Toast.makeText(getActivity().getApplicationContext(), itemName + "에 단어를 추가해보세요.", Toast.LENGTH_SHORT).show();
                        else{
                            SearchFragment searchFragment = new SearchFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                            Bundle args = new Bundle();
                            args.putString("searchText", itemWord.get(position).getWord());
                            searchFragment.setArguments(args);

                            fragmentManager.beginTransaction()
                                    .add(R.id.containerView, searchFragment)
                                    .addToBackStack("fragBack")
                                    .commit();
                        }
                    }

                }));

        btn_listen = (Button)view.findViewById(R.id.note_word_listen);
        btn_edit = (Button)view.findViewById(R.id.note_word_edit);

        btn_listen.setOnClickListener(this);
        btn_edit.setOnClickListener(this);

        return view;
    }

    private void initData() {
        String nameData = new String ("2+" + itemName);
        itemWord = new ArrayList<WordData>();

        if (mworker_note != null && mworker_note.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_note.interrupt();
        }
        mworker_note = new workerNoteLoad(true, nameData, 2);
        mworker_note.start();
        try {
            mworker_note.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int i = 0;

        if(mworker_note.getNoteWord() != null) {
            isNullWord = false;
            while (i < mworker_note.getNoteWord().size()) {
                String mean = mworker_note.getNoteWord().get(i).getMean().toString().replaceAll("[?]", ", ");
                mean = mean.replaceAll("n.", "");
                itemWord.add( new WordData( mworker_note.getNoteWord().get(i).getWord().toString(), mean));
                Log.d(TAG,mworker_note.getNoteWord().get(i).getWord().toString() + " / " + mworker_note.getNoteWord().get(i).getMean().toString());
                i++;
            }
        }
        if(itemWord.isEmpty()){
            isNullWord = true;
            itemWord.add(new WordData("추가된 단어가 없습니다.", ""));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.note_word_listen:
                Toast.makeText(getActivity(),"기능 추가 예정입니다.",Toast.LENGTH_SHORT).show();
                break;

            case R.id.note_word_edit:
                Toast.makeText(getActivity(),"기능 추가 예정입니다.",Toast.LENGTH_SHORT).show();
                /*
                isEdit = !isEdit;
                 mWordAdapter = new NoteWordItemAdapter(itemWord, isEdit, context);
                 mRecyclerWordItem.setAdapter(mWordAdapter);// Set CustomAdapter as the adapter for RecyclerView.

                if(isEdit)
                    Toast.makeText(getActivity(),"아직 데이터 반영이 되지않습니다. " + String.valueOf(isEdit),Toast.LENGTH_SHORT).show();
                    */
                break;
            default:
                break;
        }
    }

    public ArrayList<WordData> setData(){
        return itemWord;
    }
}
