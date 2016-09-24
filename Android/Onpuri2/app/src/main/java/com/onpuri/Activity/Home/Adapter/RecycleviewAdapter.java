package com.onpuri.Activity.Home.Adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.Activity.Home.Fragment.HomeSentenceFragment;
import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-04.
 */
public class RecycleviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "RecycleviewAdapter";
    private static final int VIEW_TYPE_CELL = 1;
    private static final int VIEW_TYPE_FOOTER = 0;
    private ArrayList<String> senList;
    private ArrayList<String> sennumList;
    private ArrayList<String> transList;
    private ArrayList<String> listenList;
    private ArrayList<String> IdList;
    private ArrayList<String> recoList;
    FragmentActivity activity;

    public RecycleviewAdapter(FragmentActivity activity, ArrayList<String> listSentence, ArrayList<String> listSentenceNum,ArrayList<String> listTransNum, ArrayList<String> listListenNum,
                              ArrayList<String> listId, ArrayList<String> listReco, RecyclerView recyclerView) {
        this.senList=listSentence;
        this.sennumList=listSentenceNum;
        this.transList=listTransNum;
        this.listenList=listListenNum;
        this.IdList=listId;
        this.recoList=listReco;
        this.activity=activity;


    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mSenItem;
        public TextView mTransItem;
        public TextView mListenItem;
        public TextView mSenId;
        public TextView mRecoItem;

        public ItemViewHolder(View v) {
            super(v);
            mSenItem = (TextView) v.findViewById(R.id.tv_sen_item);
            mTransItem = (TextView) v.findViewById(R.id.transcount);
            mListenItem = (TextView) v.findViewById(R.id.listencount);
            mSenId = (TextView) v.findViewById(R.id.id);
            mRecoItem = (TextView) v. findViewById(R.id.recocount);

            mSenItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeSentenceFragment hsf = new HomeSentenceFragment();
                    FragmentManager fm = activity.getSupportFragmentManager();

                    Bundle args = new Bundle();
                    args.putString("sen", senList.get(getPosition()));
                    args.putString("sen_num", sennumList.get(getPosition()));
                    args.putString("id", IdList.get(getPosition()));
                    hsf.setArguments(args);

                    fm.beginTransaction()
                            .replace(R.id.root_home, hsf)
                            .addToBackStack(null)
                            .commit();
                }
            });

        }
    }

    //create new views(invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.mSenItem.setText(senList.get(position));
        itemViewHolder.mTransItem.setText(transList.get(position));
        itemViewHolder.mListenItem.setText(listenList.get(position));
        itemViewHolder.mSenId.setText(IdList.get(position)+"님");
        itemViewHolder.mRecoItem.setText(recoList.get(position));

        itemViewHolder.itemView.setBackgroundColor(Color.WHITE);
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
