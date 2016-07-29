package com.onpuri.Activity;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
public class NewSenFragment extends Fragment implements View.OnClickListener{
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
        //현재 사용자의 OS버전이 마시멜로우 인지 체크한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //사용자 단말기의 권한 중 전화걸기 권한이 허용되어 있는지 체크한다.
            int permissionResult = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA);

            // CAMERA 권한이 없을 떄
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                //  Package는 Android Application의 ID이다.
                //CAMERA 권한조사  거부한 이력이 없다면 false를 리턴한다.

                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("이 기능을 사용하기 위해서는 단말기의 \"카메라\"권한이 필요합니다. 계속하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1000);
                                    }
                                }
                            })
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(), "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();

                }
                // 최초로 권한을 요청 할 때
                else {
                    // CAMERA 권한을 안드로이드 OS에 요청합니다.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1000);
                }
            }
            //CAMERA권한이 있을 경우
            else {
                openCamera();
            }

        }
        // 사용자의 버전이 마시멜로우 이하일때
        else {
            openCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 사용자 요청, 요청한 권한들, 응답들

        if (requestCode == 1000) {
            // 요청한 권한을 사용자가 허용했다면
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
            }
            else {
                Toast.makeText(getActivity(), "권한요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }
}
