package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FEMAScoreModel
{
    @SerializedName("FemaFormID")
    @Expose
    private Integer FemaFormID;

    @SerializedName("BuildingType")
    @Expose
    private String BuildingType;

    @SerializedName("BasicScore")
    @Expose
    private Double BasicScore;

    @SerializedName("MidRise")
    @Expose
    private Double MidRise;

    @SerializedName("HighRise")
    @Expose
    private Double HighRise;

    @SerializedName("VertIrreg")
    @Expose
    private Double VertIrreg;

    @SerializedName("PlanIrreg")
    @Expose
    private Double PlanIrreg;

    @SerializedName("PreCode")
    @Expose
    private Double PreCode;

    @SerializedName("PostBenchMark")
    @Expose
    private Double PostBenchMark;

    @SerializedName("SoilTypeC")
    @Expose
    private Double SoilTypeC;

    @SerializedName("SoilTypeD")
    @Expose
    private Double SoilTypeD;

    @SerializedName("SoilTypeE")
    @Expose
    private Double SoilTypeE;

    @SerializedName("FinalScore")
    @Expose
    private Double FinalScore;

    public Integer getFemaFormID() {
        return FemaFormID;
    }

    public void setFemaFormID(Integer femaFormID) {
        FemaFormID = femaFormID;
    }

    public String getBuildingType() {
        return BuildingType;
    }

    public void setBuildingType(String buildingType) {
        BuildingType = buildingType;
    }

    public Double getBasicScore() {
        return BasicScore;
    }

    public void setBasicScore(Double basicScore) {
        BasicScore = basicScore;
    }

    public Double getMidRise() {
        return MidRise;
    }

    public void setMidRise(Double midRise) {
        MidRise = midRise;
    }

    public Double getHighRise() {
        return HighRise;
    }

    public void setHighRise(Double highRise) {
        HighRise = highRise;
    }

    public Double getVertIrreg() {
        return VertIrreg;
    }

    public void setVertIrreg(Double vertIrreg) {
        VertIrreg = vertIrreg;
    }

    public Double getPlanIrreg() {
        return PlanIrreg;
    }

    public void setPlanIrreg(Double planIrreg) {
        PlanIrreg = planIrreg;
    }

    public Double getPreCode() {
        return PreCode;
    }

    public void setPreCode(Double preCode) {
        PreCode = preCode;
    }

    public Double getPostBenchMark() {
        return PostBenchMark;
    }

    public void setPostBenchMark(Double postBenchMark) {
        PostBenchMark = postBenchMark;
    }

    public Double getSoilTypeC() {
        return SoilTypeC;
    }

    public void setSoilTypeC(Double soilTypeC) {
        SoilTypeC = soilTypeC;
    }

    public Double getSoilTypeD() {
        return SoilTypeD;
    }

    public void setSoilTypeD(Double soilTypeD) {
        SoilTypeD = soilTypeD;
    }

    public Double getSoilTypeE() {
        return SoilTypeE;
    }

    public void setSoilTypeE(Double soilTypeE) {
        SoilTypeE = soilTypeE;
    }

    public Double getFinalScore() {
        return FinalScore;
    }

    public void setFinalScore(Double finalScore) {
        FinalScore = finalScore;
    }
}
