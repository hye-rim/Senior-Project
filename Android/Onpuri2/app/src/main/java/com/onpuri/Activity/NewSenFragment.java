package com.onpuri.Activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kutemsys on 2016-05-03.
 */
//문장등록 tab
public class NewSenFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "NewSenFragment";
    private worker_add_sen worker_add_sen;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    private static View view;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    private static final int REQ_CODE_SELECT_IMAGE = 2;

    private Button btn_ok, btn_cancel, btn_gallery, btn_camera;
    private EditText sen;

    ViewPager viewPager;

    int i;

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

        sen = (EditText) view.findViewById(R.id.sentence);
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
                Addsentence();
                Toast.makeText(getActivity(), "등록되었습니다.", Toast.LENGTH_SHORT).show();
                sen.setText("");
                viewPager.setCurrentItem(1);
                break;

            case R.id.btn_new_sen_back:
                sen.setText("");
                viewPager.setCurrentItem(1);
                break;

            case R.id.btn_new_picture:
                openGallery();
                Toast.makeText(getActivity(), "텍스트 변환은 차후 추가 예정입니다.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_new_camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    perrmissionWork();
                } else {
                    openCamera();
                }
                break;

            default:
                break;
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    private void openCamera() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

    private void perrmissionWork() {
        String permissionsNeeded = new String();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded = new String("CAMERA");

        if (permissionsList.size() > 0) {
            if ( !permissionsNeeded.isEmpty() ) { //값이 있을 경우
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded;
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                });
                return;
            }
            requestPermissions(
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }else{
            openCamera();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList,String permission) {
        if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message,android.content.DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(getActivity()).setMessage(message)
                .setPositiveButton("OK", onClickListener).setCancelable(false)
                .setNegativeButton("Cancel", null).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ) {
                    // All Permissions Granted
                    openCamera();
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Some Permission is Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                try {
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                    ImageView image = (ImageView)view.findViewById(R.id.imageView1);

                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);

                }
                catch (FileNotFoundException e) { 		e.printStackTrace(); 			}
                catch (IOException e)                 {		e.printStackTrace(); 			}
                catch (Exception e)		         {             e.printStackTrace();			}
            }
        }
    }
    private void Addsentence() {
        if(worker_add_sen != null && worker_add_sen.isAlive()){  //이미 동작하고 있을 경우 중지
            worker_add_sen.interrupt();
        }
        worker_add_sen = new worker_add_sen(true);
        worker_add_sen.start();
        try {
            worker_add_sen.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class worker_add_sen extends Thread {
        private boolean isPlay = false;
        String AddSen = sen.getText().toString();

        public worker_add_sen(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {
                Log.d(TAG, "worker add trans start");
                byte[] dataByte = AddSen.getBytes();
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_ASEN;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) dataByte.length;
                for (i = 4; i < 4+dataByte.length; i++) {
                    outData[i] = (byte) dataByte[i-4];
                }
                outData[6 + dataByte.length] = (byte) PacketUser.CRC;

                try {
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, outData[3]+5); // packet transmission
                    dos.flush();

                    dis = new DataInputStream(SocketConnection.socket.getInputStream());
                    dis.read(temp, 0, 4);
                    for (int index = 0; index < 4; index++) {
                        inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }
                    if(inData[1] == PacketUser.ACK_ASEN) {
                        Log.d(TAG, "등록완료");
                    }
                    dis.read(temp);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlay = !isPlay;
            }
        }
    }
}
