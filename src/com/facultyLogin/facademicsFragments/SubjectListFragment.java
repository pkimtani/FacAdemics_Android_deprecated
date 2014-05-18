package com.facultyLogin.facademicsFragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.facultyLogin.facademics.FacAdemicsAppClass;
import com.facultyLogin.facademics.R;
import com.facultyLogin.facademicsHandler.DataBase;

public class SubjectListFragment extends SherlockFragment {

	public static String subName, subCode, TAG_SUB_NAME = "Subject Name", TAG_SUB_CODE = "Subject Code" ;
	public static String slot, classNum, TAG_SLOT="Slot",TAG_CLASSNUM="Class Number";
	String[] fetchedString = {};
	String[] cols = {DataBase.Cl_Num, DataBase.C_CODE, DataBase.SUB_TITLE, DataBase.Slot};
	int i, limit;
	ListView slotsList;
	ListAdapter listAdapter;
	View view;
	Context context;
	DataBase dB;
	TextView toDate, lastRefTv;
	SQLiteDatabase sdB;
	Cursor cursor;
	ArrayList<HashMap<String, String>> subjects;
	ProgressDialog dialog;
	HashMap<String, String> subMap;
	DateFragment df;
	Bundle clBundle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getSherlockActivity();
		df=new DateFragment();
		clBundle = new Bundle();
		dB = new DataBase(context);
		sdB = dB.getWritableDatabase();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		dialog = new ProgressDialog(context);
		dialog.setTitle("Creating List");
		dialog.setMessage("Please wait...");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		new SetData(dialog).execute();
		view = inflater.inflate(R.layout.sub_list_fragment, container, false);
		slotsList = (ListView) view.findViewById(R.id.subList);
		toDate = (TextView) view.findViewById(R.id.toDate);
		lastRefTv = (TextView) view.findViewById(R.id.lastRefTv);
		lastRefTv.setText(FacAdemicsAppClass.loginPref.GetPreferences(FacAdemicsAppClass.lastRefKey));
		String currentDate = new SimpleDateFormat("dd-MMM-yyyy h:m a z", Locale.getDefault()).format(new Date());
		toDate.setText("Today: "+currentDate);
		slotsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> itemAdapter, View vView, int position, long id) {
				String clNum = subjects.get(position).get(TAG_CLASSNUM).toString(), selSlot = subjects.get(position).get(TAG_SLOT).toString(), selCode = subjects.get(position).get(TAG_SUB_CODE).toString();
				clBundle.putString("clnum", clNum);
				clBundle.putString("selSlot", selSlot);
				clBundle.putString("selCode", selCode);
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				df.setArguments(clBundle);
				ft.replace(R.id.homeFrame, df, "date");
				ft.addToBackStack(null);
				ft.commit();
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
	
			subjects = new ArrayList<HashMap<String,String>>();
			cursor = sdB.query(DataBase.TB, cols, null, null, null, null, null);
			cursor.moveToFirst();
			i=0;
			subMap = new HashMap<String, String>();
			classNum = cursor.getString(cursor.getColumnIndex(DataBase.Cl_Num));
			subMap.put(TAG_CLASSNUM, classNum);
			i+=1;
			subCode = cursor.getString(cursor.getColumnIndex(DataBase.C_CODE));
			subMap.put(TAG_SUB_CODE, subCode);
			subName = cursor.getString(cursor.getColumnIndex(DataBase.SUB_TITLE));
			subMap.put(TAG_SUB_NAME, subName);
			slot = cursor.getString(cursor.getColumnIndex(DataBase.Slot));
			subMap.put(TAG_SLOT, slot);
			subjects.add(subMap);
			while(cursor.moveToNext()){
				subMap = new HashMap<String, String>();
				subName = cursor.getString(cursor.getColumnIndex(DataBase.SUB_TITLE));
				subMap.put(TAG_SUB_NAME, subName);
				subCode = cursor.getString(cursor.getColumnIndex(DataBase.C_CODE));
				subMap.put(TAG_SUB_CODE, subCode);
				slot = cursor.getString(cursor.getColumnIndex(DataBase.Slot));
				subMap.put(TAG_SLOT, slot);
				classNum = cursor.getString(cursor.getColumnIndex(DataBase.Cl_Num));
				subMap.put(TAG_CLASSNUM, classNum);
				subjects.add(subMap);
				i+=1;
			}
			cursor.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			listAdapter = new SimpleAdapter(context, subjects, R.layout.subject_row_style,
					new String[] {TAG_SUB_NAME, TAG_SUB_CODE, TAG_SLOT, TAG_CLASSNUM}, new int[] {R.id.subName, R.id.subCode, R.id.slot, R.id.classNum});
			slotsList.setAdapter(listAdapter);
			
			dial.dismiss();
			
			
			
			super.onPostExecute(result);
		}
		
	}
}
