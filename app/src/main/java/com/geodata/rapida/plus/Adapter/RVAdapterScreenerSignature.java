package com.geodata.rapida.plus.Adapter;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.geodata.rapida.plus.Activity.CreateSignatureActivity;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel;
import com.geodata.rapida.plus.SQLite.Class.InspectorSignatureClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryInspectorSignature;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineAssignedInspectors;
import com.geodata.rapida.plus.SQLite.Repository.RepositorySketchImages;
import com.geodata.rapida.plus.Tools.UserAccount;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RVAdapterScreenerSignature extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAdapterScreenerSignature.class.getSimpleName();

    Context context;
    List<AssignedInspectorsListModel> assignedInspectorsListModelList;

    public RVAdapterScreenerSignature(Context context, List<AssignedInspectorsListModel> assignedInspectorsListModelList)
    {
        this.context                         = context;
        this.assignedInspectorsListModelList = assignedInspectorsListModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_screener_signature_layout, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ((MyHolder) holder).bindView(position);

        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return assignedInspectorsListModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout rl_signature;
        EditText edt_focus;

        ImageView iv_image, iv_image_remove;

        TextView tv_screenerName, tv_position;

        Button btn_sign_signature;

        public MyHolder(View view)
        {
            super(view);

            rl_signature = itemView.findViewById(R.id.rl_signature);
            edt_focus    = itemView.findViewById(R.id.edt_focus);

            iv_image        = itemView.findViewById(R.id.iv_image);
            iv_image_remove = itemView.findViewById(R.id.iv_image_remove);

            tv_screenerName     = itemView.findViewById(R.id.tv_screenerName);
            tv_position         = itemView.findViewById(R.id.tv_position);
            btn_sign_signature  = itemView.findViewById(R.id.btn_sign_signature);
        }

        public void bindView(final int position)
        {
            final AssignedInspectorsListModel current = assignedInspectorsListModelList.get(position);

            tv_screenerName.setText(current.getInspector());

            if (current.getPosition() != null)
            {
                tv_position.setText(current.getPosition());
            }

            Cursor cursor = RepositoryInspectorSignature.realAllData(context, current.getID(), current.getMissionOrderID());

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    String SignaturePath = cursor.getString(cursor.getColumnIndex("SignaturePath"));

                    File file = new File(SignaturePath);

                    if (file.exists())
                    {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(SignaturePath, bmOptions);
                       // bitmap = initImageRotateNormal(file, bitmap);

                        btn_sign_signature.setVisibility(View.GONE);
                        rl_signature.setVisibility(View.VISIBLE);

                        /*BitmapDrawable ob = new BitmapDrawable(context.getResources(), bitmap);
                        iv_image.setBackground(ob);*/

                        Glide.with(context).load(bitmap).into(iv_image);
                    }
                    else
                    {
                        btn_sign_signature.setVisibility(View.VISIBLE);
                        rl_signature.setVisibility(View.GONE);

                    }

                    iv_image_remove.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            final AlertDialog.Builder ADSettings = new AlertDialog.Builder(context);
                            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

                            View view = inflater.inflate(R.layout.custom_dialog_title, null);
                            TextView textView = view.findViewById(R.id.tv_dialog_title);
                            String sTitle = "Signature";
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
                                    RepositoryInspectorSignature.removeInspectorSignature(context,
                                            UserAccount.UserAccountID, current.getMissionOrderID(), current.getID());

                                    file.delete(); //Delete the image.

                                    notifyDataSetChanged();
                                }
                            });
                            ADSettings.show();
                        }
                    });

                }
            }
            else
            {
                btn_sign_signature.setVisibility(View.VISIBLE);
                rl_signature.setVisibility(View.GONE);
            }

            btn_sign_signature.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    initCloseKeyboard();

                    edt_focus.requestFocus();
                    edt_focus.clearFocus();

                    final CharSequence[] options = {"Create Signature", "Capture Signature", "Attach Signature"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Add Signature");
                    builder.setItems(options, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int item)
                        {
                            if (options[item].equals("Create Signature"))
                            {
                                Intent intent = new Intent(context, CreateSignatureActivity.class);
                                intent.putExtra("Option", "Create Signature");
                                intent.putExtra("ID", current.getID());
                                intent.putExtra("MissionOrderID", current.getMissionOrderID());
                                ((Activity) context).startActivityForResult(intent, 1038);
                            }
                            else if (options[item].equals("Capture Signature"))
                            {
                                Intent intent = new Intent(context, CreateSignatureActivity.class);
                                intent.putExtra("Option", "Capture Signature");
                                intent.putExtra("ID", current.getID());
                                intent.putExtra("MissionOrderID", current.getMissionOrderID());
                                ((Activity) context).startActivityForResult(intent, 1038);
                            }
                            else if (options[item].equals("Attach Signature"))
                            {
                                Intent intent = new Intent(context, CreateSignatureActivity.class);
                                intent.putExtra("Option", "Attach Signature");
                                intent.putExtra("ID", current.getID());
                                intent.putExtra("MissionOrderID", current.getMissionOrderID());
                                ((Activity) context).startActivityForResult(intent, 1038);
                            }
                        }
                    });
                    builder.show();
                }
            });
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

    private void initCloseKeyboard()
    {
        try
        {
            View view = ((Activity) context).getCurrentFocus();

            if (view != null)
            {
                Log.e(TAG, "KEYBOARD CLOSED FROM SIGNATURE");

                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Closing error: " + e.toString());
        }
    }

    public static Bitmap eraseColor(Bitmap src, int color)
    {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap b = src.copy(Bitmap.Config.ARGB_8888, true);
        b.setHasAlpha(true);

        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++)
        {
            if (pixels[i] == color)
            {
                pixels[i] = 0;
            }
        }

        b.setPixels(pixels, 0, width, 0, 0, width, height);

        return b;
    }

}
