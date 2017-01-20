package com.passengerapp.main.network;

import com.passengerapp.main.network.model.data.AirlineData;
import com.passengerapp.main.network.model.request.DriverTokenRequest;
import com.passengerapp.main.network.model.request.GetFlightsInfoFromFVRequest;
import com.passengerapp.main.network.model.request.GetInvoiceDetailsRequest;
import com.passengerapp.main.network.model.request.GetReservationLocationRequest;
import com.passengerapp.main.network.model.request.GiveReviewToDriverRequest;
import com.passengerapp.main.network.model.request.PickUpRequest;
import com.passengerapp.main.network.model.request.RegisterPassengerRequest;
import com.passengerapp.main.network.model.request.ReservationIdRequest;
import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;
import com.passengerapp.main.network.model.request.SearchDriversRequest;
import com.passengerapp.main.network.model.request.SendCancelToDriverRequest;
import com.passengerapp.main.network.model.request.SendInvoiceToDriverRequest;
import com.passengerapp.main.network.model.request.SendPickupRequest;
import com.passengerapp.main.network.model.request.SetCompanyLinkRequest;
import com.passengerapp.main.network.model.request.StorePaymentIdRequest;
import com.passengerapp.main.network.model.request.UpdatePickupTimeRequest;
import com.passengerapp.main.network.model.response.GetDriverLocationData;
import com.passengerapp.main.network.model.response.GetFlightDetailsData;
import com.passengerapp.main.network.model.response.GetInvoiceDetailsData;
import com.passengerapp.main.network.model.response.GetReservationLocationData;
import com.passengerapp.main.network.model.response.GoogleGetLocationByNameApiResponse;
import com.passengerapp.main.network.model.response.GooglePlacesResponse;
import com.passengerapp.main.network.model.response.GoogleRoutesResponse;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.PickUpData;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.main.network.model.response.RegisterPassengerData;
import com.passengerapp.main.network.model.response.SaveFlightDetailData;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.network.model.response.SearchDriversLaterData;
import com.passengerapp.main.network.model.response.SendPickupData;
import com.passengerapp.main.network.model.response.SetCompanyLinkData;
import com.passengerapp.main.network.model.response.StorePaymentIdData;
import com.passengerapp.main.network.model.response.VerfiyCouponData;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by adventis on 10/15/15.
 */
public interface NetworkApi {

    @FormUrlEncoded
    @POST("api/VerifyCoupon")
    Observable<JsonServerResponse<VerfiyCouponData>> sendVeridyCoupon(@Field("MerchantId") String merchantId, @Field("CouponCode") String couponCode);

    @POST("api/SendPickup")
    Observable<JsonServerResponse<SendPickupData>> sendPickup(@Body SendPickupRequest request);

    @POST("api/SaveFlightDetail")
    Observable<JsonServerResponse<SaveFlightDetailData>> saveFlightDetail(@Body SaveFlightDetailRequest request);

    @POST("api/GetFlightsInfoFromFV")
    Observable<JsonServerResponse<List<SaveFlightDetailRequest>>> getFlightsInfoFromFV(@Body GetFlightsInfoFromFVRequest request);

    @POST("api/SearchDrivers")
    Observable<JsonServerResponse<List<SearchDriversData>>> searchDrivers(@Body SearchDriversRequest request);

    @POST("api/SearchDrivers")
    Observable<JsonServerResponse<List<SearchDriversLaterData>>> searchDriversLater(@Body SearchDriversRequest request);

    @POST("api/SendCancelToDriver")
    Observable<JsonServerResponse<String>> sendCancelToDriver(@Body SendCancelToDriverRequest request);

    @POST("api/UpdatePickupTime")
    Observable<JsonServerResponse<String>> updatePickupTime(@Body UpdatePickupTimeRequest request);

    @FormUrlEncoded
    @POST("api/GetPickupsForPassenger")
    Observable<JsonServerResponse<List<PickUpReservationData>>> getPickupsForPassenger(@Field("PhoneToken") String phoneToken);

    @POST("api/GetPickup")
    Observable<JsonServerResponse<List<PickUpData>>> getPickup(@Body PickUpRequest request);

    @FormUrlEncoded
    @POST("api/GetAirlines")
    Observable<JsonServerResponse<List<AirlineData>>> getAirlines(@Field("search") String request);

    @FormUrlEncoded
    @POST("api/GetFlightViewAirports")
    Observable<JsonServerResponse<List<AirlineData>>> getAirports(@Field("search") String request);

    @POST("api/SendInvoiceToDriver")
    Observable<JsonServerResponse<String>> sendInvoiceToDriver(@Body SendInvoiceToDriverRequest request);

    @POST("api/RegisterPassenger")
    Observable<JsonServerResponse<RegisterPassengerData>> registerPassenger(@Body RegisterPassengerRequest request);

    @POST("api/StorePaymentID")
    Observable<JsonServerResponse<StorePaymentIdData>> storePaymentID(@Body StorePaymentIdRequest request);

    @POST("api/GetInvoiceDetails")
    Observable<JsonServerResponse<GetInvoiceDetailsData>> getInvoiceDetails(@Body GetInvoiceDetailsRequest request);

    @POST("api/GiveReviewToDriver")
    Observable<JsonServerResponse<String>> giveReviewToDriver(@Body GiveReviewToDriverRequest request);

    @POST("api/GetReservationsLocation")
    Observable<JsonServerResponse<List<GetReservationLocationData>>> getReservationsLocation(@Body GetReservationLocationRequest request);

    @POST("api/SetCompanyLink")
    Observable<JsonServerResponse<SetCompanyLinkData>> setCompanyLink(@Body SetCompanyLinkRequest request);

    @POST("api/GetDriverLocation")
    Observable<JsonServerResponse<GetDriverLocationData>> getDriverLocation(@Body DriverTokenRequest request);


    /*@POST("api/GetDriverLocation")
    Observable<JsonServerResponse<GetDriverLocationData>> getDriverLocation(@Body DriverTokenRequest request);*/

    @POST("api/GetFlightDetails")
    Observable<JsonServerResponse<GetFlightDetailsData>> getFlightDetails(@Body ReservationIdRequest request);

    @GET("/maps/api/place/autocomplete/json?sensor=false")
    GooglePlacesResponse getNamesOfPlace(@Query(value = "input", encoded=true) String input, @Query(value="key") String key);

    @GET("/maps/api/place/textsearch/json?sensor=true")
    Observable<GoogleGetLocationByNameApiResponse> getLocationByName(@Query(value = "query", encoded=true) String query, @Query(value="key") String key);

    @GET("/maps/api/directions/json?sensor=false&mode=driving&alternatives=true")
    Observable<GoogleRoutesResponse> getRoutes(@Query(value="origin") String origin, @Query(value="destination") String dstLat);

}
