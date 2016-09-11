package com.onpuri.Activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

public class TestSolveSenFragment extends Fragment {

    private static View view;

    TextView testname;
    String name="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_test_solve_word, container, false);
        } catch (InflateException e) {
        }

        testname = (TextView) view.findViewById(R.id.testname);
        if (getArguments() != null) { //클릭한 문장 출력
            name = getArguments().getString("testname");
            testname.setText(name);
        }

        return view;
    }


}
