package com.onpuri.Activity.SideTab.Act;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-11.
 */
public class MyActTestAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "MyActTestAdapter";
    private final String ALL = "0";

    private ArrayList<String> testTitleList;
    private ArrayList<String> testPercentList;
    private ArrayList<String> testKinds;
    private Context mContext;

    public MyActTestAdapter(Context context, ArrayList<String> testTitle, ArrayList<String> testPercent, ArrayList<String> testKinds, RecyclerView recyclerView) {
        this.testTitleList = testTitle;
        this.testPercentList = testPercent;
        this.testKinds = testKinds;
        this.mContext = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mTestName;
        public TextView mTestPer;
        public TextView mTestKinds;

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
            mTestKinds = (TextView) v.findViewById(R.id.tv_act_test_kinds);

        }

        public TextView getmTestName() {
            return mTestName;
        }
        public TextView getmTestPer() {
            return mTestPer;
        }
        public TextView getmTestKinds() {
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
        itemViewHolder.mTestName.setText(testTitleList.get(position));

        if( ALL.compareTo(testKinds.get(position).toString()) == 0){
            itemViewHolder.mTestPer.setVisibility(View.GONE);
            itemViewHolder.mTestKinds.setText("전체");
        }
        else{
            itemViewHolder.mTestPer.setText(testPercentList.get(position) + "%");
            itemViewHolder.mTestKinds.setText("지정");
        }
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
    }

    @Override
    public int getItemCount() {
        return testTitleList.size();
    }
}
