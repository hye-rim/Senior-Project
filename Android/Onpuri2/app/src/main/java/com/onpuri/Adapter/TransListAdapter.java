package com.onpuri.Adapter;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.Home.TransDetailFragment;
import com.onpuri.Activity.Home.workerRecommend;
import com.onpuri.R;

import java.util.ArrayList;

public class TransListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "TransListAdapter";

    private workerRecommend worker_reco;

    String sen;
    String sen_num;
    private ArrayList<String> transList;
    private ArrayList<String> dayList;
    private ArrayList<String> recoList;
    private ArrayList<String> numList;

    FragmentTransaction ft;

    public TransListAdapter(String sen, String sen_num, ArrayList<String> list_trans, ArrayList<String> list_day, ArrayList<String> list_reco, ArrayList<String> list_num, FragmentTransaction ft, RecyclerView TransrecyclerView) {
        this.sen=sen;
        this.sen_num=sen_num;
        this.transList=list_trans;
        this.dayList=list_day;
        this.recoList=list_reco;
        this.numList=list_num;
        this.ft=ft;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView trans;
        public TextView reco;
        public ImageButton reco_trans;

        public ItemViewHolder(View v) {
            super(v);
            trans = (TextView) v.findViewById(R.id.tv_trans_item);
            reco = (TextView) v.findViewById(R.id.reco);
            reco_trans = (ImageButton) v.findViewById(R.id.reco_bnt);

            trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TransDetailFragment tdf = new TransDetailFragment();

                    Bundle args = new Bundle();
                    args.putString("sen", sen);
                    args.putString("sennum", sen_num);
                    args.putString("num", numList.get(getPosition()));
                    tdf.setArguments(args);

                    ft.replace(R.id.root_home, tdf)
                            .addToBackStack(null)
                            .commit();
                }
            });

            reco_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recommend(numList.get(getPosition()));

                }
            });
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
        itemViewHolder.reco.setText(recoList.get(position));
    }

    @Override
    public int getItemCount() {
        return transList.size();
    }

    void recommend(String num) {
        if (worker_reco != null && worker_reco.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_reco.interrupt();
        }
        worker_reco = new workerRecommend(true, "2+", num);
        worker_reco.start();
        try {
            worker_reco.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
