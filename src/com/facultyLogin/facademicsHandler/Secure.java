package com.facultyLogin.facademicsHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
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

public class Secure {
	
	String id, pass, responseText, out, data, urlFinal;
	int fac;
	ProgressDialog dialog;
	Context context;
	FragmentManager fm;

	public Secure(Context contextP, FragmentManager f, String pass){
		this.fm = f;
		this.context = contextP;
		this.pass = pass;
		id = FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.idkey);
		dialog = new ProgressDialog(context);
		dialog.setTitle("Performing Login");
		dialog.setMessage("Validating User..Please Wait...");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		//captcha parameter is removed from url below
		urlFinal = "http://facademics-test.appspot.com/key/" + id;
		new GetKey().execute(urlFinal);
	}
	
	private class GetKey extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... urls) {
			String keyUrl = urls[0];
			try {
				URL urlLink = new URL(keyUrl);
				
				HttpURLConnection conn = (HttpURLConnection) urlLink.openConnection();
				conn.connect();
				
				InputStream is = conn.getInputStream();
				BufferedReader reader =new BufferedReader(new InputStreamReader(is));
				
				while ((data = reader.readLine()) != null){
					out = data;
				}
			} catch (MalformedURLException e) {
				return out="Error";
			} catch (IOException e) {
				return out="Error";
			}
			return out;
		}

		@Override
		protected void onPostExecute(String result) {
			if(out.equals("Error")){
				Toast.makeText(context, "Oops Something Went Wrong, please retry later.", Toast.LENGTH_SHORT).show();
			}else{
				new EncryptData().execute(out);
			}
			super.onPostExecute(result);
		}
		
	}
	
	private class EncryptData extends AsyncTask<String, Void, Void>{

		int outLen;
		String key;
		BigInteger bigKey, bigPass;
		
		private BigInteger getStringAscii(String s){
			BigInteger bi;
			StringBuilder sb = new StringBuilder();
			String sAs;
			s = s.trim();
			for(int i=0; i<s.length(); i++){
				sb.append((int) s.charAt(i));
			}
			sAs = sb.toString();
			bi = new BigInteger(sAs);
			return bi;
		}
		
		private BigInteger lenConverter(String s){
			BigInteger bi;
			StringBuilder sb = new StringBuilder();
			String sAs;
			int comp;
			s = s.trim();
			for(int i=0; i<s.length(); i++){
				comp = (int) s.charAt(i);
				if(comp<100 && comp >=10){
					sb.append("0");
					sb.append(comp);
				}
				else{
					sb.append(comp);
				}
			}
			sAs = sb.toString();
			bi = new BigInteger(sAs);
			return bi;
		}
				
		@Override
		protected void onPreExecute() {
			//The 'key' is obtained from the data fetched.
			outLen = out.length();
			key = out.substring(outLen-2, outLen);
			bigKey = getStringAscii(out.substring(0, outLen-2));
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(String... params) {
			//Now the password here is converted to ASCII and the encryption is performed.
			//The ASCII of password is added to 'key'
			bigPass = lenConverter(pass);
			Log.i("Alert", "Key: "+bigPass);
			bigKey = bigKey.add(bigPass);
			String passEncy = bigKey.toString();
			FacAdemicsAppClass.loginPref.SavePreferences(FacAdemicsAppClass.passkey, passEncy);
			FacAdemicsAppClass.loginPref.SavePreferences(FacAdemicsAppClass.secureKey, key);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			FacAdemicsAppClass.initialPrefs.SavePreferences(FacAdemicsAppClass.sKeyA, 1);
			new LogAuth(context, fm, dialog);
			super.onPostExecute(result);
		}
		
	}
	
}
