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
import android.widget.ImageButton;
import android.widget.TextView;

import com.onpuri.NoteData;
import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-04.
 */
public class NoteSenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "NoteSenAdapter";
    private static final int VIEW_TYPE_CELL = 0; //sentence item
    private static final int VIEW_TYPE_FOOTER = 1; //sentence add button

    private ArrayList<NoteData> noteSenList = new ArrayList<>();

    private TextView mSenItem;
    public ImageButton mSenMore;
    public Button mSenAdd;
    private EditText mAddItem, mChangeItem;

    public NoteSenAdapter(ArrayList<NoteData> listSentence) {
        noteSenList.addAll(listSentence);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View v) {
            super(v);
            mSenItem = (TextView) v.findViewById(R.id.note_sen_item);
            mSenMore = (ImageButton) v.findViewById(R.id.btn_sen_more);
        }
        public ImageButton getImageButton(){ return mSenMore; }
        public TextView getTextView() {  return mSenItem;  }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(View v) {
            super(v);
            mSenAdd = (Button) v.findViewById(R.id.btn_note_add_sen);
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
        switch (getItemViewType(position)){
            case VIEW_TYPE_CELL:
                Log.d(TAG, "Sentence Item set. - " + position);
                final ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
                itemViewHolder.getTextView().setText(noteSenList.get(position).getName());
                itemViewHolder.getTextView().setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "Sentence List clicked.");
                    }
                });
                itemViewHolder.getImageButton().setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "Sentence More clicked.");
                        String changeName = itemViewHolder.getTextView().getText().toString();
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder((itemViewHolder.itemView.getContext()));

                        final int posItem = itemViewHolder.getAdapterPosition();
                        mChangeItem = new EditText((itemViewHolder.itemView.getContext()));
                        mChangeItem.setText(changeName);
                        alertBuilder.setTitle("");
                        alertBuilder.setView(mChangeItem);

                        alertBuilder.setCancelable(false
                        ).setPositiveButton("이름 수정",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG,"change : " +  mChangeItem.getText().toString());
                                changeItem(posItem, mChangeItem.getText().toString());
                            }
                        }).setNegativeButton("삭제", new DialogInterface.OnClickListener() {
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
            case VIEW_TYPE_FOOTER:
                Log.d(TAG, "Add Button set. - " + position);
                final AddViewHolder addViewHolder = (AddViewHolder)holder;
                addViewHolder.getButton().setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Add Button clicked. - ");

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder((addViewHolder.itemView.getContext()));
                        alertBuilder.setTitle("문장 모음 추가하기");

                        mAddItem = new EditText((addViewHolder.itemView.getContext()));
                        alertBuilder.setView(mAddItem);

                        alertBuilder.setCancelable(false
                        ).setPositiveButton("추가",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String itemName = "문장 모음" + addViewHolder.getAdapterPosition();
                                if(!mAddItem.getText().toString().isEmpty())
                                    itemName = mAddItem.getText().toString();
                                addItem(addViewHolder.getAdapterPosition(), itemName);
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
        noteSenList.add(position, new NoteData(itemName));
        Log.d(TAG, "noteSen add : " + position);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount()-position);
    }
    private void changeItem(int position, String itemName){
        if(position < noteSenList.size()) {
            noteSenList.set(position, new NoteData(itemName));
            Log.d(TAG, "noteSen change : " + position);
            notifyItemRangeChanged(0, getItemCount());
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
