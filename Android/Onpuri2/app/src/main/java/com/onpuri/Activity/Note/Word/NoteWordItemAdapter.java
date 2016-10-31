package com.onpuri.Activity.Note.Word;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.onpuri.Data.WordData;
import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-11.
 */
public class NoteWordItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private final String TAG = "NoteWordItemAdapter";

    private ArrayList<WordData> wordItemList = new ArrayList<>();
    private Context context;

    public NoteWordItemAdapter(ArrayList<WordData> listWord,  Context context){
        wordItemList.addAll(listWord);
        this.context = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mWordItem, mWordItemMean;

        public ItemViewHolder(View v) {
            super(v);
            mWordItem = (TextView) v.findViewById(R.id.tv_note_word);
            mWordItemMean = (TextView) v.findViewById(R.id.tv_note_word_mean);
        }
        public TextView getTextViewWord() {  return mWordItem;  }
        public TextView getTextViewMean() { return mWordItemMean; }
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        Log.d(TAG, "Word Item set. - " + position);
        if( getItemViewType(position) == VIEW_TYPE_ITEM) {
            ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
            itemViewHolder.mWordItem.setText(wordItemList.get(position).getWord());
            itemViewHolder.mWordItemMean.setText(wordItemList.get(position).getMean());
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Log.d(TAG, "Word List clicked.");
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        return  VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return wordItemList.size();
    }

}
