package com.onpuri.Activity.Home.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

public class SenListenListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "SenListenListAdapter";
    private ArrayList<String> listenList;
    private ArrayList<String> listenRecoList;

    public SenListenListAdapter(ArrayList<String> list_listen, ArrayList<String> list_listen_reco,RecyclerView LIstenRecyclerView) {
        this.listenList=list_listen;
        this.listenRecoList=list_listen_reco;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView listen;
        public TextView reco;
        public ItemViewHolder(View v) {
            super(v);
            listen = (TextView) v.findViewById(R.id.item);
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
        itemViewHolder.listen.setText(listenList.get(position));
        itemViewHolder.reco.setText(listenRecoList.get(position));
    }

    @Override
    public int getItemCount() {
        return listenList.size();
    }

}
