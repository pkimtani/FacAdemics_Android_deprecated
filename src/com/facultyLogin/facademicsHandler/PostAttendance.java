package com.facultyLogin.facademicsHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.facultyLogin.facademics.AttendanceScreen;
import com.facultyLogin.facademics.FacAdemicsAppClass;

public class PostAttendance {
	
	String urlFinal, id, clNum, date, line, dateTB, AttTB, dt, mt, yr, det, regNum, attVal, data, out;
	Context context;
	URL url;
	StringBuilder sb;
	HttpURLConnection conn;
	InputStream is;
	BufferedReader reader;
	FragmentManager fm;
	FragmentTransaction ft;
	DataBaseStore dbs;
	DataBase db;
	SQLiteDatabase sldb, sldbs;
	Cursor cursorR, cursorWR, cursor;
	Boolean result, retRes;
	ProgressDialog dialog;
	
	public PostAttendance(Context c){
		this.context = c;
		id =  FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.idkey);
		db = new DataBase(context);
		sldb = db.getReadableDatabase();
		dbs = new DataBaseStore(context);
		sldbs = dbs.getReadableDatabase();
		cursorR = sldb.query(DataBase.TB, DateJSON.col, null, null, null, null, null);
		cursorR.moveToFirst();
		new PostData().execute();
	}
	
	public PostAttendance(Context c, String id, FragmentManager f)
	{
		this.context = c;
		this.id = id;
		this.fm = f;
		db = new DataBase(context);
		sldb = db.getReadableDatabase();
		dbs = new DataBaseStore(context);
		sldbs = dbs.getReadableDatabase();
		cursorR = sldb.query(DataBase.TB, DateJSON.col, null, null, null, null, null);
		cursorR.moveToFirst();
		new PostData().execute();
	}
	
	public boolean performPost(String d){
		urlFinal = "http://facademics-test.appspot.com/post/"+id+"/"+clNum+"/"+date+"/"+d;
		Log.i("Alert", urlFinal);
		try {
			url = new URL(urlFinal);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			
			is = conn.getInputStream();
			reader =new BufferedReader(new InputStreamReader(is));
		
			sb = new StringBuilder();
			
			while ((line = reader.readLine()) != null){
				sb.append(line);
			}
			out = sb.toString();
			Log.i("Alert", out);
			if(out.equals("s")){
				result = true;
				dbs.clearTable(sldbs, AttTB);
			}else{
				result = false;
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.i("Alert 2", e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("Alert 2", e.toString());
		}
		return result;
	}
	
	public String getData(String c, String d){
		d=refineDate(d);
		data="";
		AttTB = "Att_"+c+"_"+d;
		cursor = sldbs.query(AttTB, AttendanceScreen.colSA, null, null, null, null, null);
		cursor.moveToFirst();
		if(cursor.getCount()>0){
			regNum = cursor.getString(cursor.getColumnIndex(DataBase.Reg_Num));
			attVal = cursor.getString(cursor.getColumnIndex(DataBase.Att_Val));
			data = regNum+attVal;
			while(cursor.moveToNext()){
				regNum = cursor.getString(cursor.getColumnIndex(DataBase.Reg_Num));
				attVal = cursor.getString(cursor.getColumnIndex(DataBase.Att_Val));
				data = data+regNum+attVal;
			}
		}
		return data;
	}
	
	public String refineDate(String d){
		dt = d.substring(0, 2);
		mt = d.substring(3, 8);
		if(mt.equals("April")){
			mt="04";
			yr = d.substring(9, 13);
		}
		else{
			mt="05";
			yr = d.substring(7, 11);
		}
		det = dt+mt+yr;
		return det;
	}
	
	private class PostData extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setTitle("Uploading");
			dialog.setMessage("Saved Attendance..Please Wait...");
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			clNum = cursorR.getString(cursorR.getColumnIndex(DataBase.Cl_Num));
			dateTB = "date_"+clNum;
			cursorWR = sldb.query(dateTB, AttendanceJSON.colD, null, null, null, null, null);
			cursorWR.moveToFirst();
			date = cursorWR.getString(cursorWR.getColumnIndex(DataBase.Date));
			data = getData(clNum, date);
			Log.i("Alert", data);
			if(!data.equals("")){
				retRes = performPost(data);
			}
			while(cursorWR.moveToNext()){
				date = cursorWR.getString(cursorWR.getColumnIndex(DataBase.Date));
				data = getData(clNum, date);
				if(!data.equals("")){
					retRes = performPost(data);
				}
			}
			while(cursorR.moveToNext()){
				clNum = cursorR.getString(cursorR.getColumnIndex(DataBase.Cl_Num));
				dateTB = "date_"+clNum;
				cursorWR = sldb.query(dateTB, AttendanceJSON.colD, null, null, null, null, null);
				cursorWR.moveToFirst();
				date = cursorWR.getString(cursorWR.getColumnIndex(DataBase.Date));
				data = getData(clNum, date);
				if(!data.equals("")){
					retRes = performPost(data);
				}
				while(cursorWR.moveToNext()){
					date = cursorWR.getString(cursorWR.getColumnIndex(DataBase.Date));
					data = getData(clNum, date);
					if(!data.equals("")){
						retRes = performPost(data);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			cursorR.close();
			cursorWR.close();
			cursor.close();
			dialog.dismiss();
			new SubjectListJSON(context, id, fm);
			super.onPostExecute(result);
		}
		
		
		
	}
}
