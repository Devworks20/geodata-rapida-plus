package com.geodata.rapida.plus.SQLite.Class;

public class InspectorSignatureClass
{
    private String ID;
    private String UserAccountID;
    private String MissionOrderID;
    private String SignatureID;
    private String SignatureName;
    private String SignaturePath;
    private String SignatureExtension;
    private String DtAdded;
    private String isActive;
    private String isSync;

    public InspectorSignatureClass()
    {
       ID                 = "";
       UserAccountID      = "";
       MissionOrderID     = "";
       SignatureID        = "";
       SignatureName      = "";
       SignaturePath      = "";
       SignatureExtension = "";
       DtAdded            = "";
       isActive           = "1";
       isSync             = "0";
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

    public String getMissionOrderID() {
        return MissionOrderID;
    }

    public void setMissionOrderID(String missionOrderID) {
        MissionOrderID = missionOrderID;
    }

    public String getSignatureID() {
        return SignatureID;
    }

    public void setSignatureID(String signatureID) {
        SignatureID = signatureID;
    }

    public String getSignatureName() {
        return SignatureName;
    }

    public void setSignatureName(String signatureName) {
        SignatureName = signatureName;
    }

    public String getSignaturePath() {
        return SignaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        SignaturePath = signaturePath;
    }

    public String getSignatureExtension() {
        return SignatureExtension;
    }

    public void setSignatureExtension(String signatureExtension) {
        SignatureExtension = signatureExtension;
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
