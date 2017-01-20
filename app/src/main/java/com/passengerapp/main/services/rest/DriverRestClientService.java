package com.passengerapp.main.services.rest;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.passengerapp.main.SplashActivity;
import com.passengerapp.main.model.http.responses.JsonResponse;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class DriverRestClientService implements IDriverRestClientService {

	protected String serviceUrl;
	protected String apiUrl = "api";
	protected String restApiUrl = "rest-api";
	protected int timeout = 20000;//30 seconds - change to what you want
    public String TAG = "DriverRestClientService";

	public DriverRestClientService(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	protected <T> JsonResponse<T> get(String controller, String action,
									  Type responseType, Map<String, ?> urlVariables)
			throws Exception {
		return get(controller, action, responseType, urlVariables, null);
	}

	protected <T> JsonResponse<T> get(String controller, String action,
									  Type responseType, Map<String, ?> urlVariables,
			Map<String, String> headers) throws Exception {

		try {
			/*HttpHeaders _headers = getDefaultHeaders();

			if (headers != null) {
				for (String headerName : headers.keySet()) {
					_headers.set(headerName, headers.get(headerName));
				}
			}

			HttpEntity<?> requestEntiy = new HttpEntity<String>("", _headers);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate
					.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
			restTemplate.getMessageConverters().add(
					new GsonHttpMessageConverter());*/

			String url = urlVariables == null ? String.format("%s/%s/%s",
					serviceUrl, controller, action) : String.format(
					"%s/%s/%s?%s", serviceUrl, controller, action,
					queryString(urlVariables));

			/*return restTemplate.exchange(url, HttpMethod.GET, requestEntiy,
					responseType, urlVariables);*/

			//JSONObject requestObj = new JSONObject((new Gson()).toJson(request));
			RequestFuture<JSONObject> future = RequestFuture.newFuture();
			JsonObjectRequest requestJson = new JsonObjectRequest(Request.Method.GET, url,  "", future, future);
			RequestQueue queue = Volley.newRequestQueue(SplashActivity.ctx);
			queue.add(requestJson);

			JSONObject response = future.get();
			JsonResponse<T> tmpObj = (new Gson()).fromJson(response.toString(), responseType);

			return tmpObj;

		} catch (Exception exception) {
			Log.e("Exception", exception.getMessage());

			throw new Exception(exception);
		}
	}

	protected <T> JsonResponse<T> post(String controller, String action,
									   Object request, Type responseType, Map<String, ?> urlVariables)
			throws Exception {
		return post(controller, action, request, responseType, urlVariables,
				null);
	}

	@SuppressWarnings("unchecked")
	protected <T> JsonResponse<T> post(String controller, String action,
									   Object request, Type responseType, Map<String, ?> urlVariables,
									   Map<String, String> headers) throws Exception {

		try {
			String url = urlVariables == null ? String.format("%s/%s/%s",
					serviceUrl, controller, action) : String.format(
					"%s/%s/%s?%s", serviceUrl, controller, action,
					queryString(urlVariables));

			JSONObject requestObj = new JSONObject((new Gson()).toJson(request));
			RequestFuture<JSONObject> future = RequestFuture.newFuture();
			JsonObjectRequest requestJson = new JsonObjectRequest(Request.Method.POST, url,  requestObj, future, future);
			int socketTimeout = timeout;//30 seconds - change to what you want
			RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
			requestJson.setRetryPolicy(policy);
			RequestQueue queue = Volley.newRequestQueue(SplashActivity.ctx);
			queue.add(requestJson);

			JSONObject response = future.get();
			JsonResponse<T> tmpObj = (new Gson()).fromJson(response.toString(), responseType);

			return tmpObj;
		} catch (Exception exception) {
			Log.e("Exception Post", exception.toString());
			exception.printStackTrace();

			throw new Exception(exception);
		}
	}

	protected String queryString(Map<String, ?> urlVariables) {

		String query = "";

		Iterator<String> keys = urlVariables.keySet().iterator();

		while (keys.hasNext()) {
			String key = keys.next();
			query += String.format("%s=%s", key, urlVariables.get(key)
					.toString());

			if (keys.hasNext())
				query += "&";
		}

		return query;
	}

	/*protected HttpHeaders getDefaultHeaders() {

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.setAccept(Collections.singletonList(new MediaType(
				"application", "json")));
		requestHeaders.setContentType(new MediaType("application", "json"));

		return requestHeaders;
	}*/

	/*@Override
	public JsonResponse<SetCompanyLinkResponceData> SetCompanyLink(
			SetCompanyLinkData companyLinkData) {
		try {
			Type typeOfResponse = new TypeToken<JsonResponse<SetCompanyLinkResponceData>>() {}.getType();
			JsonResponse<SetCompanyLinkResponceData> response = post(apiUrl,
					"SetCompanyLink", companyLinkData,
					typeOfResponse, null);
			return response;
		} catch (Exception exception) {
			Log.e("Exception Post", exception.toString());
			return new JsonResponse<SetCompanyLinkResponceData>();
		}
	}*/

	/*@Override
	public JsonResponse<RegisterPassData> StorePaymentID(int passengerID,
			int paymentID) {
		try {
			RegisterPassData storeData = new RegisterPassData();
			storeData.PassengerID = passengerID;
			storeData.PaymentID = paymentID;

			Type typeOfResponse = new TypeToken<JsonResponse<RegisterPassData>>() {}.getType();
			JsonResponse<RegisterPassData> response = post(apiUrl,
					"StorePaymentID", storeData, typeOfResponse, null);
			return response;
		} catch (Exception exception) {
			Log.e("Exception Post", exception.toString());
			return new JsonResponse<RegisterPassData>();
		}
	}*/

	/*@Override
	public JsonResponse<List<GetReservationsLocationData>> GetReservationLocation(List<String> reservationIDS) {
		try {
			HttpSentReservationsLocation postData = new HttpSentReservationsLocation();
			postData.ReservationsID = reservationIDS;

			Type typeOfResponse = new TypeToken<JsonResponse<List<GetReservationsLocationData>>>() {}.getType();
			JsonResponse<List<GetReservationsLocationData>> response = post(
					apiUrl, "GetReservationsLocation", postData, typeOfResponse, null);
			return response;
		} catch (Exception exception) {
			Log.e("Exception Post", exception.toString());
			return new JsonResponse<List<GetReservationsLocationData>>();
		}
	}*/



}
