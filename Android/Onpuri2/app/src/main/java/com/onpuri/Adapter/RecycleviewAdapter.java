package com.onpuri.Adapter;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onpuri.Listener.EndlessRecyclerOnScrollListener;
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

    public RecycleviewAdapter(ArrayList<String> listSentence, RecyclerView recyclerView) {
        this.senList=listSentence;

    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mSenItem;

        public ItemViewHolder(View v) {
            super(v);
            mSenItem = (TextView) v.findViewById(R.id.tv_sen_item);
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar_home);
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
        Log.d(TAG, "Element " + position + " set.");

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.mSenItem.setText(senList.get(position));

        itemViewHolder.itemView.setBackgroundColor(Color.parseColor("#FDF5D2"));
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
