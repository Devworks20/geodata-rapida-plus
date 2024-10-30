package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class gisRegion
{
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("psgcCode")
    @Expose
    private String psgcCode;

    @SerializedName("regCode")
    @Expose
    private String regCode;

    @SerializedName("regDesc")
    @Expose
    private String regDesc;

    public gisRegion()
    {
        id       = "";
        psgcCode = "";
        regCode  = "";
        regDesc  = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPsgcCode() {
        return psgcCode;
    }

    public void setPsgcCode(String psgcCode) {
        this.psgcCode = psgcCode;
    }

    public String getRegCode() {
        return regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }

    public String getRegDesc() {
        return regDesc;
    }

    public void setRegDesc(String regDesc) {
        this.regDesc = regDesc;
    }
}
