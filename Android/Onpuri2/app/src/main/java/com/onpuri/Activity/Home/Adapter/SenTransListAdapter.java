package com.onpuri.Activity.Home.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

public class SenTransListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "SenTransListAdapter";
    private ArrayList<String> transList;
    private ArrayList<String> transRecoList;


    public SenTransListAdapter(ArrayList<String> list_trans, ArrayList<String> list_trans_reco, RecyclerView TransRecyclerView) {
        this.transList=list_trans;
        this.transRecoList=list_trans_reco;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView trans;
        public TextView reco;
        public ItemViewHolder(View v) {
            super(v);
            trans = (TextView) v.findViewById(R.id.item);
            reco = (TextView) v.findViewById(R.id.reco);
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
        itemViewHolder.reco.setText(transRecoList.get(position));
    }

    @Override
    public int getItemCount() {
        return transList.size();
    }

}
