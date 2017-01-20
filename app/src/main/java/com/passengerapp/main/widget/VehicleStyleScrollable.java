package com.passengerapp.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.passengerapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by adventis on 11/14/14.
 */
public class VehicleStyleScrollable extends RelativeLayout {
    public VehicleStyleScrollable(Context context) {
        super(context);
        //--- Additional custom code --
        initElement();
    }

    public VehicleStyleScrollable(Context context, AttributeSet attrs) {
        super(context, attrs);
        //--- Additional custom code --
        initElement();
    }

    public VehicleStyleScrollable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initElement();
    }

    public ArrayList<Vehicle> vehicleList;
    public HashMap<String,Integer> vehicleList_map;
    private final String TAG = "VehicleStyleScrollable";


    private static String ANY = "Any";
    private static String SEDAN = "Sedan";
    private static String SUV = "SUV";
    private static String LIMOUSINE = "Limousine";
    private static String VAN = "Van";
    private static String MINI_BUS = "Mini Bus";
    private static String SHUTTLE = "Shuttle";
    private static String LIMO_BUS = "Limo Bus";
    private static String COACH_BUS = "Coach Bus";
    private static String STRETCHED_SUV = "Stretched SUV";
    private static String PARTY_BUS = "Party Bus";

    public static String[] getCarStyleList() {
        String[] carArray = {ANY, SEDAN, SUV, LIMOUSINE, VAN, MINI_BUS, SHUTTLE, LIMO_BUS, COACH_BUS, STRETCHED_SUV, PARTY_BUS };
        return carArray;
    }

    public void setData() {
        vehicleList = new ArrayList<Vehicle>();

        vehicleList.add(new Vehicle(R.drawable.any_style_vehicle2, ANY, 0));
        vehicleList.add(new Vehicle(R.drawable.sedan_style_vehicle2, SEDAN, 1));
        vehicleList.add(new Vehicle(R.drawable.suv_style_vehicle2, SUV, 2));
        vehicleList.add(new Vehicle(R.drawable.limo_style_vehicle2, LIMOUSINE, 3));
        vehicleList.add(new Vehicle(R.drawable.minibus_style_vehicle2, VAN, 4));
        vehicleList.add(new Vehicle(R.drawable.minibus_style_vehicle2, MINI_BUS, 5));
        vehicleList.add(new Vehicle(R.drawable.shuttle_style_vehicle2, SHUTTLE, 6));
        vehicleList.add(new Vehicle(R.drawable.limo_style_vehicle2, LIMO_BUS, 7));
        vehicleList.add(new Vehicle(R.drawable.coach_style_vehicle2, COACH_BUS, 8));
        vehicleList.add(new Vehicle(R.drawable.suv_style_vehicle2, STRETCHED_SUV, 2));
        vehicleList.add(new Vehicle(R.drawable.party_style_vehicle2, PARTY_BUS, 5));

        vehicleList_map = new HashMap<String, Integer>();
        vehicleList_map = getVehicleStyleList();


        showList();
    }

    private static HashMap<String, Integer> getVehicleStyleList() {
        HashMap<String, Integer> vehicle = new HashMap<String, Integer>();
        vehicle.put(ANY,R.drawable.any_style_vehicle2);
        vehicle.put(SEDAN,R.drawable.sedan_style_vehicle2);
        vehicle.put(SUV,R.drawable.suv_style_vehicle2);
        vehicle.put(LIMOUSINE, R.drawable.limo_style_vehicle2);
        vehicle.put(VAN, R.drawable.minibus_style_vehicle2);
        vehicle.put(MINI_BUS,R.drawable.minibus_style_vehicle2);
        vehicle.put(SHUTTLE,R.drawable.shuttle_style_vehicle2);
        vehicle.put(LIMO_BUS,R.drawable.limo_style_vehicle2);
        vehicle.put(COACH_BUS,R.drawable.coach_style_vehicle2);
        vehicle.put(STRETCHED_SUV, R.drawable.suv_style_vehicle2);
        vehicle.put(PARTY_BUS, R.drawable.party_style_vehicle2);

        return vehicle;
    }

    public static int getImageForVehicleStyle(String style) {
        HashMap<String, Integer> vehicle = getVehicleStyleList();
        for(String name : vehicle.keySet()){
            if(name.equalsIgnoreCase(style)) {
                return vehicle.get(name);
            }
        }

        return 0;
    }

    ImageView rightButton;
    ImageView leftButton;

    ImageView vehicle_left;
    TextView vehicle_txt_left;
    ImageView vehicle_center;
    TextView vehicle_txt_center;
    ImageView vehicle_right;
    TextView vehicle_txt_right;

    LinearLayout center_vehicle_layout;

    SliderLayout mDemoSlider;

    Animation animationSlideInLeft, animationSlideOutRight;

    public void initElement() {
        inflate(getContext(), R.layout.vehiclestylescrollable, this);

        rightButton = (ImageView)findViewById(R.id.right_button_scroll);
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickRight();
            }
        });
        leftButton = (ImageView)findViewById(R.id.left_button_scroll);
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLeft();
            }
        });

        vehicle_left = (ImageView)findViewById(R.id.vehicle_left);
        //vehicle_center = (ImageView)findViewById(R.id.vehicle_center);
        vehicle_right = (ImageView)findViewById(R.id.vehicle_right);

        vehicle_txt_right = (TextView)findViewById(R.id.vehicle_txt_right);
        vehicle_txt_center = (TextView)findViewById(R.id.vehicle_txt_center);
        vehicle_txt_left = (TextView)findViewById(R.id.vehicle_txt_left);

        center_vehicle_layout = (LinearLayout) findViewById(R.id.center_vehicle_layout);

        mDemoSlider = (SliderLayout) findViewById(R.id.center_vehicle_slider);

        setData();
    }

    public void setMyTripStyle() {
        vehicle_txt_right.setVisibility(INVISIBLE);
        vehicle_txt_center.setVisibility(INVISIBLE);
        vehicle_txt_left.setVisibility(INVISIBLE);
        leftButton.setVisibility(INVISIBLE);
        rightButton.setVisibility(INVISIBLE);
    }

    public String getCurrentVehicleName() {

        return mDemoSlider.getCurrentSlider().getDescription();
    }

    public int getCurrentVehicleId() {

        String description = mDemoSlider.getCurrentSlider().getDescription();
        for(int i=0; i<vehicleList.size(); i++){
            if(vehicleList.get(i).title.equalsIgnoreCase(description)) {
                return vehicleList.get(i).id;
            }
        }

        return 0;
    }


    public void clickRight() {
        mDemoSlider.mViewPager.nextItem();
    }

    public void showList() {
        for(String name : vehicleList_map.keySet()){
            TextSliderView textSliderView = new TextSliderView(getContext());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(vehicleList_map.get(name))
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView baseSliderView) {

                        }
                    });

            //add your extra information
            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        setStyle("Any");
    }

    public void setStyle(String styleName) {
        int i=0;
        for(String name : vehicleList_map.keySet()){

            Log.d(TAG, "Key: "+name+" Input: "+styleName+" i: "+i);
            if(name.equalsIgnoreCase(styleName)) {
                Log.d(TAG, "mDemoSlider.mViewPager.setCurrentItem(i)");
                mDemoSlider.mViewPager.setCurrentItem(i);
            }
            i++;
        }
    }

    public void clickLeft() {
        mDemoSlider.mViewPager.prevItem();
    }

    private class Vehicle {
        public String title;
        public int id;
        public int color;

        Vehicle(int color, String title, int id) {
            this.color = color;
            this.title = title;
            this.id = id;
        }
    }
}
