package com.onpuri.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.onpuri.R;
import com.onpuri.Server.PacketUser;


public class TransDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransAddFragment";
    private com.onpuri.Server.PacketUser user;
    private static View view;
    private Toast toast;

    String sentence="";
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
            trans = getArguments().getString("sen_trans");
            id = getArguments().getString("userid");
            day = getArguments().getString("day");
            reco = getArguments().getString("reco");
            num = getArguments().getString("num");

            item_sen.setText(sentence);
            item_trans.setText(trans);
            item_userid.setText(id);
            item_day.setText(day);
            item_reco.setText(reco);
        }
        String userid = ((MainActivity)getActivity()).user.getuserId();

        Button item_reco = (Button) view.findViewById(R.id.item_reco);
        item_reco.setOnClickListener(this);
        Button item_edit = (Button) view.findViewById(R.id.item_edit);
        item_edit.setOnClickListener(this);
        ImageButton deltrans=(ImageButton)view.findViewById(R.id.del_trans);
        deltrans.setOnClickListener(this);

        if (!id.equals(userid)) {
            deltrans.setVisibility(View.INVISIBLE);
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
        args.putString("sen", sentence);
        args.putString("sen_trans", trans);

        switch (v.getId()) {
            case R.id.item_reco:
                toast = Toast.makeText(getActivity(), "추천", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.item_edit:
                final TransEditFragment tef = new TransEditFragment();
                args.putString("sen", sentence);
                args.putString("sen_trans", trans);
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
                                fm.popBackStack();
                                ft.commit();
                                Toast.makeText(getActivity(), "삭제되었습니다(구현예정)", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                                Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                break;
        }
    }
}
