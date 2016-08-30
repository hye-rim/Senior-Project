package com.onpuri.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.NoteSenFragment;
import com.onpuri.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ListenListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "ListenListAdapter";

    private ArrayList<String> listenList;
    private ArrayList<String> useridList;
    private ArrayList<String> dayList;
    private ArrayList<String> recoList;
    private ArrayList<String> numList;

    Context con;
    FragmentManager fm;

    MediaPlayer mPlayer = null;

    public ListenListAdapter(ArrayList<String> list_listen, ArrayList<String> list_userid, ArrayList<String> list_day, ArrayList<String> list_reco, ArrayList<String> list_num, Context con, FragmentManager fm, RecyclerView recyclerView) {
        this.listenList=list_listen;
        this.useridList=list_userid;
        this.dayList=list_day;
        this.recoList=list_reco;
        this.numList=list_num;
        this.con = con;
        this.fm = fm;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView listen;
        public TextView userid;
        public TextView day;
        public TextView reco;
        public Button reco_bnt;

        public ItemViewHolder(View v) {
            super(v);
            listen = (TextView) v.findViewById(R.id.tv_listen_item);
            userid = (TextView) v.findViewById(R.id.userid);
            day = (TextView) v.findViewById(R.id.day);
            reco = (TextView) v.findViewById(R.id.reco);
            reco_bnt = (Button) v.findViewById(R.id.tv_reco_item);

            listen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"듣기");
                    PlayFile(numList.get(getPosition()));
                }
            });

            reco_bnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"추천");
                }
            });
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listen_list, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.listen.setText(listenList.get(position));
        itemViewHolder.userid.setText(useridList.get(position));
        itemViewHolder.day.setText(dayList.get(position));
        itemViewHolder.reco.setText(recoList.get(position));
    }

    @Override
    public int getItemCount() {
        return listenList.size();
    }

    //파일 경로
    public static synchronized String GetFilePath(String filenum) {
        String sdcard = Environment.getExternalStorageState();
        File file;

        if ( !sdcard.equals(Environment.MEDIA_MOUNTED)) { file = Environment.getRootDirectory(); }
        else { file = Environment.getExternalStorageDirectory(); }

        String dir = file.getAbsolutePath();
        String path = dir + "/Daily E/"+filenum+"listen.mp3";

        return path;
    }

    public void PlayFile(String filenum) {
        String path = GetFilePath(filenum);

        if( mPlayer != null ) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        mPlayer = new MediaPlayer();

        try {
            mPlayer.setDataSource(path);
            mPlayer.prepare();
        } catch(IOException e) {
            Log.d(TAG, "Audio Play error");
            return;
        }
        mPlayer.start();
    }
}
