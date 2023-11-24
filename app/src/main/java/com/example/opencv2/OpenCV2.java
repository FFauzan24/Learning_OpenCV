package com.example.opencv2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class OpenCV2 extends AppCompatActivity {

    Button selectImage, camera;
    Bitmap bitmap;
    ImageView imageView;
    Mat mat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cv2);

        OpenCVLoader.initDebug();

        getPermission();

        selectImage = findViewById(R.id.selectImage);
        camera = findViewById(R.id.camera);
        imageView = findViewById(R.id.imageView);

        selectImage.setOnClickListener(view -> {
            Intent selectImage = new Intent(Intent.ACTION_GET_CONTENT);
            selectImage.setType("image/*");
            startActivityForResult(selectImage, 100);
        });

        camera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 101);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA
            }, 102);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102 && grantResults.length > 0){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                getPermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);

                mat = new Mat();

                Utils.bitmapToMat(bitmap, mat);

                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

                Utils.matToBitmap(mat, bitmap);

                imageView.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == 101 && data != null){
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            mat = new Mat();

            Utils.bitmapToMat(bitmap, mat);

            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

            Utils.matToBitmap(mat, bitmap);

            imageView.setImageBitmap(bitmap);
        }
    }
}