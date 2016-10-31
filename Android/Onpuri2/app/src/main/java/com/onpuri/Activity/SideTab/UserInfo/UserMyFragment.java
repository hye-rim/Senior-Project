package com.onpuri.Activity.SideTab.UserInfo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;
import com.onpuri.Activity.SideTab.UserInfo.workerChangeMy;

import java.util.regex.Pattern;

//내정보 프래그먼트
public class UserMyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "UserMyFragment";

    private workerChangeMy worker_change;

    TextView tv_userID, tv_userName, tv_userJoinDate;
    EditText et_userPhone, et_userNowPass, et_userNewPass, et_userNewPassCheck;
    Button btnOk, btnCancel;
    private static View view;

    String userId, name, joinDate, phone;
    String changePhone, nowPass, newPass;
    private boolean checkPw, checkNewPw, checkComparePw;
    //현재비밀번호 일치여부, 새로운 비밀번호 일치여부, 현재비밀번호 != 새로운 비밀번호

    private FragmentManager mFragmentManager;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    public static UserMyFragment newInstance() {
        UserMyFragment fragment = new UserMyFragment();
        return fragment;
    }

    public UserMyFragment() {
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
            view = inflater.inflate(R.layout.fragment_my, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        mFragmentManager = getActivity().getSupportFragmentManager();

        //선언
        tv_userID = (TextView)view.findViewById(R.id.tv_userID);
        tv_userName = (TextView)view.findViewById(R.id.tv_userName);
        tv_userJoinDate = (TextView)view.findViewById(R.id.tv_userDate);
        et_userPhone = (EditText)view.findViewById(R.id.et_userPhone); //사용자 핸드폰 번호
        et_userNowPass = (EditText)view.findViewById(R.id.et_now_pass); //현재비밀번호
        et_userNewPass = (EditText)view.findViewById(R.id.et_new_pass); //새로운비밀번호
        et_userNewPassCheck = (EditText)view.findViewById(R.id.et_new_pass_check); //새로운비밀번호확인

        btnOk = (Button)view.findViewById(R.id.btn_my_ok);
        btnCancel = (Button)view.findViewById(R.id.btn_my_cancel);

        userId = new String();
        userId = getArguments().getString("MyId");
        name = new String();
        name = getArguments().getString("MyName");
        joinDate = new String();
        joinDate = getArguments().getString("MyJoin");
        phone = new String();
        phone = getArguments().getString("MyPhone");
        nowPass = new String();
        nowPass = getArguments().getString("MyPass");

        tv_userID.setText(userId);
        tv_userName.setText(name);
        tv_userJoinDate.setText(joinDate);
        et_userPhone.setText(""+phone);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        et_userNowPass.setFilters(new InputFilter[]{filterAlphaNumEng, new InputFilter.LengthFilter(15)});
        et_userNowPass.setPrivateImeOptions("defaultInputmode=english;");
        et_userNewPass.setFilters(new InputFilter[]{filterAlphaNumEng, new InputFilter.LengthFilter(15)});
        et_userNewPass.setPrivateImeOptions("defaultInputmode=english;");
        et_userNewPassCheck.setFilters(new InputFilter[]{filterAlphaNumEng, new InputFilter.LengthFilter(15)});
        et_userNewPass.setPrivateImeOptions("defaultInputmode=english;");

        et_userNowPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPw = (et_userNowPass.getText().toString().equals(nowPass)) ? true //현재 비밀번호 일치여부
                        : false;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkPw = (nowPass.equals(et_userNowPass.getText().toString())) ? true //현재 비밀번호 일치여부
                        : false;
            }
        });

        et_userNewPassCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_userNewPassCheck.getText().toString().equals(et_userNewPass.getText().toString()) && !et_userNewPass.getText().toString().equals("")) {
                    checkNewPw = true;
                    newPass = et_userNowPass.getText().toString();
                } else {
                    checkNewPw = false;
                }
                checkComparePw = (et_userNewPass.getText().toString().equals(nowPass)) ? true //현재 비밀번호 != 새 비밀번호 여부
                        : false; //false : 현재 비밀번호 != 새 비밀번호
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_userNewPass.getText().toString().equals(et_userNewPassCheck.getText().toString()) && !et_userNewPass.getText().toString().equals("")) {
                    checkNewPw = true;
                    newPass = et_userNewPass.getText().toString();
                } else {
                    checkNewPw = false;
                }

                checkComparePw = (et_userNewPass.getText().toString().equals(nowPass)) ? true //현재 비밀번호 != 새 비밀번호 여부
                        : false; //false : 현재 비밀번호 != 새 비밀번호
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_my_ok:
                if(checkNewPw && checkPw && !checkComparePw) {
                    changePhone = phone;
                    String changeData = changePhone + "+" + newPass + "+"; //핸드폰 + 패스워드

                    worker_change = new workerChangeMy(true, changeData);
                    worker_change.start();

                    try {
                        worker_change.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (worker_change != null && worker_change.isAlive()) {  //이미 동작하고 있을 경우 중지
                        worker_change.interrupt();
                    }

                    storeSharedData();
                    Toast.makeText(getActivity(), "정보수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    FragmentManager.BackStackEntry entry = mFragmentManager.getBackStackEntryAt(0);
                    mFragmentManager.popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    mFragmentManager.beginTransaction()
                            .commit();



                }
                else if(!checkNewPw){
                    Toast.makeText(getActivity(), "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(!checkPw){
                    Toast.makeText(getActivity(), "현재 비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(checkComparePw){
                    Toast.makeText(getActivity(), "현재 비밀번호와 다른 비밀번호로 설정해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_my_cancel:
                mFragmentManager.popBackStack();
                mFragmentManager.beginTransaction()
                        .commit();
                break;

            default:
                break;
        }

    }

    private void storeSharedData() {
        setting = getActivity().getSharedPreferences("setting",0);
        editor = setting.edit();
        String id, password;

        if(setting.getBoolean("autoLogin", false)){
            id = setting.getString("ID", "");
            password = newPass;

            editor.putString("ID", id);
            editor.putString("PW", password);
            editor.putBoolean("autoLogin", true);
            editor.commit();
        }
    }

    //edittext 영문+숫자만 입력되도록 하는 함수
    public InputFilter filterAlphaNumEng = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
            Pattern ps = Pattern.compile("^[-_a-zA-Z0-9]+$");
            if(source.equals("")|| ps.matcher(source).matches()){
                source.equals(""); //백스페이스를 위해 추가한 부분
                return source;
            }

            return "";
        }
    };
}
