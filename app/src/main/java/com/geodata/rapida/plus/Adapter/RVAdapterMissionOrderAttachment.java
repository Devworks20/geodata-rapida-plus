package com.geodata.rapida.plus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Model.MOFileAttachmentsList;
import com.geodata.rapida.plus.Activity.TempFileViewActivity;

import java.io.File;
import java.util.List;


public class RVAdapterMissionOrderAttachment extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAdapterMissionOrderAttachment.class.getSimpleName();

    Context context;
    List<MOFileAttachmentsList> moFileAttachmentsLists;

    public RVAdapterMissionOrderAttachment(Context context, List<MOFileAttachmentsList> moFileAttachmentsLists)
    {
        this.context                = context;
        this.moFileAttachmentsLists = moFileAttachmentsLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_mission_order_attachment, parent, false);

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
        return moFileAttachmentsLists.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        LinearLayout ll_attachment_details, ll_no_attachment;

        ImageView iv_image_attachment;

        TextView tv_filename_attachment, tv_file_size_attachment;

        public MyHolder(View view)
        {
            super(view);

            ll_attachment_details   = itemView.findViewById(R.id.ll_attachment_details);
            ll_no_attachment        = itemView.findViewById(R.id.ll_no_attachment);

            tv_filename_attachment  = itemView.findViewById(R.id.tv_filename_attachment);
            tv_file_size_attachment = itemView.findViewById(R.id.tv_file_size_attachment);

            iv_image_attachment     = itemView.findViewById(R.id.iv_image_attachment);
        }

        public void bindView(final int position)
        {
            final MOFileAttachmentsList current = moFileAttachmentsLists.get(position);

            if (current.getMOAttachmentFilePath() != null &&
                !current.getMOAttachmentFilePath().equals("") &&
                !current.getMOAttachmentFilePath().equals(" "))
            {
                String sFileName = current.getFileName();
                tv_filename_attachment.setText(sFileName);

                final String AttachmentPath      = current.getMOAttachmentFilePath();
                final String AttachmentExtension = AttachmentPath.substring(AttachmentPath.lastIndexOf(".") + 1);

                //Set Image - Icon
                initSetIcon(context, AttachmentExtension, iv_image_attachment);

                String ExternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SRI/MissionOrder/Attachment";
                String FullFilePath = ExternalPath + "/" + sFileName;

                String[] splitCustomizePathName   = sFileName.split("\\.");
                String customizePathName = splitCustomizePathName[0];


                File file = new File(FullFilePath);

                if (file.exists())
                {
                    String FileSize = getFolderSizeLabel(file);
                    tv_file_size_attachment.setText(FileSize);

                    ll_attachment_details.setVisibility(View.VISIBLE);
                    ll_attachment_details.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(context, TempFileViewActivity.class);
                            intent.putExtra("FilePath", FullFilePath);
                            intent.putExtra("FileExtension", AttachmentExtension);
                            intent.putExtra("FileName", sFileName);
                            ((Activity) context).startActivityForResult(intent, 202);
                        }
                    });
                }
                else
                {
                    ll_attachment_details.setVisibility(View.GONE);
                    ll_no_attachment.setVisibility(View.GONE);
                }
            }
            else
            {
                ll_attachment_details.setVisibility(View.GONE);
                ll_no_attachment.setVisibility(View.GONE);
            }
        }
    }

    private void initSetIcon(Context context, String AttachmentExtension, ImageView imageView)
    {
        try
        {
            switch (AttachmentExtension.toLowerCase())
            {
                case "pdf":
                    Glide.with(context).load(R.drawable.pdf_icon).into(imageView);
                    break;
                case "docx":
                case "docs":
                    Glide.with(context).load(R.drawable.word_png).into(imageView);
                    break;
                case "ppt":
                case "pptx":
                    Glide.with(context).load(R.drawable.ppt_png).into(imageView);
                    break;
                case "apk":
                    Glide.with(context).load(R.drawable.apk_icon).into(imageView);
                    break;
                case "xlsx":
                    Glide.with(context).load(R.drawable.excel_icon).into(imageView);
                    break;
                case "avi":
                case "mpe":
                case "mpeg":
                case "mpg":
                case "3gp":
                case "mp4":
                case "MP4":
                case "video":
                    Glide.with(context).load(R.drawable.video_play_icon).into(imageView);
                    break;
                case "bmp":
                case "gif":
                case "jpg":
                case "jpeg":
                case "png":
                case "webp":
                case "heic":
                case "heif":
                case "image":
                    Glide.with(context).load(R.drawable.image_icon_png).into(imageView);
                    break;
                default:
                    Glide.with(context).load(R.drawable.file_png).into(imageView);
                    break;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public static String getFolderSizeLabel(File file)
    {
        long size = getFolderSize(file) / 1024; // Get size and convert bytes into Kb.

        if (size >= 1024)
        {
            return (size / 1024) + "MB";
        }
        else
        {
            return size + "KB";
        }
    }

    public static long getFolderSize(File file)
    {
        long size = 0;

        if (file.isDirectory())
        {
            for (File child : file.listFiles())
            {
                size += getFolderSize(child);
            }
        }
        else
        {
            size = file.length();
        }
        return size;
    }

}
