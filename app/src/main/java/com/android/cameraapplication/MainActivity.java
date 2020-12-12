package com.android.cameraapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.BitmapCompat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Uri imageUri;
    TextView textView;
    File f;
    Bitmap bitmap, convertedImage;

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
        super.onActivityResult(requestCode, resultCode, data);
        int dataSize = 0;
        if (requestCode == 0
                && resultCode == Activity.RESULT_OK) {

            imageUri = data.getData();
            System.out.println(imageUri);
            startCrop(imageUri);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    convertedImage = getResizedBitmap(bitmap, 140);
//                    System.out.println(converetdImage);
                } catch (Exception e) {
                    //handle exception
                }
//                Bitmap bitmap = result.getBitmap();
                System.out.println(uri);

//                String scheme = uri.getScheme();
//                if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
//                    try {
//                        InputStream fileInputStream = getApplicationContext().getContentResolver().openInputStream(uri);
//                        dataSize = fileInputStream.available();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("File size in bytes" + dataSize);
//
//                } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
//                    String path = bitmap..getPath();
//                    try {
//                        f = new File(path);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                System.out.println("File size in bytes " + convertedImage.getByteCount());
//                }
//                int bitmapByteCount= BitmapCompat.getAllocationByteCount(bitmap);
                textView.setText(convertedImage.getByteCount() / 1024 + " kb");
                imageView.setImageBitmap(convertedImage);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = result.getError();
                Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void startCrop(Uri uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                try {
                    share();
                } catch (NullPointerException e) {
                    Toast.makeText(this, "No image available", Toast.LENGTH_SHORT).show();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    Bitmap bitmap1;

    public void share() {
        try {
            Drawable drawable = imageView.getDrawable();
            bitmap1 = ((BitmapDrawable) drawable).getBitmap();
        } catch (Exception e) {
            Toast.makeText(this, "No image available", Toast.LENGTH_SHORT).show();
        }
        try {
            File file = new File(this.getExternalCacheDir(), File.separator + "image.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            file.setReadable(true, false);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".fileprovider", file));
            intent.setType("image/*");
            startActivity(Intent.createChooser(intent, "Share Image via.."));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "No image available", Toast.LENGTH_SHORT).show();
        }

    }


}