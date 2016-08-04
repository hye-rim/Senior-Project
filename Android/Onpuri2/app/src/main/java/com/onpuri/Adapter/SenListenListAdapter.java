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

public class SenListenListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "SenListenListAdapter";
    private ArrayList<String> listenList;

    public SenListenListAdapter(ArrayList<String> list_listen, RecyclerView LIstenRecyclerView) {
        this.listenList=list_listen;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView listen;
        public ItemViewHolder(View v) {
            super(v);
            listen = (TextView) v.findViewById(R.id.item);
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
        Log.d(TAG, "sen trans list " + position + " set.");
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.listen.setText(listenList.get(position));
    }

    @Override
    public int getItemCount() {
        return listenList.size();
    }

}
