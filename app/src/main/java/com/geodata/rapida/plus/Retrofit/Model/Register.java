package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rdulitin on 09/10/2019.
 */

public class Register {

    @SerializedName("DATVolunteerId")
    @Expose
    private Integer dATVolId;

    @SerializedName("DATVolNo")
    @Expose
    private String dATVolNo;
    @SerializedName("MobileApp")
    @Expose
    private String mobileApp;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("MiddleName")
    @Expose
    private String middleName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("CompleteName")
    @Expose
    private String completeName;
    @SerializedName("BirthDate")
    @Expose
    private String birthDate;
    @SerializedName("Gender")
    @Expose
    private String gender;
    @SerializedName("MobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("Street")
    @Expose
    private String street;
    @SerializedName("Barangay")
    @Expose
    private String barangay;
    @SerializedName("MunicipalityCity")
    @Expose
    private String municipalityCity;
    @SerializedName("ZipCode")
    @Expose
    private String zipCode;
    @SerializedName("Province")
    @Expose
    private String province;
    @SerializedName("CompleteAddress")
    @Expose
    private String completeAddress;
    @SerializedName("IMEINo")
    @Expose
    private String iMEINo;
    @SerializedName("IsConfirmed")
    @Expose
    private Boolean isConfirmed;
    @SerializedName("IsRejected")
    @Expose
    private Boolean isRejected;
    @SerializedName("IsRegistered")
    @Expose
    private Boolean isRegistered;
    @SerializedName("PDFPath")
    @Expose
    private String pDFPath;

    @SerializedName("Remarks")
    @Expose
    private String remarks;


    public Integer getDATVolId() {
        return dATVolId;
    }

    public void setDATVolId(Integer dATVolId) {
        this.dATVolId = dATVolId;
    }

    public String getDATVolNo() {
        return dATVolNo;
    }

    public void setDATVolNo(String dATVolNo) {
        this.dATVolNo = dATVolNo;
    }

    public String getMobileApp() {
        return mobileApp;
    }

    public void setMobileApp(String mobileApp) {
        this.mobileApp = mobileApp;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getMunicipalityCity() {
        return municipalityCity;
    }

    public void setMunicipalityCity(String municipalityCity) {
        this.municipalityCity = municipalityCity;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCompleteAddress() {
        return completeAddress;
    }

    public void setCompleteAddress(String completeAddress) {
        this.completeAddress = completeAddress;
    }

    public String getIMEINo() {
        return iMEINo;
    }

    public void setIMEINo(String iMEINo) {
        this.iMEINo = iMEINo;
    }

    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public Boolean getIsRejected() {
        return isRejected;
    }

    public void setIsRejected(Boolean isRejected) {
        this.isRejected = isRejected;
    }

    public Boolean getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(Boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public String getPDFPath() {
        return pDFPath;
    }

    public void setPDFPath(String pDFPath) {
        this.pDFPath = pDFPath;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}
