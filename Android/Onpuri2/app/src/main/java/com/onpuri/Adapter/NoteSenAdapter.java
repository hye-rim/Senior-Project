package com.onpuri.Adapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipData;
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
import android.widget.Toast;

import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-04.
 */
public class NoteSenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "NoteSenAdapter";
    private static final int VIEW_TYPE_CELL = 0; //sentence item
    private static final int VIEW_TYPE_FOOTER = 1; //sentence add button

    private ArrayList<String> noteSenList = new ArrayList<String>();

    private TextView mSenItem;
    private Button mSenAdd;
    private EditText mAddItem;

    public NoteSenAdapter(ArrayList<String> listSentence) {
        noteSenList.addAll(listSentence);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View v) {
            super(v);
            mSenItem = (TextView) v.findViewById(R.id.note_sen_item);
        }

        public TextView getTextView() { return mSenItem;  }
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        switch (getItemViewType(position)){
            case VIEW_TYPE_CELL:
                Log.d(TAG, "Sentence Item set. - " + position);
                ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
                itemViewHolder.getTextView().setText(noteSenList.get(position));
                itemViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "Sentence List " + position + " clicked.");
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
                        alertBuilder.setTitle("문장 모음 추가하기");

                        mAddItem = new EditText((addViewHolder.itemView.getContext()));
                        alertBuilder.setView(mAddItem);

                        alertBuilder.setCancelable(false
                        ).setPositiveButton("추가",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String itemName = "문장 모음" + noteSenList.size();
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
        noteSenList.add(itemName);
        Log.d(TAG, "noteSenList size : " + noteSenList.size());
        notifyItemInserted(noteSenList.size());
        //notifyItemRangeChanged(position, noteSenList.size());
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
