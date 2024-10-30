package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountCode
{
    @SerializedName("AccountCode1")
    @Expose
    private String AccountCode1;

    @SerializedName("AccountCodeID")
    @Expose
    private String AccountCodeID;

    @SerializedName("AccountDesc")
    @Expose
    private String AccountDesc;

    @SerializedName("Assignment")
    @Expose
    private String Assignment;

    public AccountCode()
    {
        AccountCode1  = "";
        AccountCodeID = "";
        AccountDesc   = "";
        Assignment    = "";
    }

    public String getAccountCode1()
    {
        return AccountCode1;
    }

    public void setAccountCode1(String accountCode1)
    {
        AccountCode1 = accountCode1;
    }

    public String getAccountCodeID()
    {
        return AccountCodeID;
    }

    public void setAccountCodeID(String accountCodeID)
    {
        AccountCodeID = accountCodeID;
    }

    public String getAccountDesc()
    {
        return AccountDesc;
    }

    public void setAccountDesc(String accountDesc)
    {
        AccountDesc = accountDesc;
    }

    public String getAssignment()
    {
        return Assignment;
    }

    public void setAssignment(String assignment)
    {
        Assignment = assignment;
    }
}
