package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllBarangaysOfEDIModel
{
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("brgyCode")
    @Expose
    private Integer brgyCode;

    @SerializedName("brgyDesc")
    @Expose
    private String brgyDesc;

    @SerializedName("regCode")
    @Expose
    private String regCode;

    @SerializedName("provCode")
    @Expose
    private String provCode;

    @SerializedName("citymunCode")
    @Expose
    private String citymunCode;

    public AllBarangaysOfEDIModel()
    {
        id          = "";
        brgyCode    = 0;
        brgyDesc    = "";
        regCode     = "";
        provCode    = "";
        citymunCode = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getBrgyCode() {
        return brgyCode;
    }

    public void setBrgyCode(Integer brgyCode) {
        this.brgyCode = brgyCode;
    }

    public String getBrgyDesc() {
        return brgyDesc;
    }

    public void setBrgyDesc(String brgyDesc) {
        this.brgyDesc = brgyDesc;
    }

    public String getRegCode() {
        return regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
    }

    public String getCitymunCode() {
        return citymunCode;
    }

    public void setCitymunCode(String citymunCode) {
        this.citymunCode = citymunCode;
    }
}
