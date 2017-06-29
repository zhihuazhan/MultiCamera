package com.dev.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends Activity implements View.OnClickListener {

    private boolean mIsRecordingVideo;

    private Button mRecordButton;

//    private Button mRearButton;

    public FrontCameraFragment mFragmentFront = FrontCameraFragment.newInstance();

    public RearCameraFragment mRearCameraFragment = RearCameraFragment.newInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mRecordButton = (Button) findViewById(R.id.record);
        //mRearButton = (Button) findViewById(R.id.openrear);
        List<String> list = checkPermission();
        if (list != null && !list.isEmpty()) {
            showDialog(list);
            return;
        }
        mFragmentFront = FrontCameraFragment.newInstance();
        if (null == savedInstanceState) {
            mIsRecordingVideo = false;
            getFragmentManager().beginTransaction()
                    .replace(R.id.front, mFragmentFront)
                    .replace(R.id.rear, mRearCameraFragment)
                    .commit();
            mRecordButton.setOnClickListener(CameraActivity.this);
            //mRearButton.setOnClickListener(CameraActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == mRecordButton) {
            mIsRecordingVideo = !mIsRecordingVideo;
            if (mIsRecordingVideo) {
                mRecordButton.setText(R.string.btn_stop);
            } else {
                mRecordButton.setText(R.string.btn_start);
            }
            mFragmentFront.onClick(v);
            if (mRearCameraFragment != null) {
                mRearCameraFragment.onClick(v);
            }
        }
//        else if (v == mRearButton) {
//            mRearButton.setClickable(false);
//            new Handler().post(new Runnable() {
//                @Override
//                public void run() {
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.rear, mRearCameraFragment)
//                            .commit();
//                }
//            });
//        }
    }

    public List<String> checkPermission() {
        List<String> list = new ArrayList<>();
        String packageName = getPackageName();
        PackageManager pm = getPackageManager();
        boolean camera = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.CAMERA", packageName));
        boolean record = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.RECORD_AUDIO", packageName));
//        boolean cameraFull = (PackageManager.PERMISSION_GRANTED ==
//                pm.checkPermission("android.hardware.camera.level.full", "com.leo.camera"));
        boolean storage = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", packageName));

        if (!camera) {
            list.add("Camera");
        }
        if (!record) {
            list.add("Microphone");
        }
        if (!storage) {
            list.add("Storage");
        }
        return list;
    }

    public void showDialog(List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        String result = "Please grant permissions: ";
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                result = result + ", ";
            }
            result = result + list.get(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
        builder.setTitle("App Need Permissions");
        builder.setMessage(result);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CameraActivity.this.finish();
            }

        });
        builder.create().show();
    }
}
