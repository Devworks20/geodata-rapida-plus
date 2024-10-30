package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportPDFClass
{
    @SerializedName("FilePath")
    @Expose
    private String FilePath;

    public ReportPDFClass()
    {
        FilePath = "";
    }

    public String getFilePath()
    {
        return FilePath;
    }

    public void setFilePath(String filePath)
    {
        FilePath = filePath;
    }
}
