package com.onpuri.Activity.SideTab.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;

/**
 * Created by HYERIM on 2016-07-18.
 */

//질문 프래그먼트
public class UserSetQuestionFragment extends Fragment implements View.OnClickListener {
    private static View view;

    private Button btn_ok, btn_cancel;
    private TextView questionText;
    private String userId;

    FragmentManager mFragmentManager;
    FragmentTransaction fragmentTransaction;

    public static UserSetQuestionFragment newInstance() {
        UserSetQuestionFragment fragment = new UserSetQuestionFragment();
        return fragment;
    }

    public UserSetQuestionFragment() {

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
            view = inflater.inflate(R.layout.fragment_my_set_question, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        Bundle extra = getArguments();
        userId = null;
        userId = extra.getString("SetId");

        mFragmentManager = getFragmentManager();
        questionText = (TextView)view.findViewById(R.id.tv_question);

        btn_ok = (Button)view.findViewById(R.id.btn_question_ok);
        btn_cancel = (Button)view.findViewById(R.id.btn_question_cancel);

        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_question_ok:
                Intent email = new Intent(Intent.ACTION_SEND);
                String[] mailAddr = {"phl2898@gmail.com"};

                email.setType("plaine/text");
                email.putExtra(Intent.EXTRA_EMAIL, mailAddr); // 받는사람
                email.putExtra(Intent.EXTRA_SUBJECT, "[DailyE]Question" + userId); // 제목
                email.putExtra(Intent.EXTRA_TEXT, "\n\n" + "#Question \n\n\n" + questionText.getText().toString()); // 첨부내용

                try {
                    startActivity(Intent.createChooser(email, "Send email with...?"));
                } catch (android.content.ActivityNotFoundException exception) {
                    Toast.makeText(getActivity(), "No email clients installed on device!", Toast.LENGTH_LONG).show();
                }

                mFragmentManager.popBackStack();
                mFragmentManager.beginTransaction()
                        .commit();
                break;

            case R.id.btn_question_cancel:
                mFragmentManager.popBackStack();
                mFragmentManager.beginTransaction()
                        .commit();
                break;
        }
    }
}
