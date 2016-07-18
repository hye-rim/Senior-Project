package com.onpuri.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

public class TransListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "TransListAdapter";
    private static final int VIEW_TYPE_CELL = 1;
    private static final int VIEW_TYPE_FOOTER = 0;

    private ArrayList<String> senList;

    public TransListAdapter(ArrayList<String> listSentence, RecyclerView recyclerView) {
        this.senList=listSentence;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mSenItem;
        public ItemViewHolder(View v) {
            super(v);
            mSenItem = (TextView) v.findViewById(R.id.tv_trans_item);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trans_list, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "trans list " + position + " set.");

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.mSenItem.setText(senList.get(position));

        if (position % 2 == 0) {
            itemViewHolder.itemView.setBackgroundColor(Color.parseColor("#faf5b3"));
        } else {
            itemViewHolder.itemView.setBackgroundColor(Color.parseColor("#FEE098"));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return senList.get(position) != null ? VIEW_TYPE_CELL : VIEW_TYPE_FOOTER;
    }

    @Override
    public int getItemCount() {
        return senList.size();
    }

}
