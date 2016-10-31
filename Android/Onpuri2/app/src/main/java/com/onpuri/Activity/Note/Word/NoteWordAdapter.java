package com.onpuri.Activity.Note.Word;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.Note.workerNote;
import com.onpuri.Data.*;
import com.onpuri.R;
import com.onpuri.Activity.Note.workerNoteChanges;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-04.
 */
public class NoteWordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "NoteWordAdapter";
    private static final int VIEW_TYPE_CELL = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private static final char USR_NOTE_ADD = 63; //내노트 이름 추가
    private static final char USR_NOTE_EDIT = 65; //내노트 이름 수정
    private static final char USR_NOTE_DEL = 67; //내노트 이름 삭제

    private ArrayList<NoteWordData> noteWordList;
    private ArrayList<String> noteWordNumList;


    public Button mWordAdd;
    private EditText mAddItem, mChangeItem;

    private int selectedPos = 0;
    Context context;

    workerNoteChanges mworker_add;
    workerNote mworker_note;

    String originalName, name;
    FragmentManager fm;

    Boolean isNullWord, first;
    int pos;
    public NoteWordAdapter(ArrayList<NoteWordData> listWord, ArrayList<String> listWordNum,
                           Context context, FragmentManager fragmentManager, Boolean isNullWord, Boolean first) {
        this.first = first;
        if(this.first) {
            noteWordList = new ArrayList<NoteWordData>();
            noteWordList.addAll(listWord);
            noteWordNumList = new ArrayList<String>();
            noteWordNumList.addAll(listWordNum);
            this.context = context;
            this.fm = fragmentManager;
            this.isNullWord = isNullWord;
            this.first = false;
        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mWordItem;
        public ImageButton mWordMore;
        public ItemViewHolder(View v) {
            super(v);
            mWordItem = (TextView) v.findViewById(R.id.note_word_item);
            mWordMore = (ImageButton) v.findViewById(R.id.btn_word_more);

            mWordItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isNullWord){
                        Toast.makeText(context, "새로운 단어모음을 추가해주세요.", Toast.LENGTH_LONG).show();
                    }else {
                        originalName = noteWordList.get(getAdapterPosition()).getName();

                        NoteWordFragment noteWordItem = new NoteWordFragment();

                        Bundle args = new Bundle();
                        args.putString("wordItemName", originalName);
                        args.putString("wordItemNum", noteWordNumList.get(getAdapterPosition()));
                        noteWordItem.setArguments(args);
                        fm.beginTransaction()
                                .replace(R.id.root_note, noteWordItem)
                                .addToBackStack(null)
                                .commit();
                        fm.executePendingTransactions();

                    }
                }
            });


            mWordMore.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(final View v) {
                                                name = noteWordList.get(getAdapterPosition()).getName();
                                                originalName = noteWordNumList.get(getAdapterPosition());
                                                final String[] items = {"이름수정" , "삭제"};
                                                AlertDialog.Builder noteSelectDialog = new AlertDialog.Builder(v.getContext());

                                                noteSelectDialog.setTitle("메뉴 선택")
                                                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int index) {
                                                                pos = index;
                                                                dialog.dismiss();

                                                                switch(index){
                                                                    case 0:
                                                                        mChangeItem = new EditText((v.getContext()));
                                                                        mChangeItem.setText(name);

                                                                        new AlertDialog.Builder(v.getContext())
                                                                                .setTitle("노트 이름 수정")
                                                                                .setView(mChangeItem)
                                                                                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                                                                    @Override
                                                                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                                                                                        if(keyCode == KeyEvent.KEYCODE_BACK){
                                                                                            dialog.dismiss();
                                                                                            return true;
                                                                                        }
                                                                                        return false;
                                                                                    }
                                                                                })
                                                                                .setCancelable(false)
                                                                                .setPositiveButton("수정",new DialogInterface.OnClickListener(){
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        Log.d(TAG,"change : " +  mChangeItem.getText().toString());
                                                                                        changeItem(getAdapterPosition(),  mChangeItem.getText().toString());
                                                                                        notifyDataSetChanged();
                                                                                        dialog.cancel();
                                                                                    }})
                                                                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        dialog.cancel();
                                                                                    }
                                                                                })
                                                                                .show();
                                                                        break;

                                                                    case 1:
                                                                        new AlertDialog.Builder(v.getContext())
                                                                                .setTitle("노트 삭제")
                                                                                .setMessage(name)
                                                                                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                                                                    @Override
                                                                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                                                                        if(keyCode == KeyEvent.KEYCODE_BACK){
                                                                                            dialog.dismiss();
                                                                                            return true;
                                                                                        }
                                                                                        return false;
                                                                                    }
                                                                                })
                                                                                .setCancelable(false)
                                                                                .setPositiveButton("삭제",new DialogInterface.OnClickListener(){
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        removeItem(getAdapterPosition() ,originalName);
                                                                                        notifyDataSetChanged();
                                                                                        dialog.cancel();
                                                                                    }})
                                                                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        dialog.cancel();
                                                                                    }
                                                                                })
                                                                                .show();
                                                                        break;

                                                                    default:
                                                                        break;
                                                                }

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
                                        }
            );
        }
        public ImageButton getImageButton(){ return mWordMore; }
        public TextView getTextView() {  return mWordItem;  }

        public void setData(String name) {
            mWordItem.setText(name);
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(View v) {
            super(v);
            mWordAdd = (Button) v.findViewById(R.id.btn_note_add_word);
            mWordAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder((v.getContext()));
                    alertBuilder.setTitle("단어 모음 추가하기");

                    //back key 셋팅
                    alertBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if(keyCode == KeyEvent.KEYCODE_BACK){
                                dialog.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });

                    mAddItem = new EditText((v.getContext()));
                    alertBuilder.setView(mAddItem);

                    alertBuilder.setCancelable(false
                    ).setPositiveButton("추가",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String itemName = "단어 모음" + getAdapterPosition();
                            if(!mAddItem.getText().toString().isEmpty())
                                itemName = mAddItem.getText().toString();
                            addItem(selectedPos, itemName);
                            notifyDataSetChanged();
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();  //<-- See This!

                }
            });

        }
        public Button getButton() { return mWordAdd; }

    }

    //create new views(invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == VIEW_TYPE_CELL){
            //Create viewholder for your default cell
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_word_list, parent, false);
            return new ItemViewHolder(v);
        }
        else {
            //Create viewholder for your footer view
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_word_btn, parent, false);
            return new AddViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        switch (getItemViewType(position)){
            case VIEW_TYPE_CELL:
                ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
                itemViewHolder.mWordItem.setText(noteWordList.get(position).getName());
                itemViewHolder.itemView.setSelected(selectedPos == position);
                break;
            case VIEW_TYPE_FOOTER:
                AddViewHolder addViewHolder = (AddViewHolder)holder;
                addViewHolder.itemView.setSelected(selectedPos == position);
                break;
        }

    }

    private void toServer(char opcCode, String data) {
        if (mworker_add != null && mworker_add.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_add.interrupt();
        }
        mworker_add = new workerNoteChanges(opcCode,true, data);
        mworker_add.start();
        try {
            mworker_add.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void dataChange(){
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

        noteWordList.clear();
        noteWordNumList.clear();

        //단어 모음 리스트
        int i = 0;
        if(mworker_note.getNoteWord() != null){
            while( i < mworker_note.getNoteWord().size()){
                noteWordList.add(new NoteWordData( mworker_note.getNoteWord().get(i).toString() ));
                Log.d(TAG, mworker_note.getNoteWord().get(i).toString());
                i++;
            }
            noteWordNumList.addAll(mworker_note.getNoteWordNum());
        }
        if(noteWordList.isEmpty()){
            noteWordList.add(new NoteWordData("새로운 단어 모음을 등록해보세요."));
        }

        this.notifyDataSetChanged();
    }

    private void addItem(int position, String itemNameNum) {
        String nameData = new String ("2+" + itemNameNum);

        toServer(USR_NOTE_ADD ,nameData);

        if(mworker_add.getSuccess()) {
            dataChange();

        }else{
            Toast.makeText(context, "추가에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
        this.notifyDataSetChanged();

    }

    private void changeItem(int position, String changeNameNum){
        String nameData = new String ("2+" + originalName + "+" + changeNameNum);

        toServer(USR_NOTE_EDIT ,nameData);

        if(mworker_add.getSuccess()) {
            dataChange();
        }else{
            Toast.makeText(context, "수정에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
        this.notifyDataSetChanged();
    }

    private void removeItem(int position, String itemNameNum){
        String nameData = new String ("2+" + itemNameNum);

        toServer(USR_NOTE_DEL ,nameData);

        if(mworker_add.getSuccess()) {
            dataChange();
        }else{
            Toast.makeText(context, "삭제에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return noteWordList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= noteWordList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }
}
