package com.onpuri.Activity.Home.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.onpuri.Activity.Home.Thread.workerListenMore;
import com.onpuri.Activity.MainActivity;
import com.onpuri.DividerItemDecoration;
import com.onpuri.Activity.Home.Adapter.ListenListAdapter;
import com.onpuri.R;
import com.onpuri.Activity.Note.workerNote;
import com.onpuri.Activity.Note.workerNoteItemAdd;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_dark;


public class ListenMoreFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ListenMoreFragment";
    private workerListenMore worker_listen_more;

    private static View view;

    ArrayList<String> list_listen;
    ArrayList<String> list_userid;
    ArrayList<String> list_day;
    ArrayList<String> list_reco;
    ArrayList<String> list_num;

    TextView item;
    String sentence = "";
    String sentence_num = "";

    private Context con;
    private FragmentManager fm;

    private RecyclerView RecyclerView;
    private ListenListAdapter Adapter;
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
            view = inflater.inflate(R.layout.fragment_listen_more, container, false);
        } catch (InflateException e) {
        }

        list_listen = new ArrayList<String>();
        list_userid = new ArrayList<String>();
        list_day = new ArrayList<String>();
        list_reco = new ArrayList<String>();
        list_num = new ArrayList<String>();

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num=getArguments().getString("sen_num");
            item.setText(sentence);

            item.setTextIsSelectable(true);

        }
        listen();
        noteLoad();

        ImageButton add_note = (ImageButton) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        ImageButton add_listen = (ImageButton) view.findViewById(R.id.add_listen);
        add_listen.setOnClickListener(this);

        RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_listen);
        LayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.setLayoutManager(LayoutManager);
        Adapter = new ListenListAdapter(this, getActivity(), ((MainActivity)getActivity()).mPlayer, ((MainActivity)getActivity()).user.getuserId(), list_listen, list_userid, list_reco, list_num, con, fm, RecyclerView);
        RecyclerView.setAdapter(Adapter);// Set CustomAdapter as the adapter for RecyclerView.

        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_dark);
        RecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        return view;
    }

    private void listen(){
        if (worker_listen_more != null && worker_listen_more.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_listen_more.interrupt();
        }
        worker_listen_more = new workerListenMore(true, sentence_num);
        worker_listen_more.start();
        try {
            worker_listen_more.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        list_listen.clear();
        list_userid.clear();
        list_day.clear();
        list_reco.clear();
        list_num.clear();

        for (int i = 0; i < worker_listen_more.getCount(); i++) {
            list_listen.add(worker_listen_more.getUserid().get(i).toString()+"님 "+worker_listen_more.getDay().get(i).toString());
            list_userid.add(worker_listen_more.getUserid().get(i).toString());
            list_day.add(worker_listen_more.getDay().get(i).toString());
            list_reco.add(worker_listen_more.getReco().get(i).toString());
            list_num.add(worker_listen_more.getListennum().get(i).toString());
        }
    }

    @Override
    public void onClick(View v) {
        final Bundle args = new Bundle();
        args.putString("sen", sentence);
        args.putString("sen_num", sentence_num);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        switch (v.getId()) {
            case R.id.add_note:
                mplayerStop();
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
            case R.id.add_listen:
                mplayerStop();
                final ListenAddFragment alf = new ListenAddFragment();
                alf.setArguments(args);
                ft.replace(R.id.root_home, alf);
                ft.addToBackStack(null);
                ft.commit();
                break;
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
        if (mworker_note.getNoteSen() != null) {
            while (i < mworker_note.getNoteSen().size()) {
                listNote.add(mworker_note.getNoteSen().get(i).toString());
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
            Toast.makeText(getActivity(), "목록에 이미 존재합니다.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), "추가에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
    }

    public void mplayerStop() {
        if(((MainActivity)getActivity()).mPlayer.mpfile != null) {
            ((MainActivity)getActivity()).mPlayer.mpfile.pause();
        }
    }
}
