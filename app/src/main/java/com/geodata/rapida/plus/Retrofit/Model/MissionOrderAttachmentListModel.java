package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MissionOrderAttachmentListModel
{
    @SerializedName("AttachmentPath")
    @Expose
    private String AttachmentPath;

    @SerializedName("Type")
    @Expose
    private String Type;

    public MissionOrderAttachmentListModel()
    {
        AttachmentPath = "";
        Type           = "";
    }

    public String getAttachmentPath()
    {
        return AttachmentPath;
    }

    public void setAttachmentPath(String attachmentPath)
    {
        AttachmentPath = attachmentPath;
    }

    public String getType()
    {
        return Type;
    }

    public void setType(String type)
    {
        Type = type;
    }
}
