package com.onpuri.Adapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.NoteSenFragment;
import com.onpuri.Data.NoteData;
import com.onpuri.R;
import com.onpuri.Thread.workerNoteChanges;

import java.security.Key;
import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-04.
 */
public class NoteSenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "NoteSenAdapter";
    private static final int VIEW_TYPE_CELL = 0; //sentence item
    private static final int VIEW_TYPE_FOOTER = 1; //sentence add button

    private static final char USR_NOTE_ADD = 63; //내노트 이름 추가
    private static final char USR_NOTE_EDIT = 65; //내노트 이름 수정
    private static final char USR_NOTE_DEL = 67; //내노트 이름 삭제

    private ArrayList<NoteData> noteSenList;

    private TextView mSenItem;
    public ImageButton mSenMore;
    public Button mSenAdd;
    private EditText mAddItem, mChangeItem;
    private LinearLayout mSenMoreLayout;

    public boolean isBtnClicked;

    private int selectedPos = 0;
    Context context;

    workerNoteChanges mworker_add;
    String originalName;
    FragmentManager fm;

    Boolean isNullSen;

    public NoteSenAdapter(ArrayList<NoteData> listSentence, Context context, FragmentManager fragmentManager, Boolean isNullSen, RecyclerView mRecyclerSen) {
        noteSenList = new ArrayList<>();
        noteSenList.addAll(listSentence);
        isBtnClicked = false;
        this.context = context;
        this.fm = fragmentManager;
        this.isNullSen = isNullSen;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View v) {
            super(v);
            mSenItem = (TextView) v.findViewById(R.id.note_sen_item);
            mSenMore = (ImageButton) v.findViewById(R.id.btn_sen_more);
            mSenItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isNullSen){
                        Toast.makeText(context, "새로운 문장모음을 추가해주세요.", Toast.LENGTH_LONG).show();
                    }else{
                        originalName = noteSenList.get(getPosition()).getName();

                        NoteSenFragment noteSenItem = new NoteSenFragment();

                        Bundle args = new Bundle();
                        args.putString("senItemName", originalName );
                        noteSenItem.setArguments(args);
                        fm.beginTransaction()
                                .replace(R.id.root_note, noteSenItem)
                                .addToBackStack(null)
                                .commit();
                        fm.executePendingTransactions();
                    }
                }
            });

            mSenMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    originalName = noteSenList.get(getPosition()).getName();

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder((v.getContext()));

                    mChangeItem = new EditText((v.getContext()));
                    mChangeItem.setText(originalName);
                    alertBuilder.setTitle("");
                    alertBuilder.setView(mChangeItem);

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
        public ImageButton getImageButton(){ return mSenMore; }
        public TextView getTextView() {  return mSenItem;  }

        public void setData(String name) {
            mSenItem.setText(name);
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(View v) {
            super(v);
            mSenAdd = (Button) v.findViewById(R.id.btn_note_add_sen);
            mSenAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder((v.getContext()));
                    alertBuilder.setTitle("문장 모음 추가하기");

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
                            String itemName = "문장 모음" + getPosition();
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

        public Button getButton() { return mSenAdd; }

    }

    //create new views(invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == VIEW_TYPE_CELL){
            //Create viewholder for your default cell
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_sen_list, parent, false);
            return new ItemViewHolder(v);
        }
        else {
            //Create viewholder for your footer view
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_sen_btn, parent, false);
            return new AddViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        final int itemType = getItemViewType(position);

        switch (itemType){
            case VIEW_TYPE_CELL:
                Log.d(TAG, "Sentence Item set. - " + position);
                ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
                itemViewHolder.setData(noteSenList.get(position).getName());
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
        String nameData = new String ("1+" + itemName);

        toServer(USR_NOTE_ADD ,nameData);

        if(mworker_add.getSuccess()) {
            noteSenList.add(new NoteData(itemName));
            notifyItemInserted(position);
        }else{
            Toast.makeText(context, "추가에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }

    }

    private void changeItem(int position, String changeName){
        String nameData = new String ("1+" + originalName + "+" + changeName);

        toServer(USR_NOTE_EDIT ,nameData);

        if(mworker_add.getSuccess()) {
            noteSenList.get(position).setName(changeName);
            notifyItemChanged(position);
        }else{
            Toast.makeText(context, "수정에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void removeItem(int position, String itemName){
        String nameData = new String ("1+" + itemName);

        toServer(USR_NOTE_DEL ,nameData);

        if(mworker_add.getSuccess()) {
            noteSenList.remove(position);
            notifyItemRemoved(position);
        }else{
            Toast.makeText(context, "삭제에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return noteSenList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= noteSenList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }
}
