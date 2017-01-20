package com.passengerapp.main.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.passengerapp.main.SplashActivity;
import com.passengerapp.util.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Igor on 19.10.2015.
 */
public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList = new ArrayList<String>();
    private final String LOG_TAG = "ExampleApp";
    private final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private final String OUT_JSON = "/json";

    public PlacesAutoCompleteAdapter(Context context, int layoutResourceId,
                                     int textViewResourceId) {
        super(context, layoutResourceId, textViewResourceId);
    }

    @Override
    public int getCount() {
        if(resultList != null)
            return resultList.size();

        return 0;
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        /*
        NetworkApi api = (new NetworkService("https://maps.googleapis.com")).getApi();

        GooglePlacesResponse response = api.getNamesOfPlace(input, BuildConfig.GOOGLE_API_KEY);
        if(response != null) {
            for(GooglePlacesResponse.Prediction item : response.predictions) {
                resultList.add(item.description);
            }
        }*/

        HttpURLConnection conn = null;
        JSONObject jsonObj = new JSONObject();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE
                    + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?sensor=false&key=" + Const.API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest requestJson = new JsonObjectRequest(Request.Method.GET, url.toString(),  "", future, future);
            RequestQueue queue = Volley.newRequestQueue(SplashActivity.ctx);
            queue.add(requestJson);

            jsonObj = future.get();

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } catch (Exception e) {
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}