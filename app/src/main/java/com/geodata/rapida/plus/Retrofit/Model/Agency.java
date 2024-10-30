package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Agency
{
    @SerializedName("AgencyAcronym")
    @Expose
    private String AgencyAcronym;

    @SerializedName("AgencyID")
    @Expose
    private String AgencyID;

    @SerializedName("AgencyName")
    @Expose
    private String AgencyName;


    public Agency()
    {
        AgencyAcronym = "";
        AgencyID      =  "";
        AgencyName    =  "";
    }

    public String getAgencyAcronym() {
        return AgencyAcronym;
    }

    public void setAgencyAcronym(String agencyAcronym) {
        AgencyAcronym = agencyAcronym;
    }

    public String getAgencyID() {
        return AgencyID;
    }

    public void setAgencyID(String agencyID) {
        AgencyID = agencyID;
    }

    public String getAgencyName() {
        return AgencyName;
    }

    public void setAgencyName(String agencyName) {
        AgencyName = agencyName;
    }
}
