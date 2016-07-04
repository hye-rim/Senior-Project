package com.onpuri.Activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onpuri.Adapter.RecycleviewAdapter;
import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-05-23.
 */
//내노트의 문장
public class NoteSentenceFragment extends Fragment {
    private static View view;

    ArrayList<String> listSentence;

    int i, index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_note_sentence, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }


        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}