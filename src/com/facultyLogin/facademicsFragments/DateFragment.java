package com.facultyLogin.facademicsFragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.facultyLogin.facademics.AttendanceScreen;
import com.facultyLogin.facademics.R;
import com.facultyLogin.facademicsHandler.DataBase;

public class DateFragment extends SherlockFragment {

	String classNum, classNumTable, selSlot, selCode;
	String TAG_Date="Date", TAG_Day = "Day", date,day;
	String[] cols ={DataBase.Date, DataBase.Day };
	Bundle clBundle;
	View view;
	TextView selSlotTv, selCodeTv;
	Context context;
	DataBase dB;
	SQLiteDatabase sdB;
	ListView dateList;
	ProgressDialog dialog;
	ArrayList<HashMap<String, String>> dates;
	Cursor cursor;
	Intent mIntent;
	HashMap<String, String> dtMap;
	int i, limit;
	ListAdapter listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		context = getSherlockActivity();
		clBundle = this.getArguments();
		classNum = clBundle.getString("clnum");
		selSlot = clBundle.getString("selSlot");
		selCode = clBundle.getString("selCode");
		dB = new DataBase(context);
		sdB = dB.getWritableDatabase();
		classNumTable = "date_"+classNum;
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		dialog = new ProgressDialog(context);
		dialog.setTitle("Creating List");
		dialog.setMessage("Please wait...");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		new SetData(dialog).execute();
		view = inflater.inflate(R.layout.date_lis_frag, container, false);
		dateList = (ListView) view.findViewById(R.id.dateList);
		selSlotTv = (TextView) view.findViewById(R.id.selSlot);
		selCodeTv = (TextView) view.findViewById(R.id.selSubCode);
		selSlotTv.setText(selSlot);
		selCodeTv.setText(selCode);
		dateList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> dateAdapter, View vView, int position, long id) {
				String dat = dates.get(position).get(TAG_Date).toString();
				mIntent = new Intent(context, AttendanceScreen.class);
				mIntent.putExtra("clnum", classNum);
				mIntent.putExtra("selSlot", selSlot);
				mIntent.putExtra("selCode", selCode);
				mIntent.putExtra("dt", dat);
				startActivity(mIntent);
			}
		});
		return view;
	}

	private class SetData extends AsyncTask<Void, Void, Void>{

		ProgressDialog dial;
		
		public SetData(ProgressDialog d){
			this.dial = d;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
	
			dates = new ArrayList<HashMap<String,String>>();
			cursor = sdB.query(classNumTable, cols, null, null, null, null, null);
			cursor.moveToFirst();
			i=0;
			dtMap = new HashMap<String, String>();
			date = cursor.getString(cursor.getColumnIndex(DataBase.Date));
			dtMap.put(TAG_Date, date);
			i+=1;
			day = cursor.getString(cursor.getColumnIndex(DataBase.Day));
			dtMap.put(TAG_Day, day);
			dates.add(dtMap);
			while(cursor.moveToNext()){
				dtMap = new HashMap<String, String>();
				date = cursor.getString(cursor.getColumnIndex(DataBase.Date));
				dtMap.put(TAG_Date, date);
				i+=1;
				day = cursor.getString(cursor.getColumnIndex(DataBase.Day));
				dtMap.put(TAG_Day, day);
				dates.add(dtMap);
			}
			cursor.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			listAdapter = new SimpleAdapter(context, dates, R.layout.date_row_style,
					new String[] {TAG_Date, TAG_Day}, new int[] {R.id.date, R.id.day});
			dateList.setAdapter(listAdapter);
			
			dial.dismiss();
			
			super.onPostExecute(result);
		}
		
	}
	
}
