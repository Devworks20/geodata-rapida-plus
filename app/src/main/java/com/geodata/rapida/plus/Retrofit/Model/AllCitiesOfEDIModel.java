package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllCitiesOfEDIModel
{
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("psgcCode")
    @Expose
    private String psgcCode;

    @SerializedName("citymunDesc")
    @Expose
    private String citymunDesc;

    @SerializedName("regCode")
    @Expose
    private String regCode;

    @SerializedName("provCode")
    @Expose
    private String provCode;

    @SerializedName("citymunCode")
    @Expose
    private Integer citymunCode;

    public AllCitiesOfEDIModel()
    {
        id          = "";
        psgcCode    = "";
        citymunDesc = "";
        regCode     = "";
        provCode    = "";
        citymunCode = 0;
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

    public String getCitymunDesc() {
        return citymunDesc;
    }

    public void setCitymunDesc(String citymunDesc) {
        this.citymunDesc = citymunDesc;
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

    public Integer getCitymunCode() {
        return citymunCode;
    }

    public void setCitymunCode(Integer citymunCode) {
        this.citymunCode = citymunCode;
    }
}
