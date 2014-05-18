package com.facultyLogin.facademicsHandler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateCheck {

	Context context;
	boolean connect;
	ConnectivityManager cm;
	NetworkInfo activeNetwork;
	
	public NetworkStateCheck(Context c){
		this.context = c;
	}
	public boolean state(){
		
		cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		activeNetwork = cm.getActiveNetworkInfo();
		if(activeNetwork != null &&  activeNetwork.isConnected()){
			Log.i("Alert", "True");
			return true;
		}
		else{
			Log.i("Alert", "False");
		}
		
		return false;
	}
	
	
}
