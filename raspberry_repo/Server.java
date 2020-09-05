import java.net.*;
import java.io.*;

public class Server {
 static OutputStream out;
 static ObjectInputStream in;
 static ServerSocket server;
 static ObjectOutputStream oos;
 static ObjectInputStream ois;
 static BufferedReader br;
 static String name = "";

public static void main(String[] args){
	try{
		server = new ServerSocket(8888);
		System.out.println("Starting server...");

		Socket socket = server.accept();
		System.out.println("Connection!");

		ois = new ObjectInputStream(socket.getInputStream());
		oos = new ObjectOutputStream(socket.getOutputStream());	

		br = new BufferedReader(new InputStreamReader(System.in));
		
		Thread.sleep(1000);
		oos.writeObject("WHO ARE YOU?");
		new Thread(new Runnable(){public void run(){
	             try {
		    	while(true){
				String text = br.readLine();
                            	oos.writeObject(text);
			    }
			} catch (IOException e) {
                            System.out.println(e.getMessage());}}}
        	).start();	

		name = (String)ois.readObject();	
		oos.writeObject("Hi! " + name);			

		while(true){
			System.out.println(name+"> "+ ois.readObject());	
		}

	}catch(Throwable error){
		System.out.println(error.getMessage());
	}		
	}
	
}