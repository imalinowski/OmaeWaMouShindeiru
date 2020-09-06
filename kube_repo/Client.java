import java.io.*;
import java.net.Socket;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;

public class Client {
    static Socket socket;
    static String name;
    public static void main(String[] args) throws IOException {
        try {
            socket = new Socket("192.168.0.105", 8888);
            BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
            // канал записи в сокет
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject("Who are you motherfucker?");

            System.out.println("DataOutputStream  created");
            //Thread.sleep(100);
            new Thread(() -> {
                try{
                    while(true){
                        String clientCommand = br.readLine();
                        oos.writeObject(clientCommand);
                    }
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }).start();

            name = (String)ois.readObject();
            oos.writeObject("Hi "+name);

            while(!socket.isOutputShutdown()) {

                System.out.println(date()+ name +ois.readObject());

            }


        } catch (IOException | ClassNotFoundException error ) {
            System.out.println(error.getMessage());
        }
    }
}
