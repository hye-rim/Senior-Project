package com.onpuri.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.onpuri.R;

/**
 * Created by kutemsys on 2016-05-03.
 */
//문장등록 tab
public class NewSenFragment extends Fragment implements View.OnClickListener {
    //private ArrayList<View> history;
    private static View view;
    private Button btn_ok, btn_cancel, btn_gallery, btn_camera;

    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_new_sen, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        viewPager = (ViewPager)getActivity().findViewById(R.id.viewpager);

        btn_ok = (Button)view.findViewById(R.id.btn_new_sen);
        btn_cancel = (Button)view.findViewById(R.id.btn_new_sen_back);
        btn_gallery = (Button)view.findViewById(R.id.btn_new_picture);
        btn_camera = (Button)view.findViewById(R.id.btn_new_camera);

        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);
        btn_camera.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_new_sen:
                Toast.makeText(getActivity(), "서버와의 데이터 교환은 차후 구현 예정입니다.", Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(1);
                break;

            case R.id.btn_new_sen_back:
                viewPager.setCurrentItem(1);
                break;

            case R.id.btn_new_picture:
                Toast.makeText(getActivity(), "갤러리 기능은 차후 구현 예정입니다.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_new_camera:
                Toast.makeText(getActivity(), "카메라 기능은 차후 구현 예정입니다.", Toast.LENGTH_SHORT).show();
                checkVersion();
                break;

            default:
                break;
        }
    }

    private void checkVersion() {
        //현재 사용자 os 버전 체크
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

        //}
    }

}
