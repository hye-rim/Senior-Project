package com.onpuri.Activity.Note.Sentence;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-11.
 */
public class NoteSenItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private final String TAG = "NoteSenItemAdapter";

    private ArrayList<String> senItemList = new ArrayList<>();

    private TextView mSenItem;

    public NoteSenItemAdapter(ArrayList<String> listSentence) {
        senItemList.addAll(listSentence);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View v) {
            super(v);
            mSenItem = (TextView) v.findViewById(R.id.tv_note_sen);
        }
        public TextView getTextView() {  return mSenItem;  }
    }

    //create new views(invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        //Create viewholder for your default cell
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_sen_item_list, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        Log.d(TAG, "Sentence Item set. - " + position);
        if( getItemViewType(position) == VIEW_TYPE_ITEM) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.getTextView().setText(senItemList.get(position));
            itemViewHolder.getTextView().setMaxLines(2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }


    @Override
    public int getItemCount() {
        return senItemList.size();
    }

}
