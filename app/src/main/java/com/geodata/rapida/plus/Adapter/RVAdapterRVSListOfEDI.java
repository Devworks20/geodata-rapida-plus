package com.geodata.rapida.plus.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.geodata.rapida.plus.Activity.ViewPDFNewRVSActivity;
import com.geodata.rapida.plus.Activity.ViewRVSReportPDF;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Model.EarthquakeRVSReportModel;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RVAdapterRVSListOfEDI extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAdapterRVSListOfEDI.class.getSimpleName();

    Context context;
    List<EarthquakeRVSReportModel> earthquakeRvsReportModel;

    public RVAdapterRVSListOfEDI(Context context, List<EarthquakeRVSReportModel> occupanciesModelList)
    {
        this.context            = context;
        this.earthquakeRvsReportModel = occupanciesModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_rvs_list_edi, parent, false);

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
        return earthquakeRvsReportModel.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView tv_RVSNo, tv_building_name, tv_seismicity_region,
                 tv_screening_date, tv_final_score, tv_view_pdf_report;

        public MyHolder(View view)
        {
            super(view);

            tv_RVSNo             = itemView.findViewById(R.id.tv_RVSNo);
            tv_building_name     = itemView.findViewById(R.id.tv_building_name);
            tv_seismicity_region = itemView.findViewById(R.id.tv_seismicity_region);
            tv_screening_date    = itemView.findViewById(R.id.tv_screening_date);
            tv_final_score       = itemView.findViewById(R.id.tv_final_score);
            tv_view_pdf_report   = itemView.findViewById(R.id.tv_view_pdf_report);

        }

        public void bindView(final int position)
        {
            final EarthquakeRVSReportModel current = earthquakeRvsReportModel.get(position);

            tv_RVSNo.setText(current.getEarthquakeRVSReportID());
            tv_building_name.setText(current.getBuildingName());
            tv_seismicity_region.setText(current.getSeismicity());
            tv_screening_date.setText(FormatDate(current.getScreeningDate()));
            tv_final_score.setText(current.getFinalScore());

            tv_view_pdf_report.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        if (current.getEarthquakeRVSReportPdfPath() != null &&
                                !current.getEarthquakeRVSReportPdfPath().equals(""))
                        {
                            String FileName = current.getEarthquakeRVSReportPdfPath().
                                    substring(current.getEarthquakeRVSReportPdfPath().lastIndexOf("/") + 1 );



                            Intent intent = new Intent(context, ViewRVSReportPDF.class);
                            intent.putExtra("FileName", FileName);
                            intent.putExtra("BuildingName", current.getBuildingName());
                            ((Activity) context).startActivityForResult(intent, 100);
                        }
                        else
                        {
                            Toast.makeText(context, "Failed to view the PDF report.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
    }


    private String FormatDate(String strDate)
    {
        String datetime = null;

        @SuppressLint("SimpleDateFormat")
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        @SuppressLint("SimpleDateFormat")
        DateFormat outputFormat  = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        try
        {
            Date convertedDate = inputFormat.parse(strDate);

            if (convertedDate != null)
            {
                datetime = outputFormat.format(convertedDate);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return  datetime;
    }
}
