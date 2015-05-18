package shidel.droneapp;

import java.net.*;
import java.lang.Thread;
import java.lang.Runnable;
import java.io.IOException;
import java.util.Arrays;

import shidel.droneapp.adapters.ThreadAdapter;

public class UDPHandler {
    private String userType = "c";
    private DatagramSocket clientSocket;
    private byte[] sendData = new byte[1024];
    private byte[] receiveData = new byte[1024];
    //private static final String serverURL = "ec2-52-10-29-169.us-west-2.compute.amazonaws.com";
    private static final String serverURL = "52.11.71.30";
    private InetAddress IPAddress;
    private Thread networkThread;
    private UDPCallback callback;
    private ThreadAdapter threadAdapter;

    public static abstract class UDPCallback {
        public abstract void onCommand(String cmd);
    }

    public UDPHandler(String userType, ThreadAdapter threadAdapter, UDPCallback callback) throws IOException {
        this.userType = userType;
        this.callback = callback;
        this.threadAdapter = threadAdapter;
        connect();
    }

    private boolean connect() throws IOException {

        //if (result.equals("connected")) {
        runLoop();
        //}
        return true;
    }

    private void runLoop() throws IOException {
        networkThread = new Thread(new Runnable() {
            private void connect() {
                try {
                    clientSocket = new DatagramSocket();
                    clientSocket.setSoTimeout(500);

                    IPAddress = InetAddress.getByName(serverURL);

                    sendData = ("c::" + userType).getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                    clientSocket.send(sendPacket);

                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                } catch (IOException e) {
                    return;
                }
            }
            public void run() {
                DatagramPacket receivePacket;
                connect();
                while (true) {
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    try {
                        clientSocket.receive(receivePacket);
                    } catch (SocketTimeoutException e){
                        //connect();
                        //Log.e("Error", "Socket Timeout");
                        continue;
                    } catch (IOException e) {
                        System.out.println("Could not receive the packet.");
                    }
                    final String data = new String(trim(receivePacket.getData()));

                    if (threadAdapter != null && callback != null) {
                        threadAdapter.run(callback, data);
                    }
                }
            }
        });
        networkThread.start();
    }

    static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }
        return Arrays.copyOf(bytes, i + 1);
    }

    public void join() {
        try {
            networkThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        sendData = ("s::" + userType + "::" + message).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);
    }
}