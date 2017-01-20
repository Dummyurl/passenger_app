package com.passengerapp.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.passengerapp.R;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.main.activities.PassengerBaseFragmentActivity;
import com.passengerapp.main.dialogs.SetCreditCardDataDialog;
import com.passengerapp.main.fragments.MyQuotesFragment;
import com.passengerapp.main.fragments.MyTripsFragment;
import com.passengerapp.main.fragments.MyTripsListFragment;
import com.passengerapp.main.fragments.NewTripFragment;
import com.passengerapp.main.fragments.SettingFragment;
import com.passengerapp.main.model.http.responses.JsonResponse;
import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;
import com.passengerapp.main.network.model.response.GetFlightDetailsData;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.uc.AlertDailogView;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.Const;
import com.passengerapp.util.Logger;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.util.List;

public class SettingActivity extends PassengerBaseFragmentActivity implements OnClickListener, AlertDailogView.OnCustPopUpDialogButoonClickListener {


    private final String TAG ="SettingsActivity";

	Button done;

	TextView cancel_taxi;
	TextView rate;

    LinearLayout setpickupLocation;

	private static final String LOG_TAG = "ExampleApp";
	private static final String API_KEY = "AIzaSyCEA7toSSHxQ3cnRdE4PvoxLXnetXk7Buk";

	// New changes variable declaration
	private TextView txtSettYorTrp;
	private TextView txtSettYorListTrp;
    private TextView txtSettQuotes;
	private TextView txtSettNewTrp;
	private TextView txtSettAdvancd;

	// Variables for YourTrip

	private TextView txtSettYourTripCurrLoca;
	private TextView txtYourTripDesti;
	private TextView yourTripPickupTime;
	private TextView yourTripNum_passenger;
	private TextView txtSetDriver;
	private TextView txtSettClass;
	private TextView txtYorTripFare;
	private TextView txtSettStatusMsg;
	private TextView txtSettStatContr;
	private LinearLayout llSettSpecInstruct;
	private TextView txtSettSpecInstMsg;
	private TextView txtSpcIntuctonTemp;
	private LinearLayout llScrolFunc;
	private Button front;
	private Button previous;
	private Button next;
	private Button end;
	// Flight Info
	private TextView tvFlightInfo;
	private LinearLayout llFlightInfo;

	// Variables for New trip
	private RelativeLayout rlSettNwTrpSpcInst;
	//private TextView txtSettNwTrpSpcInstMsg;
	private RelativeLayout rlSettNwTrpStyle;
	private TextView txtSettNwTrpStyle;

	private List<PickUpReservationData> pickUps;


	private LocalBroadcastManager broadcaster;

	private JsonResponse<GetFlightDetailsData> getflightdataRes;




    public static int SETTINGS_MY_TRIPS_TAB = 0;
    public static int SETTINGS_QUOTES_TAB = 1;
    public static int SETTINGS_NEW_TRIP_TAB = 2;
    public static int SETTINGS_SETTING_TAB = 3;
    public static int SETTINGS_MY_TRIPS_LIST_TAB = 4;

    public static String EXTRA_INTENT_FLAGFROM = "flagFrom";
    public static String EXTRA_INTENT_ONLYDETAIL = "onlydetailed";
    public static String EXTRA_INTENT_ORDER_ID = "orderidvalue";
	public static String EXTRA_RESERVATION_LIST_MODE = "reservationmodelist";

	public enum ReservationListMode {
		ALL,
		CAN_MODIFY,
		CAN_WRITE_REVIEW,
		HAS_REVIEW,
        ACTIVE,
        HISTORICAL,
        WRITE_REVIEW,
        GET_EMAIL_RECEIPT
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initUI();
        initFragments();

        int indexOfTab = getIntent().getIntExtra(EXTRA_INTENT_FLAGFROM, SETTINGS_MY_TRIPS_TAB);
        /*if(indexOfTab == SETTINGS_QUOTES_TAB) {
            newTripFragment.prepeareGetQuotes();
        }*/


        setTabView(indexOfTab);

        if(getIntent().getBooleanExtra(EXTRA_INTENT_ONLYDETAIL, false)) {
            LinearLayout topPanel = (LinearLayout)findViewById(R.id.txtSetTitle);
            topPanel.setVisibility(View.GONE);
        }
        if(getIntent().getIntExtra(EXTRA_INTENT_ORDER_ID, 0) != 0) {
            RelativeLayout bottomPanel = (RelativeLayout)findViewById(R.id.setting_footer_panel);
            bottomPanel.setVisibility(View.GONE);

            showTripInformation(getIntent().getIntExtra(EXTRA_INTENT_ORDER_ID, -1));

        }

        //requestGetPendingPickup();
	}

    public void showTripInformation(int idTrip) {
        myTripsFragment.showTripFromReservationID(idTrip);
    }

    private MyTripsFragment myTripsFragment;
    private MyTripsListFragment myTripsListFragment;
    private MyQuotesFragment myQuotesFragment;
    private NewTripFragment newTripFragment;
    private SettingFragment settingFragment;
    private void initFragments() {
        myTripsFragment = new MyTripsFragment();
        myQuotesFragment = new MyQuotesFragment();
        newTripFragment = new NewTripFragment();
        settingFragment = new SettingFragment();
        myTripsListFragment = new MyTripsListFragment();

        //getSupportFragmentManager().beginTransaction().add(R.id.container, newTripFragment, SETTINGS_NEW_TRIP_TAB+"").commit();
       // getSupportFragmentManager().beginTransaction().add(R.id.container, myTripsFragment, SETTINGS_MY_TRIPS_TAB+"").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, myQuotesFragment, SETTINGS_QUOTES_TAB+"").commit();
        //getSupportFragmentManager().beginTransaction().add(R.id.container, settingFragment, SETTINGS_SETTING_TAB+"").commit();
    }

	@Override
	protected void onStart() {
		broadcaster = LocalBroadcastManager.getInstance(this);
		super.onStart();
	}

	@Override
	protected void onResume() {
//		setValues();
		super.onResume();
	}



    private TextView cancel;
    //private Button newTripRequestBtn;
    private TextView newTripRequestBtn;
    private ImageView goToMapBtn;
    private ImageView goToSettingsBtn;
    private RelativeLayout getQuotesSettingBtn;

    private void initFooterPanel() {
        cancel = (TextView)findViewById(R.id.close_footer_btn);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //click button cancel

                Const.mQouteState = 1;
                List<PickUpReservationData> pickUpForPassanger = StorageDataHelper.getInstance(getBaseContext()).getDriverPickUpsForPassanger();

                if (pickUpForPassanger != null) {
                    for (int i = 0; i < pickUpForPassanger.size(); i++) {
                        if (pickUpForPassanger.get(i).ReservationStatus.equalsIgnoreCase("Active")) {
                            Const.mQouteState = 2;
                            break;
                        }
                    }
                }
                finish();
            }
        });


        //newTripRequestBtn = (Button)findViewById(R.id.new_trip_done);
        newTripRequestBtn = (TextView)findViewById(R.id.new_trip_done);
        newTripRequestBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.writeSimple("Click new Trip request button in bottom menu");
                newTripFragment.newTripButtonPressed();
            }
        });

        getQuotesSettingBtn = (RelativeLayout) findViewById(R.id.get_quotes_settings_btn);

        goToMapBtn = (ImageView)findViewById(R.id.map_footer_btn);
        goToMapBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // goto map activity
                finish();
            }
        });

        goToSettingsBtn = (ImageView)findViewById(R.id.setting_footer_btn);
        goToSettingsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // activate settings tab
                //setTabView(SETTINGS_SETTING_TAB);
                startActivity(new Intent(SettingActivity.this, SetCreditCardDataDialog.class));
                //setTabView(SETTINGS_SETTING_TAB);
            }
        });


        mytrip_nav_bar = (LinearLayout)findViewById(R.id.mytrip_nav_bar);

        mytrip_left_two = (ImageView)findViewById(R.id.mytrip_left_two);
        mytrip_left_two.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImageViewForMyTrip(view);
            }
        });
        mytrip_left_one = (ImageView)findViewById(R.id.mytrip_left_one);
        mytrip_left_one.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImageViewForMyTrip(view);
            }
        });
        mytrip_right_one = (ImageView)findViewById(R.id.mytrip_right_one);
        mytrip_right_one.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImageViewForMyTrip(view);
            }
        });
        mytrip_right_two = (ImageView)findViewById(R.id.mytrip_right_two);
        mytrip_right_two.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImageViewForMyTrip(view);
            }
        });

        mytrip_text_main = (TextView)findViewById(R.id.mytrip_text_main);
    }

    private void onClickImageViewForMyTrip(View view) {
        final View mView = view;
        mytrip_nav_bar.setEnabled(false);
        mView.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        if(mView.getId() == R.id.mytrip_left_two) {
            myTripsFragment.firstTrip();
        } else if(mView.getId() == R.id.mytrip_left_one) {
            myTripsFragment.prevTrip();
        } else if(mView.getId() == R.id.mytrip_right_one) {
            myTripsFragment.nextTrip();
        } else if(mView.getId() == R.id.mytrip_right_two) {
            myTripsFragment.lastTrip();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.setBackgroundColor(Color.TRANSPARENT);
            }
        }, 200);
        mytrip_nav_bar.setEnabled(true);
    }

    LinearLayout mytrip_nav_bar;
    public ImageView mytrip_left_two;
    public ImageView mytrip_left_one;
    public ImageView mytrip_right_one;
    public ImageView mytrip_right_two;
    public TextView mytrip_text_main;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    public void setDataForQuoteTable(List<SearchDriversData> list) {

        final List<SearchDriversData> mList = list;

        SettingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //setTabView(SETTINGS_QUOTES_TAB);
                //myQuotesFragment.setData(mList);
                //finish();
            }
        });
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


	public void initUI() {
        initFooterPanel();

        txtSettYorListTrp = (TextView) findViewById(R.id.txtSettYorListTrp);
        txtSettYorListTrp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setTabView(SETTINGS_MY_TRIPS_LIST_TAB);
            }
        });
        // Your trip
        txtSettYorTrp = (TextView) findViewById(R.id.txtSettYorTrp);
        txtSettYorTrp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setTabView(SETTINGS_MY_TRIPS_TAB);
            }
        });

        // My quotes
        txtSettQuotes = (TextView) findViewById(R.id.txtSettQuotes);
        txtSettQuotes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setTabView(SETTINGS_QUOTES_TAB);
            }
        });

        //New trip
        txtSettNewTrp = (TextView) findViewById(R.id.txtSettNewTrp);
        txtSettNewTrp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setTabView(SETTINGS_NEW_TRIP_TAB);
            }
        });

        //Settings
        txtSettAdvancd = (TextView) findViewById(R.id.txtSettAdvanced);
        txtSettAdvancd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setTabView(SETTINGS_SETTING_TAB);
            }
        });
	}

    private DriverViewModel viewModel;




	private void setYorTrip() {
		pickUps = StorageDataHelper.getInstance(this).getDriverPickUpsForPassanger();
		if (pickUps != null && pickUps.size() > 0) {
			if (pickUps.size() == 1) {
				llScrolFunc.setVisibility(View.GONE);
			}
			Const.DRIVER_TOKEN = pickUps.get(Const.currentItem).DriverToken;
			txtSettYourTripCurrLoca
					.setText(pickUps.get(Const.currentItem).PickupLocation.location_name);
			txtYourTripDesti
					.setText(pickUps.get(Const.currentItem).DestinationLocation.location_name);
			yourTripPickupTime.setText(Utils.millisToDate(
					(Long.parseLong(pickUps.get(Const.currentItem).TimeOfPickup)) * 1000,
					"hh:mm a MM/dd/yyyy"));
			yourTripNum_passenger
					.setText(pickUps.get(Const.currentItem).NumberOfPassenger
							+ "");

			txtSetDriver
					.setText(Html.fromHtml("<font color=#868686>"
							+ pickUps.get(Const.currentItem).DriverName
							+ "</font> <font color=#324f85><u>"
							+ Utils.formatPhoneNumber(pickUps
									.get(Const.currentItem).DriverPhoneNumber)
							+ "</u></font>"));
			txtSettClass.setText(pickUps.get(Const.currentItem).CabStyleRequired);
			if (pickUps.get(Const.currentItem).NumOfHours == 0.0) {

				if (pickUps.get(Const.currentItem).NumberOfPassenger == 1) {
					txtYorTripFare
							.setText("$"
									+ String.format(
											"%.2f",
											pickUps.get(Const.currentItem).EstimateFare)
									+ " for "
									+ String.format(
											"%.2f",
											pickUps.get(Const.currentItem).Distance.Value)
									+ pickUps.get(Const.currentItem).Distance.Unit
									+ ", "
									+ pickUps.get(Const.currentItem).NumberOfPassenger
									+ " passenger");
				} else {
					txtYorTripFare
							.setText("$"
									+ String.format(
											"%.2f",
											pickUps.get(Const.currentItem).EstimateFare)
									+ " for "
									+ String.format(
											"%.2f",
											pickUps.get(Const.currentItem).Distance.Value)
									+ pickUps.get(Const.currentItem).Distance.Unit
									+ ", "
									+ pickUps.get(Const.currentItem).NumberOfPassenger
									+ " passengers");
				}
			} else {
				txtYorTripFare.setText("$"
						+ String.format("%.2f",
								pickUps.get(Const.currentItem).EstimateFare)
						+ " for " + pickUps.get(Const.currentItem).NumOfHours
						+ " Hours");
			}
			if (pickUps.get(Const.currentItem).ReservationStatus
					.equalsIgnoreCase("Active")) {
				cancel_taxi.setVisibility(View.VISIBLE);
				rate.setVisibility(View.GONE);
			} else if (pickUps.get(Const.currentItem).ReservationStatus
					.equalsIgnoreCase("Completed")) {
				cancel_taxi.setVisibility(View.GONE);
				rate.setVisibility(View.VISIBLE);
			}
			txtSettStatusMsg
					.setText(pickUps.get(Const.currentItem).ReservationStatus);
			txtSettStatContr.setText((Const.currentItem + 1) + " of "
					+ pickUps.size());

			if (pickUps.get(Const.currentItem).HaveFlightInfo
					&& pickUps.get(Const.currentItem).IsAirport) {
				llSettSpecInstruct.setVisibility(View.GONE);
				llFlightInfo.setVisibility(View.VISIBLE);
				tvFlightInfo.setPaintFlags(tvFlightInfo.getPaintFlags()
						| Paint.UNDERLINE_TEXT_FLAG);
			} else if (pickUps.get(Const.currentItem).IsAirport
					&& !pickUps.get(Const.currentItem).HaveFlightInfo) {
				llSettSpecInstruct.setVisibility(View.GONE);
				llFlightInfo.setVisibility(View.VISIBLE);
				tvFlightInfo.setPaintFlags(tvFlightInfo.getPaintFlags()
						| Paint.UNDERLINE_TEXT_FLAG);
			} else {
				llSettSpecInstruct.setVisibility(View.VISIBLE);
				llFlightInfo.setVisibility(View.GONE);
				txtSpcIntuctonTemp.setText(Utils.getStringById(R.string.detailed_special_instructions));
				txtSettSpecInstMsg
						.setText(pickUps.get(Const.currentItem).SpecialInstructions);
			}
		} else {
			llScrolFunc.setVisibility(View.GONE);
			cancel_taxi.setVisibility(View.GONE);
			rate.setVisibility(View.GONE);
			txtSettStatusMsg.setText(Utils.getStringById(R.string.detailed_have_no_pickups));
		}
	}

	private ProgressDialog progressDialog = null;

	private void showProcessingDialog(String msg) {
		if (progressDialog == null)
			progressDialog = ProgressDialog.show(SettingActivity.this, "", msg,
					true, false);
	}

	private void hideProcessingDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void DisplayProcessMessage(final String msg) {
            showProcessingDialog(msg);
	}

	public void DisplayProcessMessage(final boolean hide) {

		hideProcessingDialog();

	}

	protected Handler handler = new Handler();

	public void DisplayMessage(final String msg) {
		final Context context = getApplicationContext();
		handler.post(new Runnable() {
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult_check", "RequestCode: "+requestCode+"; ResultCode: " + resultCode + " run in SettingsActivity");

        newTripFragment.onActivityResult(requestCode, resultCode, data);
        settingFragment.onActivityResult(requestCode, resultCode, data);
        myQuotesFragment.onActivityResult(requestCode, resultCode, data);
        myTripsListFragment.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onClick(View v) {
		/*if (v == cancel_taxi) {
			Intent intent = new Intent(SettingActivity.this, CancelActivity.class);
			intent.putExtra("fromCancel", "1");
			startActivity(intent);
		}*/

		 if (v == rate) {
			Intent intent = new Intent(SettingActivity.this, RateActivity.class);
			startActivity(intent);
		} else if (v == txtSetDriver) {
			try {
                List<PickUpReservationData> pickUpForPassanger = StorageDataHelper.getInstance(this).getDriverPickUpsForPassanger();

				if (pickUpForPassanger != null && pickUpForPassanger.size() > Const.currentItem) {
					Intent newIntent = new Intent(
							Intent.ACTION_CALL,
							Uri.parse("tel:"
									+ pickUpForPassanger.get(Const.currentItem).DriverPhoneNumber));
					newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(newIntent);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}  else if (v == done) {
			finish();
		}
         else if (v == front && pickUps != null) {
			Const.currentItem = 0;
			setYorTrip();
		}

		else if (v == previous && pickUps != null) {
			if (Const.currentItem > 0) {
				Const.currentItem -= 1;
				System.out
						.println("============PASENEGR CURRENT ITEM PREVIOUS:::::"
								+ Const.currentItem);
				setYorTrip();
			}
		}

		else if (v == next && pickUps != null) {
			if (Const.currentItem < pickUps.size() - 1) {
				Const.currentItem += 1;
				System.out.println("============CURRENT ITEM NXT:::::"
                        + Const.currentItem);
				setYorTrip();
			}
		}

		else if (v == end && pickUps != null) {

			Const.currentItem = pickUps.size() - 1;
			setYorTrip();
		}
	}



	public void setTabView(int tab) {
        if (tab == SETTINGS_MY_TRIPS_LIST_TAB) {
            txtSettYorListTrp.setBackgroundResource(R.color.dark_graylight);
            txtSettYorTrp.setBackgroundResource(R.color.dark_gray);
            txtSettQuotes.setBackgroundResource(R.color.dark_gray);
            txtSettAdvancd.setBackgroundResource(R.color.dark_gray);
            txtSettNewTrp.setBackgroundResource(R.color.dark_gray);

            try {
				myTripsListFragment.setListMode((ReservationListMode)getIntent().getSerializableExtra(EXTRA_RESERVATION_LIST_MODE));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, myTripsListFragment).commit();
            } catch (Exception e) {}
        } else if (tab == SETTINGS_MY_TRIPS_TAB) {
            txtSettYorListTrp.setBackgroundResource(R.color.dark_gray);
            txtSettYorTrp.setBackgroundResource(R.color.dark_graylight);
            txtSettQuotes.setBackgroundResource(R.color.dark_gray);
            txtSettAdvancd.setBackgroundResource(R.color.dark_gray);
            txtSettNewTrp.setBackgroundResource(R.color.dark_gray);

            try {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, myTripsFragment).commit();
            } catch (Exception e) {}
		} else if (tab == SETTINGS_QUOTES_TAB) {
            txtSettYorListTrp.setBackgroundResource(R.color.dark_gray);
            txtSettYorTrp.setBackgroundResource(R.color.dark_gray);
            txtSettQuotes.setBackgroundResource(R.color.dark_graylight);
            txtSettNewTrp.setBackgroundResource(R.color.dark_gray);
            txtSettAdvancd.setBackgroundResource(R.color.dark_gray);

            try {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, myQuotesFragment).commit();
            } catch (Exception e) {}
            try {
                myQuotesFragment.showInstructionDialog();
            }catch (Exception e) {

            }
		} else if (tab == SETTINGS_NEW_TRIP_TAB) {
            txtSettYorListTrp.setBackgroundResource(R.color.dark_gray);
            txtSettYorTrp.setBackgroundResource(R.color.dark_gray);
            txtSettQuotes.setBackgroundResource(R.color.dark_gray);
            txtSettNewTrp.setBackgroundResource(R.color.dark_graylight);
            txtSettAdvancd.setBackgroundResource(R.color.dark_gray);

            try {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, newTripFragment).commit();
            } catch (Exception e) {}
		} else if (tab == SETTINGS_SETTING_TAB) {
            txtSettYorListTrp.setBackgroundResource(R.color.dark_gray);
            txtSettYorTrp.setBackgroundResource(R.color.dark_gray);
            txtSettQuotes.setBackgroundResource(R.color.dark_gray);
            txtSettNewTrp.setBackgroundResource(R.color.dark_gray);
            txtSettAdvancd.setBackgroundResource(R.color.dark_graylight);

            try {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, settingFragment).commit();
            } catch (Exception e) {}
        }

        setFooterPanelOfTab(tab);
	}

    private Handler animUIHandler;
    private Runnable animUIRunnable;
    private int currentTab =0;
    public void setFooterPanelOfTab(int tab) {
        currentTab = tab;
        // do operation with footer
        if (tab == SETTINGS_MY_TRIPS_TAB) {
            stopAnimation();
            //newTripRequestBtn.setVisibility(View.INVISIBLE);
            getQuotesSettingBtn.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.GONE);
            goToSettingsBtn.setVisibility(View.VISIBLE);
            mytrip_nav_bar.setVisibility(View.VISIBLE);
            stopAnimation();
        } else if (tab == SETTINGS_QUOTES_TAB || tab == SETTINGS_MY_TRIPS_LIST_TAB) {
            stopAnimation();
            //newTripRequestBtn.setVisibility(View.INVISIBLE);
            getQuotesSettingBtn.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.VISIBLE);
            goToSettingsBtn.setVisibility(View.VISIBLE);
            mytrip_nav_bar.setVisibility(View.GONE);
            stopAnimation();
        } else if (tab == SETTINGS_NEW_TRIP_TAB) {
            stopAnimation();
            cancel.setVisibility(View.INVISIBLE);
            //newTripRequestBtn.setVisibility(View.VISIBLE);
            getQuotesSettingBtn.setVisibility(View.VISIBLE);
            final Animation animForward = AnimationUtils.loadAnimation(this, R.anim.scale_get_quotes);
            final Animation animBack = AnimationUtils.loadAnimation(this, R.anim.scale_get_quotes_back);

            animUIHandler = new Handler();

            animUIRunnable = new Runnable()
            {
                //boolean isStop = false;
                int i=0;
                public void run()
                {
                    if(i == 0) {
                        newTripRequestBtn.clearAnimation();
                        newTripRequestBtn.startAnimation(animForward);
                        i++;
                    } else {
                        newTripRequestBtn.startAnimation(animBack);
                        i=0;
                    }

                    if(currentTab == SETTINGS_NEW_TRIP_TAB) {
                        animUIHandler.postDelayed(this, 2000);
                    }
                }
            };

            //animUIRunnable.run();


            goToSettingsBtn.setVisibility(View.VISIBLE);
            mytrip_nav_bar.setVisibility(View.GONE);
        } else if (tab == SETTINGS_SETTING_TAB) {
            stopAnimation();
            //newTripRequestBtn.setVisibility(View.INVISIBLE);
            getQuotesSettingBtn.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.VISIBLE);
            goToSettingsBtn.setVisibility(View.INVISIBLE);
            mytrip_nav_bar.setVisibility(View.GONE);

        }
    }

    private void stopAnimation() {
        newTripRequestBtn.clearAnimation();
        if(animUIHandler != null && animUIRunnable != null) {
            animUIHandler.removeCallbacks(animUIRunnable);
        }
    }


	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}


    public void showFlightDetailActivity(SaveFlightDetailRequest resFlightData) {
            Intent intent = new Intent(this, FlightDetailView.class);
            intent.putExtra(Const.FLIGHT_DETAIL, resFlightData);

            startActivity(intent);
    }

	@Override
	public void OnButtonClick(int tag, int buttonIndex) {
		switch (tag) {
		case 0:
			if (buttonIndex == AlertDailogView.BUTTON_CANCEL) {
				/*Intent intent = new Intent(GetDriverService.COPA_RESULT);
				broadcaster.sendBroadcast(intent);*/
				finish();
			}
			break;
		}
	}

}
