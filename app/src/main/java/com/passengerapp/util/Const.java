package com.passengerapp.util;

import android.content.Context;
import android.os.Environment;

import com.passengerapp.main.network.model.response.GetFlightDetailsData;
import com.passengerapp.main.network.model.response.GetReservationLocationData;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.main.network.model.response.SearchDriversData;

import java.util.List;

public class Const {

	public static Context CONTEXT;

	// Arsen Limo
	// 1. com.herebygps.arsenlimo
	// 2. icon -> ic_launcher_arsen_limo
	// 3. app_name -> app_name_arsen_limo
	public static String domain = "";
	public static boolean isVIP = false;
	public static boolean isGeneric = false;
	public static String splashScreenDrawableImageID = "";
	/*public static String domain = Resources.getSystem().getString(R.string.url);
	public static boolean isVIP = Resources.getSystem().getBoolean(R.bool.is_vip);
	public static String VIP_DEFAULT = Resources.getSystem().getString(R.string.vip_default);
	public static boolean isGeneric = false;
	public static String splashScreenDrawableImageID = Resources.getSystem().getString(R.string.splashscreen_file_name);*/


	// Alen Limo
	// 1. com.herebygps.allenlimo
	// 2. icon -> ic_launcher_allen_limo
	// 3. app_name -> app_name_allen_limo
	/*public static String domain = "http://www.rideeze.com";
	public static boolean isVIP = false;
	public static String VIP_DEFAULT = "AEK";
	public static boolean isGeneric = false;
	public static int splashScreenDrawableImageID = R.drawable.splashscreen_main_allenl_imo;*/

    /*The main part of castomized apps*/
    // Customization app
    /*public static String domain = "http://www.rideeze.com";
	public static boolean isVIP = true;
	public static boolean isGeneric = false;
    //public static boolean HourlyColumns = true;
	// Change in manifest file: set "ic_launcher_allen_limo" as icon  and "app_customization" as name
	// Change package id to com.herebygps.castomized

	//Generic app
	/*public static String domain = "http://www.rideeze.com";
	public static boolean isVIP = false;
	public static boolean isGeneric = true;
    public static boolean HourlyColumns = false;*/
	// Change in manifest file: set "ic_launcher" as icon  and "app_name" as name
	// Change package id to com.herebygps.generic

	//public static String domain = "http://www.herebygps.com";
	/*public static String domain = "http://www.rideeze.com";
    public static boolean isVIP = true;
	public static boolean isGeneric = true;
    public static boolean HourlyColumns = false;*/
    /*END*/

	public static String DB_NAME = "HEREBYGPS_DB";
	public static final String PREF_FILE = "HEREBYGPS_PREF";

	public static String APP_HOME = Environment.getExternalStorageDirectory()
			.getPath() + "/HereByGps";

	public static String SD_CARD = Environment.getExternalStorageDirectory()
			.getPath() + "/";
	public static String DIR_LOG = APP_HOME + "/Log";
	public static String DIR_DATA = APP_HOME + "/data";
	public static String DIR_BACKUP = APP_HOME + "/backup";
	public static String LOG_ZIP = APP_HOME + "/HereByGps.zip";

	public static String DATA_PATH = APP_HOME + "/value";

	public static String Accuracy = "Accuracy";

	public static float FiveMileMeter = (1.60934f * 1000.0f * 5.f);// 5mile
	public static float OneMileMeter = (1.60934f * 1000.0f);
	public static float OneKiloMeter = 1000.0f;

	public static String UNIT = "UNIT";
	public static String VALUE = "VALUE";
	public static String RADIUS = "RADIUS";
	public static String MAP_DIRECTION = "MAP_DIRECTION";
	public static String DRIVER_ID = "DRIVER_ID";
//	public static String NO_PASSENGER = "NO_PASSENGER";

	public static String GPS_STATUS = "GPS_STATUS";



	public static String TIME_LABEL_FOR_PICKUP_NOW = "now";
	public static String TIME_LABEL_FOR_PICKUP_LATER = "later";



	public static String DESTINATION;
	public static String CURRENT;

	public static String EstiDistance;
//	public static String EstiTime = "0.0";

/*	public static String replyPickupUnit;
	public static float replyPickupValue;
	public static float tempReplyPickupValue;*/

	public static String currentLocationName; //DON'T DELETE
	public static double destLat;
	public static double destLong;
	public static double srcLat;
	public static double srcLong;
	public static double currLat;
	public static double currLong;
	public static String timeOfPic;
	public static String timeOfPicLastRequest;
	public static String deviceId;
	//public static int StyleType;

	/*public static String lastDESTINATION;
	public static String lastCURRENT;*/
	/*public static double lastdestLat;
	public static double lastdestLong;
	public static double lastsrcLat;
	public static double lastsrcLong;*/
//	public static String lasttimeOfPic;
//	public static String lastbookingTime;

	public static String REGI_PASSENEGRID = "REGI_PASSENEGRID";

	/*// For GCM
	public static String DRIVER_TOKEN_TITLE = "DRIVER_TOKEN";*/

	public static String DRIVER_TOKEN;
	public static String RESRAVTION_ID;
	public static String TRIP_NUMBER;
    public static long RESRAVTION_ID_MYTRIP;

    public static String BY_THE_HOUR = "by-the-hour";
    public static String BY_THE_DURATION_OF_TRIP = "duration-of-trip";

//	public static String tripType = BY_THE_DURATION_OF_TRIP;

	//public static String vehicleStyle = "Sedan";
	public static String bookingTime = TIME_LABEL_FOR_PICKUP_NOW;
	public static String comment;
	public static String Name;

	// For passengers
	public static String PASSENGER_NAME = "PASSENGER_NAME";
	public static String PASSENGER_NO = "PASSENGER_NO";
	public static String PASSENGER_EMAIL = "PASSENGER_EMAIL";

	public static String DEVICE_ID = "DEVICE_ID";

    //14853 Towne Lake Cir, Addison, TX as pickup location.
    //501 Elm St, Dallas, TX as Destination

    //Google Moto G Android 4.4.4
    //cheap Chinese phone Android 4.3

	// private static final String API_KEY =
	// "AIzaSyBx8nJrdm5SuQJZtBSApDu64-9f9lRdL3";
	public static final String API_KEY = "AIzaSyCEA7toSSHxQ3cnRdE4PvoxLXnetXk7Buk";
/*
	public static JsonResponse<List<SearchDriversData>> driverData = new JsonResponse<List<SearchDriversData>>();
	public static JsonResponse<List<SearchDriversLaterData>> driverDataLater = new JsonResponse<List<SearchDriversLaterData>>();*/
	
	//public static List<GetFlightsInfoFromFVData> flightList = new ArrayList<GetFlightsInfoFromFVData>();

    public static List<GetReservationLocationData> activeDriverData = null;
	public static List<PickUpReservationData> waitingStatusDriverData = null;


	public static GetFlightDetailsData mytrip_saveflghtInfo=null;
	public static int currentItem = 0;

	// For SetCompanyLink
	public static String SET_COMPANYLINK_PHONENO = "SET_COMPANYLINK_PHONENO";
	public static String SET_COMPANYLINK_EMAIL = "SET_COMPANYLINK_EMAIL";
    public static String SET_COMPANYLINK_NAME = "SET_COMPANYLINK_NAME";
	//public static String SET_COMPANYLINK_VIPCODE = "SET_COMPANYLINK_VIPCODE";

    public static String SET_COMPANYLINK_PHONENO_TEMP = "SET_COMPANYLINK_PHONENO_TEMP";
    public static String SET_COMPANYLINK_EMAIL_TEMP = "SET_COMPANYLINK_EMAIL_TEMP";
    public static String SET_COMPANYLINK_NAME_TEMP = "SET_COMPANYLINK_NAME_TEMP";

	/*
	 * For VISIT_SET_COMPANYLINK
	 * 
	 * 0 = User hasn't visited SetCompanyLink View. 1 = User has already visited
	 * SetCompanyLink View.
	 */
	public static String VISIT_SET_COMPANYLINK = "VISIT_SET_COMPANYLINK";

	// For CREDIT CARD DATA
	public static String CC_PAYMENT_ID = "CC_PAYMENT_ID";
	public static String CC_NUMBER = "CC_NUMBER";
	
//	For MerchantId
	public static String MERCHANT_ID = "MERCHANT_ID";

    public static String CREDIT_CARD_STORED_PAYMENT_METHOD = "Stored";
    public static String CREDIT_CARD_SCANNED_PAYMENT_METHOD = "Scanned";
    public static String CASH_PAYMENT_METHOD = "Cash";


	// For Selected Driver to Cancel.
	/*
	public static String cancelDriverToken;
	public static String cancelDriverPhoneno;*/
	
//	For flightInfo
	public static String departAirportName;
	public static String departAirportCode;
	public static String ArrivalAirportName;
	public static String ArrivalAirportCode;
	public static String airlineCode;
	public static String date;
	public static String flightName;
	public static String strFlightId;
	public static int flightId;
	
//	FOR INVOICE DETAILES
	public static int invoiceId;
	public static int reservationId;
	
	//For toggle buttons in Main View 
	public static int mQouteState = 1;

	public static String IS_SHOW_WAITING_DLG_FOR_DRIVER = "showWaitingDialog";
	public static String IS_SHOW_INSTRUCTION_FOR_QUOTES_LIST = "showInstructionQuotesDialog";

	public static int DefaultSearchRadius = -1;

	public static String IS_ADDIDNG_FLIGHT_INFO_REGISTER_PASS = "IS_ADDIDNG_FLIGHT_INFO_REGISTER_PASS";
	public static String FLIGHT_DATA_EXTRA_ID = "FLIGHT_DATA_EXTRA_ID";
	public static String IS_CANCEL_FLIGHT_DATA_EXTRA_ID = "is_flight_btn";
	public static String DEPARTURE_AIRPORTCODE = "departure_airport_code";
	public static String SEARCH_DRIVER_DATA = "searchDriverData";
	public static String SEARCH_DRIVERS_DATA = "searchDriversData";
	public static String MODIFY_FLIGHT_DETAIL = "modifyFlightDetail";
	public static String FLIGHT_DETAIL = "FlightDetail";
	public static String SEND_FLIGHT_DATA_WITHOUT_REQUEST = "sendFlightDataWithoutRequest";
	public static String FLIGHT_DETAIL_TO_SEND_AFTER_PICKUP_REQUEST = "flightDetailToSendAfterPickupRequest";
	public static String FLIGHT_DETAIL_HAS_TEMP_FLIGHT_ID = "flightDetailHasTempFlightId";
	public static final String FLIGHT_LIST_RACES = "flightListOfRaces";

	public static String MODIFY_FLIGHT_DETAIL_DRIVER_CAR_CLASS = "modifyFlightDetailDriverCarClass";
	public static String MODIFY_FLIGHT_RESERVATION_ID = "modifyFlightDetailReservationId";
	public static String COUPON_CODE_INTENT_PARAM = "couponCodeParam";
	public static String REQUEST_FROM_CATEGORY_LATER = "requestFromCategoryLater";
	public static String COUPON_VALID_NAME = "couponValidName";
	public static String CURRENT_LOCATION_STRING = "Current Location";
	public static String CURRENT_MY_LOCATION_STRING = "My Location";
	public static String CAR_SERVICE_NAME = "Car Service";

	public static final String RESERVATION_ID_TO_CANCEL_DRIVER = "RESERVATION_ID_TO_CANCEL_DRIVER";
	public static final String DRIVER_PHONE_NUMBER_TO_CANCEL_DRIVER = "DRIVER_PHONE_NUMBER_TO_CANCEL_DRIVER";
	public static final String CANCEL_TO_DRIVER_IMMEDIATELY = "CANCEL_TO_DRIVER_IMMEDIATELY";



	// ReservationStatus
	public static String RESERVATION_STATUS_ACTIVE = "Active";
	public static String RESERVATION_STATUS_TIME_OUT_WAITING_CONFIRMATION = "TimeOutWaitingConfirmation";
	public static String RESERVATION_STATUS_FUTURE = "Future";
	public static String RESERVATION_STATUS_PENDING = "Pending";
	public static String RESERVATION_STATUS_DRIVER_DECLINED = "Driver Decline";
	public static String RESERVATION_STATUS_DRIVER_ON_WAY = "DriverOnWay";
	public static String RESERVATION_STATUS_COMPLETED = "Completed";
	public static String RESERVATION_SUB_STATUS_UNASSIGNED = "UnAssigned";
	public static String RESERVATION_SUB_STATUS_AWAITINGCONFIRMATION = "AwaitingConfirmation";
	public static String RESERVATION_STATUS_CAMCELLED_BY_DRIVER = "CancelledByDriver";



	public static SearchDriversData searchDriver;


    public static final Long DELAY_FOR_WAITING_DRIVER_RESPONSE = (long)15*60*100;




}


