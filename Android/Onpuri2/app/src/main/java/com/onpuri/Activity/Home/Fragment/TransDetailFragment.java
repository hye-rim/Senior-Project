package com.onpuri.Activity.Home.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.Home.Thread.workerDelete;
import com.onpuri.Activity.Home.Thread.workerRecommend;
import com.onpuri.Activity.Home.Thread.workerTransDetail;
import com.onpuri.Activity.MainActivity;
import com.onpuri.R;


public class TransDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransDetailFragment";
    private workerRecommend worker_reco;
    private workerDelete worker_delete;
    private workerTransDetail worker_trans_detail;


    private static View view;

    String sentence="";
    String sen_num="";
    String trans="";
    String id="";
    String day="";
    String reco="";
    String num="";

    TextView item_sen;
    TextView item_trans;
    TextView item_userid;
    TextView item_day;
    TextView item_reco;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_trans_detail, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }

        item_sen = (TextView) view.findViewById(R.id.tv_sentence);
        item_trans = (TextView)view.findViewById(R.id.trans_item);
        item_userid = (TextView)view.findViewById(R.id.userid);
        item_day = (TextView)view.findViewById(R.id.day);
        item_reco = (TextView)view.findViewById(R.id.reco);

        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sen_num= getArguments().getString("sennum");
            trans = getArguments().getString("trans");
            num = getArguments().getString("num");
            id = getArguments().getString("id");
            day = getArguments().getString("day");

            item_sen.setText(sentence);
            item_trans.setText(trans);
            item_userid.setText(id+"님");
            item_day.setText(day);

            item_sen.setTextIsSelectable(true);
        }

        transrecommend(num);
        item_reco.setText(worker_trans_detail.getRecommend());

        Button item_reco = (Button) view.findViewById(R.id.item_reco);
        item_reco.setOnClickListener(this);
        Button item_edit = (Button) view.findViewById(R.id.item_edit);
        item_edit.setOnClickListener(this);
        ImageButton del_trans=(ImageButton)view.findViewById(R.id.del_trans);
        del_trans.setOnClickListener(this);

        String userid = ((MainActivity)getActivity()).user.getuserId();
        if (!id.equals(userid)) {
            del_trans.setVisibility(View.INVISIBLE);
        }

        return view;
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onClick(View v) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        final Bundle args = new Bundle();

        switch (v.getId()) {
            case R.id.item_edit:
                final TransEditFragment tef = new TransEditFragment();
                args.putString("sen", sentence);
                args.putString("sen_trans", trans);
                args.putString("sen_num", sen_num);
                tef.setArguments(args);
                ft.replace(R.id.root_home, tef);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.del_trans:
                new AlertDialog.Builder(getActivity())
                        .setTitle("선택한 해석을 삭제하시겠습니까?")
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
                                final FragmentManager fm = getActivity().getSupportFragmentManager();
                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                delete();
                                fm.popBackStack();
                                ft.commit();
                                Toast.makeText(getActivity(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                                Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                break;
            case R.id.item_reco:
                recommend();
                ft.detach(this).attach(this).commit();
        }
    }
    void recommend() {// 해석을 추천
        if (worker_reco != null && worker_reco.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_reco.interrupt();
        }
        worker_reco = new workerRecommend(true, "2+", num);
        worker_reco.start();
        try {
            worker_reco.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void delete() { //해석을 삭제
        if (worker_delete != null && worker_delete.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_delete.interrupt();
        }
        worker_delete = new workerDelete(true, "2+", num);
        worker_delete.start();
        try {
            worker_delete.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void transrecommend(String num) { //해석의 추천수 호출
        if (worker_trans_detail != null && worker_trans_detail.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker_trans_detail.interrupt();
        }
        worker_trans_detail = new workerTransDetail(true, num);
        worker_trans_detail.start();
        try {
            worker_trans_detail.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
