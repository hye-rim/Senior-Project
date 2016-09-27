package com.onpuri.Activity.Test.Solving;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;

public class TestSolveStartFrgment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TestSolveStartFrgment";

    private static View view;

    TextView testname, mTestNumTextView;
    String name, num, quiz;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_test_solve_start, container, false);
        } catch (InflateException e) {}


        testname = (TextView) view.findViewById(R.id.testname);
        mTestNumTextView = (TextView)view.findViewById(R.id.test_num);
        if (getArguments() != null) { //클릭한 문장 출력
            name = getArguments().getString("testname","null");
            num = getArguments().getString("testnum");
            quiz = getArguments().getString("testquiz");
            testname.setText(name);
            mTestNumTextView.setText("문제 수\n"+quiz);
        }

        Button start = (Button) view.findViewById(R.id.start);
        start.setOnClickListener(this);
        Button back = (Button) view.findViewById(R.id.back);
        back.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();

        switch (v.getId()) {
            case R.id.start:
                if(quiz != "0") {
                    final TestSolveWordFragment tswf = new TestSolveWordFragment();

                    Bundle args = new Bundle();
                    args.putString("testname", name);
                    args.putString("testnum", num);
                    tswf.setArguments(args);

                    fm.beginTransaction()
                            .replace(R.id.root_test, tswf)
                            .addToBackStack(null)
                            .commit();
                }
                else{

                }
                break;

            case R.id.back:
                fm.popBackStack();
                fm.beginTransaction().commit();
        }
    }
}
