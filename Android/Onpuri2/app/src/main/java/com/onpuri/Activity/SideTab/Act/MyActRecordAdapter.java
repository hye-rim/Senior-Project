package com.onpuri.Activity.SideTab.Act;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-07-17.
 */
public class MyActRecordAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "MyActRecordAdapter";

    private ArrayList<String> senList;

    public MyActRecordAdapter(ArrayList<String> listSentence, RecyclerView recyclerView) {
        this.senList=listSentence;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mSenItem;
        public ItemViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });

            mSenItem = (TextView) v.findViewById(R.id.tv_act_record);

        }

        public TextView getTextView() {
            return mSenItem;
        }
    }

    //create new views(invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        //Create viewholder for your default cell
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_act_record_list, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");
        // Get element from your dataset at this position and replace the contents of the view with that element

        ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
        itemViewHolder.mSenItem.setText(senList.get(position));
        itemViewHolder.mSenItem.setMaxLines(2);
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
    }

    @Override
    public int getItemCount() {
        return senList.size();
    }

}
