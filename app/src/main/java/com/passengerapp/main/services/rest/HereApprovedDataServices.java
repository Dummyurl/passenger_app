package com.passengerapp.main.services.rest;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.passengerapp.main.model.http.responses.HereApprovedJsonResponse;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HereApprovedDataServices {
	private HttpClient _httpclient;
	HttpGet getClient = null;
	HttpPost postClient = null;
	List<Cookie> cookies;
	private static HereApprovedDataServices _obj;

	// private String baseURI = "http://192.168.137.1/nopcom/gateway/";
	// private String baseURI = "http://192.168.0.102/nopcom/gateway/";
	// private String baseURI = "http://169.254.69.201/nopcom/gateway/";
	private String baseURI = "https://hereapproved.com/gateway/";
	private Context context;

	private HereApprovedDataServices(Context context) {

		_httpclient = getNewHttpClient();

		// _httpclient=new DefaultHttpClient();
		this.context = context;

	}

	public static HereApprovedDataServices getDataServiceObject(Context context) {
		if (_obj == null) {
			_obj = new HereApprovedDataServices(context);
		}
		return _obj;
	}

	private HttpResponse initialiseHTTPClient(String Url, String serviceType,
			AbstractHttpEntity entity) {
		HttpResponse response = null;

		/*
		 * _httpclient.getParams().setBooleanParameter(
		 * "http.protocol.expect-continue", false);
		 */

		if (serviceType.equals("GET")) {
			getClient = new HttpGet(Url);
			// if(DataContext.Session != null) getClient.setHeader("token",
			// DataContext.Session);
			try {
				response = _httpclient.execute(getClient);
			} catch (ClientProtocolException e) {
				Log.e("DataServices", "Error executing http client !");
				return null;
			} catch (IOException e) {
				Log.e("DataServices", "Error executing http client !");
				return null;
			}
		} else if (serviceType.equals("POST")) {
			postClient = new HttpPost(Url);
			if (entity != null)
				postClient.setEntity(entity);
			postClient.setHeader("Content-Type", "application/json");
            //postClient.setHeader("PhoneToken", Pref.getValue(MainActivity.ctx, Const.GCM_TOKEN, ""));
			 //if(DataContext.Session != null) postClient.setHeader("token",
			 //DataContext.Session);

			try {
				response = _httpclient.execute(postClient);
			} catch (ClientProtocolException e) {
				Log.e("DataServices", "Error executing http client !");
				return null;
			} catch (IOException e) {
				Log.e("DataServices", e.getMessage());
				return null;
			}
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private <T> T processHttpResponse(String Url, String serviceType,
			AbstractHttpEntity entity, T returnEntity) {
		HttpResponse response = initialiseHTTPClient(Url, serviceType, entity);
		StatusLine statusLine = response.getStatusLine();
		System.out.println("====================HERE APPROVED GATEWAY RESPOMCE CODE:::::::::::"+statusLine.getStatusCode());
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				response.getEntity().writeTo(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String responseString = out.toString();

			Log.e("result", responseString);
			
			Gson gson = new Gson();

			HereApprovedJsonResponse<T> status = new HereApprovedJsonResponse<T>();
			// status.content = returnEntity;
			JsonParser p = new JsonParser();
			JsonElement jelement = p.parse(responseString).getAsJsonObject().get("content");
//			JsonElement jelement = p.parse(responseString);
			// Type typ = status.getClass();
			// Class<T> clazz = new Class<T>();
			// Type myType = (new JsonResponse<T>()).getType();
			// status.content = returnEntity;
			// status = (JsonResponse<T>) gson.fromJson(responseString,
			// status.getClass());

			returnEntity = (T) gson.fromJson(jelement, returnEntity.getClass());
			// Log.d("returnEntity",(String) returnEntity);
			// processResponse(status);
			// returnEntity = status.content;

		}
		return returnEntity;
	}

	public String EnrollCustomer(String fname/*, String lname*/,
			String CCnumber, String expiryDate, String cardType,String merchantId) {
		String enrollEntity = "0";

		JSONObject objEnrollData = new JSONObject();
		try {

			try {

				objEnrollData.put("FirstName",fname);
				objEnrollData.put("LastName",fname);
				objEnrollData.put("CCNumber",CCnumber);
				objEnrollData.put("ExpiryDate",expiryDate);
				objEnrollData.put("MerchantId",merchantId);
				
				if(cardType.equals("Visa"))
				{
					cardType = "V";
				}
				else if(cardType.equals("Master Card"))
				{
					cardType = "M";
				}
				else if(cardType.equals("American Express"))
				{
					cardType = "X";
				}
				else if(cardType.equals("Discover"))
				{
					cardType = "R";
				}
				
				objEnrollData.put("CardType",cardType);
				if(merchantId !=null && !merchantId.equals("") && !merchantId.equalsIgnoreCase("null"))
				{
					objEnrollData.put("IsCaptive",true);
				}
				else{
					objEnrollData.put("IsCaptive", false);
                    objEnrollData.put("IsFarmed", false);
				}
				

				StringEntity entity;
				entity = new StringEntity(objEnrollData.toString(), HTTP.UTF_8);

				Log.e("EnrollCustomer", objEnrollData.toString());

				entity.setContentType("application/json");
				enrollEntity = processHttpResponse(baseURI + "EnrollCustomer", "POST",entity, enrollEntity);

			} catch (UnsupportedEncodingException e) {
				Log.e("error", "Unsupported Encoding Exception");
			}

		} catch (JSONException e) {
			Log.e("error", "JSONException");
		}

		return enrollEntity;
	}

	/*public HereApprovedJsonResponse<String> StorePaymentID(String passengerID,
			String paymentID) {
		HereApprovedJsonResponse<String> storeEntity = new HereApprovedJsonResponse<String>();

		JSONObject objStorePayment = new JSONObject();
		try {
			objStorePayment.put("PassengerID", passengerID);
			objStorePayment.put("PaymentID", paymentID);

			StringEntity entity = null;
			try {
				entity = new StringEntity(objStorePayment.toString(),
						HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.e("StorePaymentID", objStorePayment.toString());
			entity.setContentType("application/json");
			storeEntity = processHttpResponse(baseURI + "StorePaymentID",
					"POST", entity, storeEntity);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return storeEntity;
	}*/

	public HttpClient getNewHttpClient() {

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			// registry.register(new Scheme("http",
			// PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

}
