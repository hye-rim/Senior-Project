package com.onpuri.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-04.
 */
public class NoteWordAdapter extends RecyclerView.Adapter<NoteWordAdapter.ViewHolder>  {
    private final String TAG = "NoteWordAdapter";
    private static final int VIEW_TYPE_CELL = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private ArrayList<String> noteWordList = new ArrayList<String>();

    public TextView mWordItem;
    public Button mWordAdd;

    public NoteWordAdapter(ArrayList<String> listWord) {
        noteWordList.addAll(listWord);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Word List " + getAdapterPosition() + " clicked.");
                }
            });

            mWordItem = (TextView) v.findViewById(R.id.note_word_item);
            mWordAdd = (Button) v.findViewById(R.id.btn_note_add_word);
        }

        public TextView getTextView() {
            return mWordItem;
        }
    }

    //create new views(invoked by the layout manager)
    @Override
    public NoteWordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == VIEW_TYPE_CELL){
            //Create viewholder for your default cell
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_word_list, parent, false);
        }
        else {
            //Create viewholder for your footer view
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_word_btn, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteWordAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "Word List " + position + " set.");
        // Get element from your dataset at this position and replace the contents of the view with that element
        if(position < noteWordList.size())
            holder.getTextView().setText(noteWordList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Log.d(TAG, "Word List " + position + " clicked.");
            }
        });

    }

    @Override
    public int getItemCount() {
        return noteWordList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == noteWordList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }
}
