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

public class ListenListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "ListenListAdapter";
    private ArrayList<String> senList;

    public ListenListAdapter(ArrayList<String> list_listen, RecyclerView recyclerView) {
        this.senList=list_listen;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mSenItem;
        public ItemViewHolder(View v) {
            super(v);
            mSenItem = (TextView) v.findViewById(R.id.tv_listen_item);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listen_list, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "listen list " + position + " set.");
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.mSenItem.setText(senList.get(position));

    }

    @Override
    public int getItemCount() {
        return senList.size();
    }

}
