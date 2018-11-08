
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import static server.Server.t1;
import static server.Server.t2;

public class Server 
{
    public static  Thread t1,t2;
    
    public static void main(String args[])
    {
        try
        {
            System.out.println("Server Battaglia Navale 1.0 started.");
            ServerSocket server = new ServerSocket(8080);
            
            System.out.println("Waiting for client 1..."); 
            Socket client1 = server.accept();
            
            System.out.println("Client 1 connected.");
            System.out.println("Waiting for client 2..."); 
            Socket client2 = server.accept();
            
            System.out.println("Client 2 connected.");
            System.out.println("Game started.");
            
            DataInputStream in1= new DataInputStream(client1.getInputStream());
            DataInputStream in2= new DataInputStream(client2.getInputStream());
            DataOutputStream out1= new DataOutputStream(client1.getOutputStream());
            DataOutputStream out2= new DataOutputStream(client2.getOutputStream());
            
            
            out1.writeUTF("1-1");
            out2.writeUTF("1-2");
            
          Runnable r1 = new clientHandler(out1,in2);
          t1 = new Thread(r1);
          t1.start();
          System.out.println("Thread 1 go");
          
          Runnable r2 = new clientHandler(out2,in1);
          t2 = new Thread(r2);
          t2.start();
          System.out.println("Thread 2 go");
            
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            t1.interrupt();
            t2.interrupt();
            System.exit(0);
        }
    }
}

//legge dal client 1 e scrive sul client 2
class clientHandler implements Runnable
{
    DataInputStream in;
    DataOutputStream out;
    
    public clientHandler(DataOutputStream out, DataInputStream in)
    {
        this.in=in;
        this.out=out;
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            try 
            {
                String s = in.readUTF();
                System.out.println(s);
                out.writeUTF(s);
            } catch (IOException ex) 
            {
               ex.printStackTrace();
                t1.interrupt();
                t2.interrupt();
                System.exit(0);
            }
        }
    }
}
