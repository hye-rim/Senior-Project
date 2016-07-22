package com.onpuri.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.onpuri.R;


public class TransDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransAddFragment";
    private static View view;
    private Toast toast;

    String sentence="";
    String trans="";
    TextView item_sen;
    TextView item_trans;

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
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            trans = getArguments().getString("sen_trans");
            item_sen.setText(sentence);
            item_trans.setText(trans);
        }
        Button add_note = (Button) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        Button item_reco = (Button) view.findViewById(R.id.item_reco);
        item_reco.setOnClickListener(this);
        Button item_edit = (Button) view.findViewById(R.id.item_edit);
        item_edit.setOnClickListener(this);

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
            case R.id.add_note:
                final CharSequence[] items = {"노트1", "노트2", "노트3"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("노트를 선택해 주세요(노트 연동은 구현 예정)")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                Toast.makeText(getActivity(), items[index] + "선택", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                            }
                        }).show();
                break;
            case R.id.item_reco:
                toast = Toast.makeText(getActivity(), "추천", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.item_edit:
                final TransEditFragment tef = new TransEditFragment();
                args.putString("sen", sentence);
                args.putString("sen_trans", trans);
                tef.setArguments(args);
                ft.replace(R.id.root_frame, tef);
                ft.addToBackStack(null);
                ft.commit();
                break;
        }
    }

}
