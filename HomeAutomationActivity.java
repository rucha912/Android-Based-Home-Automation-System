package com.example.wirelessharddrive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class HomeAutomationActivity extends Activity {
	private ToggleButton toggleBtn1, toggleBtn2,toggleBtn3,toggleBtn4;
	Button device1;
	String whatToSend = "0";
	private static final String TAG = "com.example.wirelessharddrive";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_automation);

		toggleBtn1 = (ToggleButton) findViewById(R.id.toggleButton1);
		toggleBtn2 = (ToggleButton) findViewById(R.id.toggleButton2);
		toggleBtn3 = (ToggleButton) findViewById(R.id.toggleButton3);
		toggleBtn4 = (ToggleButton) findViewById(R.id.toggleButton4);

		toggleBtn1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if(isChecked)
				{
					whatToSend = "11";
					SendFileTask sendFileTask = new SendFileTask();
					sendFileTask.execute();
				}
				else
				{
					whatToSend = "10";
					SendFileTask sendFileTask = new SendFileTask();
					sendFileTask.execute();
				}				
			}
		});

		toggleBtn2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if(isChecked)
				{
					whatToSend = "21";
					SendFileTask sendFileTask = new SendFileTask();
					sendFileTask.execute();
				}
				else
				{
					whatToSend = "20";
					SendFileTask sendFileTask = new SendFileTask();
					sendFileTask.execute();
				}				
			}
		});

		toggleBtn3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if(isChecked)
				{
					whatToSend = "31";
					SendFileTask sendFileTask = new SendFileTask();
					sendFileTask.execute();
				}
				else
				{
					whatToSend = "30";
					SendFileTask sendFileTask = new SendFileTask();
					sendFileTask.execute();
				}				
			}
		});

		toggleBtn4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if(isChecked)
				{
					whatToSend = "41";
					SendFileTask sendFileTask = new SendFileTask();
					sendFileTask.execute();
				}
				else
				{
					whatToSend = "40";
					SendFileTask sendFileTask = new SendFileTask();
					sendFileTask.execute();
				}				
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_automation, menu);
		return true;
	}

	private class SendFileTask extends AsyncTask<String, String, String> 
	{

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				Socket client = new Socket(MainActivity.ipaddress, MainActivity.portnumber); // connect to the server
				DataOutputStream dos=new DataOutputStream(client.getOutputStream());
				DataInputStream din=new DataInputStream(client.getInputStream());

				dos.writeUTF(whatToSend);
				Log.d(TAG, "WhatToSend"+whatToSend);
				dos.flush();

				dos.close();
				din.close();
				client.close(); // closing the connection

			} 
			catch (UnknownHostException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}

			return "";
		}
	}

}
