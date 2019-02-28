package com.facultyLogin.facademics;

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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facultyLogin.facademicsHandler.DataBase;
import com.facultyLogin.facademicsHandler.DataBaseStore;

public class AttendanceScreen extends SherlockActivity {
	 
	  ArrayList<HashMap<String, Object>> record;
	  ArrayList<String> regList;
	  LayoutInflater inflater;
	  ListView attnList;
	  Cursor cursorS,cursorA, cursor, cursorSA;
	  DataBase dB;
	  DataBaseStore dbs;
	  SQLiteDatabase sdB, sldb;
	  Bundle dtBundle;
	  Intent intent;
	  HashMap<String , Object> studMap;
	  boolean[] checkBoxState;
	  Context context;
	  TextView selSlotTv, selCodeTv, selDateTv;
	  ProgressDialog dialog;
	  String TAG_name  = "name", TAG_reg = "reg num", TAG_attn = "attn value";
	  String[] colsS = {DataBase.Reg_Num, DataBase.Stud_Name}, colsA = {DataBase.Att_Val}, col = {DataBase.Reg_Num};
	  public static final String[] colSA = {DataBase.Reg_Num, DataBase.Att_Val};
	  String date, classNum, clDtTable, studTable, refDate, dt, mt, yr, det, name, selSlot, selCode, regNum, attnVal, clAttStrtb;
	 
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.attn_list_frag);
		  context = AttendanceScreen.this;
		  classNum = getIntent().getExtras().getString("clnum");
		  date = getIntent().getExtras().getString("dt");
		  selSlot = getIntent().getExtras().getString("selSlot");
		  selCode = getIntent().getExtras().getString("selCode");
		  refDate = refineDate(date);
		  dB = new DataBase(context);
		  sdB = dB.getWritableDatabase();
		  studTable = "Student_"+classNum;
		  clDtTable = "date_"+classNum+"_"+refDate;
		 
		  dbs = new DataBaseStore(context);
		  sldb = dbs.getReadableDatabase();
		  clAttStrtb = "Att_"+classNum+"_"+refDate;
		  dbs.createStoreTb(sldb, clAttStrtb);
		  selDateTv = (TextView) findViewById(R.id.selDate);
		  selSlotTv = (TextView) findViewById(R.id.selSlotAttn);
		  selCodeTv = (TextView) findViewById(R.id.selSubCodeAttn);
		  attnList = (ListView) findViewById(R.id.attnList);
		  inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 	  
		  record=new ArrayList<HashMap<String, Object>>();
		  
		  cursorSA = sldb.query(clAttStrtb, colSA, null, null, null, null, null);
		  cursorA = sdB.query(clDtTable, colsA, null, null, null, null, null);
		  cursorS = sdB.query(studTable, colsS, null, null, null, null, null);
		  
		  new GetVals().execute();
	 }
	  
	  public String refineDate(String d){
			dt = d.substring(0, 2);
			mt = d.substring(3, 8);
			if(mt.equals("April")){
				mt="04";
				yr = d.substring(9, 13);
			}
			else{
				mt="05";
				yr = d.substring(7, 11);
			}
			det = dt+mt+yr;
			return det;
		}
	  
	  
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.atten_sheet, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cancel:
			intent = new Intent(context, HomeScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.save:
			dialog = new ProgressDialog(context);
			dialog.setTitle("Saving Attendance");
			dialog.setMessage("Please Wait..");
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			new StoreVals().execute();
			return true;
		}
		return super.onOptionsItemSelected(item);			
}

	private class CustomAdapter extends ArrayAdapter<HashMap<String, Object>>
	{
		
	   ViewHolder viewHolder;
	   
	   public CustomAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> players) {
		   super(context, textViewResourceId, players);
	   }
	 
	 
	   private class ViewHolder
	   {
		   TextView name, regNum;
		   CheckBox checkBox;
	   }
	 
	   
	 
	   @Override
	   public View getView(final int position, View convertView, ViewGroup parent) {
	 
		   if(convertView==null)
		   {
			   convertView=inflater.inflate(R.layout.attn_row_style, null);
			   viewHolder=new ViewHolder();
	 
			   //cache the views
			   viewHolder.regNum=(TextView) convertView.findViewById(R.id.regnum);
			   viewHolder.name=(TextView) convertView.findViewById(R.id.name);
			   viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.attnCB);
			   convertView.setTag( viewHolder);
	    
	 
		   }
		   else
			   viewHolder=(ViewHolder) convertView.getTag();
	 
		   //set the data to be displayed
		   viewHolder.name.setText(record.get(position).get(TAG_name).toString());
		   viewHolder.regNum.setText(record.get(position).get(TAG_reg).toString());
		   
		   viewHolder.checkBox.setChecked(checkBoxState[position]);
	        
		   viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
	     
			   public void onClick(View v) {
				   if(((CheckBox)v).isChecked()){
					   checkBoxState[position]=true;
				   }
				   else{
					   checkBoxState[position]=false;
				   }
			   }
		   });
		   return convertView;
	   }
	 
	}
	
	private class GetVals extends AsyncTask<Void, Void, Void>{

		ProgressDialog d;
		@Override
		protected void onPreExecute() {
			d = new ProgressDialog(context);
			d.setTitle("Fetching Attendance");
			d.setMessage("Please Wait...");
			d.setCanceledOnTouchOutside(false);
			d.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			//Log.i("Alert", "Check: "+cursorA.getCount());
			if(cursorA.getCount()!=0){
				cursorS.moveToFirst();
				cursorA.moveToFirst();
				studMap = new HashMap<String, Object>();
				attnVal = cursorA.getString(cursorA.getColumnIndex(DataBase.Att_Val));
				studMap.put(TAG_attn, attnVal);
				name = cursorS.getString(cursorS.getColumnIndex(DataBase.Stud_Name));
				studMap.put(TAG_name, name);
				regNum = cursorS.getString(cursorS.getColumnIndex(DataBase.Reg_Num));
				studMap.put(TAG_reg, regNum);
				record.add(studMap);
				while(cursorS.moveToNext() && cursorA.moveToNext()){
					studMap = new HashMap<String, Object>();
					attnVal = cursorA.getString(cursorA.getColumnIndex(DataBase.Att_Val));
					studMap.put(TAG_attn, attnVal);
					name = cursorS.getString(cursorS.getColumnIndex(DataBase.Stud_Name));
					studMap.put(TAG_name, name);
					regNum = cursorS.getString(cursorS.getColumnIndex(DataBase.Reg_Num));
					studMap.put(TAG_reg, regNum);
					record.add(studMap);
				}
				cursorS.close();
				cursorA.close();
			}else{
				//Log.i("Alert", "In else");
				cursorS.moveToFirst();
				studMap = new HashMap<String, Object>();
				attnVal = "Present";
				studMap.put(TAG_attn, attnVal);
				name = cursorS.getString(cursorS.getColumnIndex(DataBase.Stud_Name));
				studMap.put(TAG_name, name);
				regNum = cursorS.getString(cursorS.getColumnIndex(DataBase.Reg_Num));
				studMap.put(TAG_reg, regNum);
				record.add(studMap);
				while(cursorS.moveToNext()){
					studMap = new HashMap<String, Object>();
					attnVal = "Present";
					studMap.put(TAG_attn, attnVal);
					name = cursorS.getString(cursorS.getColumnIndex(DataBase.Stud_Name));
					studMap.put(TAG_name, name);
					regNum = cursorS.getString(cursorS.getColumnIndex(DataBase.Reg_Num));
					studMap.put(TAG_reg, regNum);
					record.add(studMap);
				}
				cursorS.close();
			}
			  
			  checkBoxState = new boolean[record.size()];
			  for(int i=0; i<record.size();i++){
				  if(record.get(i).get(TAG_attn).toString().equals("Present") || record.get(i).get(TAG_attn).toString().equals("present") || record.get(i).get(TAG_attn).toString().equals("p")){
					  checkBoxState[i]=true;
					 // Log.i("Alert", "State"+checkBoxState[i]);
				  }else{
					  checkBoxState[i]=false;
					 // Log.i("Alert", "State"+checkBoxState[i]);
				  }
			  }
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			selSlotTv.setText(selSlot);
			selCodeTv.setText(selCode);
			selDateTv.setText(date);
			d.dismiss();
			final CustomAdapter adapter= new CustomAdapter(context, R.layout.attn_row_style, record);
			attnList.setAdapter(adapter);
			super.onPostExecute(result);
		}
		
	}
	
	private class StoreVals extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			
			regList = new ArrayList<String>();
			cursor = sdB.query(studTable, col, null, null, null, null, null);
			cursor.moveToFirst();
			int i=0;
			regNum = cursor.getString(cursor.getColumnIndex(DataBase.Reg_Num));
			regList.add(i, regNum);
			i+=1;
			while(cursor.moveToNext()){
				regNum = cursor.getString(cursor.getColumnIndex(DataBase.Reg_Num));
				regList.add(i, regNum);
				i+=1;
			}
			
			dbs.clearTable(sldb, clAttStrtb);
			dB.delAttTB(sdB, clDtTable);
			
			for(i = 0; i<record.size(); i++){
				if(checkBoxState[i]==true){
					sldb.execSQL("insert into " + clAttStrtb + " values ('" + regList.get(i) + "', 'p');");
					sdB.execSQL("insert into " + clDtTable + " values ('" + regList.get(i) + "', 'present');");
					//Log.i("Alert", "Present");
				}
				else{
					sldb.execSQL("insert into " + clAttStrtb + " values ('" + regList.get(i) + "', 'a');");
					sdB.execSQL("insert into " + clDtTable + " values ('" + regList.get(i) + "', 'absent');");
					//Log.i("Alert", "Absent");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			cursor.close();
			//new PostAttendance(context);
			dialog.dismiss();
			intent = new Intent(context, HomeScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			super.onPostExecute(result);
		}
	}
	
}