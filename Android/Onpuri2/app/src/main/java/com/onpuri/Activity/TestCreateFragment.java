package com.onpuri.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.hrules.horizontalnumberpicker.HorizontalNumberPickerListener;
import com.onpuri.Adapter.TestListAdapter;
import com.onpuri.Listener.HomeItemClickListener;
import com.onpuri.R;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-09-11.
 */
public class TestCreateFragment extends Fragment implements View.OnClickListener, HorizontalNumberPickerListener {
    private static final String TAG = "TestCreateFragment" ;
    HorizontalNumberPicker horizontalNumberPicker1;

    private static View view;

    ArrayList<String> list_test;

    private RecyclerView TestRecyclerView;
    private com.onpuri.Adapter.TestListAdapter TestListAdapter;

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
            view = inflater.inflate(R.layout.fragment_test_create, container, false);
        } catch (InflateException e) {}

        horizontalNumberPicker1 = (HorizontalNumberPicker)view.findViewById(R.id.horizontal_number_picker1);
        horizontalNumberPicker1.setMinValue(1);
        horizontalNumberPicker1.setMaxValue(20);
        horizontalNumberPicker1.getTextValueView()
                .setTextColor(getResources().getColor(android.R.color.black));
        horizontalNumberPicker1.getButtonMinusView()
                .setTextColor(getResources().getColor(android.R.color.black));
        horizontalNumberPicker1.getButtonPlusView()
                .setTextColor(getResources().getColor(android.R.color.black));

        horizontalNumberPicker1.getTextValueView().setTextSize(20);
        horizontalNumberPicker1.getButtonMinusView().setTextSize(18);
        horizontalNumberPicker1.getButtonPlusView().setTextSize(20);

        horizontalNumberPicker1.setListener(this);

        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.testsetting:

        }
    }

    @Override
    public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker, int value) {
        if (horizontalNumberPicker.getId() == R.id.horizontal_number_picker1) {
            Log.d(TAG, "horizontal_number_picker1 current value:" + value);
        }
    }
}

