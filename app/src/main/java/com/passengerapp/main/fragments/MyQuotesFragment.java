package com.passengerapp.main.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.passengerapp.R;
import com.passengerapp.data.SearchDriversTempData;
import com.passengerapp.main.network.model.response.SearchDriversLaterData;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.dialogs.EnterCouponActivity;
import com.passengerapp.main.dialogs.PickUp.PickUpActivity;
import com.passengerapp.main.network.model.response.VerfiyCouponData;
import com.passengerapp.main.services.MemoryCache;
import com.passengerapp.main.services.OnImageLoadedListener;
import com.passengerapp.main.services.VolleyLoader;
import com.passengerapp.util.Const;
import com.passengerapp.util.Pref;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by adventis on 11/9/14.
 */
public class MyQuotesFragment extends Fragment implements OnImageLoadedListener {

    private final String TAG = "MyQuotesFragment";
    public static final int ENTER_COUPON_ACTIVITY = 1;

    private LinearLayout sQuotes_data_table;
    private LinearLayout quotesHeaderLayout;

    private final String ALL_VCLASS = "All";
    private final String CAR_SERVICE_VCLASS = "Car Service";
    public static final String TAXI_VCLASS = "Taxi";
    private final String SHUTTLE_VCLASS = "Shuttle";

    public static Context ctx = null;

    public MemoryCache memoryCache;

    public static int SEND_PICKUP_REQUEST = 5;

    public boolean isInListOfcategory = false;

    private SearchDriversTempData searchDriversTempData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        searchDriversTempData = StorageDataHelper.getInstance(getActivity()).getSearchDriversCurrentDataForRequest();

        View v;
        v = inflater.inflate(R.layout.activity_setting_my_quotes_fragment, container, false);

        ;

        if(StorageDataHelper.getInstance(getActivity()).getDriversDatasNow() != null) {
            setData(StorageDataHelper.getInstance(getActivity()).getDriversDatasNow());
        }
        if(StorageDataHelper.getInstance(getActivity()).getDriversDatasLater() != null) {
            setCategoryData(StorageDataHelper.getInstance(getActivity()).getDriversDatasLater());
        }

        lastSelectedTab = R.id.column_1_ride_all;
        //selectedCarStyle = ALL_VCLASS;
        clearASCParams();

        if(MyQuotesFragment.ctx == null) {
            MyQuotesFragment.ctx = getActivity();
        }

        initView(v);

        memoryCache = new MemoryCache();

        return v;
    }

    @Override
    public void onResume() {

        searchDriversTempData = StorageDataHelper.getInstance(getActivity()).getSearchDriversCurrentDataForRequest();
        super.onResume();
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if(isInListOfcategory && Const.bookingTime.equalsIgnoreCase("later")) {
                        isInListOfcategory = false;
                        createCategoryTable();
                        return true;
                    }

                    return false;
                }
                return false;
            }
        } );

    }

    TextView rideColumnHeader;
    ImageView locationColumnHeader;
    TextView riderRatingColumnHeader;
    TextView fareColumnHeader;
    private View initByTripNowTable(View.OnClickListener columnClick, View header) {
        rideColumnHeader = (TextView)header.findViewById(R.id.ride_column_header);
        rideColumnHeader.setOnClickListener(columnClick);
        locationColumnHeader = (ImageView)header.findViewById(R.id.location_column_header);
        locationColumnHeader.setOnClickListener(columnClick);
        riderRatingColumnHeader = (TextView)header.findViewById(R.id.rider_rating_header);
        riderRatingColumnHeader.setOnClickListener(columnClick);
        fareColumnHeader = (TextView)header.findViewById(R.id.fare_column_header);
        fareColumnHeader.setOnClickListener(columnClick);
        return header;
    }

    TextView rideColumnHeaderByHourNow;
    TextView hourColumnHeaderByHourNow;
    TextView perHourColumnHeaderByHourNow;
    TextView fareColumnHeaderByHourNow;
    private View initByHourNowTable(View.OnClickListener columnClick, View header) {
        rideColumnHeaderByHourNow = (TextView)header.findViewById(R.id.ride_column_header_by_hour_now);
        rideColumnHeaderByHourNow.setOnClickListener(columnClick);
        hourColumnHeaderByHourNow = (TextView)header.findViewById(R.id.hour_column_header);
        hourColumnHeaderByHourNow.setOnClickListener(columnClick);
        perHourColumnHeaderByHourNow = (TextView)header.findViewById(R.id.per_hour_column_header);
        perHourColumnHeaderByHourNow.setOnClickListener(columnClick);
        fareColumnHeaderByHourNow = (TextView)header.findViewById(R.id.fare_column_header_by_hour_now);
        fareColumnHeaderByHourNow.setOnClickListener(columnClick);
        return header;
    }

    LinearLayout headerByTripNow;
    LinearLayout headerByHourNow;
    LinearLayout headerCategoryLater;
    LinearLayout mainHeader;

    private void initView(View v) {
        quotesHeaderLayout = (LinearLayout) v.findViewById(R.id.quotes_data_header_layout);

        headerByTripNow = (LinearLayout) v.findViewById(R.id.quotes_header_layout_by_trip_now);
        headerByHourNow = (LinearLayout) v.findViewById(R.id.quotes_header_layout_by_hour_now);
        headerCategoryLater = (LinearLayout) v.findViewById(R.id.quotes_header_layout_category_later);

        mainHeader = (LinearLayout) v.findViewById(R.id.quotes_data_header_layout);

        sQuotes_data_table = (LinearLayout) v.findViewById(R.id.quotes_main_data_layout);

        View.OnClickListener columnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setQuoteTableColumn(view);
            }
        };

        initByTripNowTable(columnClick, v);
        initByHourNowTable(columnClick, v);
        initCategoryLaterTable(v);

        if(Const.bookingTime.equalsIgnoreCase("now")) {
            createTable();
        } else if(Const.bookingTime.equalsIgnoreCase("later")) {
            isInListOfcategory = false;
            createCategoryTable();
        }

    }

    private Dialog instrDlg;
    public void showInstructionDialog() {
        try {
            if (Pref.getValue(ctx, Const.IS_SHOW_INSTRUCTION_FOR_QUOTES_LIST, "").equalsIgnoreCase("1")) {
                return;
            }

            instrDlg = new Dialog(ctx);
            instrDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            instrDlg.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            instrDlg.setContentView(R.layout.popup_instruction_get_quotes);
            instrDlg.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            TextView ivCancelOk = (TextView) instrDlg.findViewById(R.id.skip_instruction_btn);
            final CheckBox checkBoxNotShow = (CheckBox) instrDlg.findViewById(R.id.popup_instr_checkbox_btn);

            ivCancelOk.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if (checkBoxNotShow.isChecked()) {
                            Pref.setValue(ctx, Const.IS_SHOW_INSTRUCTION_FOR_QUOTES_LIST, "1");
                        }

                        instrDlg.dismiss();
                    }
                    return false;
                }
            });

            instrDlg.setCanceledOnTouchOutside(false);
            instrDlg.show();
        } catch (Exception e) {

        }
    }

    private boolean isASC_name;
    private boolean isASC_distance;
    private boolean isASC_rating;
    private boolean isASC_fare;
    private boolean isASC_perhour;
    private boolean isASC_hour;
    private boolean isASC_capacity;

    private void clearASCParams() {
        isASC_name = true;
        isASC_distance = true;
        isASC_rating = true;
        isASC_fare = true;
        isASC_perhour = true;
        isASC_hour = true;
        isASC_capacity = true;
    }

    public int lastSelectedTab;
    private void setQuoteTableColumn(View view) {
        if(lastSelectedTab != view.getId()) {
            clearASCParams();
        }

        if(view.getId() == R.id.ride_column_header || view.getId() == R.id.ride_column_header_by_hour_now) {
            setQuoteTableColumnInActiveColor();
            rideColumnHeader.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            rideColumnHeaderByHourNow.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            nameSort(isASC_name);
            isASC_name = !isASC_name;
            createTable();
            lastSelectedTab = view.getId();
        } else if(view.getId() == R.id.location_column_header) {
            setQuoteTableColumnInActiveColor();
            locationColumnHeader.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            distanceSort(isASC_distance);
            isASC_distance = !isASC_distance;
            createTable();
            lastSelectedTab = view.getId();
        } else if( view.getId() == R.id.hour_column_header) {
            setQuoteTableColumnInActiveColor();
            hourColumnHeaderByHourNow.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            hourSort(isASC_hour);
            isASC_hour = !isASC_hour;
            createTable();
            lastSelectedTab = view.getId();
        } else if(view.getId() == R.id.rider_rating_header) {
            setQuoteTableColumnInActiveColor();
            riderRatingColumnHeader.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            ratingSort(isASC_rating);
            isASC_rating = !isASC_rating;
            createTable();
            lastSelectedTab = view.getId();
        } else if( view.getId() == R.id.per_hour_column_header) {
            setQuoteTableColumnInActiveColor();
            perHourColumnHeaderByHourNow.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            perhourSort(isASC_perhour);
            isASC_perhour = !isASC_perhour;
            createTable();
            lastSelectedTab = view.getId();
        } else if(view.getId() == R.id.fare_column_header || view.getId() == R.id.fare_column_header_by_hour_now ) {
            setQuoteTableColumnInActiveColor();
            fareColumnHeader.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            fareColumnHeaderByHourNow.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            fareSort(isASC_fare);
            isASC_fare = !isASC_fare;
            createTable();
            lastSelectedTab =view.getId();
        }
    }

    private void setQuoteTableColumnInActiveColor() {
        rideColumnHeader.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        rideColumnHeaderByHourNow.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        locationColumnHeader.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        hourColumnHeaderByHourNow.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        riderRatingColumnHeader.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        perHourColumnHeaderByHourNow.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        fareColumnHeaderByHourNow.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        fareColumnHeader.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
    }

    private List<SearchDriversData> original_adapter;
    private List<SearchDriversData> filtered_adapter;


    public void setData(List<SearchDriversData> list) {
        original_adapter = list;
        filtered_adapter = original_adapter;
    }

    private void nameSort(final boolean isASC) {
        filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }
        Collections.sort(filtered_adapter, new Comparator<SearchDriversData>() {
            @Override
            public int compare(SearchDriversData searchDriversData, SearchDriversData searchDriversData2) {
                int value = searchDriversData.VehicleShape.VehicleNumber.compareToIgnoreCase(searchDriversData2.VehicleShape.VehicleNumber);
                if(isASC) {
                    return value;
                } else {
                    return value * (-1);
                }
            }
        });
    }
    private void distanceSort(final boolean isASC) {
        filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }
        Collections.sort(filtered_adapter, new Comparator<SearchDriversData>() {
            @Override
            public int compare(SearchDriversData searchDriversData, SearchDriversData searchDriversData2) {
                if(StorageDataHelper.getInstance(getActivity()).getHourlyColumns()) {
                    if (searchDriversData.NumOfHours > searchDriversData2.NumOfHours) {
                        if (isASC) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else if (searchDriversData.NumOfHours == searchDriversData2.NumOfHours) {
                        return 0;
                    }
                } else {
                    if (searchDriversData.Distance.Value > searchDriversData2.Distance.Value) {
                        if (isASC) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else if (searchDriversData.Distance.Value == searchDriversData2.Distance.Value) {
                        return 0;
                    }
                }

                if (isASC) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }
    private void fareSort(final boolean isASC) {
        filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }
        Collections.sort(filtered_adapter, new Comparator<SearchDriversData>() {
            @Override
            public int compare(SearchDriversData searchDriversData, SearchDriversData searchDriversData2) {
                float fare1 = searchDriversData.Fare;
                float fare2 = searchDriversData2.Fare;
                if(fare1 > fare2) {
                    if(isASC) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(fare1 == fare2) {
                    return 0;
                }

                if(isASC) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }
    private void ratingSort(final boolean isASC) {
        filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }
        Collections.sort(filtered_adapter, new Comparator<SearchDriversData>() {
            @Override
            public int compare(SearchDriversData searchDriversData, SearchDriversData searchDriversData2) {
                if(StorageDataHelper.getInstance(getActivity()).getHourlyColumns()) {
                    if (searchDriversData.Rating > searchDriversData2.Rating) {
                        if (isASC) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else if (searchDriversData.Rating == searchDriversData2.Rating) {
                        return 0;
                    }
                } else {
                    if (searchDriversData.Rating > searchDriversData2.Rating) {
                        if (isASC) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else if (searchDriversData.Rating == searchDriversData2.Rating) {
                        return 0;
                    }
                }

                if (isASC) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }
    private void tipSort(final boolean isASC) {
        filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }
        Collections.sort(filtered_adapter, new Comparator<SearchDriversData>() {
            @Override
            public int compare(SearchDriversData searchDriversData, SearchDriversData searchDriversData2) {
                if(searchDriversData.IsTipIncluded && searchDriversData2.IsTipIncluded == false) {
                    if(isASC) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(searchDriversData.IsTipIncluded == searchDriversData2.IsTipIncluded) {
                    return 0;
                }

                if(isASC) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }
    private void perhourSort(final boolean isASC) {
        filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }
        Collections.sort(filtered_adapter, new Comparator<SearchDriversData>() {
            @Override
            public int compare(SearchDriversData searchDriversData, SearchDriversData searchDriversData2) {
                if(searchDriversData.HourlyRate > searchDriversData2.HourlyRate) {
                    if(isASC) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(searchDriversData.HourlyRate == searchDriversData2.HourlyRate) {
                    return 0;
                }

                if(isASC) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }
    private void hourSort(final boolean isASC) {
        filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }
        Collections.sort(filtered_adapter, new Comparator<SearchDriversData>() {
            @Override
            public int compare(SearchDriversData searchDriversData, SearchDriversData searchDriversData2) {
                if(searchDriversData.NumOfHours  > searchDriversData2.NumOfHours) {
                    if(isASC) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(searchDriversData.NumOfHours == searchDriversData2.NumOfHours) {
                    return 0;
                }

                if(isASC) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult_check", "RequestCode: " + requestCode + "; ResultCode: " + resultCode + " run in MyQuotesFragment");
        if (requestCode == SEND_PICKUP_REQUEST) {
                getActivity().finish();
        }

        if (requestCode == ENTER_COUPON_ACTIVITY) {
            if (resultCode == getActivity().RESULT_OK) {
                couponCode = (VerfiyCouponData)data.getSerializableExtra(Const.COUPON_VALID_NAME);
                if (couponCode != null) {
                    if(haveCouponClicked!=null) {
                        try {

                            /*int currentIndex = (int)haveCouponClicked.getTag();
                            if(currentIndex < filtered_adapter.size() && currentIndex > -1) {
                                filtered_adapter.get(currentIndex).applyCouponCode(getActivity().getBaseContext(), couponCode);
                                createTable();
                            }*/

                        } catch (Exception e) {}
                    }
                }
            } else {
                couponCode = null;
            }

            haveCouponClicked = null;
        }
    }

    public void imageAsyncLoaded(String url, Bitmap bmp) {
        if(memoryCache!=null) {
            memoryCache.put(url, bmp);
        }
    }

    TextView haveCouponClicked;
    private VerfiyCouponData couponCode;

    private void createTable() {
        Log.d(TAG, "Create Table");
        sQuotes_data_table.removeAllViews();

            if(searchDriversTempData.tripType == Const.BY_THE_DURATION_OF_TRIP) {
                headerByTripNow.setVisibility(View.VISIBLE);
                headerByHourNow.setVisibility(View.GONE);
                headerCategoryLater.setVisibility(View.GONE);
            } else {
                headerByHourNow.setVisibility(View.VISIBLE);
                headerByTripNow.setVisibility(View.GONE);
                headerCategoryLater.setVisibility(View.GONE);
            }


            if(filtered_adapter == null) {
                return;
            }

            for (int current = 0; current < filtered_adapter.size(); current++) {
                final SearchDriversData driver = filtered_adapter.get(current);

                View.OnClickListener clickRow = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(getActivity(),
                                PickUpActivity.class).putExtra(
                                Const.SEARCH_DRIVER_DATA, driver), SEND_PICKUP_REQUEST);
                    }
                };

                if(Const.bookingTime.equalsIgnoreCase("now")) {
                    View itemByTripNow = null;
                    if (searchDriversTempData.tripType == Const.BY_THE_DURATION_OF_TRIP) {
                        itemByTripNow = View.inflate(getActivity(), R.layout.quotes_by_trip_now_item, null);
                    } else {
                        itemByTripNow = View.inflate(getActivity(), R.layout.quotes_by_hour_now_item, null);
                    }



                    itemByTripNow.setOnClickListener(clickRow);

                    int colorCell = 0;
                    if (driver.VehicleShape.Vclass.equalsIgnoreCase(CAR_SERVICE_VCLASS)) {
                        colorCell = Color.GRAY;
                    } else if (driver.VehicleShape.Vclass.equalsIgnoreCase(TAXI_VCLASS)) {
                        colorCell = Color.YELLOW;
                    } else if (driver.VehicleShape.Vclass.equalsIgnoreCase(SHUTTLE_VCLASS)) {
                        colorCell = Color.GREEN;
                    }
                    itemByTripNow.setBackgroundColor(colorCell);

                    NetworkImageView rideImage = (NetworkImageView) itemByTripNow.findViewById(R.id.ride_image_item_imageview);

                    if (searchDriversTempData.tripType == Const.BY_THE_DURATION_OF_TRIP) {
                        TextView locationDistanceValue = (TextView) itemByTripNow.findViewById(R.id.location_distance_value_textview);
                        TextView locationDistanceUnitValue = (TextView) itemByTripNow.findViewById(R.id.location_unit_value_textview);

                        locationDistanceValue.setText(Double.parseDouble(new DecimalFormat("##.##").format(driver.Distance.Value)) + "");
                        locationDistanceUnitValue.setText(driver.Distance.Unit);

                        TextView ratingBarValue = (TextView) itemByTripNow.findViewById(R.id.rating_bar_value);
                        RatingBar ratingBar = (RatingBar) itemByTripNow.findViewById(R.id.ratingBar);

                        ratingBarValue.setText(driver.Rating + "");
                        ratingBar.setRating(Float.parseFloat(driver.Rating + ""));
                    } else {
                        TextView hourDistanceValue = (TextView) itemByTripNow.findViewById(R.id.hour_value_textview);
                        hourDistanceValue.setText(driver.NumOfHours + "");

                        TextView perHourValue = (TextView) itemByTripNow.findViewById(R.id.per_hour_value_textview);
                        perHourValue.setText(driver.HourlyRate + "");
                    }

                    TextView fareValue = (TextView) itemByTripNow.findViewById(R.id.fare_value_textview);
                    TextView styleValue = (TextView) itemByTripNow.findViewById(R.id.style_value_textview);


                    rideImage.setImageUrl(driver.VehicleShape.ExteriorImage, VolleyLoader.getInstance(getActivity()).getImageLoader());
                    //ImageLoader.getInstance().displayImage(driver.VehicleShape.ExteriorImage, rideImage);

                    /*if (memoryCache != null && memoryCache.get(driver.VehicleShape.ExteriorImage) != null) {
                        rideImage.setImageBitmap(memoryCache.get(driver.VehicleShape.ExteriorImage));
                    } else {
                        new ImageDownloader(this, rideImage).execute(driver.VehicleShape.ExteriorImage);
                    }*/

                    fareValue.setText("$" + Utils.modifyFare(driver.Fare));
                    styleValue.setText(driver.VehicleShape.Vstyle);

                    sQuotes_data_table.addView(itemByTripNow);

                } else {

                    headerByHourNow.setVisibility(View.GONE);
                    headerByTripNow.setVisibility(View.GONE);
                    headerCategoryLater.setVisibility(View.GONE);

                    View itemByTripNow = View.inflate(getActivity(), R.layout.quotes_both_later_item, null);
                    itemByTripNow.setOnClickListener(clickRow);

                    TextView bootBtn = (TextView)itemByTripNow.findViewById(R.id.boot_btn);
                    bootBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), PickUpActivity.class);
                            intent.putExtra(Const.SEARCH_DRIVER_DATA, driver);
                            intent.putExtra(Const.REQUEST_FROM_CATEGORY_LATER, true);
                            intent.putExtra(Const.COUPON_CODE_INTENT_PARAM, couponCode);

                            startActivityForResult(intent, SEND_PICKUP_REQUEST);
                        }
                    });

                    final TextView haveCouponBtn = (TextView)itemByTripNow.findViewById(R.id.have_coupon_btn);
                    haveCouponBtn.setTag(current);
                    /*if(driver.isApplyValidCouponCode) {
                        haveCouponBtn.setText(getResources().getString(R.string.pickup_activity_coupon_applied));
                        haveCouponBtn.setTextColor(getResources().getColor(R.color.white));
                        haveCouponBtn.setBackgroundColor(getResources().getColor(R.color.green));
                        haveCouponBtn.setOnClickListener(null);
                    } else {*/
                        haveCouponBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                haveCouponClicked = (TextView) v;
                                startActivityForResult(new Intent(getActivity(), EnterCouponActivity.class)
                                        .putExtra(EnterCouponActivity.MERCHANT_ID_INTENT, driver.MerchantId), ENTER_COUPON_ACTIVITY);
                            }
                        });
                    //}

                    NetworkImageView imageView = (NetworkImageView)itemByTripNow.findViewById(R.id.image_of_vehicle);
                    imageView.setImageUrl(driver.VehicleShape.ExteriorImage, VolleyLoader.getInstance(getActivity()).getImageLoader());

                    TextView totalFare = (TextView)itemByTripNow.findViewById(R.id.total_fare_value_textview);
                    totalFare.setText("$" + Utils.modifyFare(driver.Fare));

                    if(driver.FareDetails != null && !driver.FareDetails.isEmpty()) {
                        LinearLayout areaFareTable = (LinearLayout)itemByTripNow.findViewById(R.id.area_for_fare_details_later);
                        for (int i=0; i < driver.FareDetails.size(); i++) {
                            View fareItem = View.inflate(getActivity(), R.layout.activity_pickup_dare_details_item, null);
                            fareItem.setPadding(0,5,0,0);

                            TextView fareDetails = (TextView) fareItem.findViewById(R.id.fare_description);
                            TextView fareValue = (TextView) fareItem.findViewById(R.id.fare_value);

                            fareDetails.setText(driver.FareDetails.get(i).FeeDescription+":");
                            fareValue.setText("$" + Utils.modifyFare(driver.FareDetails.get(i).Amount));
                            if(i == driver.FareDetails.size()-1) {
                                fareValue.setPaintFlags(fareValue.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            }

                            areaFareTable.addView(fareItem);
                        }
                    }

                    try {
                        TextView passangerView = (TextView) itemByTripNow.findViewById(R.id.category_number_of_pass_textview);
                        passangerView.setTextColor(Color.BLACK);
                        passangerView.setText(driver.VehicleShape.numberOfPass+"");
                        TextView suitcaseView = (TextView) itemByTripNow.findViewById(R.id.category_num_of_suitcases_textview);
                        suitcaseView.setTextColor(Color.BLACK);
                        suitcaseView.setText(driver.VehicleShape.numberOfSuitcase+"");
                    }catch (Exception e) {

                    }

                    sQuotes_data_table.addView(itemByTripNow);
                }
            }
    }



    // Category table function
    private List<SearchDriversLaterData> original_category_adapter;
    private List<SearchDriversLaterData> filtered_category_adapter;
    public void setCategoryData(List<SearchDriversLaterData> list) {
        original_category_adapter = list;
        filtered_category_adapter = original_category_adapter;
    }
    TextView rideColumnHeaderCategoryLater;
    TextView suitcaseColumnHeaderCategoryLater;
    TextView fareColumnHeaderCategoryLater;
    private void initCategoryLaterTable(View header) {
        View.OnClickListener columnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCategoryQuoteTableColumn(view);
            }
        };

        rideColumnHeaderCategoryLater = (TextView)header.findViewById(R.id.ride_column_header_category_later);
        rideColumnHeaderCategoryLater.setOnClickListener(columnClick);
        suitcaseColumnHeaderCategoryLater = (TextView)header.findViewById(R.id.suitcase_column_header_category_later);
        suitcaseColumnHeaderCategoryLater.setOnClickListener(columnClick);
        fareColumnHeaderCategoryLater = (TextView)header.findViewById(R.id.fare_column_header_category_later);
        fareColumnHeaderCategoryLater.setOnClickListener(columnClick);
    }
    private void rideCategorySort(final boolean isASC) {
        filtered_category_adapter = original_category_adapter;
        if(filtered_category_adapter == null) {
            return;
        }
        Collections.sort(filtered_category_adapter, new Comparator<SearchDriversLaterData>() {
            @Override
            public int compare(SearchDriversLaterData searchDriverData, SearchDriversLaterData searchDriverData2) {
                int value = searchDriverData.CategoryName.compareToIgnoreCase(searchDriverData2.CategoryName);
                if (isASC) {
                    return value;
                } else {
                    return value * (-1);
                }
            }
        });
    }
    private void suitcaseSort(final boolean isASC) {
        filtered_category_adapter = original_category_adapter;
        if(filtered_category_adapter == null) {
            return;
        }
        Collections.sort(filtered_category_adapter, new Comparator<SearchDriversLaterData>() {
            @Override
            public int compare(SearchDriversLaterData searchDriverData, SearchDriversLaterData searchDriverData2) {
                float fare1 = searchDriverData.numberOfPass;
                float fare2 = searchDriverData2.numberOfPass;
                if(fare1 > fare2) {
                    if(isASC) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(fare1 == fare2) {
                    return 0;
                }

                if(isASC) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    private void fareCategorySort(final boolean isASC) {
        filtered_category_adapter = original_category_adapter;
        if(filtered_category_adapter == null) {
            return;
        }
        Collections.sort(filtered_category_adapter, new Comparator<SearchDriversLaterData>() {
            @Override
            public int compare(SearchDriversLaterData searchDriverData, SearchDriversLaterData searchDriverData2) {
                float fare1 = searchDriverData.Fare;
                float fare2 = searchDriverData2.Fare;
                if(fare1 > fare2) {
                    if(isASC) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(fare1 == fare2) {
                    return 0;
                }

                if(isASC) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }
    boolean isASC_ride_category = false;
    boolean isASC_suitcases_category = false;
    boolean isASC_fare_category = false;
    private void clearASCCategoryParams() {
        isASC_ride_category = true;
        isASC_suitcases_category = true;
        isASC_fare_category = true;
    }
    private void setCategoryQuoteTableColumnInActiveColor() {
        rideColumnHeaderCategoryLater.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        suitcaseColumnHeaderCategoryLater.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        fareColumnHeaderCategoryLater.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
    }
    private int lastSelectedCategoryTab;
    private void setCategoryQuoteTableColumn(View view) {
        if(lastSelectedCategoryTab != view.getId()) {
            clearASCCategoryParams();
        }

        if(view.getId() == R.id.ride_column_header_category_later) {
            setCategoryQuoteTableColumnInActiveColor();
            rideColumnHeaderCategoryLater.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            rideCategorySort(isASC_ride_category);
            isASC_ride_category = !isASC_ride_category;
            createCategoryTable();
            lastSelectedCategoryTab = view.getId();
        } else if(view.getId() == R.id.suitcase_column_header_category_later) {
            setCategoryQuoteTableColumnInActiveColor();
            suitcaseColumnHeaderCategoryLater.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            suitcaseSort(isASC_suitcases_category);
            isASC_suitcases_category = !isASC_suitcases_category;
            createCategoryTable();
            lastSelectedCategoryTab = view.getId();
        } else if( view.getId() == R.id.fare_column_header_category_later) {
            setCategoryQuoteTableColumnInActiveColor();
            fareColumnHeaderCategoryLater.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            fareCategorySort(isASC_fare_category);
            isASC_fare_category = !isASC_fare_category;
            createCategoryTable();
            lastSelectedCategoryTab = view.getId();
        }
    }
    private void createCategoryTable() {
        sQuotes_data_table.removeAllViews();
        /*headerByTripNow.setVisibility(View.GONE);
        headerByHourNow.setVisibility(View.GONE);
        headerCategoryLater.setVisibility(View.VISIBLE);*/
        //LinearLayout header = (LinearLayout) getView().findViewById(R.id.quotes_data_header_layout);
        mainHeader.setVisibility(View.GONE);

        if(filtered_category_adapter == null) {
            return;
        }
        for (int current = 0; current < filtered_category_adapter.size(); current++) {
            final SearchDriversLaterData category = filtered_category_adapter.get(current);

            View itemCategoryLater = View.inflate(getActivity(), R.layout.quotes_category_later_item, null);

            View.OnClickListener clickRow = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isInListOfcategory = true;
                    for(int i=0; i < category.Vehicles.size();i++) {
                        category.Vehicles.get(i).VehicleShape.numberOfPass = category.numberOfPass;
                        category.Vehicles.get(i).VehicleShape.numberOfSuitcase = category.NumberOfSuitcases;
                    }
                    /*setData(category.Vehicles);
                    createTable();*/

                    Intent intent = new Intent(getActivity(), PickUpActivity.class);
                    ArrayList<SearchDriversData> list = (ArrayList<SearchDriversData>) category.Vehicles;
                    intent.putExtra(Const.SEARCH_DRIVERS_DATA, list);
                    startActivityForResult(intent, SEND_PICKUP_REQUEST);
                }
            };

            itemCategoryLater.setOnClickListener(clickRow);

            NetworkImageView rideImage = (NetworkImageView) itemCategoryLater.findViewById(R.id.category_image_item_imageview);
            rideImage.setImageUrl(category.StyleCategoryImage, VolleyLoader.getInstance(getActivity()).getImageLoader());


            View passangerSuitcaseBar = itemCategoryLater.findViewById(R.id.passangers_and_suitcase_counter_vertical_layout);
            TextView passengersValue = (TextView) itemCategoryLater.findViewById(R.id.category_number_of_pass_textview);
            passengersValue.setText(category.numberOfPass+"");
            TextView suitcasesValue = (TextView) itemCategoryLater.findViewById(R.id.category_num_of_suitcases_textview);
            suitcasesValue.setText(category.NumberOfSuitcases+"");

            TextView fareValue = (TextView) itemCategoryLater.findViewById(R.id.fare_category_value_textview);
            fareValue.setText("$" + Utils.modifyFare(category.Fare));
            TextView styleValue = (TextView) itemCategoryLater.findViewById(R.id.style_category_value_textview);
            styleValue.setText(category.CategoryName);

            sQuotes_data_table.addView(itemCategoryLater);
        }
    }
}
