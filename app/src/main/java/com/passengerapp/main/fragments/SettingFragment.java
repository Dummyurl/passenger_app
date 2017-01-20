package com.passengerapp.main.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.passengerapp.R;
import com.passengerapp.main.dialogs.SetCreditCardDataDialog;
import com.passengerapp.main.uc.WheelActivity;
import com.passengerapp.util.Const;
import com.passengerapp.util.Pref;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.util.ArrayList;

/**
 * Created by adventis on 11/9/14.
 */
public class SettingFragment extends Fragment {

    private ImageView imageViewSignal;
    private ToggleButton toggleButtonGps;
    private TextView txtsignalStrnt;
    private TextView toggleButtonGps_title;
    private LinearLayout llAdvancedPaymnts;
    private TextView txtAdvancedPaymnts;
    TextView map_direction;
    TextView radius;
    TextView unit;

    private Intent openWheel;
    private Utils.GPSReception reception;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v;
        v = inflater.inflate(R.layout.activity_setting_setting_frgment, container, false);
        initView(v);
        setInitValue();

        return v;
    }

    private void initView(View v) {
        // gps panel
        imageViewSignal = (ImageView) v.findViewById(R.id.imageViewSignal);
        txtsignalStrnt = (TextView) v.findViewById(R.id.txtsignalStrnt);

        toggleButtonGps = (ToggleButton) v.findViewById(R.id.toggleButtonGps);
        toggleButtonGps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Pref.setValue(getActivity(), Const.GPS_STATUS, "1");
//                    Utils.GPSReception reception = Utils.getGPSReception(LocationManagerHelper.getInstance(getActivity()).getLastAccuracy());
                    Utils.GPSReception reception = Utils.GPSReception.NONE;
                    if(StorageDataHelper.getInstance(getActivity()).getLatestLocation() != null)
                        Utils.getGPSReception(StorageDataHelper.getInstance(getActivity()).getLatestLocation().getAccuracy());

                    txtsignalStrnt.setText(Utils.toGetTextValue(reception));
                    // reception
                    Utils.turnGPSOn(getActivity());
                    int res = Utils.toImageResource(reception);
                    imageViewSignal.setImageResource(res);

                    toggleButtonGps_title.setText("Active");
                } else {
                    Pref.setValue(getActivity(), Const.GPS_STATUS, "0");
                    Utils.turnGPSOff(getActivity());
                    imageViewSignal.setImageResource(R.drawable.setting_gpsno);
                    toggleButtonGps_title.setText("Inactive");
                }
            }
        });

        toggleButtonGps_title = (TextView) v.findViewById(R.id.toggleButtonGps_title);

        // preferences panel
        llAdvancedPaymnts = (LinearLayout) v.findViewById(R.id.llAdvancedPaymnts);
        txtAdvancedPaymnts = (TextView) v.findViewById(R.id.txtAdvancedPaymnts);
        txtAdvancedPaymnts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Pref.getValue(getActivity(), Const.REGI_PASSENEGRID,
                        "0").equals("0")) {
                    startActivity(new Intent(getActivity(),
                            SetCreditCardDataDialog.class));
                }
            }
        });

        map_direction = (TextView) v.findViewById(R.id.map_direction);
        map_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] map_directions = { "Keyboard Typing", "Voice Recognition" };

                openWheel = new Intent(getActivity(), WheelActivity.class);
                openWheel.putExtra("content_array", map_directions);
                openWheel.putExtra("selected_value", map_direction.getText()
                        .toString());
                openWheel.putExtra("tag", 2);
                startActivityForResult(openWheel, 1);
            }
        });

        radius = (TextView) v.findViewById(R.id.search_radius);
        radius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> radiuses = new ArrayList<String>();
                for (int i = 0; i <= 69; i++) {
                    radiuses.add((i + 1) + "");
                }
                String[] radiusArr = new String[radiuses.size()];
                radiusArr = radiuses.toArray(radiusArr);

                openWheel = new Intent(getActivity(), WheelActivity.class);
                openWheel.putExtra("content_array", radiusArr);
                openWheel.putExtra("selected_value", radius.getText().toString());
                openWheel.putExtra("tag", 3);
                startActivityForResult(openWheel, 1);
            }
        });

        unit = (TextView) v.findViewById(R.id.unit);
        unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] unites = { "mile", "Km" };
                openWheel = new Intent(getActivity(), WheelActivity.class);
                openWheel.putExtra("content_array", unites);
                openWheel.putExtra("selected_value", unit.getText().toString());
                openWheel.putExtra("tag", 4);
                startActivityForResult(openWheel, 1);
            }
        });
    }

    public void setInitValue() {
        if (Pref.getValue(getActivity(), Const.GPS_STATUS, "1").equals(
                "1")) {
//            reception = Utils.getGPSReception(LocationManagerHelper.getInstance(getActivity()).getLastAccuracy());
            reception = Utils.GPSReception.NONE;
            if(StorageDataHelper.getInstance(getActivity()).getLatestLocation() != null)
                reception = Utils.getGPSReception(StorageDataHelper.getInstance(getActivity()).getLatestLocation().getAccuracy());

            // reception
            Utils.turnGPSOn(getActivity());

            // String rec = toString(reception);
            int res = Utils.toImageResource(reception);

            txtsignalStrnt.setText(Utils.toGetTextValue(reception));
            imageViewSignal.setImageResource(res);
            toggleButtonGps.setChecked(true);
        }

        /*map_direction.setText(Pref.getValue(getActivity(),Const.MAP_DIRECTION, "Keyboard Typing"));*/
        radius.setText(Pref.getValue(getActivity(), Const.RADIUS, (Const.DefaultSearchRadius != -1) ? Const.DefaultSearchRadius+"":"5"));
        unit.setText(Pref.getValue(getActivity(), Const.UNIT, "mile"));

        String paymentNo = Pref.getValue(getActivity(), Const.CC_NUMBER,
                "");
        if (paymentNo != null && !paymentNo.equals("")) {
            SpannableString paymentContent = new SpannableString(
                    "XXXX XXXXXX X"
                            + paymentNo.substring(paymentNo.length() - 4,
                            paymentNo.length()));
            paymentContent.setSpan(new UnderlineSpan(), 0,
                    ("XXXX XXXXXX X" + paymentNo.substring(
                            paymentNo.length() - 4, paymentNo.length()))
                            .length(), 0);
            txtAdvancedPaymnts.setText(paymentContent);
        } else {
            SpannableString paymentContent = new SpannableString(
                    Utils.getStringById(R.string.settings_fragment_update_cc));
            paymentContent.setSpan(new UnderlineSpan(), 0,
                    (Utils.getStringById(R.string.settings_fragment_update_cc)).length(), 0);
            txtAdvancedPaymnts.setText(paymentContent);
        }
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) { // success
            switch (data.getIntExtra("tag", 0)) {
                case 2:
                    map_direction.setText(data.getStringExtra("selected_value"));
                    Pref.setValue(getActivity(), Const.MAP_DIRECTION,
                            map_direction.getText().toString());
                    break;
                case 3:
                    radius.setText(data.getStringExtra("selected_value"));
                    Pref.setValue(getActivity(), Const.RADIUS, radius
                            .getText().toString());
                    break;
                case 4:
                    unit.setText(data.getStringExtra("selected_value"));
                    Pref.setValue(getActivity(), Const.UNIT, unit.getText()
                            .toString());
                    break;

            }
        }
    }*/
}
