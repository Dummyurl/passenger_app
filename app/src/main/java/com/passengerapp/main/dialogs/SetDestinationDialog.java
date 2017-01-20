package com.passengerapp.main.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.passengerapp.BuildConfig;
import com.passengerapp.R;
import com.passengerapp.main.network.model.data.AirportData;
import com.passengerapp.data.LocationBean;
import com.passengerapp.data.SearchDriversTempData;
import com.passengerapp.main.activities.PassengerBaseAppCompatActivity;
import com.passengerapp.main.adapter.PlacesAutoCompleteAdapter;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.data.LocationData;
import com.passengerapp.main.network.model.data.RouteData;
import com.passengerapp.main.network.model.response.GoogleGetLocationByNameApiResponse;
import com.passengerapp.main.network.model.response.GoogleRoutesResponse;
import com.passengerapp.main.widget.AutoCompleteWithClearButton;
import com.passengerapp.util.Const;
import com.passengerapp.util.LocationBll;
import com.passengerapp.util.Logger;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SetDestinationDialog extends PassengerBaseAppCompatActivity {

	private TextView cancel_popup;
	private TextView route;
	private TextView titleActionBar;


	// For Spech to text
	protected static final int RESULT_SPEECH = 1;
	protected static final int RESULT_SPEECH_CURR = 2;


	private ArrayList<LocationBean> locHistoryList;
	private LocationBean locbean;
	private RelativeLayout rl_main_view;
	private TextView tv_item_name;

	private LocationBll locBll;


	private LocationBean pickupLocationFromHistory;
	private LocationBean destinationLocationFromHistory;

	Map<String, String> prepeareDataFlurryEvents = new HashMap<String, String>();


	private boolean flagCurrentItemSelected = false;
	private boolean flagDestiItemSelected = false;





    /*@Bind(R.id.pickup_location_autocomplete) AutoCompleteWithClearButton pickupAutoCompleteEditText;
    @Bind(R.id.destination_location_autocomplete) AutoCompleteWithClearButton destinationAutoCompleteEditText;*/
    @Bind(R.id.llHistoryContinr) LinearLayout llHistoryContinr;
    @Bind(R.id.txtHistoryMsg) TextView txtHistoryMsg;
    @Bind(R.id.sclHistory) ScrollView sclHistory;


    private SearchDriversTempData searchDriversTempData;


   /* @OnItemClick(R.id.pickup_location_autocomplete)
    public void onItemClickPickupAutocomplete(int position){
        flagCurrentItemSelected = true;
        pickupAutoCompleteEditText.setSelection(position);
        searchDriversTempData.pickUpLocation.location_name = pickupAutoCompleteEditText.getText().toString();
    }

    @OnFocusChange(R.id.pickup_location_autocomplete)
    public void onFocusChangePickupAutocomplete(boolean hasFocus) {
            if (hasFocus) {
                if (destinationAutoCompleteEditText.getItemCount() == 1) {
                    flagDestiItemSelected = true;
                    destinationAutoCompleteEditText.setSelection(0);
                }
                generateUI(0);
            }
    }

    @OnTextChanged(value = R.id.pickup_location_autocomplete, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChangePickupAutocomplete(){
        flagCurrentItemSelected = false;
    }*/

   /* @OnClick(R.id.pickup_location_textview)
    public void onClickPickupAutocomplete(){
            if (Pref.getValue(SetDestinationDialog.this, Const.MAP_DIRECTION, "Keyboard Typing").equals("Voice Recognition")) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH_CURR);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), Utils.getStringById(R.string.set_destination_dialog_not_support_speech), Toast.LENGTH_SHORT).show();
                }
            }
    }*/

   /* @OnItemClick(R.id.destination_location_autocomplete)
    public void onItemClickDestinationAutocomplete(int position) {
        flagDestiItemSelected = true;
        destinationAutoCompleteEditText.setSelection(position);

        searchDriversTempData.destinationLocation.location_name = destinationAutoCompleteEditText.getText().toString();
    }

    @OnFocusChange(R.id.destination_location_autocomplete)
    public void onFocusChangeDestinationAutocomplete(boolean hasFocus) {
        if (hasFocus) {
            if (pickupAutoCompleteEditText.getItemCount() == 1) {
                flagCurrentItemSelected = true;
                pickupAutoCompleteEditText.setSelection(0);
            }
            generateUI(1);
        }
    }

    @OnTextChanged(value = R.id.destination_location_autocomplete, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChangeDestinationAutocomplete(){
        flagDestiItemSelected = false;
    }*/

    /*@OnClick(R.id.destination_location_autocomplete)
    public void onClickDestinationAutocomplete() {
        if (Pref.getValue(SetDestinationDialog.this, Const.MAP_DIRECTION, "Keyboard Typing").equals("Voice Recognition")) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

            try {
                startActivityForResult(intent, RESULT_SPEECH);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(), Utils.getStringById(R.string.set_destination_dialog_not_support_speech), Toast.LENGTH_SHORT).show();
            }
        }
    }*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_set_destination);

        ButterKnife.bind(this);

        searchDriversTempData = StorageDataHelper.getInstance(getApplicationContext()).getSearchDriversTempDataForRequest();

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.back_done_actionbar);
        Toolbar parent =(Toolbar) getSupportActionBar().getCustomView().getParent();
        parent.setContentInsetsAbsolute(0,0);

        cancel_popup = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.back_textview_btn);
        route = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.done_textview_btn);
        titleActionBar = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.title_of_activity);
        titleActionBar.setVisibility(View.INVISIBLE);
        initAutoCompleteEditText();






		locBll = new LocationBll(SetDestinationDialog.this);

		cancel_popup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearRouteData();
				finish();
			}
		});

		route.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String result = validation();
				if (result != null) {
					Utils.showWarningMsg(SetDestinationDialog.this, result);
				} else {
					searchDriversTempData.destinationLocation.location_name = destinationAutoCompleteEditText.getText().toString().trim();
                    searchDriversTempData.pickUpLocation.location_name = pickupAutoCompleteEditText.getText().toString().trim();

					prepeareRouteData();
				}

			}
		});

		if(searchDriversTempData.pickUpLocation.location_name != null && searchDriversTempData.pickUpLocation.location_name.isEmpty())
		{
			pickupAutoCompleteEditText.setText(searchDriversTempData.pickUpLocation.location_name);
			flagCurrentItemSelected = true;
		} else {
			pickupAutoCompleteEditText.setText(Utils.getStringById(R.string.set_destination_dialog_current_location));
		}

		if(searchDriversTempData.destinationLocation.location_name != null && !searchDriversTempData.destinationLocation.location_name.isEmpty())
		{
			destinationAutoCompleteEditText.setText(searchDriversTempData.destinationLocation.location_name);
			flagDestiItemSelected = true;
		} else {
			destinationAutoCompleteEditText.setText("");
		}

		generateUI(1);
	}

    AutoCompleteWithClearButton pickupAutoCompleteEditText;
    AutoCompleteWithClearButton destinationAutoCompleteEditText;
    public void initAutoCompleteEditText() {
        pickupAutoCompleteEditText = (AutoCompleteWithClearButton)findViewById(R.id.pickup_location_autocomplete);
        pickupAutoCompleteEditText.setAdapter(new PlacesAutoCompleteAdapter(SetDestinationDialog.this, R.layout.destination_dropdown, R.id.tvRowListLoc));
        pickupAutoCompleteEditText.setThreshold(2);

        pickupAutoCompleteEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                flagCurrentItemSelected = true;
                pickupAutoCompleteEditText.setSelection(position);
                searchDriversTempData.pickUpLocation.location_name = pickupAutoCompleteEditText.getText().toString();
            }
        });

        pickupAutoCompleteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (destinationAutoCompleteEditText.getItemCount() == 1) {
                        flagDestiItemSelected = true;
                        destinationAutoCompleteEditText.setSelection(0);
                    }
                    generateUI(0);
                }
            }
        });

        pickupAutoCompleteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flagCurrentItemSelected = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destinationAutoCompleteEditText = (AutoCompleteWithClearButton)findViewById(R.id.destination_location_autocomplete);
        destinationAutoCompleteEditText.setAdapter(new PlacesAutoCompleteAdapter(SetDestinationDialog.this, R.layout.destination_dropdown,R.id.tvRowListLoc));
        destinationAutoCompleteEditText.setThreshold(2);

        destinationAutoCompleteEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                flagDestiItemSelected = true;
                destinationAutoCompleteEditText.setSelection(position);

                searchDriversTempData.destinationLocation.location_name = destinationAutoCompleteEditText.getText().toString();
            }
        });

        destinationAutoCompleteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (pickupAutoCompleteEditText.getItemCount() == 1) {
                        flagCurrentItemSelected = true;
                        pickupAutoCompleteEditText.setSelection(0);
                    }
                    generateUI(1);
                }
            }
        });

        destinationAutoCompleteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flagDestiItemSelected = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        searchDriversTempData = StorageDataHelper.getInstance(getApplicationContext()).getSearchDriversTempDataForRequest();
        super.onResume();
    }

    @Override
    protected void onPause() {
        StorageDataHelper.getInstance(getApplicationContext()).setSearchDriversTempDataForRequest(searchDriversTempData);
        super.onPause();
    }

    @Override
	protected void onDestroy() {
        ButterKnife.unbind(this);
		super.onDestroy();

		pickupLocationFromHistory = null;
		destinationLocationFromHistory = null;
	}

	private String validation()
	{
		String result = null;
		if(pickupAutoCompleteEditText.getText().toString().trim().equals(""))
		{
			result = Utils.getStringById(R.string.set_destination_dialog_please_enter_current_location);
		}
		else if( destinationAutoCompleteEditText.getText().toString().trim().equals(""))
		{
			result = Utils.getStringById(R.string.set_destination_dialog_please_enter_destination_location);
		}
		else if((!pickupAutoCompleteEditText.getText().toString().trim().equals(Const.CURRENT_LOCATION_STRING) && !pickupAutoCompleteEditText.getText().toString().trim().equals(Const.CURRENT_MY_LOCATION_STRING)) && !flagCurrentItemSelected)
		{
			result = Utils.getStringById(R.string.set_destination_dialog_please_enter_valid_current_location);
		}
		else if(!flagDestiItemSelected)
		{
			result = Utils.getStringById(R.string.set_destination_dialog_please_enter_valid_destination_location);
		}
		return result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    destinationAutoCompleteEditText.setText(text.get(0));
                }
                break;
            }
            case RESULT_SPEECH_CURR: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    pickupAutoCompleteEditText.setText(text.get(0));
                }
                break;
            }
		}
	}

	/*
	 * o = Locationtype Pickup 1 = Locationtype Destination;
	 */
	private void generateUI(final int loctype) {
		llHistoryContinr.removeAllViews();
		locHistoryList = new ArrayList<LocationBean>();

		locHistoryList = locBll.getPickLocation();
		if (loctype == 0) {
			locbean = new LocationBean();
//			locbean.name = LocationManagerHelper.getInstance(this).getLocationName(LocationManagerHelper.getInstance(this).getLatestLocation());
			locbean.name = Const.CURRENT_MY_LOCATION_STRING;
			locHistoryList.add(0, locbean);
		}

		locHistoryList.addAll(locBll.getDestinationlocation());

		//Added from AirportList
		List<AirportData> airports = StorageDataHelper.getInstance(this).getAirports();
		if(airports != null) {
			for(int i=0; i < airports.size(); i++) {
				LocationBean item = new LocationBean();
				item.id = 0;
				item.name = airports.get(i).AirportCode;
				item.Longitude = airports.get(i).Longitude;
				item.Latitude = airports.get(i).Latitude;

				locHistoryList.add(item);
			}
		}

		if (locHistoryList != null && locHistoryList.size() > 0) {
			sclHistory.setVisibility(View.VISIBLE);
			txtHistoryMsg.setVisibility(View.GONE);
			for (int position = 0; position < locHistoryList.size(); position++) {

				locbean = locHistoryList.get(position);

				View view = LayoutInflater.from(SetDestinationDialog.this)
						.inflate(R.layout.row_items, null);
				tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);

				tv_item_name.setText(locbean.name);

				if(locbean.name.equalsIgnoreCase(Const.CURRENT_MY_LOCATION_STRING)) {
					ImageView gpsImage = (ImageView) view.findViewById(R.id.gps_image);
					gpsImage.setVisibility(View.VISIBLE);
					tv_item_name.setTextColor(getResources().getColor(R.color.taxi_main_blue));

				}

				rl_main_view = (RelativeLayout) view.findViewById(R.id.rl_main_view);
				rl_main_view.setTag(locbean);
				rl_main_view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						LocationBean locBean = (LocationBean) view.getTag();
						if (loctype == 0) {
							pickupAutoCompleteEditText.setText(locBean.name);
							pickupLocationFromHistory = locBean;
							prepeareDataFlurryEvents.put("Set PickUp From History", (new Gson()).toJson(locBean));
						} else {
							destinationAutoCompleteEditText.setText(locBean.name);
							destinationLocationFromHistory = locBean;
							prepeareDataFlurryEvents.put("Set Destination From History", (new Gson()).toJson(locBean));
						}
						flagDestiItemSelected = true;

					}
				});

				if ((position % 2) == 0) {
					rl_main_view
							.setBackgroundColor(Color.parseColor("#d8d8d8"));

				} else {
					rl_main_view
							.setBackgroundColor(Color.parseColor("#f2f2f2"));

				}

				llHistoryContinr.addView(view);

			}
		} else {
			sclHistory.setVisibility(View.GONE);
			txtHistoryMsg.setVisibility(View.VISIBLE);
		}
	}


	private ArrayList<RouteData> routeArray;
	private int curRoute = 0;

	protected Handler handlerToast = new Handler();

	public void DisplayMessage(final String msg) {
		final Context context = getApplicationContext();
		handlerToast.post(new Runnable() {
			public void run() {
				try {
					int duration = Toast.LENGTH_LONG;
					Toast.makeText(context, msg, duration).show();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

    public void prepeareRouteData() {
        getPickUpLocationData();
    }

    public void getDestinationLocationData() {
        if(destinationLocationFromHistory != null) {
            searchDriversTempData.destinationLocation.latitude = String.valueOf(destinationLocationFromHistory.Latitude);
            searchDriversTempData.destinationLocation.longitude = String.valueOf(destinationLocationFromHistory.Longitude);

            getRoutes();
        } else {
            DisplayProcessMessage(Utils.getStringById(R.string.set_destination_dialog_prepeare_route));
            final NetworkApi api = (new NetworkService("https://maps.googleapis.com")).getApi();
            api.getLocationByName(searchDriversTempData.destinationLocation.location_name, BuildConfig.GOOGLE_API_KEY)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<GoogleGetLocationByNameApiResponse, SearchDriversTempData>() {
                        @Override
                        public SearchDriversTempData call(GoogleGetLocationByNameApiResponse googleGetLocationByNameApiResponse) {
                            if (googleGetLocationByNameApiResponse.results != null) {
                                searchDriversTempData.destinationLocation.longitude = "0.0";
                                searchDriversTempData.destinationLocation.latitude = "0.0";

                                if (googleGetLocationByNameApiResponse != null
                                        && googleGetLocationByNameApiResponse.results.length > 0) {
                                    searchDriversTempData.destinationLocation.latitude = String.valueOf(googleGetLocationByNameApiResponse.results[0].geometry.location.lat);
                                    searchDriversTempData.destinationLocation.longitude = String.valueOf(googleGetLocationByNameApiResponse.results[0].geometry.location.lng);
                                    searchDriversTempData.destinationLocation.location_name = String.valueOf(googleGetLocationByNameApiResponse.results[0].formatted_address);


                                    LocationBll locBll = new LocationBll(getApplicationContext());
                                    LocationBean localBean = new LocationBean();
                                    localBean.name = searchDriversTempData.destinationLocation.location_name;
                                    localBean.Latitude = Float.parseFloat(searchDriversTempData.destinationLocation.latitude);
                                    localBean.Longitude = Float.parseFloat(searchDriversTempData.destinationLocation.longitude);

                                    locBll.insertEndLocation(localBean);
                                }

                                return searchDriversTempData;
                            }

                            return null;
                        }
                    }).subscribe(new Subscriber<SearchDriversTempData>() {
                @Override
                public void onCompleted() {
                    DisplayProcessMessage(false);
                }

                @Override
                public void onError(Throwable e) {
                    DisplayProcessMessage(false);
                }

                @Override
                public void onNext(SearchDriversTempData searchDriversTempData) {
                    getRoutes();
                }
            });
        }
    }

	public void getPickUpLocationData() {
            if (searchDriversTempData.pickUpLocation.location_name.equalsIgnoreCase(Const.CURRENT_LOCATION_STRING) || searchDriversTempData.pickUpLocation.location_name.equalsIgnoreCase(Const.CURRENT_MY_LOCATION_STRING)) {
                searchDriversTempData.pickUpLocation.latitude = String.valueOf(StorageDataHelper.getInstance(getApplicationContext()).getLatestLocation().getLatitude());
                searchDriversTempData.pickUpLocation.longitude = String.valueOf(StorageDataHelper.getInstance(getApplicationContext()).getLatestLocation().getLongitude());
                getDestinationLocationData();
            } else if (pickupLocationFromHistory != null) {
                searchDriversTempData.pickUpLocation.latitude = String.valueOf(pickupLocationFromHistory.Latitude);
                searchDriversTempData.pickUpLocation.longitude = String.valueOf(pickupLocationFromHistory.Longitude);
                getDestinationLocationData();
            } else {
                DisplayProcessMessage(Utils.getStringById(R.string.set_destination_dialog_prepeare_route));
                final NetworkApi api = (new NetworkService("https://maps.googleapis.com")).getApi();
                api.getLocationByName(searchDriversTempData.pickUpLocation.location_name, BuildConfig.GOOGLE_API_KEY+"")
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Func1<GoogleGetLocationByNameApiResponse, SearchDriversTempData>() {
                            @Override
                            public SearchDriversTempData call(GoogleGetLocationByNameApiResponse googleGetLocationByNameApiResponse) {
                                if (googleGetLocationByNameApiResponse.results != null) {
                                    searchDriversTempData.pickUpLocation.longitude = "0.0";
                                    searchDriversTempData.pickUpLocation.latitude = "0.0";

                                    if (googleGetLocationByNameApiResponse != null
                                            && googleGetLocationByNameApiResponse.results.length > 0) {
                                        searchDriversTempData.pickUpLocation.latitude = String.valueOf(googleGetLocationByNameApiResponse.results[0].geometry.location.lat);
                                        searchDriversTempData.pickUpLocation.longitude = String.valueOf(googleGetLocationByNameApiResponse.results[0].geometry.location.lng);
                                        searchDriversTempData.pickUpLocation.location_name = String.valueOf(googleGetLocationByNameApiResponse.results[0].formatted_address);


                                        LocationBll locBll = new LocationBll(getApplicationContext());
                                        LocationBean localBean = new LocationBean();
                                        localBean.name = searchDriversTempData.pickUpLocation.location_name;
                                        localBean.Latitude = Float.parseFloat(searchDriversTempData.pickUpLocation.latitude);
                                        localBean.Longitude = Float.parseFloat(searchDriversTempData.pickUpLocation.longitude);

                                        locBll.insertPickLocation(localBean);
                                    }

                                    return searchDriversTempData;
                                }

                                return null;
                            }
                        }).subscribe(new Subscriber<SearchDriversTempData>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(SearchDriversTempData searchDriversTempData) {
                        getDestinationLocationData();
                    }
                });
            }
        }


    private void getRoutes() {
        if(!isNetworkAvailable())
            return;

        DisplayProcessMessage(Utils.getStringById(R.string.set_destination_dialog_prepeare_route));
        final NetworkApi api = (new NetworkService("https://maps.googleapis.com")).getApi();

        api.getRoutes(searchDriversTempData.pickUpLocation.latitude+","+searchDriversTempData.pickUpLocation.longitude,
                searchDriversTempData.destinationLocation.latitude+","+searchDriversTempData.destinationLocation.longitude)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<GoogleRoutesResponse, ArrayList<RouteData>>() {
                    @Override
                    public ArrayList<RouteData> call(GoogleRoutesResponse googleRoutesResponse) {
                        ArrayList<RouteData> routes = new ArrayList<RouteData>();
                        for(GoogleRoutesResponse.Route route : googleRoutesResponse.routes) {
                            RouteData routeData = new RouteData();
                            routeData.pointsArray = decodePoly(route.overview_polyline.points);

                            routeData.estiDistance = route.legs[0].distance.text;
                            routeData.estiDuration = route.legs[0].duration.text;
                            routeData.estiHour = route.legs[0].duration.value/3600;

                            routes.add(routeData);
                        }


                        return routes;
                    }
                }).subscribe(new Subscriber<ArrayList<RouteData>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                        DisplayMessage(Utils.getStringById(R.string.set_destination_dialog_no_route));
                    }

                    @Override
                    public void onNext(ArrayList<RouteData> httpRouteDatas) {
                        curRoute = 0;
                        showRouteInfo(httpRouteDatas);
                        finish();
                    }
                });
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


	public void clearRouteData() {
		searchDriversTempData.destinationLocation = new LocationData();
		searchDriversTempData.pickUpLocation = new LocationData();
		prepeareDataFlurryEvents.clear();
	}

	private void showRouteInfo(ArrayList<RouteData> routeArray) {
		RouteData info = routeArray.get(curRoute);

		Logger.writeSimple("Route item is: " + (new Gson()).toJson(info).toString());

		String[]values = info.estiDuration.toString().split(" ");
		if(values.length == 4)
		{
            int value = 0;
			if (values[1].toString().indexOf("min") != -1) {
                value = (Integer.parseInt(values[0].toString())*60);
			} else if (values[1].toString().indexOf("hour") != -1) {
                value = (Integer.parseInt(values[0].toString())*3600);
			} else if (values[1].toString().indexOf("days") != -1) {
                value = (Integer.parseInt(values[0].toString())*86400);
			} else if (values[1].toString().indexOf("second") != -1) {
                value = Integer.parseInt(values[0].toString());
			} else {
                value = 0;
			}

			if (values[3].toString().indexOf("min") != -1) {
                value += (Integer.parseInt(values[2].toString())*60);
			} else if (values[3].toString().indexOf("hour") != -1) {
                value += (Integer.parseInt(values[2].toString())*3600);
			} else if (values[3].toString().indexOf("days") != -1) {
                value += (Integer.parseInt(values[2].toString())*86400);
			} else if (values[3].toString().indexOf("second") != -1) {
                value += (Integer.parseInt(values[2].toString()));
			} else {
                value = 0;
			}

            searchDriversTempData.timeByHourMode = value;
		}

		if(values.length == 2) {
            int value = 0;
			if (values[1].toString().indexOf("min") != -1) {
                value = (Integer.parseInt(values[0].toString())*60);
			} else if (values[1].toString().indexOf("hour") != -1) {
                value = (Integer.parseInt(values[0].toString())*3600);
			} else if (values[1].toString().indexOf("days") != -1) {
                value = (Integer.parseInt(values[0].toString())*86400);
			} else if (values[1].toString().indexOf("second") != -1) {
                value = Integer.parseInt(values[0].toString());
			} else {
                value = 0;
			}

            searchDriversTempData.timeByHourMode = value;
		}

		if (info.estiDistance.indexOf("mi") != -1) {
			searchDriversTempData.replyPickupUnit = "mile";
            searchDriversTempData.replyPickupValue = Float.parseFloat(info.estiDistance.toString().substring(0,info.estiDistance.toString().indexOf("mi")).replaceAll(",",""));
		} else if (info.estiDistance.indexOf("km") != -1) {
            searchDriversTempData.replyPickupUnit = "mile";
            searchDriversTempData.replyPickupValue= (float)(Float.parseFloat(info.estiDistance.toString().substring(0,info.estiDistance.toString().indexOf("km")).replaceAll(",","")) * 0.621371);
		} else {
            searchDriversTempData.replyPickupUnit = "mile";
            searchDriversTempData.replyPickupValue = 0.0f;
		}
	}
}
