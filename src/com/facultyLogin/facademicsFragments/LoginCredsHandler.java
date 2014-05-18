package com.facultyLogin.facademicsFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.facultyLogin.facademics.FacAdemicsAppClass;
import com.facultyLogin.facademics.HomeScreen;
import com.facultyLogin.facademics.R;
import com.facultyLogin.facademicsHandler.LogAuth;
import com.facultyLogin.facademicsHandler.NetworkStateCheck;

public class LoginCredsHandler extends SherlockDialogFragment{

	String id, pass;
	View view;
	EditText idET, passET;
	Boolean autoRefresh, nwState = false;
	HomeScreen main;
	SubjectListFragment subListFrag;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		id = FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.idkey);
		pass = FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.passkey);
		AlertDialog.Builder loginMenu = new AlertDialog.Builder(getSherlockActivity());
		LayoutInflater inlater = getSherlockActivity().getLayoutInflater();
		view = inlater.inflate(R.layout.prefs_layout, null);
		idET = (EditText) view.findViewById(R.id.Id);
		passET = (EditText) view.findViewById(R.id.Pass);
		loginMenu.setView(view);
		loginMenu.setTitle("Login Details");
		loginMenu.setCancelable(false);
		if(id!=""||pass!=""){
			idET.setText(id);
			passET.setText(pass);
		}
		
		loginMenu.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				id = idET.getText().toString();
				pass = passET.getText().toString();
				
				if(id.equals("")){
					FacAdemicsAppClass.initialPrefs.SavePreferences(FacAdemicsAppClass.valueKey, 1);
					changeFrag();
					nwState= false;
				}
				else
				{
					if(pass.equals("")){
						FacAdemicsAppClass.initialPrefs.SavePreferences(FacAdemicsAppClass.valueKey, 1);
						FacAdemicsAppClass.loginPref.SavePreferences(FacAdemicsAppClass.idkey, id);
						changeFrag();
						nwState = false;
					}else{
						nwState = new NetworkStateCheck(getSherlockActivity()).state();
						FacAdemicsAppClass.initialPrefs.SavePreferences(FacAdemicsAppClass.valueKey, 0);
						FacAdemicsAppClass.loginPref.SavePreferences(FacAdemicsAppClass.idkey, id);
						FacAdemicsAppClass.loginPref.SavePreferences(FacAdemicsAppClass.passkey, pass);
					}
				}					
				if(nwState== true)
					//new CaptchHandlerDialog().show(getFragmentManager(), "Captcha");
					new LogAuth(getSherlockActivity(), getFragmentManager());
				else
					changeFrag();
				
			}
		});
		loginMenu.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				LoginCredsHandler.this.getDialog().cancel();
			}
		});
		return loginMenu.create();
	}

	public void changeFrag(){
		subListFrag = new SubjectListFragment();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.homeFrame, subListFrag, "Subject");
		ft.commit();
	}
	
	public class Changer{
		SubjectListFragment subListFrag;
		FragmentManager fm;
		
		public Changer(){
			subListFrag = new SubjectListFragment();
			fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.homeFrame, subListFrag, "Subject");
			ft.commit();
		}
		
	}
	
	
}
