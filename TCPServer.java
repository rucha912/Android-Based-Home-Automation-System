import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

	public static void main(String argv[]) throws IOException 
	{ 
		String PATH = "D://others//";
		ServerSocket welcomeSocket = new ServerSocket(8888);
		System.out.println("Server Started running on 8888 port no... ");

		while(true) {

			try{
				System.out.println("Waiting for client to connect..");
				Socket connectionSocket = welcomeSocket.accept();
				System.out.println("Client connected..");
				
				DataInputStream din  = new DataInputStream( connectionSocket.getInputStream() );
				DataOutputStream dout = new DataOutputStream(connectionSocket.getOutputStream());
				
				String i=din.readUTF();
				System.out.println("Data from Client : "+i);
				//Create channel to read data comming from Client..
				
				if(i.equals("1"))
				{		
							String fileName = din.readUTF();
							System.out.println("filename"+fileName);
							
							int fileSize =  din.readInt();
							System.out.println("filesize");
							byte [] data = new byte[fileSize];
							System.out.println("filesize "+fileSize);
							
							int dataToRead = fileSize;
							int gotData = 0;
							
							while(gotData != fileSize)
							{
								gotData += din.read(data, gotData, dataToRead-gotData);
							}
							System.out.println("gotData: "+gotData);
							
							FileOutputStream fout  = new FileOutputStream(PATH+fileName);
							fout.write(data);
							fout.flush();
							fout.close();
				}
				else if(i.equals("2"))
				{
					File dic = new File(PATH);
					String [] files = dic.list();
					System.out.println("files:" + files.length);

					dout.writeInt(files.length);
					dout.flush();
					
					for(String file : files)
					{
						System.out.println("file:" + file);
						dout.writeUTF(file);
						dout.flush();
					}
				}
				else if(i.equals("3"))
				{
					String filename = din.readUTF();
					
					File dic = new File(PATH + filename);
					byte [] data = new byte[(int)dic.length()];
					
					FileInputStream fin = new FileInputStream(dic);
					fin.read(data);
					
					dout.writeInt(data.length);
					dout.flush();
					
					dout.write(data);
					dout.flush();
					
				}
				else
				{
					System.out.println("Device ON/OFF:" + i);					
				}
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
			
		}
	} 
}