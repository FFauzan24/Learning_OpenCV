package com.example.opencv2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.core.Mat;

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


        selectImage = findViewById(R.id.selectImage);
        camera = findViewById(R.id.camera);
        imageView = findViewById(R.id.imageView);

        selectImage.setOnClickListener(view -> {
            Intent selectImage = new Intent(Intent.ACTION_GET_CONTENT);
            selectImage.setType("image/*");
            startActivityForResult(selectImage, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null){

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}