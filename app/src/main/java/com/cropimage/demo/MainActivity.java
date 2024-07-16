package com.cropimage.demo;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cropimage.imaging.TRSPictureEditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    static public int REQUEST_PHOTO_FROM_ALBUM = 1;

    ImageView imageView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("####", "Clicked the show button");
                selectPhotoFromAlbum();
            }
        });

        imageView = findViewById(R.id.imageView);
    }

    private long getFileSizeFromUri(Uri uri) {
        long fileSize = 0;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getLong(sizeIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileSize;
    }

    private Bitmap resizeImage(Bitmap bitmap) {
        int MAX_WIDTH = 3000;
        int MAX_HEIGHT = 3000;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > MAX_WIDTH || height > MAX_HEIGHT) {
            float scaleWidth = ((float) MAX_WIDTH) / width;
            float scaleHeight = ((float) MAX_HEIGHT) / height;
            float scaleFactor = Math.min(scaleWidth, scaleHeight);

            width = Math.round(width * scaleFactor);
            height = Math.round(height * scaleFactor);

            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
        return bitmap;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ContentResolver contentResolver = getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (inputStream != null) {
            inputStream.close();
        }
        return bitmap;
    }

    private void startImageEditor(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            long fileSizeInBytes = getFileSizeFromUri(imageUri);
            Log.d("####", "selected image file size:" + fileSizeInBytes);
            if(fileSizeInBytes> 5 * 1024 * 1024) {
                Log.d("####", "The image is too large.");
                return;
            }
            bitmap = getBitmapFromUri(imageUri);
        }catch (Exception e) {

        }

        if(bitmap == null) {
            return;
        }

        bitmap = resizeImage(bitmap);
        if(bitmap == null) {
            return;
        }

        //BOX_ENABLE, CIRCLE_ENABLE, TXT_ENABLE, PAINT_ENABLE, ARROW_ENABLE, MOSAIC_ENABLE, CLIP_ENABLE
        TRSPictureEditor.setStyle(TRSPictureEditor.CLIP_ENABLE | TRSPictureEditor.BOX_ENABLE | TRSPictureEditor.TXT_ENABLE | TRSPictureEditor.PAINT_ENABLE);
        TRSPictureEditor.edit(this, bitmap, new TRSPictureEditor.EditAdapter() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
    }

    void selectPhotoFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PHOTO_FROM_ALBUM);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照或相册选择后的处理
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PHOTO_FROM_ALBUM) {
                if (data == null) {
                    return;
                }

                // 处理选择照片结果
                Uri photoUri = data.getData();
                //新的图片编辑
                startImageEditor(photoUri);
            }
        }
    }
}