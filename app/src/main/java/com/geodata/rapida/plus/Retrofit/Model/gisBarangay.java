package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class gisBarangay
{
    @SerializedName("brgyCode")
    @Expose
    private String brgyCode;

    @SerializedName("brgyDesc")
    @Expose
    private String brgyDesc;

    @SerializedName("citymunCode")
    @Expose
    private String citymunCode;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("provCode")
    @Expose
    private String provCode;

    @SerializedName("regCode")
    @Expose
    private String regCode;

    public gisBarangay()
    {
        brgyCode    = "";
        brgyDesc    = "";
        citymunCode = "";
        id          = "";
        provCode    = "";
        regCode     = "";
    }

    public String getBrgyCode() {
        return brgyCode;
    }

    public void setBrgyCode(String brgyCode) {
        this.brgyCode = brgyCode;
    }

    public String getBrgyDesc() {
        return brgyDesc;
    }

    public void setBrgyDesc(String brgyDesc) {
        this.brgyDesc = brgyDesc;
    }

    public String getCitymunCode() {
        return citymunCode;
    }

    public void setCitymunCode(String citymunCode) {
        this.citymunCode = citymunCode;
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

    public String getRegCode() {
        return regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }
}
