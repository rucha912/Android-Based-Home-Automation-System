package com.example.wirelessharddrive;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity
{
	Button start,configure,homeautomation;
	//String ipAdress,port;
	public static String ipaddress="192.168.43.232";
	public static int portnumber = 8888;	
	
	//public static String ipaddress;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		start=(Button)findViewById(R.id.button1);
		configure=(Button)findViewById(R.id.button2);
		homeautomation=(Button)findViewById(R.id.button3);
		
		start.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(ipaddress.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please enter IP address", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Intent i=new Intent(getApplicationContext(),MenuActivity.class);
					startActivity(i);
				}
				
				
			}
		});
		homeautomation.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(ipaddress.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please enter IP address", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Intent i=new Intent(getApplicationContext(),HomeAutomationActivity.class);
					startActivity(i);
				}
				
				
			}
		});
		
		configure.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(),ConfigActivity.class);
				startActivity(i);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
