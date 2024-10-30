package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PagerModel
{
    @SerializedName("CurrentPage")
    @Expose
    private Integer CurrentPage;

    @SerializedName("EndPage")
    @Expose
    private Integer EndPage;

    @SerializedName("Entries")
    @Expose
    private Integer Entries;

    @SerializedName("PageSize")
    @Expose
    private Integer PageSize;

    @SerializedName("StartPage")
    @Expose
    private Integer StartPage;

    @SerializedName("TotalItems")
    @Expose
    private Integer TotalItems;

    @SerializedName("TotalPages")
    @Expose
    private Integer TotalPages;

    public PagerModel()
    {
        CurrentPage = 0;
        EndPage     = 0;
        Entries     = 0;
        PageSize    = 0;
        StartPage   = 0;
        TotalItems  = 0;
        TotalPages  = 0;
    }

    public Integer getCurrentPage() {
        return CurrentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        CurrentPage = currentPage;
    }

    public Integer getEndPage() {
        return EndPage;
    }

    public void setEndPage(Integer endPage) {
        EndPage = endPage;
    }

    public Integer getEntries() {
        return Entries;
    }

    public void setEntries(Integer entries) {
        Entries = entries;
    }

    public Integer getPageSize() {
        return PageSize;
    }

    public void setPageSize(Integer pageSize) {
        PageSize = pageSize;
    }

    public Integer getStartPage() {
        return StartPage;
    }

    public void setStartPage(Integer startPage) {
        StartPage = startPage;
    }

    public Integer getTotalItems() {
        return TotalItems;
    }

    public void setTotalItems(Integer totalItems) {
        TotalItems = totalItems;
    }

    public Integer getTotalPages() {
        return TotalPages;
    }

    public void setTotalPages(Integer totalPages) {
        TotalPages = totalPages;
    }
}
