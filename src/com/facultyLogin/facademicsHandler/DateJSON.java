package com.facultyLogin.facademicsHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.util.Log;

import com.facultyLogin.facademicsFragments.SubjectListFragment;

public class DateJSON {

	Context context;
	String id, urlFinal, urlFetch, cnum_date, cnum, line;
	public static final String[] col={DataBase.Cl_Num};
	public static final String Date_tag = "date";
	public static final String Day_tag = "day";
	JSONArray jsonObj = null;
	int limit;
	ProgressDialog dialog;
	Cursor cursor;
	DataBase dB;
	SQLiteDatabase sdB;
	SubjectListFragment slf;
	ContentValues cv;
	FragmentManager fm;
	StringBuilder sb;
	URL urlLink;
	HttpURLConnection conn;
	InputStream is;
	BufferedReader reader;

	public DateJSON(Context contextP, String id, FragmentManager f){
		this.fm = f;
		this.context = contextP;
		this.id = id;
		slf= new SubjectListFragment();
		dB = new DataBase(context);
		sdB = dB.getWritableDatabase();
		cursor = sdB.query(DataBase.TB, col, null, null, null, null, null);
		cursor.moveToFirst();
		new GetData().execute();
	}
	
	public String createURL(String c){
		Log.i("Alert 2", cnum);
		cnum_date="date_"+c;
		dB.createDateTB(sdB, cnum_date);
		dB.delDateTB(sdB, cnum_date);
		urlFinal = "http://facademics-test.appspot.com/date/"+id+"/"+c;
		return urlFinal;
	}
	
	public class GetData extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setTitle("Updating");
			dialog.setMessage("Dates Database..");
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			cursor.close();
			dialog.dismiss();
			new StudentJSON(context, id, fm);
			super.onPostExecute(result);
		}
		
		@Override
		protected Void doInBackground(Void... URL) {
			cnum = cursor.getString(cursor.getColumnIndex(DataBase.Cl_Num));
			urlFetch = createURL(cnum);
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
			
				jsonObj = new JSONArray(sb.toString());
						
			
				for(int i = 0; i<jsonObj.length(); i++){
				
					JSONObject subJson = jsonObj.getJSONObject(i);
					String code = subJson.getString(Date_tag);
					String title = subJson.getString(Day_tag);
					sdB.execSQL("insert into " + cnum_date + " values ('" + code + "', '" + title + "');");
				}
				
				while(cursor.moveToNext()){
					cnum = cursor.getString(cursor.getColumnIndex(DataBase.Cl_Num));
					urlFetch = createURL(cnum);
					urlLink = new URL(urlFetch);
					conn = (HttpURLConnection) urlLink.openConnection();
					conn.connect();
				
					is = conn.getInputStream();
					reader =new BufferedReader(new InputStreamReader(is));
				
					sb = new StringBuilder();
					
					while ((line = reader.readLine()) != null){
						sb.append(line);
					}
				
					jsonObj = new JSONArray(sb.toString());
							
				
					for(int i = 0; i<jsonObj.length(); i++){
					
						JSONObject subJson = jsonObj.getJSONObject(i);
						String code = subJson.getString(Date_tag);
						String title = subJson.getString(Day_tag);
						sdB.execSQL("insert into " + cnum_date + " values ('" + code + "', '" + title + "');");
					}
					
				}
			} catch (MalformedURLException e) {
				Log.i("Alert", "X: "+e);
				e.printStackTrace();
			} catch (IOException e) {
				Log.i("Alert", "X: "+e);
				e.printStackTrace();
			} catch (JSONException e) {
				Log.i("Alert", "X: "+e);
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
