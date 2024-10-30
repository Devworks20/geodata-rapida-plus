package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FinalScoreModel
{
    @SerializedName("FinalScores")
    @Expose
    private String FinalScores;

    public FinalScoreModel()
    {
        FinalScores = "";
    }

    public String getFinalScores() {
        return FinalScores;
    }

    public void setFinalScores(String finalScores) {
        FinalScores = finalScores;
    }
}
