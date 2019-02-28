package com.facultyLogin.facademics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facultyLogin.facademicsFragments.LoginCredsHandler;
import com.facultyLogin.facademicsFragments.SubjectListFragment;
import com.facultyLogin.facademicsHandler.LogAuth;
import com.facultyLogin.facademicsHandler.NetworkStateCheck;

public class HomeScreen extends SherlockFragmentActivity {
	
	Fragment curr, subList, dateList, regStuds;
	SubjectListFragment subListFrag;
	String id, pass;
	Boolean nwState;
	int i;
	Button attnBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		changeFrag();
		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		i = FacAdemicsAppClass.initialPrefs.GetPreferences(FacAdemicsAppClass.valueKey);
		id =  FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.idkey);
		if(i == 1){
			new LoginCredsHandler().show(getSupportFragmentManager(), "Login Creds");
		}
		super.onPostCreate(savedInstanceState);
	}

	public void changeFrag(){
		subListFrag = new SubjectListFragment();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.homeFrame, subListFrag, "Subject");
		ft.commit();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.home_screen, menu);
		return true;
	}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.refersh:
				if(FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.idkey).equals("") || FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.passkey).equals(""))
					new LoginCredsHandler().show(getSupportFragmentManager(), "Login Creds");
				else if(nwState = new NetworkStateCheck(getApplicationContext()).state()== false)
					Toast.makeText(getApplicationContext(), "Not Connected to Internet...Please Connect to Internet First..", Toast.LENGTH_LONG).show();
				else
					//new CaptchHandlerDialog().show(getSupportFragmentManager(), "Captcha");
					new LogAuth(HomeScreen.this, getSupportFragmentManager());
					//new DateJSON(HomeScreen.this, FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.idkey), getSupportFragmentManager());
				return true;
			case R.id.login:
				new LoginCredsHandler().show(getSupportFragmentManager(), "Login Creds");
				return true;
			}
			return super.onOptionsItemSelected(item);			
	}
		

}
