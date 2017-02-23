package com.example.wirelessharddrive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


import android.view.ViewGroup;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ClipData.Item;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity
{
	public String filepath="";
	public String filename="";
	private Socket client;
	public byte[] byteMessage;

	private final String PATH_PREFIX = "/storage/emulated/0/other/"; 

	ArrayList<String> str = new ArrayList<String>();

	private Boolean firstLvl = true;

	private static final String TAG = "F_PATH";

	private Item[] fileList;
	private File path = new File(Environment.getExternalStorageDirectory() + "");
	private String chosenFile;
	private static final int DIALOG_LOAD_FILE = 1000;

	ProgressDialog progressWaitDialog = null;
	ListAdapter adapter;
	int fileSIZE =0;
	ListView list;

	String[] toDisplayNameArr = null;
	Integer[] toDisplayIconArr = null;

	String parent_folder= new String("..");		//parent folder name

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		loadFileList();


		toDisplayNameArr = new String[] {"other"};
		toDisplayIconArr = new Integer[] {R.drawable.folder};


		list = (ListView) findViewById(R.id.listView1);
		CustomList adapter = new CustomList(MenuActivity.this, toDisplayNameArr, toDisplayIconArr);  //display root folder at the start of application
		list.setAdapter(adapter);


		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{

				//if condition check... user click on parent folder ?
				if(!toDisplayNameArr[position].equalsIgnoreCase("other"))		
				{
					String fileOfIntrest = toDisplayNameArr[position];
					Log.e("TAG", "fileOfIntrest: " + fileOfIntrest);

					//Connect to server and get this file...
					progressWaitDialog = ProgressDialog.show(MenuActivity.this, "", getResources().getText(R.string.loading), true);
					progressWaitDialog.show();

					ReadFileTasker task = new ReadFileTasker();
					task.execute(new String[] {fileOfIntrest});
				}
				else 
				{
					String folderOfIntrest = toDisplayNameArr[position];
					Log.e("TAG", "folderOfIntrest: " + folderOfIntrest);

					//Connect to server and get directory listing of this folder...
					progressWaitDialog = ProgressDialog.show(MenuActivity.this, "", getResources().getText(R.string.loading), true);
					progressWaitDialog.show();

					DirectoryListingTasker task = new DirectoryListingTasker();
					task.execute(new String[] {folderOfIntrest});

				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override  
	public boolean onOptionsItemSelected(MenuItem item) {  
		switch (item.getItemId()) {  
		case R.id.item1:  
			//Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();  
			showDialog(DIALOG_LOAD_FILE);
			return true;     

		default:  
			return super.onOptionsItemSelected(item);  
		}

	}


	private void loadFileList() {
		try {
			path.mkdirs();
		} catch (SecurityException e) {
			Log.e(TAG, "unable to write on the sd card ");
		}

		// Checks whether path exists
		if (path.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					// Filters based on whether the file is hidden or not
					return (sel.isFile() || sel.isDirectory())
							&& !sel.isHidden();

				}
			};

			String[] fList = path.list(filter);
			fileList = new Item[fList.length];

			for (int i = 0; i < fList.length; i++) {
				fileList[i] = new Item(fList[i], R.drawable.file_icon);

				// Convert into file path
				File sel = new File(path, fList[i]);

				// Set drawables
				if (sel.isDirectory()) {
					fileList[i].icon = R.drawable.directory_icon;
					Log.d("DIRECTORY", fileList[i].file);
				} else {
					Log.d("FILE", fileList[i].file);
				}
			}

			if (!firstLvl) {
				Item temp[] = new Item[fileList.length + 1];
				for (int i = 0; i < fileList.length; i++) {
					temp[i + 1] = fileList[i];
				}
				temp[0] = new Item("Up", R.drawable.directory_up);
				fileList = temp;
			}
		} else {
			Log.e(TAG, "path does not exist");
		}

		adapter = new ArrayAdapter<Item>(this,
				android.R.layout.select_dialog_item, android.R.id.text1,fileList) 
				{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				// creates view
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);

				// put the image on the text view
				textView.setCompoundDrawablesWithIntrinsicBounds(
						fileList[position].icon, 0, 0, 0);

				// add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				textView.setCompoundDrawablePadding(dp5);

				return view;
			}
				};

	}

	private class Item 
	{
		public String file;
		public int icon;

		public Item(String file, Integer icon) {
			this.file = file;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return file;
		}
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(this);

		if (fileList == null) {
			Log.e(TAG, "No files loaded");
			dialog = builder.create();
			return dialog;
		}

		switch (id) {
		case DIALOG_LOAD_FILE:
			builder.setTitle("Choose your file");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chosenFile = fileList[which].file;
					File sel = new File(path + "/" + chosenFile);
					if (sel.isDirectory()) {
						firstLvl = false;

						// Adds chosen directory to list
						str.add(chosenFile);
						fileList = null;
						path = new File(sel + "");

						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						Log.d(TAG, path.getAbsolutePath());

					}

					// Checks if 'up' was clicked
					else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

						// present directory removed from list
						String s = str.remove(str.size() - 1);

						// path modified to exclude present directory
						path = new File(path.toString().substring(0,
								path.toString().lastIndexOf(s)));
						fileList = null;

						// if there are no more directories in the list, then
						// its the first level
						if (str.isEmpty()) {
							firstLvl = true;
						}
						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						Log.d(TAG, path.getAbsolutePath());

					}
					// File picked
					else 
					{
						// Perform action with file picked
						Toast.makeText(getApplicationContext(),"File: " + sel.getAbsolutePath(), Toast.LENGTH_SHORT).show();
						filepath = sel.getAbsolutePath();
						filename = sel.getName();

						//String data=FileReadWrite.readFile(filepath);
						File f = new File(filepath);
						Log.e("TAG", "File size:" + f.length());

						//............
						fileSIZE = (int) f.length();
						byteMessage = new byte[(int)f.length()];

						try
						{
							//Read file data
							FileInputStream fin = new FileInputStream(f);

							int byteToRead = 0;
							while(byteToRead < byteMessage.length)
							{
								byteToRead = byteToRead + fin.read(byteMessage, byteToRead, byteMessage.length - byteToRead);					
							}
							fin.close();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						Log.e("TAG", "SIZE = " + byteMessage.length);

						progressWaitDialog = ProgressDialog.show(MenuActivity.this, "", getResources().getText(R.string.loading), true);
						progressWaitDialog.show();

						SendFileTask task = new SendFileTask();
						task.execute(new String[] {""});						
					}
				}
			});
			break;
		}
		dialog = builder.show();
		return dialog;
	}


	private class SendFileTask extends AsyncTask<String, Void, String> 
	{

		@Override
		protected String doInBackground(String... params)
		{
			try
			{   

				client = new Socket(MainActivity.ipaddress, MainActivity.portnumber); // connect to the server
				DataOutputStream dos=new DataOutputStream(client.getOutputStream());
				DataInputStream din=new DataInputStream(client.getInputStream());

				// dos.write("1".getBytes(), 0, 1);
				//........................to upload a file
				dos.writeUTF("1");
				//Log.e("TAG", "SIZE=" + "1".getBytes().length);
				dos.flush();

				/////
				//String sLen = ""+byteMessage.length;				
				//while (sLen.length() != 10)
				//sLen = "0" + sLen;

				//................................................................


				//Now send filename..
				dos.writeUTF(filename);
				dos.flush();

				Log.d(TAG, "Send file size to server.......");
				dos.writeInt(byteMessage.length);
				dos.flush();
				//.........................................
				////
				//Log.d("TAG", "sLen=" + sLen);
				//dos.write(sLen.getBytes());     
				//dos.flush();

				//Now send actual file data...
				dos.write(byteMessage);
				dos.flush();

				//Send Filename's length first..
				String tmp = filename;
				filename = "other/" + tmp;

				String nLen = ""+ filename.getBytes().length;
				while (nLen.length() != 10)
					nLen = "0" + nLen;
				///
				Log.e("TAG", "nLen=" + nLen);
				//dos.write(nLen.getBytes());
				//dos.flush();



				byte [] ack = new byte[1];
				//din.read(ack);

				din.close();
				dos.close();
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

		protected void onPostExecute(String result) {

			progressWaitDialog.dismiss();
		}

	}

	class DirectoryListingTasker extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... input) 
		{
			String finalDirListString = "";
			try
			{
				byteMessage = input[0].getBytes();
				client = new Socket(MainActivity.ipaddress, MainActivity.portnumber); // connect to the server
				DataOutputStream dos=new DataOutputStream(client.getOutputStream());
				DataInputStream din=new DataInputStream(client.getInputStream());

				dos.writeUTF("2");
				dos.flush();
				
				
				int noOfFile = din.readInt();
				String [] files = new String[noOfFile];
				
				for(int i=0; i<noOfFile; i++)
				{
					finalDirListString += "F,"+din.readUTF() + ":";
				}
				
				/*//dos.write("1".getBytes(), 0, 1);
				//dos.write("2".getBytes());
				//dos.flush();
				//......................................................
				dos.writeUTF(filename);
				dos.flush();

				Log.d(TAG, "Send file size to server.......");
				dos.writeInt(byteMessage.length);
				dos.flush();

				dos.write(byteMessage);
				dos.flush();
				//..........................
				/////
				String sLen = ""+byteMessage.length;		
				while (sLen.length() != 10)
					sLen = "0" + sLen;
				////
				Log.e("TAG", "sLen=" + sLen);
				//dos.write(sLen.getBytes());
				//dos.flush();

				//Now send actual folder name data...
				//dos.write(byteMessage);
				//dos.flush();
*/
				//Server always send size in 10 bytes
				/*byte [] size = new byte[10];
				din.read(size);

				int iSize = Integer.parseInt((new String(size)));
				Log.e("TAG", "iSize=" + iSize);

				byte [] dirNameBuff = new byte[iSize];
				din.read(dirNameBuff);

				finalDirListString = new String(dirNameBuff);
				Log.e("TAG", "finalDirListString=" + finalDirListString);*/

				din.close();
				dos.close();
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
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
			}
			return finalDirListString;
		}

		protected void onPostExecute(String result) 
		{
			//Here we got dir listing  from server in following format inside result:
			//folder1:D,.:D,aa.txt:F,folder2:D,..:D,mydata.txt:F,
			Log.e("TAG", "in onPostExecute, result:" + result);

			String [] filesDetails = result.split(":");

			toDisplayNameArr = new String[filesDetails.length];
			toDisplayIconArr = new Integer[filesDetails.length];

			int j=0;

			for(int i=0; i<filesDetails.length; i++)
			{
				Log.e("TAG", "fileDetails:" + filesDetails[i]);
				//Here we will get  entry like this:
				// folder1:D   <folder/file name>:<type>

				String [] fileDetails = filesDetails[i].split(",");

				if(fileDetails[0].equals(".") || fileDetails[0].equals(".."))
					continue;

				toDisplayNameArr[j] = fileDetails[1];				
				if(fileDetails[0].equalsIgnoreCase("D"))
				{
					//It is directory...
					toDisplayIconArr [j] = R.drawable.folder;
					Log.e("TAG", "it is DIR");
				}
				else
				{
					//It is a file..
					toDisplayIconArr [j] = R.drawable.file;
					Log.e("TAG", "it is FILE");
				}
				j++;
			}
			CustomList adapter1 = new CustomList(MenuActivity.this, toDisplayNameArr, toDisplayIconArr);
			list.setAdapter(adapter1);	
			progressWaitDialog.dismiss();
		}		

	}


	class ReadFileTasker extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... input) 
		{
			String finalFileNameString = input[0];
			try
			{
				//byteMessage = finalFileNameString.getBytes();
				client = new Socket(MainActivity.ipaddress, MainActivity.portnumber); // connect to the server
				DataOutputStream dos=new DataOutputStream(client.getOutputStream());
				DataInputStream din=new DataInputStream(client.getInputStream());

				dos.writeUTF("3");
				dos.flush();

				dos.writeUTF(finalFileNameString);
				dos.flush();
				
				
				int fileSize = din.readInt();
				byte [] data = new byte[fileSize];
				
				int dataToRead = fileSize;
				int gotData = 0;
				
				while(gotData != fileSize)
				{
					gotData += din.read(data, gotData, dataToRead-gotData);
				}
				
				//Write contains into file...
				//File f = new File(Environment.+ finalFileNameString);

				File f = new File(PATH_PREFIX + finalFileNameString);
				FileOutputStream fos = new FileOutputStream(f);
				Log.e("TAG", "writing contains into file:" + f.getAbsolutePath());
				fos.write(data);
				fos.flush();
				fos.close();

				din.close();
				dos.close();
				client.close(); // closing the connection

			}
			catch (UnknownHostException e) 
			{
				Log.e("TAG", "Error" + e.getMessage());
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				Log.e("TAG", "Error" + e.getMessage());
				e.printStackTrace();
			}			
			catch (Exception e) 
			{
				Log.e("TAG", "Error" + e.getMessage());
				System.out.println(e.getMessage());
			}
			return finalFileNameString;
		}

		protected void onPostExecute(String result) 
		{
			Log.e("TAG", "in onPostExecute, result:" + result);
			progressWaitDialog.dismiss();

			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			File file = new File(PATH_PREFIX + result);
			Log.e("TAG", "file path:" + file.getAbsolutePath());
			intent.setDataAndType(Uri.fromFile(file), "image/*");
			startActivity(intent);
		}
	}

}
