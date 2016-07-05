package com.onpuri.Adapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-04.
 */
public class NoteWordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "NoteWordAdapter";
    private static final int VIEW_TYPE_CELL = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private ArrayList<String> noteWordList = new ArrayList<String>();

    public TextView mWordItem;
    public Button mWordAdd;
    private EditText mAddItem;

    public NoteWordAdapter(ArrayList<String> listWord) {
        noteWordList.addAll(listWord);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View v) {
            super(v);
            mWordItem = (TextView) v.findViewById(R.id.note_word_item);
        }

        public TextView getTextView() {  return mWordItem;  }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(View v) {
            super(v);
            mWordAdd = (Button) v.findViewById(R.id.btn_note_add_word);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)){
            case VIEW_TYPE_CELL:
                Log.d(TAG, "Word Item set. - " + position);
                ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
                itemViewHolder.getTextView().setText(noteWordList.get(position));
                itemViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "Word List " + position + " clicked.");
                    }
                });
                break;
            case VIEW_TYPE_FOOTER:
                Log.d(TAG, "Add Button set. - " + position);
                final AddViewHolder addViewHolder = (AddViewHolder)holder;
                addViewHolder.getButton().setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Add Button clicked. - " + position);

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder((addViewHolder.itemView.getContext()));
                        alertBuilder.setTitle("단어 모음 추가하기");

                        mAddItem = new EditText((addViewHolder.itemView.getContext()));
                        alertBuilder.setView(mAddItem);

                        alertBuilder.setCancelable(false
                        ).setPositiveButton("추가",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String itemName = "단어 모음" + noteWordList.size();
                                if(!mAddItem.getText().toString().isEmpty())
                                    itemName = mAddItem.getText().toString();
                                addItem(position , itemName);
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

                break;
        }
    }

    private void addItem(int position, String itemName) {
        noteWordList.add(itemName);
        Log.d(TAG, "noteSenList size : " + noteWordList.size());
        notifyItemInserted(noteWordList.size());
        //notifyItemRangeChanged(position, noteSenList.size());
    }

    @Override
    public int getItemCount() {
        return noteWordList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == noteWordList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }
}
