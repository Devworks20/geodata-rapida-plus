package com.geodata.rapida.plus.SQLite.Class;

public class UserAccountClass
{
    private String ID;
    private Integer EmployeeID;
    private String UserAccountID;
    private String Username;
    private String Password;
    private String RoleName;
    private String CompleteName;
    private String AppID;
    private String Position;
    private String DtAdded;
    private String isActive;

    public UserAccountClass()
    {
        ID            = "";
        EmployeeID    =  0;
        UserAccountID = "";
        Username      = "";
        Password      = "";
        RoleName      = "";
        CompleteName  = "";
        AppID         = "";
        Position      = "";
        DtAdded       = "";
        isActive      = "0";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Integer getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(Integer employeeID) {
        EmployeeID = employeeID;
    }

    public String getUserAccountID() {
        return UserAccountID;
    }

    public void setUserAccountID(String userAccountID) {
        UserAccountID = userAccountID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getRoleName() {
        return RoleName;
    }

    public void setRoleName(String roleName) {
        RoleName = roleName;
    }

    public String getCompleteName() {
        return CompleteName;
    }

    public void setCompleteName(String completeName) {
        CompleteName = completeName;
    }

    public String getAppID() {
        return AppID;
    }

    public void setAppID(String appID) {
        AppID = appID;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getDtAdded() {
        return DtAdded;
    }

    public void setDtAdded(String dtAdded) {
        DtAdded = dtAdded;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
