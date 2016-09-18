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

import com.onpuri.R;

public class TestSolveFinishFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TestSolveFinishFrgment";

    private static View view;

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
            view = inflater.inflate(R.layout.fragment_test_solve_finish, container, false);
        } catch (InflateException e) {}

        Button finish = (Button) view.findViewById(R.id.finish);
        finish.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();

        switch (v.getId()) {
            case R.id.finish:
                FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(0);
                fm.popBackStack(entry.getId(), 0);
                fm.beginTransaction().commit();
        }
    }
}
