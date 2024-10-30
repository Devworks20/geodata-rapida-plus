package com.geodata.rapida.plus.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.geodata.rapida.plus.R;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
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
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RapidVisualScreeningActivity extends AppCompatActivity
{
    private static final String TAG = RapidVisualScreeningActivity.class.getSimpleName();

    String PdfFolderName;

    Button btn_generate_pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapid_visual_screening);

        initViews();

    }

    private void initViews()
    {
        PdfFolderName =  "SRI" + "/" + "Rapid Visual Screening";

        btn_generate_pdf = findViewById(R.id.btn_generate_pdf);

        initListeners();
    }

    private void initListeners()
    {
        btn_generate_pdf.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e(TAG, "BUTTON CLICKED");

                createFolder();
            }
        });
    }

    private void createFolder()
    {
        File theFile = new File(Environment.getExternalStorageDirectory() + "/" + PdfFolderName);

        if (!theFile.exists())
        {
            theFile.mkdirs();

        }
        else
        {
            initDocument();
        }
    }

    private void initDocument()
    {
        try
        {
            String FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PdfFolderName + "/" + "Report" + "." + "PDF";

            Document document = new Document(PageSize.LEGAL);

            try
            {
                PdfWriter.getInstance(document, new FileOutputStream(FILE));

                document.open();

                initGeneratePDF(document);

                Toast.makeText(this, "PDF Generated!", Toast.LENGTH_SHORT).show();
            }
            catch (FileNotFoundException | DocumentException e)
            {
                Log.e(TAG, e.toString());

                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            document.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGeneratePDF(Document document) throws DocumentException  //Set PDF document Properties
    {

        Log.e(TAG, "GENERATE PDF");

        Date date = new Date();

        // Font Style for Document
        Font catFont                   = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font header1                   = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        Font header2                   = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font header3                   = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        Font secondPageTitle           = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
        Font secondPageTitleUnderlined = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD | Font.UNDERLINE);

        Font smallNormal               = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font smallBold                 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font italicize                 = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);

        Font normal                    = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL | Font.UNDERLINE);

        try
        {
          /*  InputStream check = getResources().getAssets().open("checka.png");
            Bitmap bm = BitmapFactory.decodeStream(check);
            ByteArrayOutputStream str = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, str);

            Image chk = Image.getInstance(str.toByteArray());
            chk.scaleAbsolute(10f, 10f);

            InputStream uncheka = getResources().getAssets().open("uncheka.png");
            Bitmap bmc = BitmapFactory.decodeStream(uncheka);
            ByteArrayOutputStream strc = new ByteArrayOutputStream();
            bmc.compress(Bitmap.CompressFormat.PNG, 100, strc);

            Image uchk = Image.getInstance(strc.toByteArray());
            uchk.scaleAbsolute(10f, 10f);*/


            //HEADER
            PdfPTable table = new PdfPTable(3);
            table.setTotalWidth(550);
            table.setLockedWidth(true);
            table.setWidths(new int[]{70, 410, 70});

            Drawable logo1 = ContextCompat.getDrawable(this, R.drawable.logo1_png);
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) logo1);
            assert bitmapDrawable != null;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            Image LogoLeft = Image.getInstance(stream.toByteArray());
            LogoLeft.scaleAbsolute(81f, 80f);

            PdfPCell cells;
            cells = new PdfPCell();
            cells.setPaddingTop(15);
            cells.addElement(new Chunk(LogoLeft, 17, -28));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setHorizontalAlignment(Element.ALIGN_BASELINE);
            cells.setBorder(Rectangle.NO_BORDER);
            table.addCell(cells);

            Phrase headerPhrase = new Phrase(new Chunk("Republic of the Philippines", header1));
            headerPhrase.add(new Phrase(new Chunk("\nDEPARTMENT OF PUBLIC WORKS AND HIGHWAYS", header1)));
            headerPhrase.add(new Phrase(new Chunk("\n" +"CENTRAL OFFICE", header2)));
            headerPhrase.add(new Phrase(new Chunk("\nManila", header1)));
            headerPhrase.add(new Phrase(new Chunk("\n\nRapid Visual Screening for Potential Seismic Hazards", header3)));

            cells = new PdfPCell(headerPhrase);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.NO_BORDER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setHorizontalAlignment(Element.ALIGN_BASELINE);
            cells.setBorder(Rectangle.NO_BORDER);
            table.addCell(cells);
            //END HEADER

            document.add(table);

            /*
            //2ND HEADER
            table = new PdfPTable(2);
            table.setTotalWidth(numwithd);
            table.setWidths(new int[]{450, 100});
            table.setLockedWidth(true);

            cells = new PdfPCell(new Phrase("\n", smallNormal));
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(2);
            cells.setPadding(5);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("CONTROL NO. : ", smallNormal));
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPadding(5);
            table.addCell(cells);
            //ADD TO CELL 1

            cells = new PdfPCell(new Phrase(ATISInspection_TABHOST.ControlNo, smallNormal));
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPadding(5);
            table.addCell(cells);
            //ADD TO CELL 2

            cells = new PdfPCell(new Phrase("Date:", smallNormal));
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.NO_BORDER);
            table.addCell(cells);
            //ADD TO CELL 3

            String insDate = new SimpleDateFormat("MMM dd,yyyy", Locale.getDefault()).format(date);
            Chunk dateUnderline = new Chunk(insDate);
            dateUnderline.setUnderline(0.1f, -2f);
            cells = new PdfPCell(new Phrase(String.valueOf(dateUnderline), smallNormal));
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.NO_BORDER);
            table.addCell(cells);

            Phrase p1Phrase = new Phrase(new Chunk("SITE VERIFICATION REPORT (BPD)", secondPageTitleUnderlined));
            cells = new PdfPCell(p1Phrase);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(2);
            cells.setPaddingTop(15);
            cells.setPaddingBottom(15);
            table.addCell(cells);
            //END 2ND HEADER

            document.add(table);


            //ERI
            table = new PdfPTable(4);
            table.setTotalWidth(numwithd);
            table.setLockedWidth(true);
            table.setWidths(new int[]{180, 130, 120, 120});

            cells = new PdfPCell(new Phrase("Earthquake Recording Instrumentation", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (rb_ERI_Building1.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building1.getText().toString(), smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building1.getText().toString(), smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingTop(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (rb_ERI_Building2.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building2.getText().toString(), smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building2.getText().toString(), smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingTop(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("\nType of Building: \n", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            //Building
            if (rb_ERI_Building3.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building3.getText().toString(), smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            } else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building3.getText().toString(), smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            //Commercial
            if (rb_ERI_Building6.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building6.getText().toString(), smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building6.getText().toString() , smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            //Hospital
            if (rb_ERI_Building4.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building4.getText().toString(), smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building4.getText().toString() , smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            //Industrial
            if (rb_ERI_Building7.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building7.getText().toString(), smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building7.getText().toString() , smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            //Provincial
            if (rb_ERI_Building5.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building5.getText().toString(), smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + rb_ERI_Building5.getText().toString() , smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Device Location", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(1);
            cells.setPadding(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (cbo_groundFloor.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + "Ground Floor/Lower Basement", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + "Ground Floor/Lower Basement", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setColspan(3);
            cells.setPadding(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setPaddingTop(10);
            cells.setColspan(1);
            cells.setPadding(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (cbo_middleFloor.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + "Middle Floor", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + "Middle Floor", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setColspan(3);
            cells.setPadding(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setPaddingTop(5);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (cbo_floorBelowRoof.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + "Floor Below Roof", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            } else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + "Floor Below Roof", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setColspan(3);
            cells.setPadding(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setPaddingTop(5);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (cbo_otherLocation.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1));
                Paragraph parag2 = new Paragraph(new Chunk("   " + "Other Location: ", smallNormal));
                Paragraph parag3 = new Paragraph(new Chunk(edt_eri_otherLocation.getText().toString(), normal));

                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                comba.add(parag3);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1));
                Paragraph parag2 = new Paragraph(new Chunk("   " + "Other Location: " + "", smallNormal));
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(3);
            cells.setPadding(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            Phrase DINPhrase = new Phrase("Details Installation Number: ", smallNormal);
            DINPhrase.add(new Phrase(new Chunk(edt_eri_detailsInstallationNumber.getText().toString(), normal)));
            cells = new PdfPCell(DINPhrase);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPaddingTop(20);
            cells.setPadding(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (cbo_eri_DIN.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1));
                Paragraph parag2 = new Paragraph(new Chunk("   " + cbo_eri_DIN.getText().toString(), smallNormal));
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axis
                Paragraph parag1 = new Paragraph(new Chunk(uchk, 3, -1));
                Paragraph parag2 = new Paragraph(new Chunk("   " + cbo_eri_DIN.getText().toString() + "", smallNormal));
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(2);
            cells.setPadding(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            Phrase LCNPhrase = new Phrase("Latest Certification No.: ", smallNormal);
            LCNPhrase.add(new Phrase(new Chunk(edt_eri_latestCertNo.getText().toString(), normal)));
            cells = new PdfPCell(LCNPhrase);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            Phrase PCPhrase =  new Phrase("Physical Condition: ", smallNormal);
            PCPhrase.add(new Phrase(new Chunk(edt_eri_physicalCondition.getText().toString(), normal)));
            cells = new PdfPCell(PCPhrase);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(10);
            cells.setPaddingBottom(20);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            Phrase SVPhrase = new Phrase("Installation under Supervisor of: \n\n              ", smallNormal);
            SVPhrase.add(new Phrase(new Chunk(edt_eri_superVisorOf.getText().toString(), normal)));
            cells = new PdfPCell(SVPhrase);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(10);
            cells.setPaddingBottom(30);
            cells.setBorder(Rectangle.LEFT| Rectangle.BOTTOM);
            table.addCell(cells);

            Phrase CBPhrase = new Phrase("Confirmed by: \n\n        ", smallNormal);
            CBPhrase.add(new Phrase(new Chunk(edt_eri_confirmedBy.getText().toString(), normal)));
            cells = new PdfPCell(CBPhrase);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT| Rectangle.BOTTOM);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(10);
            cells.setPaddingBottom(30);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);
            //END ERI

            document.add(table);


            //1ST DOCUMENT
            table = new PdfPTable(4);
            table.setTotalWidth(numwithd);
            table.setLockedWidth(true);
            table.setWidths(new int[]{180, 130, 120, 120});

            //NEW PAGE
            cells = new PdfPCell(new Phrase("A. BASE BUILDING INFORMATION", smallBold));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Building Name: " + ATISInspection_TABHOST.company_name, smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Owner: " + ATISInspection_TABHOST.OwnerCompleteName, smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("L.C. No.: " + "", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Date Issued: " + "", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("(If Corporation) " + "", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("TCT No.: " + ATISInspection_TABHOST.TCTNo, smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Address: " + ATISInspection_TABHOST.CompleteAddress +
                    "               _____________________________________________",
                    smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setRowspan(3);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("No. of Storeys : " + ATISInspection_TABHOST.NoOfFloors, smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase("Floor Area : " + ATISInspection_TABHOST.FloorArea + "sq. m.", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Type of Occupancy:", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Building Permit No.: " + ATISInspection_TABHOST.BPNo, smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Date Issued: " + ATISInspection_TABHOST.BPDate, smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (one == null)
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("   " + "Residential", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("   " + "Residential", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (two == null)
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("   " + "Industrial", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("   " + "Industrial", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Occupancy Permit No.: " + ATISInspection_TABHOST.OccupancyPermitNo, smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(1);
            cells.setRowspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Date Issued: " + ATISInspection_TABHOST.DateOccupancyPermitNo, smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.BOTTOM);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(1);
            cells.setRowspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (three == null)
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("   " + "Commercial", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axis
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("   " + "Commercial", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (four == null)
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + "Others:(mixed use)",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("   " + "Others:(mixed use)",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (five == null)
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("   " + "Institutional", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                //X axis  Y axiss
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("   " + "Institutional", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase("B. ESTABLISHING INFORMATION:", smallBold));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Establishment name: " + "", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Unit/Stall No.: " + "", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Owner: " + "" + " " + "" + " "
                    + "", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Level/Storey: " + "", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("(If Corporation) :" + "", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Floor Area : " + "" + "sq. m.", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingBottom(30);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);
            //END 1ST DOCUMENT

            document.add(table);


            //NEW PAGE - FOR SECOND PAGE
            document.newPage();


            //2ND DOCUMENT
            table = new PdfPTable(4);
            table.setTotalWidth(numwithd);
            table.setLockedWidth(true);
            table.setWidths(new int[]{125, 90, 75, 260});

            cells = new PdfPCell(new Phrase("", smallBold));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setPaddingBottom(100);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("C. VERIFICATION DATA", smallBold));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.TOP);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("I. Work Status", smallBold));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(3);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("II. Site Condition", smallBold));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (NewConstruction.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "New Construction",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "New Construction",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (Renovation.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Renovation", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Renovation", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (AsBuilt.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "As-Built", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "As-Built", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (vacantLot.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Vacant Lot", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Vacant Lot", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (demolition.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Demolition", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            } else {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Demolition", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (repair.isChecked()) {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Repair", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            } else {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Repair", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (otherChk.isChecked()) {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Others", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            } else {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Others", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (withExist.isChecked()) {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "With Existing Structures\n       ",
                                smallNormal)); //This gonna be normal font
                Paragraph parag3 = new Paragraph(new Chunk(withExistEDT.getText().toString(), normal));
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                comba.add(parag3);
                cells = new PdfPCell(new Phrase(comba));
            } else {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "With Existing Structures\n       ",
                                smallNormal)); //This gonna be normal font
                Paragraph parag3 = new Paragraph(new Chunk(withExistEDT.getText().toString(), normal));
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                comb.add(parag3);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(5);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (addExten.isChecked()) {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "Addition/Extension",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            } else {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "Addition/Extension",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (fencing.isChecked()) {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Fencing", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            } else {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Fencing", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.BOTTOM);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(et1.getText().toString(), normal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(1);
            cells.setPadding(5);
            cells.setPaddingBottom(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);
            //2ND DOCUMENT

            document.add(table);


            //3RD DOCUMENT
            table = new PdfPTable(4);
            table.setTotalWidth(numwithd);
            table.setLockedWidth(true);
            table.setWidths(new int[]{145, 145, 120, 140});

            if (notYet.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Not yet started", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Not yet started", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);


            if (started.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Started", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "Started", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (withRivers.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "With Rivers/Creeks/Streams/Easements",
                                smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new Phrase(comba));
            } else {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "With Rivers/Creeks/Streams/Easements",
                                smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(2);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (onGoing.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "On-going", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "On-going", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (withNotice.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "With Notice of Violation",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "With Notice of Violation",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (atTheRear.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "at the rear", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "at the rear", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (withinThe.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "within the property",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "within the property",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (stopConst.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "Stop Construction",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "Stop Construction",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (withCease.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "With Cease & Desist\n          Order",
                                smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "With Cease & Desist\n          Order",
                                smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (atTheSide.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "at the side/s", smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "at the side/s", smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (adjoin.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "adjoining property",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "adjoining property",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(1);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);


            if (withAdmin.isChecked())
            {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "With Administrative Hearing",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "With Administrative Hearing",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT | Rectangle.LEFT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(2);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (withInformal.isChecked())
            {
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "With Informal Settlers\n" +
                        "       ", smallNormal)); //This gonna be normal font
                Paragraph parag3 = new Paragraph(new Chunk(withInformalEDT.getText().toString(), normal));
                Paragraph comba = new Paragraph();
                comba.add(parag1);
                comba.add(parag2);
                comba.add(parag3);
                cells = new PdfPCell(new Phrase(comba));
            }
            else
            {
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  " + "With Informal Settlers\n" +
                        "       ", smallNormal)); //This gonna be normal font
                Paragraph parag3 = new Paragraph(new Chunk(withInformalEDT.getText().toString(), normal));
                Paragraph comb = new Paragraph();
                comb.add(parag1);
                comb.add(parag2);
                comb.add(parag3);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(2);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            try
            {
                Cursor cursor = SiteInspectionRepository.readAllSiteInspectionOther(getApplicationContext());
                if (cursor.getCount() != 0)
                {
                    cursor.moveToFirst();
                    do
                    {
                        String otherTitle   = cursor.getString(cursor.getColumnIndex("otherTitle"));
                        String otherMessage = cursor.getString(cursor.getColumnIndex("otherMessage"));
                        String isActive     = cursor.getString(cursor.getColumnIndex("isActive"));

                        cells = new PdfPCell(new Phrase("", normal));
                        cells.setBackgroundColor(BaseColor.WHITE);
                        cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                        cells.setBorderWidth(borderwidth);
                        cells.setColspan(2);
                        cells.setPadding(5);
                        cells.setPaddingBottom(10);
                        cells.setBorderColor(BaseColor.BLACK);
                        table.addCell(cells);

                        if (isActive.equals("1"))
                        {
                            Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                            Paragraph parag2 = new Paragraph(new Chunk("  " + otherTitle +"\n" +
                                    "       ", smallNormal)); //This gonna be normal font
                            Paragraph parag3 = new Paragraph(new Chunk(otherMessage, normal));
                            Paragraph comba = new Paragraph();
                            comba.add(parag1);
                            comba.add(parag2);
                            comba.add(parag3);
                            cells = new PdfPCell(new Phrase(comba));
                        }
                        else
                        {
                            Paragraph parag1 = new Paragraph(
                                    new Chunk(uchk, 3, -1)); //This gonna be normal font
                            Paragraph parag2 = new Paragraph(new Chunk("  " + otherTitle +"\n" +
                                    "       ", smallNormal)); //This gonna be normal font
                            Paragraph parag3 = new Paragraph(new Chunk(otherMessage, normal));
                            Paragraph comb = new Paragraph();
                            comb.add(parag1);
                            comb.add(parag2);
                            comb.add(parag3);
                            cells = new PdfPCell(new Phrase(comb));
                        }
                        cells.setBorder(Rectangle.RIGHT);
                        cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cells.setColspan(2);
                        cells.setRowspan(1);
                        cells.setPadding(5);
                        cells.setBorderWidth(borderwidth);
                        cells.setBorderColor(BaseColor.BLACK);
                        table.addCell(cells);

                    }
                    while (cursor.moveToNext());
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }


            cells = new PdfPCell(
                    new Phrase("__________________________________________________", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" ", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setRowspan(6);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (siteClear.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "05%     Site Clearing/Groun Operation",
                                smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "05%     Site Clearing/Groun Operation",
                                smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT | Rectangle.LEFT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(2);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (excav.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "10%     Excavation for Foundation",
                                smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(
                        new Chunk("  " + "10%     Excavation for Foundation",
                                smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT | Rectangle.LEFT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(2);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);


            if (constFounda.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  "
                        + "25%     Construction of Foundation including pile driving\n           "
                        + "           laying of Reinforcing bars",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  "
                        + "25%     Construction of Foundation including pile driving\n           "
                        + "           laying of Reinforcing bars",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT | Rectangle.LEFT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(2);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (constSuper.isChecked()) {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  "
                        + "50%     Construction of Superstructure up to\n                      "
                        + "2.00 meters abpve established grade",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  "
                        + "50%     Construction of Superstructure up to\n                      "
                        + "2.00 meters abpve established grade",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT | Rectangle.LEFT);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(2);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (constSuper1.isChecked())
            {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(new Chunk(chk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  "
                        + "100%     Construction of Superstructure up to\n                      "
                        + "2.00 meters",
                        smallNormal)); //This gonna be normal font
                Paragraph comba = new Paragraph();
                comba.add(parag1_1);
                comba.add(parag1);
                comba.add(parag2);
                cells = new PdfPCell(new
                        Phrase(comba));
            } else {
                Paragraph parag1_1 = new Paragraph(new Chunk("    ", smallNormal));
                Paragraph parag1 = new Paragraph(
                        new Chunk(uchk, 3, -1)); //This gonna be normal font
                Paragraph parag2 = new Paragraph(new Chunk("  "
                        + "100%     Construction of Superstructure up to\n                      "
                        + "2.00 meters",
                        smallNormal)); //This gonna be normal font
                Paragraph comb = new Paragraph();
                comb.add(parag1_1);
                comb.add(parag1);
                comb.add(parag2);
                cells = new PdfPCell(new Phrase(comb));
            }
            cells.setBorder(Rectangle.RIGHT | Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setColspan(2);
            cells.setRowspan(1);
            cells.setPadding(5);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("I, hereby certify that the findings/" +
                    "data recorded are factual & accurate to the best" +
                    "ability and good judgment.", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingBottom(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" ", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingBottom(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);


            //1ST SIGNATURE
            Bitmap bitmapSign1 = siteInspection_SP_ERI_SPN.getTransparentSignatureBitmap();
            ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
            bitmapSign1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream1);
            Image signature1 = Image.getInstance(byteArrayOutputStream1.toByteArray());
            signature1.setAlignment(Image.ALIGN_CENTER);
            signature1.scaleAbsolute(50f, 50f);

            PdfPCell cellSignature;
            cellSignature = new PdfPCell(signature1);
            cellSignature.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellSignature.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cellSignature.setBorderWidth(borderwidth);
            cellSignature.setColspan(2);
            cellSignature.setPadding(5);
            cellSignature.setBorderColor(BaseColor.BLACK);
            table.addCell(cellSignature);

            //2ND SIGNATURE
            Bitmap bitmapSign2 = siteInspection_SP_ERI_SPN2.getTransparentSignatureBitmap();
            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            bitmapSign2.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
            Image signature2 = Image.getInstance(byteArrayOutputStream2.toByteArray());
            signature2.setAlignment(Image.ALIGN_CENTER);
            signature2.scaleAbsolute(50f, 50f);

            PdfPCell cellSignature2;
            cellSignature2 = new PdfPCell(signature2);
            cellSignature2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellSignature2.setBorder(Rectangle.RIGHT);
            cellSignature2.setBorderWidth(borderwidth);
            cellSignature2.setColspan(2);
            cellSignature2.setPadding(5);
            cellSignature2.setBorderColor(BaseColor.BLACK);
            table.addCell(cellSignature2);


            cells = new PdfPCell(new Phrase(edt_signature1_name.getText().toString() + "\n"
                    +"_____________________________________\n" +
                    "Signature over Printed Name", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingBottom(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_signature2_name.getText().toString() + "\n"
                    +"_____________________________________\n" +
                    "Chief, Building Permit Division", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingBottom(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase("Performed in the Presence of:", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.LEFT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingBottom(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingBottom(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);


            Bitmap bitmapSign3 = siteInspection_SP_ERI_SPN3.getTransparentSignatureBitmap();
            ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
            bitmapSign3.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream3);
            Image signature3 = Image.getInstance(byteArrayOutputStream3.toByteArray());
            signature3.setAlignment(Image.ALIGN_CENTER);
            signature3.scaleAbsolute(50f, 50f);

            PdfPCell cellSignature3;
            cellSignature3 = new PdfPCell(signature3);
            cellSignature3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellSignature3.setBorder(Rectangle.LEFT);
            cellSignature3.setBorderWidth(borderwidth);
            cellSignature3.setColspan(2);
            cellSignature3.setPadding(5);
            cellSignature3.setBorderColor(BaseColor.BLACK);
            table.addCell(cellSignature3);


            cells = new PdfPCell(
                    new Phrase("Contact No.: " + edt_signature3_contactNo.getText().toString() +
                            "\n                     " +
                            "______________________________", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingBottom(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);


            Phrase performedPhrase = new Phrase(
                    new Chunk(edt_signature3_name.getText().toString()+"\n_____________________________________\n", smallNormal));
            performedPhrase.add(new Phrase(new Chunk("Printed Name", smallBold)));
            cells = new PdfPCell(performedPhrase);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingBottom(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Date / Time:  " + edt_signature3_dateTime.getText().toString() +
                    "\n                     " +
                    "______________________________", smallNormal));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(2);
            cells.setPadding(5);
            cells.setPaddingBottom(10);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);
            //END 3RD DOCUMENT

            document.add(table);

            document.newPage();


            //IMAGE CAPTURED
            PdfPTable table = new PdfPTable(4);
            table.setTotalWidth(550);
            table.setLockedWidth(true);
            table.setWidths(new int[]{145, 145, 120, 140});

            cells = new PdfPCell(new Phrase("\n", smallBold));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("IMAGE CAPTURED\n", smallBold));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("\n", smallBold));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setBorderWidth(borderwidth);
            cells.setColspan(4);
            cells.setPadding(5);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Ground Floor/ Lower Basement", smallNormal));
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
            cells.setColspan(2);
            cells.setPaddingTop(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Middle Floor", smallNormal));
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.RIGHT | Rectangle.TOP);
            cells.setColspan(2);
            cells.setPaddingTop(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (groundFloorBM != null)
            {
                ByteArrayOutputStream byteArrayOutputStreamCam1 = new ByteArrayOutputStream();
                groundFloorBM.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamCam1);

                Image imageCam1 =Image.getInstance(byteArrayOutputStreamCam1.toByteArray());
                imageCam1.setAlignment(Image.ALIGN_CENTER);
                imageCam1.scaleAbsolute(224.5f, 120f);

                cells = new PdfPCell(imageCam1);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                cells.setColspan(2);
                cells.setPadding(10);
                cells.setBorderWidth(borderwidth);
                cells.setBorderColor(BaseColor.BLACK);
                table.addCell(cells);
            }
            else
            {
                cells = new PdfPCell(new Phrase("NO PICTURE", smallNormal));
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                cells.setColspan(2);
                cells.setPaddingTop(50);
                cells.setPaddingBottom(10);
                cells.setBorderWidth(borderwidth);
                cells.setBorderColor(BaseColor.BLACK);
                table.addCell(cells);
            }

            if (middleFloorBM != null)
            {
                ByteArrayOutputStream byteArrayOutputStreamCam2 = new ByteArrayOutputStream();
                middleFloorBM.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamCam2);

                Image imageCam2 =Image.getInstance(byteArrayOutputStreamCam2.toByteArray());
                imageCam2.setAlignment(Image.ALIGN_CENTER);
                imageCam2.scaleAbsolute(224.5f, 120f);

                cells = new PdfPCell(imageCam2);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder( Rectangle.RIGHT | Rectangle.BOTTOM);
                cells.setColspan(2);
                cells.setPadding(10);
                cells.setBorderWidth(borderwidth);
                cells.setBorderColor(BaseColor.BLACK);
                table.addCell(cells);
            }
            else
            {
                cells = new PdfPCell(new Phrase("NO PICTURE", smallNormal));
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder( Rectangle.RIGHT | Rectangle.BOTTOM);
                cells.setColspan(2);
                cells.setPaddingTop(50);
                cells.setPaddingBottom(10);
                cells.setBorderWidth(borderwidth);
                cells.setBorderColor(BaseColor.BLACK);
                table.addCell(cells);
            }

            cells = new PdfPCell(new Phrase("Floor Below Roof", smallNormal));
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setColspan(2);
            cells.setPadding(10);
            cells.setPaddingTop(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Other Location", smallNormal));
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.RIGHT);
            cells.setColspan(2);
            cells.setPadding(10);
            cells.setPaddingTop(10);
            cells.setBorderWidth(borderwidth);
            cells.setBorderColor(BaseColor.BLACK);
            table.addCell(cells);

            if (floorBelowRoofBP != null)
            {
                ByteArrayOutputStream byteArrayOutputStreamCam3 = new ByteArrayOutputStream();
                floorBelowRoofBP.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamCam3);

                Image imageCam3 =Image.getInstance(byteArrayOutputStreamCam3.toByteArray());
                imageCam3.setAlignment(Image.ALIGN_CENTER);
                imageCam3.scaleAbsolute(224.5f, 120f);

                cells = new PdfPCell(imageCam3);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                cells.setColspan(2);
                cells.setPadding(10);
                cells.setBorderWidth(borderwidth);
                cells.setBorderColor(BaseColor.BLACK);
                table.addCell(cells);
            }
            else
            {
                cells = new PdfPCell(new Phrase("NO PICTURE", smallNormal));
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                cells.setColspan(2);
                cells.setPaddingTop(50);
                cells.setPaddingBottom(10);
                cells.setBorderWidth(borderwidth);
                cells.setBorderColor(BaseColor.BLACK);
                table.addCell(cells);
            }

            if (otherLocationBP != null)
            {
                ByteArrayOutputStream byteArrayOutputStreamCam4 = new ByteArrayOutputStream();
                otherLocationBP.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamCam4);

                Image imageCam4 =Image.getInstance(byteArrayOutputStreamCam4.toByteArray());
                imageCam4.setAlignment(Image.ALIGN_CENTER);
                imageCam4.scaleAbsolute(224.5f, 120f);

                cells = new PdfPCell(imageCam4);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder( Rectangle.RIGHT | Rectangle.BOTTOM);
                cells.setColspan(2);
                cells.setPadding(10);
                cells.setBorderWidth(borderwidth);
                cells.setBorderColor(BaseColor.BLACK);
                table.addCell(cells);
            }
            else
            {
                cells = new PdfPCell(new Phrase("NO PICTURE", smallNormal));
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder( Rectangle.RIGHT | Rectangle.BOTTOM);
                cells.setColspan(2);
                cells.setPaddingTop(50);
                cells.setPaddingBottom(10);
                cells.setBorderWidth(borderwidth);
                cells.setBorderColor(BaseColor.BLACK);
                table.addCell(cells);
            }

            document.add(table);*/



        }
        catch (IOException | BadElementException e)
        {
            Log.e(TAG, e.toString());
        }
    }
}


