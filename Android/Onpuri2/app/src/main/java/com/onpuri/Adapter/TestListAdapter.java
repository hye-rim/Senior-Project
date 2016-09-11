package com.onpuri.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-10.
 */
public class TestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "TestListAdapter";
    private ArrayList<String> testList = null;

    public TestListAdapter(RecyclerView TransRecyclerView) {
        testList = new ArrayList<String>();
        this.testList.add("문장 쪽지시험");
        this.testList.add("쪽지시험 2");
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView test;

        public ItemViewHolder(View v) {
            super(v);
            test = (TextView) v.findViewById(R.id.test);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_list, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.test.setText(testList.get(position));
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

}