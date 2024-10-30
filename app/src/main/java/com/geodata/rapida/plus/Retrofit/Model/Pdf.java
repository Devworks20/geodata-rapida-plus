package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rdulitin on 20/01/2020.
 */

public class Pdf {

    @SerializedName("DATVolunteerId")
    @Expose
    private Integer dATVolunteerId;

    @SerializedName("DocumentByte")
    @Expose
    private String documentByte;

    @SerializedName("DocumentName")
    @Expose
    private String documentName;

    @SerializedName("Remarks")
    @Expose
    private String remarks;

    public Integer getDATVolunteerId() {
        return dATVolunteerId;
    }

    public void setDATVolunteerId(Integer dATVolunteerId) {
        this.dATVolunteerId = dATVolunteerId;
    }

    public String getDocumentByte() {
        return documentByte;
    }

    public void setDocumentByte(String documentByte) {
        this.documentByte = documentByte;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
