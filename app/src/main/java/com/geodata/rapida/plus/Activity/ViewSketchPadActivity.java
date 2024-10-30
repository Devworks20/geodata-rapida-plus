package com.geodata.rapida.plus.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.geodata.rapida.plus.Adapter.RVAdapterSketchImages;
import com.geodata.rapida.plus.R;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ViewSketchPadActivity extends AppCompatActivity
{
    private static final String TAG = ViewSketchPadActivity.class.getSimpleName();

    ImageView iv_back, iv_sketch;

    String SketchPath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sketch_pad);

        initViews();
    }

    private void initViews()
    {
        iv_back = findViewById(R.id.iv_back);
        iv_sketch = findViewById(R.id.iv_sketch);

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            SketchPath      = extras.getString("SketchPath");

            initSetImage();
        }

        initListeners();
    }

    private void initSetImage()
    {
        try
        {
            File file = new File(SketchPath);

            if (file.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(SketchPath, bmOptions);
                //bitmap = initImageRotateNormal(file, bitmap);

                Glide.with(this).load(bitmap).into(iv_sketch);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initListeners()
    {
        iv_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    //Customize Bitmap to Normal angle of Picture
    private static Bitmap initImageRotateNormal(File imagePath, Bitmap bitmap)
    {
        ExifInterface ei = null;

        try
        {
            ei = new ExifInterface((imagePath.getAbsolutePath()));
        }
        catch (IOException e)
        {
            Log.e(TAG, e.toString());
        }

        int orientation = Objects.requireNonNull(ei).getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap;

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    //Fix Auto rotate in Some Camera
    private static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0,0, source.getWidth(), source.getHeight(), matrix, true);
    }

}