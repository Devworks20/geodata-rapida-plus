package com.geodata.rapida.plus.SQLite.Class;

public class RVSSaveDraftDataClass
{
    private String ID;
    private String ScreenerID;
    private String MissionOrderID;
    private String Category;
    private String NoOfPersons;
    private String SoilType;
    private String Comments;
    private String DetailedEvaluation;
    private String BackgroundInformation;
    private String FindingsObservations;
    private String CommentsRecommendations;
    private String AdminName;
    private String AdminPosition;


    public RVSSaveDraftDataClass()
    {
        ID                      = "";
        ScreenerID              = "";
        MissionOrderID          = "";
        Category                = "";
        NoOfPersons             = "";
        SoilType                = "";
        Comments                = "";
        DetailedEvaluation      = "";
        BackgroundInformation   = "";
        FindingsObservations    = "";
        CommentsRecommendations = "";
        AdminName               = "";
        AdminPosition           = "";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getScreenerID() {
        return ScreenerID;
    }

    public void setScreenerID(String screenerID) {
        ScreenerID = screenerID;
    }

    public String getMissionOrderID() {
        return MissionOrderID;
    }

    public void setMissionOrderID(String missionOrderID) {
        MissionOrderID = missionOrderID;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getNoOfPersons() {
        return NoOfPersons;
    }

    public void setNoOfPersons(String noOfPersons) {
        NoOfPersons = noOfPersons;
    }

    public String getSoilType() {
        return SoilType;
    }

    public void setSoilType(String soilType) {
        SoilType = soilType;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getDetailedEvaluation() {
        return DetailedEvaluation;
    }

    public void setDetailedEvaluation(String detailedEvaluation) {
        DetailedEvaluation = detailedEvaluation;
    }

    public String getBackgroundInformation() {
        return BackgroundInformation;
    }

    public void setBackgroundInformation(String backgroundInformation) {
        BackgroundInformation = backgroundInformation;
    }

    public String getFindingsObservations() {
        return FindingsObservations;
    }

    public void setFindingsObservations(String findingsObservations) {
        FindingsObservations = findingsObservations;
    }

    public String getCommentsRecommendations() {
        return CommentsRecommendations;
    }

    public void setCommentsRecommendations(String commentsRecommendations) {
        CommentsRecommendations = commentsRecommendations;
    }

    public String getAdminName() {
        return AdminName;
    }

    public void setAdminName(String adminName) {
        AdminName = adminName;
    }

    public String getAdminPosition() {
        return AdminPosition;
    }

    public void setAdminPosition(String adminPosition) {
        AdminPosition = adminPosition;
    }
}
