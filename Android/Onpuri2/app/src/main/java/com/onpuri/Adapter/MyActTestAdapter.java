package com.onpuri.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-11.
 */
public class MyActTestAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "MyActTestAdapter";

    private ArrayList<String> senList;
    private Context mContext;

    public MyActTestAdapter(Context context, ArrayList<String> listSentence, RecyclerView recyclerView) {
        this.senList=listSentence;
        this.mContext = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mTestName;
        public TextView mTestPer;


        public ItemViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });

            mTestName = (TextView) v.findViewById(R.id.tv_act_test_name);
            mTestPer = (TextView) v.findViewById(R.id.tv_act_test_per);

        }

        public TextView getTextView() {
            return mTestName;
        }
        public TextView getmTestPer() {
            return mTestPer;
        }
    }

    //create new views(invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        //Create viewholder for your default cell
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_act_test_list, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");
        // Get element from your dataset at this position and replace the contents of the view with that element

        ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
        itemViewHolder.mTestName.setText(senList.get(position));
        itemViewHolder.mTestPer.setText("0%");
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
