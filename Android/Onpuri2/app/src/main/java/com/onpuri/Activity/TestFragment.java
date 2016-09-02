package com.onpuri.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.onpuri.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by kutemsys on 2016-09-02.
 */
public class TestFragment  extends Fragment implements View.OnClickListener{
    private static final String TAG = "TestFragment";

    private static View view;

    private Button btn_solving, btn_creating;

    ViewPager viewPager;

    int i;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_test, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        viewPager = (ViewPager)getActivity().findViewById(R.id.viewpager);

        btn_solving = (Button)view.findViewById(R.id.btn_test_solving);
        btn_creating = (Button)view.findViewById(R.id.btn_test_creating);
        btn_solving.setOnClickListener(this);
        btn_creating.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_test_solving:
                break;

            case R.id.btn_test_creating:
                break;

            default:
                break;
        }
    }
}
