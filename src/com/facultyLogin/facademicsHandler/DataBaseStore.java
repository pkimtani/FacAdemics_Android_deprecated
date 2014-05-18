package com.facultyLogin.facademicsHandler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseStore extends SQLiteOpenHelper {
	
	public static final String DBS = "faculty_store";
	
	public DataBaseStore(Context context) {
		super(context, DBS, null, DataBase.VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public void createStoreTb(SQLiteDatabase sdb, String tbN){
		String createTb = "create table if not exists "+ tbN +" ( "+
			DataBase.Reg_Num + " text, "+
			DataBase.Att_Val + " text);";
		sdb.execSQL(createTb);
	}
	
	public void clearTable(SQLiteDatabase sdb, String tbN){
		String clearTb = "delete from " + tbN +";";
			sdb.execSQL(clearTb);
	}
	
}
