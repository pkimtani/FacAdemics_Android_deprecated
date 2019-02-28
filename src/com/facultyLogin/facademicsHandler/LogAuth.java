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
	
	String id, pass, responseText, out, data, urlFinal, key;
	int fac;
	ProgressDialog dialog;
	Context context;
	FragmentManager fm;
//String captcha is removed from constructor.
	public LogAuth(Context contextP, FragmentManager f){
		this.fm = f;
		this.context = contextP;
		dialog = new ProgressDialog(context);
		dialog.setTitle("Performing Login");
		dialog.setMessage("Validating User..Please Wait...");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		id = FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.idkey);
		fac = FacAdemicsAppClass.initialPrefs.GetPreferences(FacAdemicsAppClass.dbKey);
		pass = FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.passkey);
		key = FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.secureKey);
		urlFinal = "http://facademics-test.appspot.com/login/" + id + "/" + pass + key;
		new GoLogin().execute(urlFinal);
	}
	public LogAuth(Context contextP, FragmentManager f, ProgressDialog d){
		this.fm = f;
		this.context = contextP;
		id = FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.idkey);
		fac = FacAdemicsAppClass.initialPrefs.GetPreferences(FacAdemicsAppClass.dbKey);
		pass = FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.passkey);
		key = FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.secureKey);
		this.dialog = d;
		//captcha parameter is removed from url below
		urlFinal = "http://facademics-test.appspot.com/login/" + id + "/" + pass + "/" + key;
		new GoLogin().execute(urlFinal);
	}
	
	private class GoLogin extends AsyncTask<String, Void, String>{

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
			} catch (MalformedURLException e) {
				Log.i("Alert", "Excption: "+e);
				return out = e.toString();
			} catch (IOException e) {
				Log.i("Alert", "Excption: "+e);
				return out = e.toString();
			}
			return out;
		}
		
		@Override
		protected void onPostExecute(String result) {
			responseText = result;
			dialog.dismiss();
			if(responseText.equals("")){
				Toast.makeText(context, "Oops Something Went Wrong, please retry later.", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(context, "Oops Something Went Wrong, please retry later.", Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}
	}
}
