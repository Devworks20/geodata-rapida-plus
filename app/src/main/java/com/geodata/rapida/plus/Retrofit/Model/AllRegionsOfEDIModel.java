package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllRegionsOfEDIModel
{
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("psgcCode")
    @Expose
    private String psgcCode;

    @SerializedName("regDesc")
    @Expose
    private String regDesc;

    @SerializedName("regCode")
    @Expose
    private Integer regCode;

    public AllRegionsOfEDIModel()
    {
        id       = "";
        psgcCode = "";
        regDesc  = "";
        regCode  = 0;
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

    public String getRegDesc() {
        return regDesc;
    }

    public void setRegDesc(String regDesc) {
        this.regDesc = regDesc;
    }

    public Integer getRegCode() {
        return regCode;
    }

    public void setRegCode(Integer regCode) {
        this.regCode = regCode;
    }
}
