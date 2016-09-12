package com.onpuri.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Adapter.SenListenListAdapter;
import com.onpuri.Adapter.TestListAdapter;
import com.onpuri.Listener.HomeItemClickListener;
import com.onpuri.R;

import java.util.ArrayList;


public class TestSolveFragment extends Fragment implements View.OnClickListener{

    private static View view;

    ArrayList<String> list_test;

    private RecyclerView TestRecyclerView;
    private TestListAdapter TestListAdapter;

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
            view = inflater.inflate(R.layout.fragment_test_solve, container, false);
        } catch (InflateException e) {}

        testlist();

        LinearLayout setting = (LinearLayout) view.findViewById(R.id.testsetting);
        setting.setOnClickListener(this);

        TestRecyclerView = (RecyclerView) view.findViewById(R.id.test_list);

        TestListAdapter = new TestListAdapter(list_test, TestRecyclerView);
        TestRecyclerView.setAdapter(TestListAdapter);
        TestRecyclerView.addOnItemTouchListener(
                new HomeItemClickListener(getActivity().getApplicationContext(), TestRecyclerView ,new HomeItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final TestSolveStartFrgment tssf = new TestSolveStartFrgment();
                        FragmentManager fm = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putString("testname", list_test.get(position));
                        tssf.setArguments(args);

                        fm.beginTransaction()
                                .replace(R.id.root_test, tssf)
                                .addToBackStack("testlist")
                                .commit();
                    }

                    public void onLongItemClick(View view, int position) {}
                })
        );
        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.testsetting:
                LayoutInflater layout = getActivity().getLayoutInflater();
                final View setview = layout.inflate(R.layout.test_setting, null);

                final AlertDialog.Builder Setting = new AlertDialog.Builder(getActivity());
                Setting.setView(setview)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RadioGroup group1= (RadioGroup)setview.findViewById(R.id.radioGroup1);
                                RadioButton button1= (RadioButton)group1.findViewById(group1.getCheckedRadioButtonId());
                                TextView set_text1 = (TextView)view.findViewById(R.id.set1);
                                set_text1.setText(button1.getText().toString());
                            }
                        });

                AlertDialog alert = Setting.create();
                alert.show();
                break;
        }
    }
    void testlist() {
        list_test = new ArrayList<String>();
        this.list_test.add("70%  test님    문장 쪽지시험");
        this.list_test.add("80%  admin님   쪽지시험 2");
    }

}
