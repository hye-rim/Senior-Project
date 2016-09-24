package com.onpuri.Activity.Home.Adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.onpuri.Activity.Home.Fragment.TransDetailFragment;
import com.onpuri.Activity.Home.Fragment.TransMoreFragment;
import com.onpuri.Activity.Home.Thread.workerRecommend;
import com.onpuri.Activity.Home.Thread.workerTransMore;
import com.onpuri.R;

import java.util.ArrayList;

public class TransListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "TransListAdapter";
    private workerTransMore worker_trans_more;
    private workerRecommend worker_reco;

    String sen;
    String sen_num;

    private ArrayList<String> transList;
    private ArrayList<String> useridList;
    private ArrayList<String> dayList;
    private ArrayList<String> recoList;
    private ArrayList<String> numList;

    FragmentTransaction ft;
    TransMoreFragment fragment;

    public TransListAdapter(TransMoreFragment fragment, String sen, String sen_num, FragmentTransaction ft, RecyclerView TransrecyclerView) {
        this.sen=sen;
        this.sen_num=sen_num;
        this.ft=ft;
        this.fragment=fragment;
        transList = new ArrayList<String>();
        useridList = new ArrayList<String>();
        dayList = new ArrayList<String>();
        recoList = new ArrayList<String>();
        numList = new ArrayList<String>();
        translation();

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
                    args.putString("trans", transList.get(getPosition()));
                    args.putString("num", numList.get(getPosition()));
                    args.putString("id", useridList.get(getPosition()));
                    args.putString("day", dayList.get(getPosition()));
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
                    translation();
                    ft.detach(fragment).attach(fragment).commit();
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

    private void translation() {
        if (worker_trans_more != null && worker_trans_more.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_trans_more.interrupt();
        }
        worker_trans_more = new workerTransMore(true, sen_num);
        worker_trans_more.start();
        try {
            worker_trans_more.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        transList.clear();
        useridList.clear();
        dayList.clear();
        recoList.clear();
        numList.clear();

        for (int i = 0; i < worker_trans_more.getCount(); i++) {
            transList.add(worker_trans_more.getTrans().get(i).toString());
            useridList.add(worker_trans_more.getUserid().get(i).toString());
            dayList.add(worker_trans_more.getDay().get(i).toString());
            recoList.add(worker_trans_more.getReco().get(i).toString());
            numList.add(worker_trans_more.getTransnum().get(i).toString());
        }
    }
}
