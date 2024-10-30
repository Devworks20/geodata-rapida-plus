package com.geodata.rapida.plus.SQLite.Class;

public class SketchImagesClass
{
    private String ID;
    private String UserAccountID;
    private String Category;
    private String SketchID;
    private String SketchName;
    private String SketchPath;
    private String SketchExtension;
    private String DtAdded;
    private String isActive;
    private String isSync;

    public SketchImagesClass()
    {
       ID              = "";
       UserAccountID   = "";
       Category        = "";
       SketchID        = "";
       SketchName      = "";
       SketchPath      = "";
       SketchExtension = "";
       DtAdded         = "";
       isActive        = "1";
       isSync          = "0";
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    public String getUserAccountID() {
        return UserAccountID;
    }

    public void setUserAccountID(String userAccountID) {
        UserAccountID = userAccountID;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getSketchID()
    {
        return SketchID;
    }

    public void setSketchID(String sketchID)
    {
        SketchID = sketchID;
    }

    public String getSketchName()
    {
        return SketchName;
    }

    public void setSketchName(String sketchName)
    {
        SketchName = sketchName;
    }

    public String getSketchPath()
    {
        return SketchPath;
    }

    public void setSketchPath(String sketchPath)
    {
        SketchPath = sketchPath;
    }

    public String getSketchExtension()
    {
        return SketchExtension;
    }

    public void setSketchExtension(String sketchExtension)
    {
        SketchExtension = sketchExtension;
    }

    public String getDtAdded()
    {
        return DtAdded;
    }

    public void setDtAdded(String dtAdded)
    {
        DtAdded = dtAdded;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public String getIsSync()
    {
        return isSync;
    }

    public void setIsSync(String isSync)
    {
        this.isSync = isSync;
    }
}
