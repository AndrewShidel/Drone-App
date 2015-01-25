package shidel.droneapp;


import java.net.*;
import java.lang.Thread;
import java.lang.Runnable;
import java.io.IOException;

class UDPHandler {
    private String serverURL = "";
    private String userType = "c";
    private DatagramSocket clientSocket;
    public String latestMessage;
    private byte[] sendData = new byte[1024];
    private byte[] receiveData = new byte[1024];
    InetAddress IPAddress;
    Thread networkThread;

    public UDPHandler(String serverURL, String userType) throws IOException {
        this.serverURL = serverURL;
        this.userType = userType;
        connect();
    }
    private boolean connect() throws IOException{

        //if (result.equals("connected")) {
        runLoop();
        //}
        return true;
    }
    private void runLoop() throws IOException{
        networkThread = new Thread(new Runnable() {
            public void run() {
                DatagramPacket receivePacket;
                try {
                    clientSocket = new DatagramSocket();
                    IPAddress = InetAddress.getByName(serverURL);

                    sendData = ("c::" + userType).getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                    clientSocket.send(sendPacket);

                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);


                    String result = new String(receivePacket.getData());
                } catch(IOException e){return;}

                byte[] ping = new byte[1024];
                while (true) {
                    //ping = ("p::" + userType).getBytes();
                    try {
                        //    DatagramPacket sendPacket = new DatagramPacket(ping, ping.length, IPAddress, 9876);
                        //    clientSocket.send(sendPacket);

                        receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);

                        latestMessage = new String(receivePacket.getData());
                        System.out.println("Message:" + latestMessage);
                    }catch(IOException e){e.printStackTrace();}
                }
            }
        });
        networkThread.start();
    }
    public void join() {
        try {
            networkThread.join();
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String message) throws IOException{
        sendData = ("s::" + userType+"::" + message).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);
    }
}

