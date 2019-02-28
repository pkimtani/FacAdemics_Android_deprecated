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
import android.widget.Toast;

import com.facultyLogin.facademicsFragments.SubjectListFragment;

public class StudentJSON {


	Context context;
	String id, urlFinal, urlFetch, cnum_stud, cnum, line;
	String[] col={DataBase.Cl_Num};
	public static final String Name_tag = "name";
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

	public StudentJSON(Context contextP, String id, FragmentManager f){
		this.fm = f;
		this.context = contextP;
		this.id = id;
		slf= new SubjectListFragment();
		dB = new DataBase(context);
		sdB = dB.getWritableDatabase();
		cursor = sdB.query(DataBase.TB, col, null, null, null, null, null);
		cursor.moveToFirst();
		limit = 1;
		while(cursor.moveToNext()){
			limit +=1;
		}
		limit *= 4;
		cursor.moveToFirst();
		new GetData().execute();
	}
	
	public String createURL(String c){
		cnum_stud="Student_"+c;
		dB.createStTB(sdB, cnum_stud);
		dB.delStTB(sdB, cnum_stud);
		urlFinal = "http://facademics-test.appspot.com/student/"+id+"/"+c;
		return urlFinal;
	}
	
	public class GetData extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setTitle("Updating");
			dialog.setMessage("Students Database..");
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			
			super.onPostExecute(result);
			if(result==true){
				dialog.dismiss();
				new AttendanceJSON(context, id, fm);
			}else{
				dialog.dismiss();
				Toast.makeText(context, "Oops Something Went Wrong, please retry later.", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected Boolean doInBackground(Void... URL) {
			try {
				
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
					String code = subJson.getString(AttendanceJSON.Reg_tag);
					String title = subJson.getString(Name_tag);
					sdB.execSQL("insert into " + cnum_stud + " values ('" + code + "', '" + title + "');");
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
						String code = subJson.getString(AttendanceJSON.Reg_tag);
						String title = subJson.getString(Name_tag);
						sdB.execSQL("insert into " + cnum_stud + " values ('" + code + "', '" + title + "');");
					}
					
				}
				cursor.close();
				
			} catch (MalformedURLException e) {
				return false;
			} catch (IOException e) {
				return false;
			} catch (JSONException e) {
				return false;
			}
			return true;
		}
		
	}
	
}
