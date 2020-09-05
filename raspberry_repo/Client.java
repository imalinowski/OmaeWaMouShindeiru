import java.net.*;
import java.io.*;

public class Client{
 static OutputStream out;
 static ObjectInputStream in;
 static Socket socket;
	public static void main(String[] args){
		try{	
			socket = new Socket("192.168.0.101",8888);
			System.out.println("Connection!");
		}catch(Throwable error){
			System.out.println(error.getMessage());
		}		
	}
}