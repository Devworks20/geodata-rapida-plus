package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel
{
    @SerializedName("EmployeeID")
    @Expose
    private Integer EmployeeID;

    @SerializedName("UserAccountID")
    @Expose
    private String UserAccountID;

    @SerializedName("RoleName")
    @Expose
    private String RoleName;

    @SerializedName("CompleteName")
    @Expose
    private String CompleteName;

    @SerializedName("AppID")
    @Expose
    private String AppID;

    @SerializedName("Position")
    @Expose
    private String Position;

    public Integer getEmployeeID()
    {
        return EmployeeID;
    }

    public void setEmployeeID(Integer employeeID)
    {
        EmployeeID = employeeID;
    }

    public String getUserAccountID()
    {
        return UserAccountID;
    }

    public void setUserAccountID(String userAccountID)
    {
        UserAccountID = userAccountID;
    }

    public String getRoleName()
    {
        return RoleName;
    }

    public void setRoleName(String roleName)
    {
        RoleName = roleName;
    }

    public String getCompleteName()
    {
        return CompleteName;
    }

    public void setCompleteName(String completeName)
    {
        CompleteName = completeName;
    }

    public String getAppID()
    {
        return AppID;
    }

    public void setAppID(String appID)
    {
        AppID = appID;
    }

    public String getPosition()
    {
        return Position;
    }

    public void setPosition(String position)
    {
        Position = position;
    }

}
