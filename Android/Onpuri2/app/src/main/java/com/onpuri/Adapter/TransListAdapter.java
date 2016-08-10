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
    private ArrayList<String> transList;
    private ArrayList<String> dayList;
    private ArrayList<String> recoList;

    public TransListAdapter(ArrayList<String> list_trans, ArrayList<String> list_day, ArrayList<String> list_reco,RecyclerView TransrecyclerView) {
        this.transList=list_trans;
        this.dayList=list_day;
        this.recoList=list_reco;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView trans;
        public TextView day;
        public TextView reco;
        public ItemViewHolder(View v) {
            super(v);
            trans = (TextView) v.findViewById(R.id.tv_trans_item);
            day = (TextView) v.findViewById(R.id.day);
            reco = (TextView) v.findViewById(R.id.reco);
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
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.trans.setText(transList.get(position));
        itemViewHolder.day.setText(dayList.get(position));
        itemViewHolder.reco.setText(recoList.get(position));
    }

    @Override
    public int getItemCount() {
        return transList.size();
    }

}
