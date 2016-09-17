package com.onpuri.Activity.Home;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.onpuri.Activity.MainActivity;
import com.onpuri.Adapter.TransListAdapter;
import com.onpuri.DividerItemDecoration;
import com.onpuri.Listener.HomeItemClickListener;
import com.onpuri.R;
import com.onpuri.Activity.Note.workerNote;
import com.onpuri.Activity.Note.workerNoteItemAdd;
import com.onpuri.Thread.workerTransMore;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_dark;

public class TransMoreFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransMoreFragment";
    private workerTransMore worker_trans_more;

    private static View view;

    ArrayList<String> list_trans;
    ArrayList<String> list_userid;
    ArrayList<String> list_day;
    ArrayList<String> list_reco;
    ArrayList<String> list_num;

    TextView item;
    String sentence = "";
    String sentence_num = "";

    private RecyclerView RecyclerView;
    private TransListAdapter Adapter;
    protected RecyclerView.LayoutManager LayoutManager;

    private ArrayList<String> listNote;
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

        list_trans = new ArrayList<String>();
        list_userid = new ArrayList<String>();
        list_day = new ArrayList<String>();
        list_reco = new ArrayList<String>();
        list_num = new ArrayList<String>();

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num=getArguments().getString("sen_num");
            item.setText(sentence);
        }

        translation();
        noteLoad();

        ImageButton add_note = (ImageButton) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        ImageButton add_trans = (ImageButton) view.findViewById(R.id.add_trans);
        add_trans.setOnClickListener(this);

        RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_trans);
        LayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.setLayoutManager(LayoutManager);
        Adapter = new TransListAdapter(list_trans, list_day, list_reco, RecyclerView);
        RecyclerView.setAdapter(Adapter);
        RecyclerView.addOnItemTouchListener(
                new HomeItemClickListener(getActivity().getApplicationContext(), RecyclerView ,new HomeItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Adapter.notifyDataSetChanged();
                        final TransDetailFragment tdf = new TransDetailFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();

                        Bundle args = new Bundle();
                        args.putString("sen", sentence);
                        args.putString("sen_trans", list_trans.get(position));
                        args.putString("userid", list_userid.get(position));
                        args.putString("day", list_day.get(position));
                        args.putString("reco", list_reco.get(position));
                        tdf.setArguments(args);

                        ft.replace(R.id.root_home, tdf);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    public void onLongItemClick(View view, int position) {
                        String userid = ((MainActivity)getActivity()).user.getuserId();
                        Log.d(TAG,list_userid.get(position));
                        Log.d(TAG, userid);
                        if (list_userid.get(position).equals(userid)) {
                            Log.d(TAG, "true");
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("선택한 해석을 삭제하시겠습니까?")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            final FragmentManager fm = getActivity().getSupportFragmentManager();
                                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                            fm.popBackStack();
                                            ft.commit();
                                            Toast.makeText(getActivity(), "삭제되었습니다(구현예정)", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dlg, int sumthin) {
                                            Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT).show();
                                        }

                                    }).show();
                        }
                        else {
                            Log.d(TAG, "false");
                            Toast.makeText(getContext(), "본인이 등록한 해석만 삭제가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );

        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_dark);
        RecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        return view;
    }

    private void translation() {
        if (worker_trans_more != null && worker_trans_more.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_trans_more.interrupt();
        }
        worker_trans_more = new workerTransMore(true, sentence_num);
        worker_trans_more.start();
        try {
            worker_trans_more.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        list_trans.clear();
        list_userid.clear();
        list_day.clear();
        list_reco.clear();
        list_num.clear();

        for (int i = 0; i < worker_trans_more.getCount(); i++) {
            list_trans.add(worker_trans_more.getTrans().get(i).toString());
            list_userid.add(worker_trans_more.getUserid().get(i).toString());
            list_day.add(worker_trans_more.getDay().get(i).toString());
            list_reco.add(worker_trans_more.getReco().get(i).toString());
            list_num.add(worker_trans_more.getTransnum().get(i).toString());
        }
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

                                if( index == listNote.size() -1  ){
                                    Toast.makeText(getActivity(), "기능 추가 예정입니다.", Toast.LENGTH_SHORT).show();
                                }else {
                                    selectNote(items[index]);
                                }
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
        }


        listNote.add("새로운 문장 모음 등록하기");

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
            Toast.makeText(getActivity(), item + "에 추가되었습니다.", Toast.LENGTH_LONG).show();
        }else if( mworker_item_add.getResult() == 2){
            Toast.makeText(getActivity(), item + "에 이미 있습니다.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), "추가에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
    }
}
