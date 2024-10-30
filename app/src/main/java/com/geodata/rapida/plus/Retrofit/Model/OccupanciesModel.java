package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OccupanciesModel
{
    @SerializedName("UseOfCharacterOccupancyID")
    @Expose
    private Integer UseOfCharacterOccupancyID;

    @SerializedName("Description")
    @Expose
    private String Description;

    private String isActive;

    public OccupanciesModel()
    {
        UseOfCharacterOccupancyID = 0;
        Description               = "";
        isActive                  = "0";
    }

    public Integer getUseOfCharacterOccupancyID()
    {
        return UseOfCharacterOccupancyID;
    }

    public void setUseOfCharacterOccupancyID(Integer useOfCharacterOccupancyID)
    {
        UseOfCharacterOccupancyID = useOfCharacterOccupancyID;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

}
