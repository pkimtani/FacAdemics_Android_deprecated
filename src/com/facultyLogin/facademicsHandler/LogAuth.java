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
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.facultyLogin.facademics.FacAdemicsAppClass;

public class LogAuth {
	
	String id, pass, idkey = "Id", passkey = "Password", responseText, out, data, urlFinal;
	int fac;
	ProgressDialog dialog;
	Context context;
	FragmentManager fm;
	public LogAuth(Context contextP, FragmentManager f){
		this.fm = f;
		this.context = contextP;
		id = FacAdemicsAppClass.loginPref.GetPreferences(idkey);
		fac = FacAdemicsAppClass.initialPrefs.GetPreferences(FacAdemicsAppClass.dbKey);
		pass = FacAdemicsAppClass.loginPref.GetPreferences(passkey);
		dialog = new ProgressDialog(context);
		dialog.setTitle("Loading Data");
		dialog.setMessage("Please Wait");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		urlFinal = "http://facademics-test.appspot.com/login/" + id + "/" + pass;
		new GoLogin().execute(urlFinal);
	}
	
	public class GoLogin extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... URL) {
			
			String loginUrl = URL[0];
			
			try {
				URL urlLink = new URL(loginUrl);
				
				HttpURLConnection conn = (HttpURLConnection) urlLink.openConnection();
				conn.connect();
				
				InputStream is = conn.getInputStream();
				BufferedReader reader =new BufferedReader(new InputStreamReader(is));
				
				while ((data = reader.readLine()) != null){
					out = data;
				}
				Log.i("Alert", "Output: "+out);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				Log.i("Alert", "Excption: "+e);
			} catch (IOException e) {
				e.printStackTrace();
				Log.i("Alert", "Excption: "+e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			responseText = out;
			if(out.equals("")){
				Toast.makeText(context, "Oops Something Went Wrong, we are extremely sorry, please kill and restart app.", Toast.LENGTH_SHORT).show();
			}
			if(responseText.equals("success")){
				if(fac>1)
					new PostAttendance(context, id, fm);
				else
					new SubjectListJSON(context, id, fm);
			}
			else if(responseText.equals("invalid"))
				Toast.makeText(context, "User ID or Password is invalid", Toast.LENGTH_SHORT).show();
			else if(responseText.equals("soe"))
				Toast.makeText(context, "Some Error Occured. Sorry for incovenience, retry after sommetime", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(context, "Oops Something Went Wrong, we are extremely sorry, please kill and restart app.", Toast.LENGTH_SHORT).show();
			dialog.dismiss();
			super.onPostExecute(result);
		}
	}
}
