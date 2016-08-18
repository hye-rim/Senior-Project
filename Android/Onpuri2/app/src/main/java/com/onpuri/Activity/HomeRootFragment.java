package com.onpuri.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.onpuri.R;


public class HomeRootFragment extends Fragment {

    private static final String TAG = "HomeRootFragment";
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_root, container, false);

        //데이터 받아오기
        userId = null;
        userId = getArguments().getString("UserId"); //UserId라는 키에 해당되는 String 값 받아옴

        Toast.makeText(getActivity(), "userId : " + userId, Toast.LENGTH_SHORT).show(); //잘 출력되면 지우룜

        //이동할 프래그먼트에 데이터 담아서 전송
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("UserId", userId); //아이디 전송
        homeFragment.setArguments(bundle);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.root_home, homeFragment)
                .commit();

        return view;
    }

}