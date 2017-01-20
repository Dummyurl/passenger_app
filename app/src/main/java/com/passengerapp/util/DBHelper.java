package com.passengerapp.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public Context context;

	public DBHelper(Context context) {
		super(context, Const.DB_NAME, null, 33);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void execute(String statment) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL(statment);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
			db = null;
		}
	}

	public Cursor query(String statment) {
		Cursor cur = null;
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			
			cur = db.rawQuery(statment, null);
			cur.moveToPosition(-1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.close();
			db = null;
		}

		return cur;
	}

	public static String getDBStr(String str) {

		str = str != null ? str.replaceAll("'", "''") : null;
		str = str != null ? str.replaceAll("&#039;", "''") : null;
		str = str != null ? str.replaceAll("&amp;", "&") : null;

		return str;

	}

	public void upgrade(int level) {
		switch (level) {
		case 0:
			doUpdate1();
		case 1:
			// doUpdate2();
		case 2:
			// doUpdate3();
		case 3:
			// doUpdate4();
		}
	}

	private void doUpdate1() {

		this.execute("CREATE TABLE pick_location ( "
				+ "pick_id INTEGER PRIMARY KEY AUTOINCREMENT," + "name TEXT,"+"longitude FLOAT,"+"latitude FLOAT)");

		this.execute("CREATE TABLE end_location ( "
				+ "end_id INTEGER PRIMARY KEY AUTOINCREMENT," + "name TEXT,"+"longitude FLOAT,"+"latitude FLOAT)");
	}
}
