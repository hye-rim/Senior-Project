package com.onpuri.Activity.Note.Sentence;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import com.onpuri.Activity.Note.workerNote;
import com.onpuri.Activity.Note.workerNoteChanges;
import com.onpuri.Data.NoteData;
import com.onpuri.R;

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
    private ArrayList<String> noteSenNumList;


    public Button mSenAdd;
    private EditText mAddItem, mChangeItem;

    public boolean isBtnClicked;

    private int selectedPos = 0;
    Context context;

    workerNoteChanges mworker_add;
    workerNote mworker_note;

    String originalName,name;
    FragmentManager fm;

    Boolean isNullSen;
    Boolean first;

    private int pos;

    public NoteSenAdapter(ArrayList<NoteData> listSentence, ArrayList<String> listSentenceNum ,
                          Context context, FragmentManager fragmentManager, Boolean isNullSen, Boolean first) {
        this.first= first;
        if( this.first){
            noteSenList = new ArrayList<NoteData>();
            noteSenList.addAll(listSentence);

            this.noteSenNumList = new ArrayList<String>();
            this.noteSenNumList.addAll(listSentenceNum);

            isBtnClicked = false;
            this.context = context;
            this.fm = fragmentManager;
            this.isNullSen = isNullSen;
            this.first = false;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mSenItem;
        public ImageButton mSenMore;
        public ItemViewHolder(View v) {
            super(v);
            mSenItem = (TextView) v.findViewById(R.id.note_sen_item);
            mSenMore = (ImageButton) v.findViewById(R.id.btn_sen_more);

            mSenItem.setMaxLines(2);
            mSenItem.setEllipsize(TextUtils.TruncateAt.END);
            mSenItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isNullSen){
                        Toast.makeText(context, "새로운 문장모음을 추가해주세요.", Toast.LENGTH_LONG).show();
                    }else{
                        originalName = noteSenList.get(getAdapterPosition()).getName();

                        NoteSenFragment noteSenItem = new NoteSenFragment();

                        Bundle args = new Bundle();
                        args.putString("senItemName", originalName );
                        args.putString("senItemNum", noteSenNumList.get(getAdapterPosition()));
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
                                            public void onClick(final View v) {
                                                name = noteSenList.get(getAdapterPosition()).getName();
                                                originalName = noteSenNumList.get(getAdapterPosition());
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
                            String itemName = "문장 모음" + getAdapterPosition();
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
                ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
                itemViewHolder.mSenItem.setText(noteSenList.get(position).getName());
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

        noteSenList.clear();
        noteSenNumList.clear();

        //문장 모음 리스트
        int i = 0;
        if(mworker_note.getNoteSen() != null){
            while( i < mworker_note.getNoteSen().size()){
                noteSenList.add(new NoteData( mworker_note.getNoteSen().get(i).toString() ));
                Log.d(TAG, mworker_note.getNoteSen().get(i).toString());
                i++;
            }
            noteSenNumList.addAll(mworker_note.getNoteSenNum());
        }
        if(noteSenList.isEmpty()){
            isNullSen = true;
            noteSenList.add(new NoteData("새로운 문장 모음을 등록해보세요."));
        }

        this.notifyDataSetChanged();
    }


    private void addItem(int position, String itemName) {
        String nameData = new String ("1+" + itemName);

        toServer(USR_NOTE_ADD ,nameData);

        if(mworker_add.getSuccess()) {
            dataChange();
        }else{
            Toast.makeText(context, "추가에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
        this.notifyDataSetChanged();

    }

    private void changeItem(int position, String changeName){
        String nameData = new String ("1+" + originalName + "+" + changeName);

        toServer(USR_NOTE_EDIT ,nameData);

        if(mworker_add.getSuccess()) {
            dataChange();
        }else{
            Toast.makeText(context, "수정에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
        this.notifyDataSetChanged();

    }

    private void removeItem(int position, String itemName){
        String nameData = new String ("1+" + itemName);

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
        return noteSenList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= noteSenList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }
}
