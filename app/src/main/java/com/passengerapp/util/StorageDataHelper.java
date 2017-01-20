package com.passengerapp.util;

import android.content.Context;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.passengerapp.main.network.model.data.AirportData;
import com.passengerapp.main.network.model.request.GiveReviewToDriverRequest;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.data.SearchDriversTempData;
import com.passengerapp.main.network.model.response.SetCompanyLinkData;
import com.passengerapp.main.model.http.data.PendingPickupData;
import com.passengerapp.main.network.model.data.DistanceData;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.network.model.response.SearchDriversLaterData;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adventis on 10/8/15.
 */
public class StorageDataHelper {
    private static StorageDataHelper instance;

    private Context mCtx;

    private static final String SEARCH_DRIVERS_CURRENT_DATA = "SEARCH_DRIVERS_CURRENT_DATA";
    private SearchDriversTempData searchDriversCurrentDataForRequest;
    public SearchDriversTempData getSearchDriversCurrentDataForRequest() {
        if(searchDriversCurrentDataForRequest == null) {
            Type typeOfObject = new TypeToken<SearchDriversTempData>(){}.getType();
            this.searchDriversCurrentDataForRequest = (SearchDriversTempData)readFromStorage(SEARCH_DRIVERS_TEMP_DATA, typeOfObject);
            if(this.searchDriversCurrentDataForRequest == null)
                this.searchDriversCurrentDataForRequest = new SearchDriversTempData();
        }
        return searchDriversCurrentDataForRequest;
    }

    public void setSearchDriversCurrentDataForRequest(SearchDriversTempData searchDriversCurrentDataForRequest) {
        this.searchDriversCurrentDataForRequest = searchDriversCurrentDataForRequest;
        writeToStorageObject(searchDriversCurrentDataForRequest, SEARCH_DRIVERS_TEMP_DATA);
    }

    private static final String SEARCH_DRIVERS_TEMP_DATA = "SEARCH_DRIVERS_TEMP_DATA";
    private SearchDriversTempData searchDriversTempDataForRequest;
    public SearchDriversTempData getSearchDriversTempDataForRequest() {
        if(searchDriversTempDataForRequest == null) {
            Type typeOfObject = new TypeToken<SearchDriversTempData>(){}.getType();
            this.searchDriversTempDataForRequest = (SearchDriversTempData)readFromStorage(SEARCH_DRIVERS_TEMP_DATA, typeOfObject);
            if(this.searchDriversTempDataForRequest == null)
                this.searchDriversTempDataForRequest = new SearchDriversTempData();
        }
        return searchDriversTempDataForRequest;
    }

    public void setSearchDriversTempDataForRequest(SearchDriversTempData searchDriversTempDataForRequest) {
        this.searchDriversTempDataForRequest = searchDriversTempDataForRequest;
        writeToStorageObject(searchDriversTempDataForRequest, SEARCH_DRIVERS_TEMP_DATA);
    }

    //Search radius settings
    private static final String SEARCH_RADIUS_DATA = "SEARCH_RADIUS_DATA";
    private DistanceData searchRadius;
    public DistanceData getSearchRadius() {
        if(searchRadius == null) {
            Type typeOfObject = new TypeToken<DistanceData>(){}.getType();
            this.searchRadius = (DistanceData)readFromStorage(SEARCH_RADIUS_DATA, typeOfObject);
            if(searchRadius == null) {
                searchRadius = new DistanceData();
                searchRadius.Unit = "mile";
                searchRadius.Value = 5.0f;
            }
        }
        return searchRadius;
    }

    public void setSearchRadius(DistanceData searchRadius) {
        this.searchRadius = searchRadius;
        writeToStorageObject(searchRadius, SEARCH_RADIUS_DATA);
    }



    // list of drivers quotes for now
    private static final String DRIVERS_QUOTES_LATER = "DRIVERS_QUOTES_LATER";
    private JsonServerResponse<List<SearchDriversLaterData>> driversDatasLater;
    public List<SearchDriversLaterData> getDriversDatasLater() {
        if(driversDatasLater == null) {
            Type typeOfObject = new TypeToken<JsonServerResponse<List<SearchDriversLaterData>>>(){}.getType();
            this.driversDatasLater = (JsonServerResponse<List<SearchDriversLaterData>>)readFromStorage(DRIVERS_QUOTES_LATER, typeOfObject);
        }

        if(driversDatasLater == null)
            return null;

        return driversDatasLater.Content;
    }

    public boolean getIsAirportFromDriversDatasLater() {
        if(driversDatasLater == null) {
            Type typeOfObject = new TypeToken<JsonServerResponse<List<SearchDriversLaterData>>>(){}.getType();
            this.driversDatasLater = (JsonServerResponse<List<SearchDriversLaterData>>)readFromStorage(DRIVERS_QUOTES_LATER, typeOfObject);
        }
        return driversDatasLater.IsAirport;
    }

    public void setDriversDatasLater(JsonServerResponse<List<SearchDriversLaterData>> driversDatasLater) {
        this.driversDatasLater = driversDatasLater;
        writeToStorageObject(driversDatasLater, DRIVERS_QUOTES_LATER);
    }



    // list of drivers quotes for now
    private static final String DRIVERS_QUOTES_NOW = "DRIVERS_QUOTES_NOW";
    private JsonServerResponse<List<SearchDriversData>> driversDatasNow;
    public  List<SearchDriversData> getDriversDatasNow() {
        if(driversDatasNow == null) {
            Type typeOfObject = new TypeToken< JsonServerResponse<List<SearchDriversData>>>(){}.getType();
            this.driversDatasNow = ( JsonServerResponse<List<SearchDriversData>>)readFromStorage(DRIVERS_QUOTES_NOW, typeOfObject);
        }
        if(driversDatasNow == null)
            return null;

        return driversDatasNow.Content;
    }

    public boolean  getIsAirportFromGetDriversDatasNow() {
        if(driversDatasNow == null) {
            Type typeOfObject = new TypeToken< JsonServerResponse<List<SearchDriversData>>>(){}.getType();
            this.driversDatasNow = ( JsonServerResponse<List<SearchDriversData>>)readFromStorage(DRIVERS_QUOTES_NOW, typeOfObject);
        }
        return driversDatasNow.IsAirport;
    }

    public void setDriversDatasNow( JsonServerResponse<List<SearchDriversData>> driversDatasNow) {
        this.driversDatasNow = driversDatasNow;
        writeToStorageObject(driversDatasNow, DRIVERS_QUOTES_NOW);
    }

    // location data
    private static final String LATEST_LOCATION = "LATEST_LOCATION";
    private Location latestLocation;
    public Location getLatestLocation() {
        if(latestLocation == null) {
            Type typeOfObject = new TypeToken<Location>() {}.getType();
            this.latestLocation = (Location)readFromStorage(LATEST_LOCATION, typeOfObject);
        }
        return latestLocation;
    }

    public void setLatestLocation(Location latestLocation) {
        this.latestLocation = latestLocation;
        writeToStorageObject(latestLocation, LATEST_LOCATION);
    }


    // pending status data to update waiting dialog
    private static final String PENDING_PICKUP_STATUS = "PENDING_PICKUP_STATUS";
    private PendingPickupData pendingPickupStatus;
    public PendingPickupData getPendingPickupStatus() {
        if(pendingPickupStatus == null) {
            Type typeOfObject = new TypeToken<PendingPickupData>() {}.getType();
            this.pendingPickupStatus = (PendingPickupData)readFromStorage(PENDING_PICKUP_STATUS, typeOfObject);
        }
        return pendingPickupStatus;
    }

    public void setPendingPickupStatus(PendingPickupData pendingPickupStatus) {
        this.pendingPickupStatus = pendingPickupStatus;
        writeToStorageObject(pendingPickupStatus, PENDING_PICKUP_STATUS);
    }


    // list of reviews which wrote user
    private static final String REVIEW_TO_DRIVER_LIST = "REVIEW_TO_DRIVER_LIST";
    private List<GiveReviewToDriverRequest> reviewToDriverList;
    public List<GiveReviewToDriverRequest> getReviewToDriverList() {
        if(reviewToDriverList == null) {
            Type typeOfObject = new TypeToken<List<PickUpReservationData>>() {}.getType();
            this.reviewToDriverList = (List<GiveReviewToDriverRequest>)readFromStorage(REVIEW_TO_DRIVER_LIST, typeOfObject);
        }
        return reviewToDriverList;
    }
    public void addReviewToDeriver(GiveReviewToDriverRequest review) {
        this.reviewToDriverList = getReviewToDriverList();
        if(this.reviewToDriverList == null) {
            this.reviewToDriverList = new ArrayList<GiveReviewToDriverRequest>();
        }

        this.reviewToDriverList.add(review);
        writeToStorageObject(reviewToDriverList, REVIEW_TO_DRIVER_LIST);
    }
//    public void setReviewToDriverList(List<GiveReviewToDriverRequest> reviewToDriverList) {
//        this.reviewToDriverList = reviewToDriverList;
//        writeToStorageObject(reviewToDriverList, REVIEW_TO_DRIVER_LIST);
//    }

    // list of pickups for passanger which get from "GetPickupsForPassenger API"
    private static final String PICKUPS_FOR_PASSANGER_FLAG = "PICKUPS_FOR_PASSANGER_FLAG";
    private List<PickUpReservationData> driverPickUpsForPassanger;
    public List<PickUpReservationData> getDriverPickUpsForPassanger() {
        if(driverPickUpsForPassanger == null) {
            Type typeOfObject = new TypeToken<List<PickUpReservationData>>() {}.getType();
            this.driverPickUpsForPassanger = (List<PickUpReservationData>)readFromStorage(PICKUPS_FOR_PASSANGER_FLAG, typeOfObject);
        }
        return driverPickUpsForPassanger;
    }
    public void setDriverPickUpsForPassanger(List<PickUpReservationData> driverPickUpsForPassanger) {
        this.driverPickUpsForPassanger = driverPickUpsForPassanger;
        writeToStorageObject(driverPickUpsForPassanger, PICKUPS_FOR_PASSANGER_FLAG);
    }

    // vipCode which entered user or empty string as default
    private static final String VIP_CODE_FLAG = "VIP_CODE_FLAG";
    private String vipCode;
    public String getVipCode() {
        if(vipCode == null) {
            Type typeOfObject = new TypeToken<String>() {}.getType();
            this.vipCode = (String)readFromStorage(VIP_CODE_FLAG, typeOfObject);
        }
        return vipCode;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
        writeToStorageObject(vipCode, VIP_CODE_FLAG);
    }
    // phoneToken it's GCM toke
    private static final String PHONE_TOKEN_FLAG = "PHONE_TOKEN_FLAG";
    private String phoneToken;
    public String getPhoneToken() {
        if(phoneToken == null) {
            Type typeOfObject = new TypeToken<String>() {}.getType();
            this.phoneToken = (String)readFromStorage(PHONE_TOKEN_FLAG, typeOfObject);
        }
        return phoneToken;
    }

    public void setPhoneToken(String phoneToken) {
        this.phoneToken = phoneToken;
        writeToStorageObject(phoneToken, PHONE_TOKEN_FLAG);
    }

    // companyLinkData
    private static final String COMPANY_LINK_DATA_FLAG = "COMPANY_LINK_DATA_FLAG";
    private SetCompanyLinkData companyLinkData;
    public SetCompanyLinkData getCompanyLinkData() {
        if(companyLinkData == null) {
            Type typeOfObject = new TypeToken<SetCompanyLinkData>() {}.getType();
            this.companyLinkData = (SetCompanyLinkData)readFromStorage(COMPANY_LINK_DATA_FLAG, typeOfObject);
        }
        return companyLinkData;
    }
    public void setCompanyLinkData(SetCompanyLinkData companyLinkData) {
        this.companyLinkData = companyLinkData;
        writeToStorageObject(companyLinkData, COMPANY_LINK_DATA_FLAG);
    }

    // Airports from companyLinkData
    public String getPassangerId() {
        if(getCompanyLinkData()!=null) {
            return getCompanyLinkData().PassengerID;
        }
        return null;
    }

    // Airports from companyLinkData
    public List<AirportData> getAirports() {
        if(getCompanyLinkData()!=null) {
            return getCompanyLinkData().Airports;
        }
        return null;
    }

    // ShownFV from companyLinkData
    public boolean getShownStyleImages() {
        if(getCompanyLinkData()!=null) {
            return getCompanyLinkData().ShownStyleImages;
        }
        return true;
    }

    // ShownFV from companyLinkData
    public boolean getShownFV() {
        if(getCompanyLinkData()!=null) {
            return getCompanyLinkData().ShownFV;
        }
        return true;
    }

    // DisplayFilterBar from companyLinkData
    public boolean getDisplayFilterBar() {
        if(getCompanyLinkData()!=null) {
            return getCompanyLinkData().DisplayFilterBar;
        }
        return true;
    }

    // HourlyColumns from companyLinkData
    public boolean getHourlyColumns() {
        if(getCompanyLinkData()!=null) {
            return getCompanyLinkData().HourlyColumns;
        }
        return false;
    }

    // termsAndConditionsValue from companyLinkData
    public JSONObject getTermsAndConditions() {
        if(getCompanyLinkData() != null) {
            try {
                return (new JSONObject(getCompanyLinkData().TermsAndConditions.toString()));
            } catch (Exception e) {

            }
        }

        return null;
    }

    public StorageDataHelper(){}

    public static StorageDataHelper getInstance(Context ctx) {
        if(instance == null) {
            instance = new StorageDataHelper();
        }
        instance.mCtx = ctx;
        return instance;
    }

    private void writeToStorageObject(Object data, String flag) {
        String dataToSave = "";
        if(data != null) {
            if (data.getClass() == String.class) {
                dataToSave = (String) data;
            } else {
                dataToSave = convertToJson(data);
            }
        }

        try {
            Pref.setValue(mCtx, flag, dataToSave);
        }catch (Exception e) {

        }
    }

    private Object readFromStorage(String flag, Type type) {
        String value = Pref.getValue(mCtx, flag, "");
        return (new Gson()).fromJson(value, type);
    }

    private String convertToJson(Object object) {
        return (new Gson()).toJson(object);
    }



}
