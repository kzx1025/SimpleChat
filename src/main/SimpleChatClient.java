package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class SimpleChatClient {
    JTextArea incoming;
    // JEditorPane incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket socket;
    String address;
    String user_name;
    ChatRecord cr;

    FileOutputStream fs;
    ObjectOutputStream os = null;

    public SimpleChatClient() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            address = addr.getHostAddress();
            user_name = addr.getHostName();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        SimpleDateFormat rq = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = rq.format(new Date()) + ".ser";
        File file = new File(fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            fs = new FileOutputStream(rq.format(new Date()) + ".ser");
            os = new ObjectOutputStream(fs);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public static void main(String args[]) {
        SimpleChatClient client = new SimpleChatClient();

        client.go();
    }

    public void go() {
        JFrame frame = new JFrame(user_name + "'s SimpleChat");
        JPanel mainPanel = new JPanel();
        incoming = new JTextArea(15, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        // incoming=new JEditorPane();
        incoming.setEnabled(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("send");
        sendButton.addActionListener(new SendButtonListener());
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);

        setUpNetworking();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        Thread serverExist = new Thread(new CheckServer());
        serverExist.start();

        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(600, 320);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int value = JOptionPane.showConfirmDialog(null, "确定要关闭吗？");
                if (value == JOptionPane.OK_OPTION) {
                    writer.println(user_name + "quit the chat");
                    writer.flush();
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    try {
                        os.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    System.exit(0);
                }
            }

        });
    }

    public void setUpNetworking() {
        try {
            socket = new Socket("localhost", 5000);
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(socket.getOutputStream());
            System.out.println("networking established");
            writer.println(address + "join the chat");
            writer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            incoming.append("sorry, can not connect the server!");
            e.printStackTrace();
        }

    }

    public class SendButtonListener implements ActionListener { // in class (neibulei)

        @Override
        public void actionPerformed(ActionEvent arg0) {
            // TODO Auto-generated method stubadasd
            try {
                if ((!outgoing.getText().equals("")) &&(outgoing.getText() != null)) {
                    writer.println(address + ":");
                    writer.println("     " + outgoing.getText());
                    writer.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            try {
                socket.sendUrgentData(0xFF);
            } catch (Exception ex) {
                incoming.append("the sever maybe occured some problems"); // click the send can make
                                                                          // this warning
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }

    }

    public class IncomingReader implements Runnable {
        public void run() {
            String message;
            SimpleDateFormat tm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeString = null;
            try {
                while ((message = reader.readLine()) != null) {
                    incoming.append(message + "\n");
                    timeString = tm.format(new Date());
                    cr = new ChatRecord();
                    cr.setUser_name(user_name);
                    cr.setRecord(message);
                    cr.setTime(timeString);
                    os.writeObject(cr);
                }
            } catch (Exception e) {
                // TODO: handle exception

                // e.printStackTrace();
            }



        }
    }

    public class CheckServer implements Runnable {


        public void run() {
            // TODO Auto-generated method stub
            try {
                Socket testSocket = new Socket("localhost", 5000);
                while (true) {
                    testSocket.sendUrgentData(0xFF);
                    Thread.sleep(2000);
                }
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                incoming.append("the sever maybe occured some problems,can't disconnect");
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                incoming.append("the sever maybe occured some problems,can't disconnect");
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }


    }



}
