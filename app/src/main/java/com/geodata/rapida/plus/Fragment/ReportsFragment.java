package com.geodata.rapida.plus.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMissionOrders;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ReportsFragment extends Fragment
{
    private static final String TAG = ReportsFragment.class.getSimpleName();

    ListView lvReports;

    LinearLayout ll_no_reports_available;

    View view;

    TextView tv_reportPDFLink;

    PDFView pdfView;

    File file;

    String MissionOrderNo, PDFFileName, MissionOrderID, SeismicityRegion, ReasonForInspector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(getActivity())); //Getting Crash - Restart

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reports, container, false);

        initViews();

        return view;
    }

    private void initViews()
    {
        lvReports = view.findViewById(R.id.listViewReports);
        lvReports.setVisibility(View.GONE);

        ll_no_reports_available = view.findViewById(R.id.ll_no_reports_available);

        tv_reportPDFLink = view.findViewById(R.id.tv_reportPDFLink);

        pdfView  = view.findViewById(R.id.pdfView);


        try
        {
            Bundle extras = requireActivity().getIntent().getExtras();

            if(extras != null)
            {
                MissionOrderNo     = extras.getString("MissionOrderNo");
                MissionOrderID     = extras.getString("MissionOrderID");
                SeismicityRegion   = extras.getString("SeismicityRegion");
                ReasonForInspector = extras.getString("ReasonForInspector");

                Log.e(TAG, "MissionOrderNo: " + MissionOrderNo + "\n" +
                                "MissionOrderID: " + MissionOrderID + "\n" +
                                "SeismicityRegion: " + SeismicityRegion + "\n" +
                                "ReasonForInspector: " + ReasonForInspector + "\n"
                        );

                Cursor cursor = RepositoryOnlineMissionOrders.realAllData(getContext(), UserAccount.UserAccountID, MissionOrderNo);

                if (cursor.getCount()!=0)
                {
                    if (cursor.moveToFirst())
                    {
                        String FilePath = cursor.getString(cursor.getColumnIndexOrThrow("ReportPath"));
                        String MissionOrderType = cursor.getString(cursor.getColumnIndexOrThrow("MissionOrderType"));

                        if (!FilePath.trim().isEmpty())
                        {
                            Log.e(TAG, "MissionOrderType: " + MissionOrderType);

                            PDFFileName = FilePath.substring(FilePath.lastIndexOf("/") + 1 );

                            Log.e(TAG, "PDFFileName: " + PDFFileName);

                            if (ReasonForInspector.contains("RESA"))
                            {
                                String FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SRI/RESA/" + PDFFileName;

                                file = new File(FILE);
                            }
                            else if (ReasonForInspector.contains("DESA"))
                            {
                                String FILE;

                                if (MissionOrderType.equalsIgnoreCase("SRI"))
                                {
                                    FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SRI/RVS Scoring/" + PDFFileName;
                                }
                                else
                                {
                                    FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SRI/DESA/" + PDFFileName;
                                }
                                file = new File(FILE);
                            }
                            else
                            {
                                String FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SRI/RVS Scoring/" + PDFFileName;

                                Log.e(TAG, "FILE: " + FILE);

                                file = new File(FILE);
                            }

                            initViewPDF();
                        }
                        else
                        {
                            ll_no_reports_available.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else
                {
                    ll_no_reports_available.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                ll_no_reports_available.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initPDFReport()
    {
        try
        {
            if (file.exists())
            {
                tv_reportPDFLink.setVisibility(View.VISIBLE);
                tv_reportPDFLink.setText(PDFFileName);

                tv_reportPDFLink.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                            m.invoke(null);

                            Uri uri = Uri.fromFile(file);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initViewPDF()
    {
        try
        {
            if (file != null && file.exists())
            {
                Log.e(TAG, "FILE EXIST");

                pdfView.setBackgroundColor(Color.LTGRAY);

                pdfView.fromFile(file)
                        //.pages(0, 1, 2, 3, 4) // all pages are displayed by default
                        .enableSwipe(true) // allows to block changing pages using swipe
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .defaultPage(0)
                        .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                        .password(null)
                        .scrollHandle(new DefaultScrollHandle(getContext()))
                        .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                        .spacing(10) // spacing between pages in dp. To define spacing color, set view background
                        .load();
            }
            else
            {
                ll_no_reports_available.setVisibility(View.VISIBLE);

                Log.e(TAG, "FILE NOT EXIST");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            ll_no_reports_available.setVisibility(View.VISIBLE);
        }
    }

    //All REPORTS PDF
    private void initPDFReportList()
    {
        try
        {
            String path = Environment.getExternalStorageDirectory().getPath();

            File file = new File(path, "SRI" + "/" + "RVS Scoring");

            Object[] arrays = getListFiles(file);

            ArrayList<String> fileNames = (ArrayList<String>) arrays[0];
            final ArrayList<File> files = (ArrayList<File>) arrays[1];

            lvReports.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, fileNames));

            lvReports.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    try
                    {
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);

                        Uri pathh = Uri.fromFile(files.get(position));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(pathh, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(), "No report to view", Toast.LENGTH_SHORT).show();
        }
    }

    private Object[] getListFiles(File parentDir)
    {
        ArrayList<String> str = new ArrayList<String>();
        ArrayList<File> inFile = new ArrayList<File>();
        File[] files = parentDir.listFiles();

        for (File file : files)
        {
            if (file.getName().endsWith(".pdf"))
            {
                str.add(file.getName());
                inFile.add(file);
            }
        }
        return new Object[]{str, inFile};
    }
}