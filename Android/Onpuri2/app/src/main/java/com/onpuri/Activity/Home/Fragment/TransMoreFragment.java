package com.onpuri.Activity.Home.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.Home.Adapter.TransListAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.R;
import com.onpuri.Activity.Note.workerNote;
import com.onpuri.Activity.Note.workerNoteItemAdd;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_dark;

public class TransMoreFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransMoreFragment";

    private static View view;

    TextView item;
    String sentence = "";
    String sentence_num = "";
    String num="";

    private RecyclerView RecyclerView;
    private TransListAdapter Adapter;
    protected RecyclerView.LayoutManager LayoutManager;

    private ArrayList<String> listNote, listNoteNum;
    private workerNote mworker_note;
    private workerNoteItemAdd mworker_item_add;
    private int pos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_trans_more, container, false);
        } catch (InflateException e) {}

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num=getArguments().getString("sen_num");
            item.setText(sentence);

            item.setTextIsSelectable(true);

        }

        noteLoad();

        ImageButton add_note = (ImageButton) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        ImageButton add_trans = (ImageButton) view.findViewById(R.id.add_trans);
        add_trans.setOnClickListener(this);

        RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_trans);
        LayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.setLayoutManager(LayoutManager);
        Adapter = new TransListAdapter(this, sentence, sentence_num, getFragmentManager().beginTransaction(), RecyclerView);
        RecyclerView.setAdapter(Adapter);

        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_dark);
        RecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onClick(View v) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        final Bundle args = new Bundle();
        args.putString("sen", sentence);
        args.putString("sen_num", sentence_num);

        switch (v.getId()) {
            case R.id.add_note:
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
                break;
            case R.id.add_trans:
                final TransAddFragment atf = new TransAddFragment();
                atf.setArguments(args);
                ft.replace(R.id.root_home, atf);
                ft.addToBackStack(null);
                ft.commit();
                break;
        }
    }

    private void noteLoad(){
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
        if(mworker_note.getNoteSen() != null){
            while( i < mworker_note.getNoteSen().size()){
                listNote.add( mworker_note.getNoteSen().get(i).toString() );
                Log.d(TAG, mworker_note.getNoteSen().get(i).toString());
                i++;
            }
            listNoteNum.addAll(mworker_note.getNoteSenNum());
        }
    }

    private void selectNote(String item) {
        String nameData = new String ("1+" + item + "+" );
        if (mworker_item_add != null && mworker_item_add.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_item_add.interrupt();
        }
        mworker_item_add = new workerNoteItemAdd(true, nameData, Integer.parseInt(sentence_num));
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
}
