package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingNoOfPersons
{
    @SerializedName("NoOfPersons")
    @Expose
    private String NoOfPersons;

    @SerializedName("NoOfPersonsID")
    @Expose
    private String NoOfPersonsID;

    public BuildingNoOfPersons()
    {
        NoOfPersons   = "";
        NoOfPersonsID = "";
    }

    public String getNoOfPersons() {
        return NoOfPersons;
    }

    public void setNoOfPersons(String noOfPersons) {
        NoOfPersons = noOfPersons;
    }

    public String getNoOfPersonsID() {
        return NoOfPersonsID;
    }

    public void setNoOfPersonsID(String noOfPersonsID) {
        NoOfPersonsID = noOfPersonsID;
    }
}
