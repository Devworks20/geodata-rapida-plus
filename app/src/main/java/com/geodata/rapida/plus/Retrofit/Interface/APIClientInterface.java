package com.geodata.rapida.plus.Retrofit.Interface;

import com.geodata.rapida.plus.Retrofit.Model.AllBarangaysOfEDIModel;
import com.geodata.rapida.plus.Retrofit.Model.AllCitiesOfEDIModel;
import com.geodata.rapida.plus.Retrofit.Model.AllDistrictOfficesOfEDIModel;
import com.geodata.rapida.plus.Retrofit.Model.AllProvincesOfEDIModel;
import com.geodata.rapida.plus.Retrofit.Model.AllRegionsOfEDIModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingInfoTableModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingInventoryYearDataModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingTypeModel;
import com.geodata.rapida.plus.Retrofit.Model.EarthquakeRVSReportModel;
import com.geodata.rapida.plus.Retrofit.Model.FallingHazardsModel;
import com.geodata.rapida.plus.Retrofit.Model.LoginModel;
import com.geodata.rapida.plus.Retrofit.Model.MissionOrdersModel;
import com.geodata.rapida.plus.Retrofit.Model.NoOfPersonsModel;
import com.geodata.rapida.plus.Retrofit.Model.OccupanciesModel;
import com.geodata.rapida.plus.Retrofit.Model.ReportPDFClass;
import com.geodata.rapida.plus.Retrofit.Model.SoilTypesModel;
import com.geodata.rapida.plus.Retrofit.Model.TokenModel;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIClientInterface
{
    //@Body = Raw
    //@Field = x-www-form-urlencoded
    //@Path  = {url}
    //@HeaderMap = Header Authorization
    //@Query = /?url
    //@Url = Customize URL

    /*@GET("api/Login/{Username}/{Password}/{SystemType}")
    Call<LoginModel> LoginUserAccount(@HeaderMap Map<String,String> headers,
                                      @Path("Username") String Username,
                                      @Path("Password") String Password,
                                      @Path("SystemType") String SystemType);*/

    //Optimized
    @FormUrlEncoded
    @POST("token")
    Call<TokenModel>ValidateToken(@Field("grant_type") String grant_type,
                                  @Field("Username") String Username,
                                  @Field("Password") String Password);

    //Optimized
    @GET("api/login/{username}/{password}")
    Call<LoginModel> LoginUserAccount(@HeaderMap Map<String,String> headers,
                                      @Path("username") String Username,
                                      @Path("password") String Password);
    //Optimized
    @FormUrlEncoded
    @POST("api/request-forgot-password")
    Call<String> RequestForgotPassword(@Field("Email") String Email);



    /* GET - SEISMIC */
    @GET("api/seismic/mission-orders-by-screenerID/{screenerID}")
    Call<List<MissionOrdersModel>> GETAllSeismicMissionOrders(@Path("screenerID") Integer screenerID);

    @GET("api/seismic/inspection-reports/{screenerID}")
    Call<ReportPDFClass> GETAllSeismicInspectionReports(@Query("screenerID") Integer screenerID);


    //Optimized
    @GET("api/seismic/inspection-reports/{screenerID}/{missionOrderID}")
    Call<ReportPDFClass> GETAllSeismicInspectionReports(@Path("screenerID") Integer screenerID,
                                                        @Path("missionOrderID") Integer missionOrderID);

    //Optimized
    @GET("api/seismic/falling-hazards")
    Call<List<FallingHazardsModel>> GETAllFallingHazards();


    /* POST - SEISMIC */
    //Optimized
    @Multipart
    @POST("api/seismic/submit-inspection-report")
    Call<String> POSTSeismicInspectionReport(@Part MultipartBody.Part pdfFile,
                                             @Part("MissionOrderID") RequestBody FileType);

    //Optimized
    @FormUrlEncoded
    @POST("api/seismic/submit-final-score")
    Call<String> POSTSeismicFinalScore(@Field("MissionOrderID") Integer MissionOrderID,
                                       @Field("FinalScore") Double FinalScore);





    /* GET - EARTHQUAKE */
    @GET("api/earthquake/mission-orders- by-screenerID/{screenerID}")
    Call<List<MissionOrdersModel>> GETAllEarthquakeMissionOrders(@Path("screenerID") Integer screenerID);

    //Optimized
    @GET("api/earthquake/RESA-reports/{screenerID}/{missionOrderID}")
    Call<ReportPDFClass> GETAllEarthquakeRESAReports(@Path("screenerID") Integer screenerID,
                                                     @Path("missionOrderID") Integer missionOrderID);

    //Optimized
    @GET("api/earthquake/DESA-reports/{screenerID}/{missionOrderID}")
    Call<ReportPDFClass> GETAllEarthquakeDESAReports(@Path("screenerID") Integer screenerID,
                                                     @Path("missionOrderID") Integer missionOrderID);


    //Optimized
    @GET("api/earthquake/RVS-inspection-reports/{screenerID}")
    Call<List<EarthquakeRVSReportModel>> GetAllEarthquakeRVSInspectionReports(@Path("screenerID") Integer screenerID);


    /* POST - EARTHQUAKE */
    //Optimized
    @Multipart
    @POST("api/earthquake/submit-RVS-report")
    Call<Integer> POSTEarthquakeInspectionReport(@Part MultipartBody.Part pdfFile,
                                                 @Part("BuildingGUID") RequestBody BuildingGUID,
                                                 @Part("ScreenerID") RequestBody ScreenerID);
    //Optimized
    @FormUrlEncoded
    @POST("api/earthquake/submit-RVS-final-score")
    Call<String> POSTEarthquakeFinalScore(@Field("FinalScore") String FinalScore,
                                          @Field("SteelFinalScore") String SteelFinalScore,
                                          @Field("ConcreteFinalScore") String ConcreteFinalScore,
                                          @Field("EarthquakeRVSReportID") Integer EarthquakeRVSReportID);

    //Optimized
    @Multipart
    @POST("api/earthquake/submit-RESA-or-DESA-report")
    Call<String> PostRESAorDESAReport(@Part MultipartBody.Part pdfFile,
                                      @Part("MissionOrderID") RequestBody MissionOrderID);








    /* GET - FILE MAINTENANCE */
    //Optimized
    @GET("occupancies")
    Call<List<OccupanciesModel>> GetAllOccupancies();

    //Optimized
    @GET("building-no-of-persons")
    Call<List<NoOfPersonsModel>> GetAllNoOfPersons();

    //Optimized
    @GET("soil-types")
    Call<List<SoilTypesModel>> GetAllSoilTypes();

    //Optimized
    @GET("building-types")
    Call<List<BuildingTypeModel>> GETBuildingType();





    //EARTHQUAKE DAMAGE REPORTING
    //Optimized
    @GET("regions")
    Call<List<AllRegionsOfEDIModel>> GETAllRegions();

    //Optimized
    @GET("provinces/{regCode}")
    Call<List<AllProvincesOfEDIModel>> GETAllProvinces(@Path("regCode") Integer regCode);

    //Optimized
    @GET("cities/{provCode}")
    Call<List<AllCitiesOfEDIModel>> GETAllCities(@Path("provCode") Integer provCode);

    //Optimized
    @GET("barangays/{cityCode}")
    Call<List<AllBarangaysOfEDIModel>> GETAllBarangays(@Path("cityCode") Integer cityCode);

    //Optimized
    @GET("districts/{regCode}")
    Call<List<AllDistrictOfficesOfEDIModel>> GETAllDistricts(@Path("regCode") Integer regCode);

    //Optimized
    @GET("buildings/{InventoryYear}/{AccountCode}/{District}/{Region}/{City}/{Province}/{Barangay}/{StructureType}/{BuildingAge}/{OccupancyType}/{ModuleName}/{SearchKeyword}/{tableActionID}/{page}")
    Call<BuildingInfoTableModel> GETAllBuildingsInventoryYear(@Path("InventoryYear") Integer InventoryYear,
                                                              @Path("AccountCode") Integer AccountCode,
                                                              @Path("District") Integer District,
                                                              @Path("Region") Integer Region,
                                                              @Path("City") Integer City,
                                                              @Path("Province") Integer Province,
                                                              @Path("Barangay") Integer Barangay,
                                                              @Path("StructureType") Integer StructureType,
                                                              @Path("BuildingAge") Integer BuildingAge,
                                                              @Path("OccupancyType") Integer OccupancyType,
                                                              @Path("ModuleName") String ModuleName,
                                                              @Path("SearchKeyword") String SearchKeyword,
                                                              @Path("tableActionID") String tableActionID,
                                                              @Path("page") Integer page);

    //Optimized
    @GET("building/{assetId}/{InventoryYear}")
    Call<BuildingInventoryYearDataModel> GETBuildingInventoryYear(@Path("assetId") String assetId, @Path("InventoryYear") Integer InventoryYear);

}
