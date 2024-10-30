package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class gisProvince
{
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("provCode")
    @Expose
    private String provCode;

    @SerializedName("provDesc")
    @Expose
    private String provDesc;

    @SerializedName("psgcCode")
    @Expose
    private String psgcCode;

    @SerializedName("regCode")
    @Expose
    private String regCode;


    public gisProvince()
    {
        this.id       = "";
        this.provCode = "";
        this.provDesc = "";
        this.psgcCode = "";
        this.regCode  = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
    }

    public String getProvDesc() {
        return provDesc;
    }

    public void setProvDesc(String provDesc) {
        this.provDesc = provDesc;
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
}
