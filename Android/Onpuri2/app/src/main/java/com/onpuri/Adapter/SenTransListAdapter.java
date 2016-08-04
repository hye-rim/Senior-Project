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

public class SenTransListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "SenTransListAdapter";
    private ArrayList<String> transList;

    public SenTransListAdapter(ArrayList<String> list_trans, RecyclerView TransRecyclerView) {
        this.transList=list_trans;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView trans;
        public ItemViewHolder(View v) {
            super(v);
            trans = (TextView) v.findViewById(R.id.item);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sen_list, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.trans.setText(transList.get(position));
    }

    @Override
    public int getItemCount() {
        return transList.size();
    }

}
