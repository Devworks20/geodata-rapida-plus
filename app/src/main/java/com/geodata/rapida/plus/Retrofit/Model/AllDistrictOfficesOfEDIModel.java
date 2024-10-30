package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllDistrictOfficesOfEDIModel
{
    @SerializedName("DistrictOffice1")
    @Expose
    private String DistrictOffice1;

    @SerializedName("DistrictOfficeID")
    @Expose
    private String DistrictOfficeID;

    @SerializedName("regCode")
    @Expose
    private Integer regCode;

    public AllDistrictOfficesOfEDIModel()
    {
        DistrictOffice1   = "";
        DistrictOfficeID  = "";
        regCode           = 0;
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

    public Integer getRegCode() {
        return regCode;
    }

    public void setRegCode(Integer regCode) {
        this.regCode = regCode;
    }
}
