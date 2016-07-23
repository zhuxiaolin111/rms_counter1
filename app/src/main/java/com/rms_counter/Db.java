package com.rms_counter;

import java.sql.Struct;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Db{
	private SQLiteDatabase db;
	private Context context;
	
	public Db(){
	}

	public void openDb(Context context) {
			db = context.openOrCreateDatabase("/sdcard/posmac.db", Context.MODE_PRIVATE,
				null);
	}

	public void closeDb() {
			db.close();
	}

	public void beginTransaction() {
		// TODO Auto-generated method stub
		db.beginTransaction();
	}

	public void execSQL(String string) {
		// TODO Auto-generated method stub
		db.execSQL(string, null);
	}

	public void setTransactionSuccessful() {
		// TODO Auto-generated method stub
		db.setTransactionSuccessful();
	}

	public void endTransaction() {
		// TODO Auto-generated method stub
		db.endTransaction();
	}

	public Cursor rawQuery(String string, Object object) {
		// TODO Auto-generated method stub
		Cursor c = db.rawQuery(string, null);
		return c;
	}
}
