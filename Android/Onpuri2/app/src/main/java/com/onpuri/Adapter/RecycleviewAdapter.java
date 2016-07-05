package com.onpuri.Adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-04.
 */
public class RecycleviewAdapter extends RecyclerView.Adapter<RecycleviewAdapter.ViewHolder> {
    private final String TAG = "RecycleviewAdapter";
    private ArrayList<String> senList = new ArrayList<String>();

    public TextView mSenItem;

    public RecycleviewAdapter(ArrayList<String> listSentence) {
        senList.addAll(listSentence);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });

            mSenItem = (TextView) v.findViewById(R.id.tv_sen_item);

        }

        public TextView getTextView() {
            return mSenItem;
        }
    }

    //create new views(invoked by the layout manager)
    @Override
    public RecycleviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecycleviewAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");
        // Get element from your dataset at this position and replace the contents of the view with that element
        holder.getTextView().setText(senList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

            }
        });

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FEE098"));
            holder.getTextView().setTypeface(null, Typeface.NORMAL);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#faf5b3"));
            holder.getTextView().setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public int getItemCount() {
        return senList.size();
    }

}
