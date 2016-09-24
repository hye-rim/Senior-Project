package com.onpuri.Activity.Test.Solving;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.onpuri.Activity.Test.Solving.workerTestList;
import com.onpuri.Adapter.TestListAdapter;
import com.onpuri.Listener.HomeItemClickListener;
import com.onpuri.R;

import java.util.ArrayList;


public class TestSolveFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TestSolveFragment" ;
    private static View view;

    private workerTestList worker_test_list;

    ArrayList<String> list_title = new ArrayList<String>();
    ArrayList<String> list_id = new ArrayList<String>();
    ArrayList<String> list_part = new ArrayList<String>();
    ArrayList<String> list_quiz = new ArrayList<String>();
    ArrayList<String> list_num = new ArrayList<String>();

    private RecyclerView TestRecyclerView;
    private TestListAdapter TestListAdapter;

    Button btn_word, btn_sen;
    TextView btn_selftest;

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

        testlist("1");

        btn_word = (Button) view.findViewById(R.id.word);
        btn_word.setOnClickListener(this);
        btn_sen = (Button) view.findViewById(R.id.sen);
        btn_sen.setOnClickListener(this);
        btn_selftest = (TextView) view.findViewById(R.id.selftest);
        btn_selftest.setOnClickListener(this);

        TestRecyclerView = (RecyclerView) view.findViewById(R.id.test_list);

        TestListAdapter = new TestListAdapter(list_title, list_id, list_part, list_quiz, TestRecyclerView);
        TestRecyclerView.setAdapter(TestListAdapter);
        TestRecyclerView.addOnItemTouchListener(
                new HomeItemClickListener(getActivity().getApplicationContext(), TestRecyclerView ,new HomeItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final TestSolveStartFrgment tssf = new TestSolveStartFrgment();
                        FragmentManager fm = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putString("testname", list_title.get(position));
                        args.putString("testnum", list_num.get(position));
                        args.putString("testquiz", list_quiz.get(position));
                        tssf.setArguments(args);

                        fm.beginTransaction()
                                .replace(R.id.root_test, tssf)
                                .addToBackStack(null)
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
                testlist("2");
                TestListAdapter.notifyDataSetChanged();
                break;

            case R.id.word:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btn_word.setBackground(getResources().getDrawable(R.color.fuzzy_peach));
                    btn_sen.setBackground(getResources().getDrawable(R.drawable.btn_border));
                }else{
                    btn_word.setBackgroundResource((R.color.fuzzy_peach));
                    btn_sen.setBackgroundResource((R.drawable.btn_border));
                }
                testlist("1");
                TestListAdapter.notifyDataSetChanged();
                break;

            case R.id.selftest:
                final TestSolveStartFrgment tssf = new TestSolveStartFrgment();
                FragmentManager fm = getActivity().getSupportFragmentManager();

                Bundle args = new Bundle();
                args.putString("testname", "자가 시험");
                tssf.setArguments(args);

                fm.beginTransaction()
                        .replace(R.id.root_test, tssf)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    private void testlist(String num) {
        if(worker_test_list != null && worker_test_list.isAlive()){
            worker_test_list.interrupt();
        }
        worker_test_list = new workerTestList(true, num);
        worker_test_list.start();
        try {
            worker_test_list.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        list_title.clear();
        list_id.clear();
        list_part.clear();
        list_quiz.clear();
        list_num.clear();

        for (int i = 0; i < worker_test_list.getCount(); i++) {
            list_title.add(worker_test_list.getTitle().get(i).toString());
            list_id.add(worker_test_list.getUserid().get(i).toString());
            list_part.add(worker_test_list.getPart().get(i).toString());
            list_quiz.add(worker_test_list.getQuiz().get(i).toString());
            list_num.add(worker_test_list.getNum().get(i).toString());
        }
    }

}
