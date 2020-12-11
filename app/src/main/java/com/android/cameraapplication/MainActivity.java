package com.android.cameraapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.BitmapCompat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Uri imageUri;
    TextView textView;
    File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.imageSize);
    }

    public void clickImage(View view) {
        CropImage.activity().start(MainActivity.this);
//    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//    startActivityForResult(intent,0);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int dataSize = 0;
        if (requestCode == 0
                && resultCode == Activity.RESULT_OK) {

            imageUri = data.getData();
            System.out.println(imageUri);
            startCrop(imageUri);

        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                System.out.println(uri);
                String scheme = uri.getScheme();
                if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                    try {
                        InputStream fileInputStream = getApplicationContext().getContentResolver().openInputStream(uri);
                        dataSize = fileInputStream.available();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("File size in bytes" + dataSize);

                } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
                    String path = uri.getPath();
                    try {
                        f = new File(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("File size in bytes " + f.length());
                }
//                int bitmapByteCount= BitmapCompat.getAllocationByteCount(bitmap);
                textView.setText(f.length() / 1024 + " kb");
                imageView.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = result.getError();
                Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void startCrop(Uri uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

}