package com.facultyLogin.facademicsFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.facultyLogin.facademics.FacAdemicsAppClass;
import com.facultyLogin.facademics.R;
import com.facultyLogin.facademicsHandler.CaptchaFetch;

public class CaptchHandlerDialog extends SherlockDialogFragment {

	String id, pass, idkey = "Id", passkey = "Password", captchaText;
	View view;
	static ImageView imv;
	TextView out;
	EditText cap;
	FragmentManager fm;
	
	@Override
	public void onStart() {
		super.onStart();
		id = FacAdemicsAppClass.loginPref.GetPreferences(idkey);
		fm = getFragmentManager();
		new Captcha().getNewCaptcha(getSherlockActivity());
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
	AlertDialog.Builder captcha = new AlertDialog.Builder(getSherlockActivity());
	LayoutInflater inflate = getSherlockActivity().getLayoutInflater();
	view = inflate.inflate(R.layout.captcha_fetch_layout, null);
	captcha.setView(view);
	captcha.setTitle("Captcha Window");
	captcha.setCancelable(false);
	imv = (ImageView) view.findViewById(R.id.captchaImageView);
	cap = (EditText) view.findViewById(R.id.captchaText);
	captcha.setPositiveButton("Login", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			captchaText = cap.getText().toString();
			
			//new LogAuth(getSherlockActivity(), captchaText, fm);
		}
	});
    captcha.setNegativeButton("Cancel", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			CaptchHandlerDialog.this.getDialog().cancel();
		}
	});
    
    return captcha.create();
	}

	public class Captcha {
		
	public void getNewCaptcha(Context context){
		
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle("Fetching Captcha");
		dialog.setMessage("Please Wait");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		new CaptchaFetch(getSherlockActivity(), imv, dialog, id);
		
	}
	
	}
}