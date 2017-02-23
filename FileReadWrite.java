package com.example.wirelessharddrive;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class FileReadWrite {
	
	
	public static String readFile(String fileName)
	{
		
		
		try
		{
		//File readfile=new File("/sdcard/mydata.txt");
		FileInputStream fi=new FileInputStream(fileName);				
		BufferedReader br=new BufferedReader(new InputStreamReader(fi));
		String readdata="";
		String abuffer="";
		while((readdata=br.readLine())!=null)
		
		{
		
		
			abuffer += readdata + "\n";
		}
		//textField.setText(abuffer);
		br.close();
		return abuffer;
		
	} catch (Exception e) {
		System.out.println(e);
	
	}// onClick
		return null;
	
	}
	
	public static void writeFile(String fileName,String data)
	{
		
		 try {
				File myfile=new File(fileName);
				myfile.createNewFile();
				
				//data = textField.getText().toString();// get the text message on the text fie
				
				
			     
			         FileOutputStream fOut = new FileOutputStream(myfile);
			         //OutputStreamWriter ost=new OutputStreamWriter(fOut);
			         fOut.write(data.getBytes());
			         
			         //ost.append(textField.getText());
			        // ost.close();
			        
			         fOut.close();
			       
			     	
			         
			      } catch (Exception e) {
			         // TODO Auto-generated catch block
			         e.printStackTrace();
			      }
				
				 
			}
	
		
	
	

}
