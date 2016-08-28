package com.onpuri.Adapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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

import com.onpuri.Data.*;
import com.onpuri.R;
import com.onpuri.Thread.workerNoteChanges;

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

    public TextView mWordItem;
    public ImageButton mWordMore;
    public Button mWordAdd;
    private EditText mAddItem, mChangeItem;

    private int selectedPos = 0;
    Context context;

    workerNoteChanges mworker_add;
    String originalName;


    public NoteWordAdapter(ArrayList<NoteWordData> listWord, Context context) {
        noteWordList = new ArrayList<>();
        noteWordList.addAll(listWord);
        this.context = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View v) {
            super(v);
            mWordItem = (TextView) v.findViewById(R.id.note_word_item);
            mWordMore = (ImageButton) v.findViewById(R.id.btn_word_more);

            mWordMore.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                originalName = noteWordList.get(getPosition()).getName();

                                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder((v.getContext()));

                                                mChangeItem = new EditText((v.getContext()));
                                                mChangeItem.setText(originalName);
                                                alertBuilder.setTitle("");
                                                alertBuilder.setView(mChangeItem);

                                                alertBuilder.setCancelable(false
                                                ).setPositiveButton("이름 수정",new DialogInterface.OnClickListener(){
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Log.d(TAG,"change : " +  mChangeItem.getText().toString());
                                                        changeItem(getPosition(),  mChangeItem.getText().toString());
                                                    }
                                                }).setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        removeItem(getPosition() , mChangeItem.getText().toString());
                                                        dialog.cancel();
                                                    }
                                                });
                                                AlertDialog alertDialog = alertBuilder.create();
                                                alertDialog.show();  //<-- See This!
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
                            String itemName = "단어 모음" + getPosition();
                            if(!mAddItem.getText().toString().isEmpty())
                                itemName = mAddItem.getText().toString();
                            addItem(selectedPos, itemName);
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
                Log.d(TAG, "Word Item set. - " + position);
                ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
                itemViewHolder.setData(noteWordList.get(position).getName());
                itemViewHolder.itemView.setSelected(selectedPos == position);
                break;
            case VIEW_TYPE_FOOTER:
                Log.d(TAG, "Add Button set. - " + position);
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

    private void addItem(int position, String itemName) {
        String nameData = new String ("2+" + itemName);

        toServer(USR_NOTE_ADD ,nameData);

        if(mworker_add.getSuccess()) {
            noteWordList.add(new NoteWordData(itemName));
            notifyItemInserted(position);
        }else{
            Toast.makeText(context, "추가에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }

    }

    private void changeItem(int position, String changeName){
        String nameData = new String ("2+" + originalName + "+" + changeName);

        toServer(USR_NOTE_EDIT ,nameData);

        if(mworker_add.getSuccess()) {
            noteWordList.get(position).setName(changeName);
            notifyItemChanged(position);
        }else{
            Toast.makeText(context, "수정에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void removeItem(int position, String itemName){
        String nameData = new String ("2+" + itemName);

        toServer(USR_NOTE_DEL ,nameData);

        if(mworker_add.getSuccess()) {
            noteWordList.remove(position);
            notifyItemRemoved(position);
        }else{
            Toast.makeText(context, "삭제에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
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
