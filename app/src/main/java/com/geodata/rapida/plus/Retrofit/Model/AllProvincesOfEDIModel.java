package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllProvincesOfEDIModel
{
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("psgcCode")
    @Expose
    private String psgcCode;

    @SerializedName("provDesc")
    @Expose
    private String provDesc;

    @SerializedName("regCode")
    @Expose
    private String regCode;

    @SerializedName("provCode")
    @Expose
    private Integer provCode;

    public AllProvincesOfEDIModel()
    {
        id       = "";
        psgcCode = "";
        provDesc = "";
        regCode  = "";
        provCode = 0;
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

    public String getProvDesc() {
        return provDesc;
    }

    public void setProvDesc(String provDesc) {
        this.provDesc = provDesc;
    }

    public String getRegCode() {
        return regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }

    public Integer getProvCode() {
        return provCode;
    }

    public void setProvCode(Integer provCode) {
        this.provCode = provCode;
    }
}
