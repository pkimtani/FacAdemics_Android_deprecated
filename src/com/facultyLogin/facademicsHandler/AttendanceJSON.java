package com.facultyLogin.facademicsHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.facultyLogin.facademics.FacAdemicsAppClass;
import com.facultyLogin.facademics.R;
import com.facultyLogin.facademicsFragments.SubjectListFragment;

public class AttendanceJSON {

	Context context;
	String id, urlFinal, AttTb, urlFetch, dt, mt, yr, det, cnum_date, cnum_stud, cnum, line, date, cnum_date_tb, output, sreg, att="Present";
	public static final String[] colD={DataBase.Date};
	String[] colS={DataBase.Reg_Num, DataBase.Stud_Name};
	public static final String Reg_tag = "regnum";
	public static final String Att_tag = "atten_val";
	JSONArray jsonObj = null;
	int limitC, limitD;
	ProgressDialog dialog;
	Cursor cursor, cursorD, cursorS;
	DataBase dB;
	DataBaseStore dbs;
	SQLiteDatabase sdB, sldb;
	SubjectListFragment slf;
	Boolean res;
	ContentValues cv;
	FragmentManager fm;
	StringBuilder sb;
	URL urlLink;
	HttpURLConnection conn;
	InputStream is;
	BufferedReader reader;
	
	public AttendanceJSON(Context contextP, String id, FragmentManager f){
		this.fm = f;
		this.context = contextP;
		this.id = id;
		slf= new SubjectListFragment();
		dB = new DataBase(context);
		sdB = dB.getWritableDatabase();
		dbs = new DataBaseStore(context);
		sldb = dbs.getWritableDatabase();
		cursor = sdB.query(DataBase.TB, DateJSON.col, null, null, null, null, null);		
		cursor.moveToFirst();
		new GetData().execute();
	}
	
	public String createURL(String c, String d){
		det=refineDate(d);
		AttTb = "Att_"+c+"_"+det;
		dbs.createStoreTb(sldb, AttTb);
		cnum_date_tb= "date_"+c+"_"+det;
		dB.createAttTB(sdB, cnum_date_tb);
		dB.delAttTB(sdB, cnum_date_tb);
		det = dt+"-"+mt+"-"+yr;
		urlFinal = "http://facademics-test.appspot.com/attendance/"+id+"/"+c+"/"+det;
		return urlFinal;
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
	
	public boolean storeData(String cnum, String  date){
		urlFetch = createURL(cnum, date);
		try {
			urlLink = new URL(urlFetch);
			conn = (HttpURLConnection) urlLink.openConnection();
			conn.connect();
			
			is = conn.getInputStream();
			reader =new BufferedReader(new InputStreamReader(is));
	
			sb = new StringBuilder();
		
			while ((line = reader.readLine()) != null){
				sb.append(line);
			}
		
			output = sb.toString();

			if(!output.equals("ta")){
				jsonObj = new JSONArray(sb.toString());
				
				for(int i = 0; i<jsonObj.length(); i++){
					
					JSONObject subJson = jsonObj.getJSONObject(i);
					String code = subJson.getString(Reg_tag);
					String title = subJson.getString(Att_tag);
					if(title.equals("present")){
						title = "Present";
					}else{
						title = "Absent";
					}
					sdB.execSQL("insert into " + cnum_date_tb + " values ('" + code + "', '" + title + "');");
				}	
			}
		}catch (MalformedURLException e) {
			return false;
		} catch (JSONException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public class GetData extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setTitle("Updating");
			dialog.setMessage("Attendance Database..");
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			cursor.close();
			cursorD.close();
			super.onPostExecute(result);
			if(result==true){
				dialog.dismiss();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.homeFrame, slf, "Sub");
				ft.commit();
				FacAdemicsAppClass.initialPrefs.SavePreferences(FacAdemicsAppClass.dbKey, 2);
				String currentDate = new SimpleDateFormat("dd-MMM-yyyy h:m", Locale.getDefault()).format(new Date());
				FacAdemicsAppClass.loginPref.SavePreferences(FacAdemicsAppClass.lastRefKey, currentDate);
			}else{
				dialog.dismiss();
				Toast.makeText(context, "Oops Something Went Wrong, please retry later.", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected Boolean doInBackground(Void... URL) {
				cnum = cursor.getString(cursor.getColumnIndex(DataBase.Cl_Num));
				cnum_date="date_"+cnum;
				cnum_stud = "Student_"+cnum;
				
				cursorD = sdB.query(cnum_date, colD, null, null, null, null, null);
				cursorD.moveToFirst();
				
				date = cursorD.getString(cursorD.getColumnIndex(DataBase.Date));
				res = storeData(cnum, date);
				if(res==false){
					return false;
				}
				while(cursorD.moveToNext()){
					date = cursorD.getString(cursorD.getColumnIndex(DataBase.Date));
					res = storeData(cnum, date);
					if(res==false){
						return false;
					}
				}
				while(cursor.moveToNext()){
					cnum = cursor.getString(cursor.getColumnIndex(DataBase.Cl_Num));
					cnum_date="date_"+cnum;
					cnum_stud = "Student_"+cnum;
									
					cursorD = sdB.query(cnum_date, colD, null, null, null, null, null);
					cursorD.moveToFirst();
					
					date = cursorD.getString(cursorD.getColumnIndex(DataBase.Date));
					res = storeData(cnum, date);
					if(res==false){
						return false;
					}
					while(cursorD.moveToNext()){
						date = cursorD.getString(cursorD.getColumnIndex(DataBase.Date));
						res = storeData(cnum, date);
						if(res==false){
							return false;
						}
					}
				}
			return res;
		}
	}
}