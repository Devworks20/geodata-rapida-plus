package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class gisCity
{
    @SerializedName("citymunCode")
    @Expose
    private String citymunCode;

    @SerializedName("citymunDesc")
    @Expose
    private String citymunDesc;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("provCode")
    @Expose
    private String provCode;

    @SerializedName("psgcCode")
    @Expose
    private String psgcCode;

    @SerializedName("regCode")
    @Expose
    private String regCode;

    public gisCity()
    {
        this.citymunCode = "";
        this.citymunDesc = "";
        this.id          = "";
        this.provCode    = "";
        this.psgcCode    = "";
        this.regCode    = "";
    }

    public String getCitymunCode() {
        return citymunCode;
    }

    public void setCitymunCode(String citymunCode) {
        this.citymunCode = citymunCode;
    }

    public String getCitymunDesc() {
        return citymunDesc;
    }

    public void setCitymunDesc(String citymunDesc) {
        this.citymunDesc = citymunDesc;
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
