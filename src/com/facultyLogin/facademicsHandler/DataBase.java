
package com.facultyLogin.facademicsHandler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataBase extends SQLiteOpenHelper {
	
	public static final String DB = "faculty_read";
	public static final String DB_post = "faculty_post";
	public static final String TB = "faculty_subList";
	public static final String C_CODE = "c_code";
	public static final String SUB_TITLE = "sub_name";
	public static final String Cl_Num = "class_num";
	public static final String Slot = "slot";
	public static final String Date = "date";
	public static final String Day = "day";
	public static final String Reg_Num = "reg_num";
	public static final String Stud_Name = "stud_name";
	public static final String Att_Val = "att_val";
	public static final int VERSION = 1;
	
	public final String createTB = "create table if not exists "+ TB +" ( "+
			Cl_Num + " text, "+
			C_CODE + " text, "+
			SUB_TITLE + " text, "+
			Slot + " text);";
	public final String delTB = "delete from " + TB +";";
	public final String insDefTB = "insert into " + TB + " values ('No Class', 'No Code', 'No Subject', 'No Slot');";

	public DataBase(Context context) {
		super(context, DB, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public void setDef(SQLiteDatabase db){
		db.execSQL(insDefTB);
	}
	
	public void delTB(SQLiteDatabase db){
		db.execSQL(delTB);
	}
	
	public void createDateTB(SQLiteDatabase db, String name){
		String dateTB = "create table if not exists "+ name +" ( "+
				Date + " text, "+
				Day + " text);";
		db.execSQL(dateTB);
	}
	
	public void delDateTB(SQLiteDatabase db, String name){
		String delt = "delete from " + name +";";
		db.execSQL(delt);
		Log.i("Alert", "Date Table Cleared");
	}
	
	public void createStTB(SQLiteDatabase db, String name){
		String stTB = "create table if not exists "+ name +" ( "+
				Reg_Num + " text, "+
				Stud_Name + " text);";
		db.execSQL(stTB);
	}
	
	public void delStTB(SQLiteDatabase db, String name){
		String delst = "delete from " + name +";";
		db.execSQL(delst);
	}
	
	public void createAttTB(SQLiteDatabase db, String name){
		String attTB = "create table if not exists "+ name +" ( "+
				Reg_Num + " text, "+
				Att_Val + " text);";
		db.execSQL(attTB);
	}
	
	public void delAttTB(SQLiteDatabase db, String name){
		String delatt = "delete from " + name +";";
		db.execSQL(delatt);
	}

}
