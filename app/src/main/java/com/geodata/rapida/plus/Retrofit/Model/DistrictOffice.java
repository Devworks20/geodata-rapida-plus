package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DistrictOffice
{
    @SerializedName("Code")
    @Expose
    private String Code;

    @SerializedName("DistrictOffice1")
    @Expose
    private String DistrictOffice1;

    @SerializedName("DistrictOfficeID")
    @Expose
    private String DistrictOfficeID;

    @SerializedName("regCode")
    @Expose
    private String regCode;

    public DistrictOffice()
    {
        Code             = "";
        DistrictOffice1  = "";
        DistrictOfficeID = "";
        regCode          = "";
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getDistrictOffice1() {
        return DistrictOffice1;
    }

    public void setDistrictOffice1(String districtOffice1) {
        DistrictOffice1 = districtOffice1;
    }

    public String getDistrictOfficeID() {
        return DistrictOfficeID;
    }

    public void setDistrictOfficeID(String districtOfficeID) {
        DistrictOfficeID = districtOfficeID;
    }

    public String getRegCode() {
        return regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }
}
