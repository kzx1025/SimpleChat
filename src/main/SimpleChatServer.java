package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;



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


    public class Filehandler implements Runnable {
        Socket fileSocket;

        public Filehandler(Socket clientFileSocket) {
            fileSocket = clientFileSocket;
        }

        public void run() {
            try {
                while (true) {

                    // System.out.println("开始监听...");
                    /*
                     * 如果没有访问它会自动等待
                     */
                    // System.out.println("有链接");
                    if (!fileSocket.isClosed()) {
                        System.out.println("有链接");
                        receiveFile(fileSocket);
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("服务器异常");
                e.printStackTrace();
            }

        }
    }

    public void go() {
        clientOutputStreams = new ArrayList<PrintWriter>();
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            ServerSocket serverfileSocket = new ServerSocket(5555);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread thread = new Thread(new Clienthandler(clientSocket));
                thread.start();
                System.out.println("got a connection");

                Socket clientFileSocket = serverfileSocket.accept();
                Thread fileThread = new Thread(new Filehandler(clientFileSocket));
                fileThread.start(); // start the thread receiving the file.

            }
        } catch (Exception e) {

            // TODO: handle exception
            e.printStackTrace();
        }


    }

    public void tellEveryone(String message) {
        Iterator<PrintWriter> users = clientOutputStreams.iterator();
        while (users.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) users.next();
                writer.println(message);
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收文件方法
     * 
     * @param socket
     * @throws IOException
     */
    public static void receiveFile(Socket socket) throws IOException {
        byte[] inputByte = null;
        int length = 0;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        String filePath = "/home/zhxke/backup/" + new Random().nextInt(10000);
        try {
            try {
                dis = new DataInputStream(socket.getInputStream());
                File f = new File("/home/zhxke/backup/");
                if (!f.exists()) {
                    f.mkdir();
                }
                /*
                 * 文件存储位置
                 */
                fos = new FileOutputStream(new File(filePath));
                inputByte = new byte[1024];
                System.out.println("开始接收数据...");
                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
                    fos.write(inputByte, 0, length);
                    fos.flush();
                }
                System.out.println("完成接收：" + filePath);
            } finally {
                if (fos != null) fos.close();
                if (dis != null) dis.close();
                if (socket != null) socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
