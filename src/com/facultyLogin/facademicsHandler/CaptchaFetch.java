package com.facultyLogin.facademicsHandler;

import java.io.InputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

public class CaptchaFetch{

	Toast t;
	ProgressDialog mProgressDialog;
	ImageView imv;
	Context context;
	ProgressDialog dialog;
	String id, urlFinal;
	public CaptchaFetch(Context context, ImageView im, ProgressDialog dialog, String id){
		this.context = context;
		this.imv = im;
		this.dialog = dialog;
		this.id = id;
		urlFinal = "http://dummyfacteser.appspot.com/captchaPage/" + id;
		new DownImage().execute(urlFinal);
	}
	
  public class DownImage  extends AsyncTask<String, Void, Bitmap> {
    
	  @Override
    protected void onPreExecute() {
        
		  super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... URL) {

        String imageURL = URL[0];

        Bitmap bitmap = null;
        try {
            InputStream input = new java.net.URL(imageURL).openStream();
            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(input), 300, 100, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        imv.setImageBitmap(result);
        dialog.dismiss();
    } 
  }
}

