package com.passengerapp.util;

import android.content.Context;
import android.database.Cursor;
import com.passengerapp.data.LocationBean;

import java.util.ArrayList;

public class LocationBll {

	public Context context;

	public LocationBll(Context context) {
		this.context = context;
	}

	/*
	 * Insert Unit information
	 */
	public void insertPickLocation(LocationBean locationBean) {
		DBHelper dbHelper = null;
		String sql = null;

		try {
			sql = "INSERT INTO pick_location " + " (name, longitude, latitude)" + " VALUES ('"
					+ locationBean.name + "',"+locationBean.Longitude+","+locationBean.Latitude+")";

			dbHelper = new DBHelper(this.context);

            if(!isLocationInDatabase(dbHelper, locationBean.name, PICK_LOCATION)) {
                dbHelper.execute(sql);
            }

		} catch (Exception e) {
			e.printStackTrace();
		}

		// release
		dbHelper = null;
		sql = null;
	}

    public boolean isLocationInDatabase(DBHelper dbHelper, String value, int location_type) {
		Cursor itemLocation = null;
		if(location_type == END_LOCATION) {
			itemLocation = dbHelper.query("SELECT * FROM end_location WHERE name = '"+value+"'; ");
		} else {
			itemLocation = dbHelper.query("SELECT * FROM pick_location WHERE name = '"+value+"'; ");
		}

        if(itemLocation.getCount() == 0) {
           return false;
        }

        return true;
    }
	
	/*
	 * Insert Unit information
	 */
	private int END_LOCATION = 1;
	public void insertEndLocation(LocationBean locationBean) {
		DBHelper dbHelper = null;
		String sql = null;

		try {
			sql = "INSERT INTO end_location " + " (name, longitude, latitude)" + " VALUES ('"
					+ locationBean.name + "',"+locationBean.Longitude+","+locationBean.Latitude+")";

			dbHelper = new DBHelper(this.context);

            if(!isLocationInDatabase(dbHelper, locationBean.name, END_LOCATION)) {
                dbHelper.execute(sql);
            }

		} catch (Exception e) {
			e.printStackTrace();
		}

		// release
		dbHelper = null;
		sql = null;
	}

	
	/*
	 * Get PickUplocation hestory information
	 */
	private int PICK_LOCATION = 1;
	public ArrayList<LocationBean> getPickLocation() {
		DBHelper dbHelper = null;
		String sql = null;
		Cursor cursor = null;
		LocationBean locbean;
		ArrayList<LocationBean> locationList = new ArrayList<LocationBean>();
		try {

			sql = "SELECT pick_id,name,longitude,latitude  FROM pick_location group by name";
			dbHelper = new DBHelper(this.context);
			cursor = dbHelper.query(sql);

			if (cursor != null && cursor.getCount() > 0) {

				while (cursor.moveToNext()) {

					locbean = new LocationBean();
					locbean.id = cursor.getInt(0);
					locbean.name = cursor.getString(1);
					locbean.Longitude = cursor.getFloat(2);
					locbean.Latitude = cursor.getFloat(3);


					locationList.add(locbean);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			dbHelper = null;
			if (cursor != null && !cursor.isClosed())
				cursor.close();
		}

		// release
		dbHelper = null;
		sql = null;
		cursor = null;

		return locationList;
	}
	/*
	 * Get My Items information
	 */
	public ArrayList<LocationBean> getDestinationlocation() {
		DBHelper dbHelper = null;
		String sql = null;
		Cursor cursor = null;
		LocationBean locbean;
		ArrayList<LocationBean> locationList = new ArrayList<LocationBean>();
		try {

			sql = "SELECT end_id,name,longitude,latitude FROM end_location group by name";
			dbHelper = new DBHelper(this.context);
			cursor = dbHelper.query(sql);

			if (cursor != null && cursor.getCount() > 0) {

				while (cursor.moveToNext()) {

					locbean = new LocationBean();
					locbean.id = cursor.getInt(0);
					locbean.name = cursor.getString(1);
					locbean.Longitude = cursor.getFloat(2);
					locbean.Latitude = cursor.getFloat(3);

					locationList.add(locbean);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			dbHelper = null;
			if (cursor != null && !cursor.isClosed())
				cursor.close();
		}

		// release
		dbHelper = null;
		sql = null;
		cursor = null;

		return locationList;
	}
	
}
