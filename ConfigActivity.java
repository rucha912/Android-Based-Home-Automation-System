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

public class ConfigActivity extends Activity 
{
  Button ok;
  EditText ipAddress,portNo;
 // public static String ipText,portNoText;
  public static String ipaddress,portnumber;
  
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		
		ok=(Button)findViewById(R.id.button1);
		portNo=(EditText)findViewById(R.id.editText2);
		ok.setOnClickListener(new OnClickListener() {
			
		
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				
				try{
				MainActivity.ipaddress=ipAddress.getText().toString();
				MainActivity.portnumber=Integer.parseInt(portNo.getText().toString()) ;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				if(MainActivity.ipaddress.equals("") || MainActivity.ipaddress==null)
				{
					Toast.makeText(getApplicationContext(), "Please enter IP address", Toast.LENGTH_SHORT).show();
				}
				else
				{
				
			    finish();
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.config, menu);
		return true;
	}

}
