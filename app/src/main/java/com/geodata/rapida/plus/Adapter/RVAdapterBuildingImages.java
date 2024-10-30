package com.geodata.rapida.plus.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Class.ImagesClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryImages;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class RVAdapterBuildingImages extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAdapterBuildingImages.class.getSimpleName();

    Context context;
    List<ImagesClass> imagesClassList;
    ImageView iv_camera, iv_gallery;

    public RVAdapterBuildingImages(Context context, List<ImagesClass> imagesClassList, ImageView iv_camera, ImageView iv_gallery)
    {
        this.context         = context;
        this.imagesClassList = imagesClassList;
        this.iv_camera       = iv_camera;
        this.iv_gallery      = iv_gallery;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_for_image, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ((MyHolder) holder).bindView(position);

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return imagesClassList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        CardView cv_layout;

        ImageView iv_building_image, iv_building_image_remove;

        public MyHolder(View view)
        {
            super(view);

            cv_layout                = itemView.findViewById(R.id.cv_layout);

            iv_building_image        = itemView.findViewById(R.id.iv_building_image);
            iv_building_image_remove = itemView.findViewById(R.id.iv_building_image_remove);

        }

        public void bindView(final int position)
        {
            final ImagesClass current = imagesClassList.get(position);

            File file = new File(current.getImagePath());

            if (file.exists())
            {
                try
                {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(current.getImagePath(), bmOptions);
                    //bitmap = initImageRotateNormal(file, bitmap);

                    Glide.with(context).load(bitmap).into(iv_building_image);
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }

            iv_building_image_remove.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        final AlertDialog.Builder ADSettings = new AlertDialog.Builder(context);
                        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

                        View view = inflater.inflate(R.layout.custom_dialog_title, null);
                        TextView textView = view.findViewById(R.id.tv_dialog_title);
                        String sTitle = "Building Image";
                        textView.setText(sTitle);
                        textView.setTextColor(Color.BLACK);

                        ADSettings.setCustomTitle(view);
                        ADSettings.setMessage("Are you sure you want to remove this ?");
                        ADSettings.setCancelable(true);
                        ADSettings.setNegativeButton("CLOSE", null);
                        ADSettings.setPositiveButton("YES", new DialogInterface.OnClickListener()
                        {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                RepositoryImages.removePhoto(context, current.getID());
                                imagesClassList.remove(position);
                                notifyDataSetChanged();

                                iv_camera.setEnabled(true);
                                iv_gallery.setEnabled(true);

                                initDisableButtons(false, null, iv_camera);
                                initDisableButtons(false, null, iv_gallery);

                                file.delete(); //Delete the image.
                            }
                        });
                        ADSettings.show();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });

            iv_building_image.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    initFileView(current.getImagePath(), current.getImageExtension());
                }
            });

        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void initFileView(String FilePath, String FileExtension)
    {
        try
        {
            //File theFile = new File(Environment.getExternalStorageDirectory() + FilePath);
            File theFile = new File(FilePath);

            boolean isVideo = FileExtension.contains("3gp") || FileExtension.contains("mpg") || FileExtension.contains("mpeg") ||
                    FileExtension.contains("mpe") || FileExtension.contains("mp4") || FileExtension.contains("avi");

            boolean isJPEG = FileExtension.contains("jpg") || FileExtension.contains("jpeg") || FileExtension.contains("png");

            if (Build.VERSION.SDK_INT >= 22)
            {
                try
                {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);

                    if (theFile.exists())
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);

                        try
                        {
                            Uri ff = Uri.fromFile(theFile);

                            if (FileExtension.contains("doc") || FileExtension.contains("docx"))
                            {
                                // Word document
                                intent.setDataAndType(ff, "application/msword");
                            }
                            else if (FileExtension.contains("pdf"))
                            {
                                // PDF file
                                intent.setDataAndType(ff, "application/pdf");
                            }
                            else if (FileExtension.contains("ppt") || FileExtension.contains("pptx"))
                            {
                                // Powerpoint file
                                intent.setDataAndType(ff, "application/vnd.ms-powerpoint");
                            }
                            else if (FileExtension.contains("xls") || FileExtension.contains("xlsx"))
                            {
                                // Excel file
                                intent.setDataAndType(ff, "application/vnd.ms-excel");
                            }
                            else if (FileExtension.contains("zip"))
                            {
                                // ZIP file
                                intent.setDataAndType(ff, "application/zip");
                            }
                            else if (FileExtension.contains("rar"))
                            {
                                // RAR file
                                intent.setDataAndType(ff, "application/x-rar-compressed");
                            }
                            else if (FileExtension.contains("rtf"))
                            {
                                // RTF file
                                intent.setDataAndType(ff, "application/rtf");
                            }
                            else if (FileExtension.contains("wav") || FileExtension.contains("mp3"))
                            {
                                // WAV audio file
                                intent.setDataAndType(ff, "audio/x-wav");
                            }
                            else if (FileExtension.contains("gif"))
                            {
                                // GIF file
                                intent.setDataAndType(ff, "image/gif");
                            }
                            else if (isJPEG)
                            {
                                // JPG file
                                intent.setDataAndType(ff, "image/jpeg");
                            }
                            else if (FileExtension.contains("txt"))
                            {
                                // Text file
                                intent.setDataAndType(ff, "text/plain");
                            }
                            else if (isVideo)
                            {
                                // Video files
                                intent.setDataAndType(ff, "video/*");
                            }
                            else
                            {
                                intent.setDataAndType(ff, "*/*");
                            }

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        catch (ActivityNotFoundException e)
                        {
                            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();

                            Log.e(TAG, e.toString());
                        }
                    }
                    else
                        Toast.makeText(context, "File Corrupted", Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    Log.e(TAG,  e.toString());

                    Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                if (theFile.exists())
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    try
                    {
                        Uri ff = Uri.fromFile(theFile);

                        if (FileExtension.contains("doc") || FileExtension.contains("docx"))
                        {
                            // Word document
                            intent.setDataAndType(ff, "application/msword");
                        }
                        else if (FileExtension.contains("pdf"))
                        {
                            // PDF file
                            intent.setDataAndType(ff, "application/pdf");
                        }
                        else if (FileExtension.contains("ppt") || FileExtension.contains("pptx"))
                        {
                            // Powerpoint file
                            intent.setDataAndType(ff, "application/vnd.ms-powerpoint");
                        }
                        else if (FileExtension.contains("xls") || FileExtension.contains("xlsx"))
                        {
                            // Excel file
                            intent.setDataAndType(ff, "application/vnd.ms-excel");
                        }
                        else if (FileExtension.contains("zip"))
                        {
                            // ZIP file
                            intent.setDataAndType(ff, "application/zip");
                        }
                        else if (FileExtension.contains("rar"))
                        {
                            // RAR file
                            intent.setDataAndType(ff, "application/x-rar-compressed");
                        }
                        else if (FileExtension.contains("rtf"))
                        {
                            // RTF file
                            intent.setDataAndType(ff, "application/rtf");
                        }
                        else if (FileExtension.contains("wav") || FileExtension.contains("mp3"))
                        {
                            // WAV audio file
                            intent.setDataAndType(ff, "audio/x-wav");
                        }
                        else if (FileExtension.contains("gif"))
                        {
                            // GIF file
                            intent.setDataAndType(ff, "image/gif");
                        }
                        else if (isJPEG)
                        {
                            // JPG file
                            intent.setDataAndType(ff, "image/jpeg");
                        }
                        else if (FileExtension.contains("txt"))
                        {
                            // Text file
                            intent.setDataAndType(ff, "text/plain");
                        }
                        else if (isVideo)
                        {
                            // Video files
                            intent.setDataAndType(ff, "video/*");
                        }
                        else
                        {
                            intent.setDataAndType(ff, "*/*");
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        context.startActivity(intent);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();

                        Log.e(TAG, e.toString());
                    }
                }
                else
                    Toast.makeText(context, "File Corrupted", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,  e.toString());

            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
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

    public static void initDisableButtons(boolean status, Button button, ImageView imageView)
    {
        if (status)
        {
            AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
            alpha.setDuration(0);
            alpha.setFillAfter(true);

            if (button != null)
            {
                button.startAnimation(alpha);
            }
            else if (imageView != null)
            {
                imageView.startAnimation(alpha);
            }
        }
        else
        {
            if (button != null)
            {
                button.clearAnimation();
            }
            else if (imageView != null)
            {
                imageView.clearAnimation();
            }
        }
    }
}
