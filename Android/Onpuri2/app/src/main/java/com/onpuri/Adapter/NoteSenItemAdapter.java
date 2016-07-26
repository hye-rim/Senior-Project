package com.onpuri.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-11.
 */
public class NoteSenItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EDIT = 1;
    private final String TAG = "NoteSenItemAdapter";

    private ArrayList<String> senItemList = new ArrayList<>();
    private Boolean isEdit;

    private TextView mSenItem;
    private TextView mSenEdit;

    private ImageButton mUp, mDown, mDelete;

    public NoteSenItemAdapter(ArrayList<String> listSentence, Boolean isEdit) {
        senItemList.addAll(listSentence);
        this.isEdit = isEdit;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View v) {
            super(v);
            mSenItem = (TextView) v.findViewById(R.id.tv_note_sen);
        }
        public TextView getTextView() {  return mSenItem;  }
    }

    public class EditViewHolder extends RecyclerView.ViewHolder {
        public EditViewHolder(View v) {
            super(v);
            mSenEdit = (TextView) v.findViewById(R.id.tv_note_sen_edit);
            mUp = (ImageButton)v.findViewById(R.id.btn_sen_item_up);
            mDown = (ImageButton)v.findViewById(R.id.btn_sen_item_down);
            mDelete = (ImageButton)v.findViewById(R.id.btn_sen_item_delete);
        }
        public TextView getTextViewEdit() {  return mSenEdit;  }
        public ImageButton getButtonUp() { return mUp; }
        public ImageButton getButtonDown() { return mDown; }
        public ImageButton getButtonDelete() { return mDelete; }
    }
    //create new views(invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == VIEW_TYPE_ITEM) {
            //Create viewholder for your default cell
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_sen_item_list, parent, false);
            return new ItemViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_sen_item_edit, parent, false);
            return new EditViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        Log.d(TAG, "Sentence Item set. - " + position);
        if( getItemViewType(position) == VIEW_TYPE_ITEM) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.getTextView().setText(senItemList.get(position));
            itemViewHolder.getTextView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Sentence List clicked.");
                }
            });
        }else {
            EditViewHolder editViewHolder = (EditViewHolder) holder;
            editViewHolder.getTextViewEdit().setText(senItemList.get(position));
            editViewHolder.getButtonUp().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getAdapterPosition() > 0) {
                        String temp;
                        temp = senItemList.remove(holder.getAdapterPosition());
                        senItemList.add(holder.getAdapterPosition() - 1, temp);
                        notifyItemMoved(holder.getAdapterPosition(), holder.getAdapterPosition() - 1);
                    }
                }
            });
            editViewHolder.getButtonDown().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getAdapterPosition() < senItemList.size() - 1 ) {
                        String temp;
                        temp = senItemList.remove(holder.getAdapterPosition());
                        senItemList.add(holder.getAdapterPosition() + 1, temp);
                        notifyItemMoved(holder.getAdapterPosition(), holder.getAdapterPosition() + 1);
                    }
                }
            });
            editViewHolder.getButtonDelete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    senItemList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (isEdit ? VIEW_TYPE_EDIT : VIEW_TYPE_ITEM);
    }


    @Override
    public int getItemCount() {
        return senItemList.size();
    }

}
