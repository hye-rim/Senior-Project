package com.onpuri.Activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onpuri.Adapter.SenListenListAdapter;
import com.onpuri.Adapter.TestListAdapter;
import com.onpuri.R;


public class TestSolveFragment extends Fragment {

    private static View view;

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

        TestRecyclerView = (RecyclerView) view.findViewById(R.id.test_list);

        TestListAdapter = new TestListAdapter(TestRecyclerView);
        TestRecyclerView.setAdapter(TestListAdapter);

        return view;
    }

}
