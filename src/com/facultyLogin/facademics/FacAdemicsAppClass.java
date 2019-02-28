package com.facultyLogin.facademics;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.facultyLogin.facademicsHandler.DataBase;

public class FacAdemicsAppClass extends Application {

	public static LoginPref loginPref;
	public static InitialPrefs initialPrefs;
	public static final String idkey = "Id", sKeyA="secureAttempt", passkey = "Password", valueKey = "ValueNull", dbKey = "dbValue", lastRefKey = "lastRefreshed", secureKey = "SKey";
	String id, pass, lastRef;
	DataBase dB;
	SQLiteDatabase sdB;
	
	@Override
	public void onCreate() {
		super.onCreate();
		loginPref = new LoginPref(getApplicationContext());
		initialPrefs = new InitialPrefs(getApplicationContext());
		
		id = loginPref.GetPreferences(idkey);
		pass = loginPref.GetPreferences(passkey);
		lastRef = loginPref.GetPreferences(lastRefKey);
		
		Toast.makeText(getApplicationContext(), "!!Welcome to FacAdemics!!", Toast.LENGTH_SHORT).show();
		
		if(id=="" || pass==""){
			initialPrefs.SavePreferences(valueKey , 1);
		}
		
		if(lastRef.equals("")){
			loginPref.SavePreferences(lastRefKey, "Never Refreshed");
		}

		dB = new DataBase(getApplicationContext());
		sdB = dB.getWritableDatabase();
		dB.onCreate(sdB);
		if(initialPrefs.GetPreferences(dbKey)==0){
			dB.setDef(sdB);
			Log.i("Alert", "FAC "+dbKey);
			initialPrefs.SavePreferences(dbKey , 1);
			Log.i("Alert", "FAC "+dbKey);
		}
	}
 public class LoginPref{
		//This preference holds values needed for authentication and some basic tasks
		private SharedPreferences login_pref;
		private Editor editor;
			
		public LoginPref(Context context) {
		this.login_pref = getSharedPreferences("Login", 0);
		this.editor = login_pref.edit();
		editor.commit();
		}

		public String GetPreferences(String key) {
		    return login_pref.getString(key, "");

		}

		public void SavePreferences(String key, String value) {
		editor.putString(key, value);    
		editor.commit();
		}
		}
 
 public class InitialPrefs{
		//This preference holds that values realted to attempts or an id wether a particular value is passed/stored before or not
		private SharedPreferences ini_pref;
		private Editor editor;
			
		public InitialPrefs(Context context) {
		this.ini_pref = getSharedPreferences("Initial", 0);
		this.editor = ini_pref.edit();
		editor.commit();
		}

		public void SavePreferences(String key, int value) {
			
			editor.putInt(key, value);    
			editor.commit();
			
		}

		public int GetPreferences(String key) {
		    return ini_pref.getInt(key, 0);

		}
		} 
}
