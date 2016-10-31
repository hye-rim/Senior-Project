package com.onpuri.Activity.Home.Adapter;

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

    private ArrayList<String> list_title;
    private ArrayList<String> list_id;
    private ArrayList<String> list_part;
    private ArrayList<String> list_quiz;

    public TestListAdapter(ArrayList<String> list_title, ArrayList<String> list_id, ArrayList<String> list_part, ArrayList<String> list_quiz, RecyclerView TransRecyclerView) {
        this.list_title=list_title;
        this.list_id=list_id;
        this.list_part=list_part;
        this.list_quiz=list_quiz;

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView test;
        public TextView userid;
        public TextView partcount;
        public TextView quizcount;

        public ItemViewHolder(View v) {
            super(v);
            test = (TextView) v.findViewById(R.id.test);
            userid = (TextView) v.findViewById(R.id.id);
            partcount = (TextView) v.findViewById(R.id.usercount);
            quizcount = (TextView) v.findViewById(R.id.quizcount);

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
        itemViewHolder.test.setText(list_title.get(position));
        itemViewHolder.userid.setText(list_id.get(position)+"님");
        itemViewHolder.partcount.setText(list_part.get(position));
        itemViewHolder.quizcount.setText(list_quiz.get(position));

    }

    @Override
    public int getItemCount() {
        return list_title.size();
    }

}