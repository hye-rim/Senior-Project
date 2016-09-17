package com.onpuri.Activity.SideTab.Setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.onpuri.R;

/**
 * Created by HYERIM on 2016-07-18.
 */

//공지사항 프래그먼트
public class UserSetNoticeFragment extends Fragment {
    private static View view;
    ListView listView;
    static final String[] LIST_MENU = {"공지사항1", "공지사항2", "공지사항3"};

    public static UserSetNoticeFragment newInstance() {
        UserSetNoticeFragment fragment = new UserSetNoticeFragment();
        return fragment;
    }

    public UserSetNoticeFragment() {

    // Required empty public constructor
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
            view = inflater.inflate(R.layout.fragment_my_set_notice, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU );
        listView = (ListView)view.findViewById(R.id.list_notice);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "아직 공지 내용이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
