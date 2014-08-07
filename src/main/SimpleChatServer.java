package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;



public class SimpleChatServer {
    ArrayList<PrintWriter> clientOutputStreams;

    public static void main(String[] args) {
        new SimpleChatServer().go();

    }

    public class Clienthandler implements Runnable {
        BufferedReader reader;
        Socket socket;


        public Clienthandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            // TODO Auto-generated method stub
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    tellEveryone(message);
                }
            } catch (Exception e) {
                // TODO: handle exception

            }

        }

    }

    public void go() {
        clientOutputStreams = new ArrayList<PrintWriter>();
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread thread = new Thread(new Clienthandler(clientSocket));
                thread.start();
                System.out.println("got a connection");

            }
        } catch (Exception e) {

            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void tellEveryone(String message) {
        Iterator<PrintWriter> users = clientOutputStreams.iterator();
        while (users.hasNext()) {
            try{
            PrintWriter writer = (PrintWriter) users.next();
            writer.println(message);
            writer.flush();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
