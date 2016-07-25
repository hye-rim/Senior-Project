package com.onpuri.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.onpuri.Activity.NoteWordFragment;
import com.onpuri.Data.NoteData;
import com.onpuri.Data.WordData;
import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-11.
 */
public class NoteWordItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EDIT = 1;
    private final String TAG = "NoteWordItemAdapter";

    private ArrayList<WordData> wordItemList = new ArrayList<>();
    private Boolean isEdit;
    private Context context;

    public NoteWordItemAdapter(ArrayList<WordData> listWord, Boolean isEdit, Context context){
        wordItemList.addAll(listWord);
        this.isEdit = isEdit;
        this.context = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mWordItem, mWordItemMean;

        public ItemViewHolder(View v) {
            super(v);
            mWordItem = (TextView) v.findViewById(R.id.tv_note_word);
            mWordItemMean = (TextView) v.findViewById(R.id.tv_note_word_mean);
        }
        public TextView getTextViewWord() {  return mWordItem;  }
        public TextView getTextViewMean() { return mWordItemMean; }
    }

    public class EditViewHolder extends RecyclerView.ViewHolder {
        private TextView mWordEdit, mWordMeanEdit;
        private ImageButton mUp, mDown, mDelete;

        public EditViewHolder(View v) {
            super(v);
            mWordEdit = (TextView) v.findViewById(R.id.tv_note_word_edit);
            mWordMeanEdit = (TextView) v.findViewById(R.id.tv_note_word_mean_edit);
            mUp = (ImageButton)v.findViewById(R.id.btn_word_item_up);
            mDown = (ImageButton)v.findViewById(R.id.btn_word_item_down);
            mDelete = (ImageButton)v.findViewById(R.id.btn_word_item_delete);
        }
        public TextView getTextViewEdit() {  return mWordEdit;  }
        public TextView getTextViewMeanEdit() { return mWordMeanEdit; }
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
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_word_item_list, parent, false);
            return new ItemViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_word_item_edit, parent, false);
            return new EditViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        Log.d(TAG, "Word Item set. - " + position);
        if( getItemViewType(position) == VIEW_TYPE_ITEM) {
            ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
            itemViewHolder.mWordItem.setText(wordItemList.get(position).getWord());
            itemViewHolder.mWordItemMean.setText(wordItemList.get(position).getMean());
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Log.d(TAG, "Word List clicked.");
                }
            });
        }else {
            EditViewHolder editViewHolder = (EditViewHolder) holder;
            editViewHolder.getTextViewEdit().setText(wordItemList.get(position).getWord());
            editViewHolder.getTextViewMeanEdit().setText(wordItemList.get(position).getMean());
            editViewHolder.getButtonUp().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getAdapterPosition() > 0) {
                        WordData temp;
                        temp = wordItemList.remove(holder.getAdapterPosition());
                        wordItemList.add(holder.getAdapterPosition() - 1, temp);
                        notifyItemMoved(holder.getAdapterPosition(), holder.getAdapterPosition() - 1);

                    }
                }
            });
            editViewHolder.getButtonDown().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getAdapterPosition() < wordItemList.size() - 1) {
                        WordData temp;
                        temp = wordItemList.remove(holder.getAdapterPosition());
                        wordItemList.add(holder.getAdapterPosition() + 1, temp);
                        notifyItemMoved(holder.getAdapterPosition(), holder.getAdapterPosition() + 1);
                    }
                }
            });
            editViewHolder.getButtonDelete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wordItemList.remove(holder.getAdapterPosition());
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
        return wordItemList.size();
    }

}
