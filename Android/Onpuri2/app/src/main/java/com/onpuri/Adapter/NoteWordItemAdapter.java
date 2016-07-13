package com.onpuri.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-11.
 */
public class NoteWordItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "NoteWordItemAdapter";

    private ArrayList<String> wordItemList;

    private TextView mWordItem;

    public NoteWordItemAdapter(ArrayList<String> listWord){
        wordItemList.addAll(listWord);
        wordItemList = new ArrayList<String>();

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View v) {
            super(v);
            mWordItem = (TextView) v.findViewById(R.id.tv_note_word);
        }
        public TextView getTextView() {  return mWordItem;  }
    }

    //create new views(invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        //Create viewholder for your default cell
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_word_item_list, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        Log.d(TAG, "Word Item set. - " + position);
        final ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
        itemViewHolder.getTextView().setText(wordItemList.get(position));
        itemViewHolder.getTextView().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "Word List clicked.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordItemList.size();
    }

}
