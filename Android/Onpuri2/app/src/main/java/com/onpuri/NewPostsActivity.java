package com.onpuri;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;

public class NewPostsActivity extends Activity {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private Uri mImageCaptureUri;

    private CloseSystem CloseSystem; //BackKeyPressed,close
    private ActivityList actManager = ActivityList.getInstance();

    private static final String TAG = "NewCamera";
    private static final int IN_IMAGE_SIZE = 8; //1/8크기로 처리

    private Camera mCamera;
    private ImageView mImage;
    private boolean mInProgress;

    RelativeLayout newCamera;
    ImageView newImage;
    EditText newPostPro;

    private SurfaceHolder.Callback mSurfaceListener = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mCamera = Camera.open();
            Log.i(TAG, "Camera opened");

            try {
                mCamera.setPreviewDisplay(holder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(width, height);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            Log.i(TAG, "Camera preview started");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            mCamera.release();
            mCamera = null;
            Log.i(TAG, "Camera released");

        }
    };


    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mCamera != null && mInProgress == false) {
                mCamera.takePicture(mShutterListener, null, mPictureListner);
                mInProgress = true;
            }
        }
    };

    private Camera.ShutterCallback mShutterListener = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.i(TAG, "onShutter");
        }
    };
    private Camera.PictureCallback mPictureListner = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(TAG, "Picture Taken");
            if (data != null) {
                Log.i(TAG, "JPEG Picture Taken");
                // 처리하는 이미지의 크기를 축소
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = IN_IMAGE_SIZE;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                // 이미지 뷰 이미지 설정
                mImage.setImageBitmap(bitmap);
                // 정지된 프리뷰를 재개
                camera.startPreview();
                // 처리중 플래그를 떨어뜨림
                mInProgress = false;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_new_posts);
        CloseSystem = new CloseSystem(this); //backKey Event

        mImage = (ImageView) findViewById(R.id.iv_new);
        //zznewCamera = (RelativeLayout) findViewById(R.id.rl_newCamera);
        //newImage = (ImageView) findViewById(R.id.iv_newImage);
        newPostPro = (EditText) findViewById(R.id.et_newPostPro);
        SurfaceView surface = (SurfaceView) findViewById(R.id.surface_view);
        SurfaceHolder holder = surface.getHolder();

        holder.addCallback(mSurfaceListener);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Button btnNewCamera = (Button) findViewById(R.id.btnNewCamera);
        btnNewCamera.setOnClickListener(mButtonListener);
    }
    private void doTakePhotoAction()
    {
    /*
     * 참고 해볼곳
     * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
     * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
     * http://www.damonkohler.com/2009/02/android-recipes.html
     * http://www.firstclown.us/tag/android/
     */

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
        //intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    private void doTakeAlbumAction()
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case CROP_FROM_CAMERA:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    mImage.setImageBitmap(photo);
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 90);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }
        }
    }


    public void onClick(View v) {
      /*  switch (v.getId()) {
            case R.id.btnNewPosts:
                newCamera.setVisibility(View.GONE);
                newImage.setVisibility(View.GONE);
                newPostPro.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNewText:
                newCamera.setVisibility(View.VISIBLE);
                newImage.setVisibility(View.GONE);
                newPostPro.setVisibility(View.GONE);
                break;

            case R.id.btnNewGallery:
                newCamera.setVisibility(View.GONE);
                newImage.setVisibility(View.VISIBLE);
                newPostPro.setVisibility(View.GONE);
                break;
        }*/

        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakePhotoAction();
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener).show();

    }
}