package com.geodata.rapida.plus.SQLite.Class;

public class ImagesClass
{
    private String ID;
    private String UserAccountID;
    private String Category;
    private String ImageID;
    private String ImageType;
    private String ImageName;
    private String ImagePath;
    private String ImageExtension;
    private String DtAdded;
    private String Description;
    private String isActive;
    private String isSync;

    public ImagesClass()
    {
       ID             = "";
       UserAccountID  = "";
       Category       = "";
       ImageID        = "";
       ImageType      = "";
       ImageName      = "";
       ImagePath      = "";
       ImageExtension = "";
       DtAdded        = "";
       Description    = "";
       isActive       = "1";
       isSync         = "0";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserAccountID()
    {
        return UserAccountID;
    }

    public void setUserAccountID(String userAccountID)
    {
        UserAccountID = userAccountID;
    }

    public String getCategory()
    {
        return Category;
    }

    public void setCategory(String category)
    {
        Category = category;
    }

    public String getImageID() {
        return ImageID;
    }

    public void setImageID(String imageID) {
        ImageID = imageID;
    }

    public String getImageType() {
        return ImageType;
    }

    public void setImageType(String imageType) {
        ImageType = imageType;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getImageExtension() {
        return ImageExtension;
    }

    public void setImageExtension(String imageExtension) {
        ImageExtension = imageExtension;
    }

    public String getDtAdded() {
        return DtAdded;
    }

    public void setDtAdded(String dtAdded) {
        DtAdded = dtAdded;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getIsSync() {
        return isSync;
    }

    public void setIsSync(String isSync) {
        this.isSync = isSync;
    }
}
