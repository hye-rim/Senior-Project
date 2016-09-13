package com.onpuri.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Button;
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

    Button btn_word, btn_sen;
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

        btn_word = (Button) view.findViewById(R.id.word);
        btn_word.setOnClickListener(this);
        btn_sen = (Button) view.findViewById(R.id.sen);
        btn_sen.setOnClickListener(this);

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
            case R.id.sen:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btn_sen.setBackground(getResources().getDrawable(R.color.fuzzy_peach));
                    btn_word.setBackground(getResources().getDrawable(R.drawable.btn_border));
                }else{
                    btn_sen.setBackgroundResource((R.color.fuzzy_peach));
                    btn_word.setBackgroundResource((R.drawable.btn_border));
                }
                break;

            case R.id.word:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btn_word.setBackground(getResources().getDrawable(R.color.fuzzy_peach));
                    btn_sen.setBackground(getResources().getDrawable(R.drawable.btn_border));
                }else{
                    btn_word.setBackgroundResource((R.color.fuzzy_peach));
                    btn_sen.setBackgroundResource((R.drawable.btn_border));
                }
                break;
        }
    }
    void testlist() {
        list_test = new ArrayList<String>();
        this.list_test.add("70%  test님    문장 쪽지시험");
        this.list_test.add("80%  admin님   쪽지시험 2");
    }

}
