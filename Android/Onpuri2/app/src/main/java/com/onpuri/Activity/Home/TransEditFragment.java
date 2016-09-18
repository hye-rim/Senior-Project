package com.onpuri.Activity.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.onpuri.R;

/**
 * Created by kutemsys on 2016-07-21.
 */

public class TransEditFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransEditFragment";
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
            view = inflater.inflate(R.layout.fragment_trans_edit, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }

        item_sen = (TextView) view.findViewById(R.id.sentence);
        item_trans = (TextView)view.findViewById(R.id.edittrans);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            trans = getArguments().getString("sen_trans");
            item_sen.setText(sentence);
            item_trans.setText(trans);

        }

        Button btn_new_trans = (Button) view.findViewById(R.id.btn_new_trans);
        btn_new_trans.setOnClickListener(this);
        Button btn_new_trans_back = (Button) view.findViewById(R.id.btn_new_trans_back);
        btn_new_trans_back.setOnClickListener(this);

        return view;
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_new_trans:
                toast = Toast.makeText(getActivity(), "등록", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.btn_new_trans_back:
                toast = Toast.makeText(getActivity(), "취소", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

}
