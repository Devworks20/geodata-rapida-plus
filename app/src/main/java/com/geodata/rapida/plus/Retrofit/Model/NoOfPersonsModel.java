package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoOfPersonsModel
{
    @SerializedName("NoOfPersonsID")
    @Expose
    private Integer NoOfPersonsID;

    @SerializedName("NoOfPersons")
    @Expose
    private String NoOfPersons;

    public NoOfPersonsModel()
    {
        NoOfPersonsID = 0;
        NoOfPersons   = "";
    }

    public Integer getNoOfPersonsID()
    {
        return NoOfPersonsID;
    }

    public void setNoOfPersonsID(Integer noOfPersonsID)
    {
        NoOfPersonsID = noOfPersonsID;
    }

    public String getNoOfPersons()
    {
        return NoOfPersons;
    }

    public void setNoOfPersons(String noOfPersons)
    {
        NoOfPersons = noOfPersons;
    }
}
