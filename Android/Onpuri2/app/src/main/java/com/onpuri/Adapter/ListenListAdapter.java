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
    private ArrayList<String> listenList;
    private ArrayList<String> useridList;
    private ArrayList<String> dayList;
    private ArrayList<String> recoList;

    public ListenListAdapter(ArrayList<String> list_listen, ArrayList<String> list_userid, ArrayList<String> list_day, ArrayList<String> list_reco, RecyclerView recyclerView) {
        this.listenList=list_listen;
        this.useridList=list_userid;
        this.dayList=list_day;
        this.recoList=list_reco;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView listen;
        public TextView userid;
        public TextView day;
        public TextView reco;
        public ItemViewHolder(View v) {
            super(v);
            listen = (TextView) v.findViewById(R.id.tv_listen_item);
            userid = (TextView) v.findViewById(R.id.userid);
            day = (TextView) v.findViewById(R.id.day);
            reco = (TextView) v.findViewById(R.id.reco);

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
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.listen.setText(listenList.get(position));
        itemViewHolder.userid.setText(useridList.get(position));
        itemViewHolder.day.setText(dayList.get(position));
        itemViewHolder.reco.setText(recoList.get(position));
    }

    @Override
    public int getItemCount() {
        return listenList.size();
    }

}
