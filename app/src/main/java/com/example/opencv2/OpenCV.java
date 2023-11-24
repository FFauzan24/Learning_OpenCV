package com.example.opencv2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCV extends AppCompatActivity {


    private ImageView imageViewOriginal, imageViewConvolved;
    private Button btnProcess;

    private int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private Mat originalImage = null;

    static {
        OpenCVLoader.initDebug();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv);

        imageViewOriginal = findViewById(R.id.imageViewOriginal);
        imageViewConvolved = findViewById(R.id.imageViewConvolved);
        btnProcess = findViewById(R.id.btnProcess);

        btnProcess.setOnClickListener(v -> {
            checkCameraPermission();
        });

    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            dispacherTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispacherTakePictureIntent();
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispacherTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            if (extras != null){
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                processImage(imageBitmap);
            }
        }
    }

    private void processImage(Bitmap bitmap) {
        originalImage = bitmapToMat(bitmap);
        imageViewOriginal.setImageBitmap(bitmap);

        Mat kernel = new Mat(3, 3, CvType.CV_32F);
        float[] kernelData = {
                -1, -1, -1,
                -1, 8, -1,
                -1, -1, -1
        };
        kernel.put(0,0, kernelData);

        Mat convolvedImage = applyConvolution(originalImage, kernel);
        imageViewConvolved.setImageBitmap(matToBitmap(convolvedImage));
    }

    private Mat bitmapToMat(Bitmap bitmap){
        Mat mat = new Mat();
        org.opencv.android.Utils.bitmapToMat(bitmap, mat);
        return mat;
    }

    private Bitmap matToBitmap(Mat mat){
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }
    private Mat applyConvolution(Mat inputImage, Mat kernel){
        Mat outputImage = new Mat();
        Imgproc.filter2D(inputImage, outputImage, -1, kernel);
        return outputImage;
    }
}