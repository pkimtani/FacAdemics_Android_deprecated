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
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.facultyLogin.facademics.FacAdemicsAppClass;
import com.facultyLogin.facademicsFragments.SubjectListFragment;

public class SubjectListJSON {
	
	Context context;
	String id, urlFinal;
	public static final String Class_num_tag = "class number";
	public static final String Code_tag = "code";
	public static final String Title_tag = "name";
	public static final String Slot_tag = "slot";
	JSONArray jsonObj = null;
	ProgressDialog dialog;
	DataBase dB;
	SQLiteDatabase sdB;
	SubjectListFragment slf;
	ContentValues cv;
	FragmentManager fm;

	public SubjectListJSON(Context contextP, String id, FragmentManager f){
		this.fm = f;
		this.context = contextP;
		this.id = id;
		slf = new SubjectListFragment();
		urlFinal = "http://facademics-test.appspot.com/subject/"+id;
		new GetData().execute(urlFinal);
		dB = new DataBase(context);
		sdB = dB.getWritableDatabase();
		dB.delTB(sdB);
		
	}
	
	public class GetData extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setTitle("Updating");
			dialog.setMessage("Subject List..");
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result==true){
				dialog.dismiss();
				new DateJSON(context, id, fm);
				FacAdemicsAppClass.initialPrefs.SavePreferences(FacAdemicsAppClass.dbKey, 1);
			}else{
				dialog.dismiss();
				Toast.makeText(context, "Oops Something Went Wrong, please retry later.", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected Boolean doInBackground(String... URL) {
			String urlFetch = URL[0];
			
			try {
				URL urlLink = new URL(urlFetch);
				
				HttpURLConnection conn = (HttpURLConnection) urlLink.openConnection();
				conn.connect();
				
				InputStream is = conn.getInputStream();
				BufferedReader reader =new BufferedReader(new InputStreamReader(is));
				
				StringBuilder sb = new StringBuilder();
				String line;
				
				while ((line = reader.readLine()) != null){
					sb.append(line);
				}
				
				jsonObj = new JSONArray(sb.toString());
				
				
				for(int i = 0; i<jsonObj.length(); i++){
					
					JSONObject subJson = jsonObj.getJSONObject(i);
					String code = subJson.getString(Code_tag);
					String title = subJson.getString(Title_tag);
					String class_num = subJson.getString(Class_num_tag);
					String slot = subJson.getString(Slot_tag);
					sdB.execSQL("insert into " + DataBase.TB + " values ('" + class_num + "', '" + code + "', '" + title + "', '" + slot + "');");
				}
				
				
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
