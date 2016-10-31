package com.onpuri.Activity.Home.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.onpuri.Activity.Home.Fragment.ListenMoreFragment;
import com.onpuri.Activity.Home.Thread.workerDelete;
import com.onpuri.Activity.Home.Thread.workerRecommend;
import com.onpuri.Activity.Home.MediaPlayerManager;
import com.onpuri.R;

import java.util.ArrayList;

public class ListenListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final String TAG = "ListenListAdapter";

    private workerRecommend worker_reco;
    private workerDelete worker_delete;

    private final FragmentActivity activity;

    private ArrayList<String> listenList;
    private ArrayList<String> useridList;
    private ArrayList<String> recoList;
    private ArrayList<String> numList;

    public ImageButton del_listen;

    MediaPlayerManager mPlayer;
    Context con;
    FragmentManager fm;
    ListenMoreFragment fragment;
    String id;

    public ListenListAdapter(ListenMoreFragment fragment, FragmentActivity activity, MediaPlayerManager mPlayer, String id,
                             ArrayList<String> list_listen, ArrayList<String> list_userid, ArrayList<String> list_reco, ArrayList<String> list_num, Context con, FragmentManager fm, RecyclerView recyclerView) {
        this.listenList=list_listen;
        this.useridList=list_userid;
        this.recoList=list_reco;
        this.numList=list_num;
        this.con = con;
        this.fm = fm;
        this.mPlayer = mPlayer;
        this.id = id;
        this.activity = activity;
        this.fragment = fragment;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView listen;
        public TextView reco;
        public ImageButton reco_listen;

        public ItemViewHolder(View v) {
            super(v);
            listen = (TextView) v.findViewById(R.id.tv_listen_item);
            reco = (TextView) v.findViewById(R.id.reco);
            reco_listen = (ImageButton) v.findViewById(R.id.reco_bnt);
            del_listen = (ImageButton) v.findViewById(R.id.del_listen);

            listen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayer.PlayFile(numList.get(getPosition()));
                }
            });

            del_listen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(activity)
                            .setTitle("선택한 듣기를 삭제하시겠습니까?")
                            .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if(keyCode == KeyEvent.KEYCODE_BACK){
                                        dialog.dismiss();
                                        return true;
                                    }
                                    return false;
                                }
                            })
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final FragmentManager fm = activity.getSupportFragmentManager();
                                    final FragmentTransaction ft = fm.beginTransaction();
                                    delete(numList.get(getPosition()));
                                    activity.getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                                    fm.popBackStack();
                                    ft.commit();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dlg, int sumthin) {}
                            }).show();
                }
            });

            reco_listen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recommend(numList.get(getPosition()));
                    activity.getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
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
        itemViewHolder.reco.setText(recoList.get(position));

        if (!id.equals(useridList.get(position))) {
            del_listen.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listenList.size();
    }

    void recommend(String num) {
        if (worker_reco != null && worker_reco.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_reco.interrupt();
        }
        worker_reco = new workerRecommend(true, "3+", num);
        worker_reco.start();
        try {
            worker_reco.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void delete(String num) {
        if (worker_delete != null && worker_delete.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_delete.interrupt();
        }
        worker_delete = new workerDelete(true, "3+", num);
        worker_delete.start();
        try {
            worker_delete.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
