package Server;

import java.io.*;
import java.net.*;
import java.util.Arrays;

class UDPServer {
    /*
        Steps to connect:
            - Send connect: c::[c/d]
            - Send ping when message is recieved: p::[c/d]
        Steps to send:
            - Connect
            - Send: s::[c/s]::msg
        Note: The client and drone must send a ping after recieving a message in order to recieve new messages.
    */
    public static void main(String args[]) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        Connection client = new Connection();
        Connection drone = new Connection();
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            serverSocket.receive(receivePacket);
            String sentence = new String( receivePacket.getData());
            System.out.println("RECEIVED: " + sentence);

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            String[] parts = sentence.split("::");
            System.out.println("Parts: " + parts[0] + ", " + parts[1]);
            if (parts.length>1 && parts[0].equals("c")){
                System.out.println("Connecting");
                if (parts[1].trim().equals("c")){
                    client.ip = IPAddress;
                    client.port = port;
                    System.out.println("C - Connected to:" +port );
                    client.sendData("connected", serverSocket);
                }else if (parts[1].trim().equals("d")){
                    drone.ip = IPAddress;
                    drone.port = port;
                    System.out.println("D - Connected to:" +port );
                    drone.sendData("connected", serverSocket);
                }else{
                    System.out.println("... " + parts[1]);
                }
            }else if (parts.length>2 && parts[0].equals("s")){
                if (parts[1].equals("c")){
                    drone.sendData(parts[2], serverSocket);
                }else if (parts[1].equals("d")){
                    client.sendData(parts[2], serverSocket);
                }
            }else if (parts.length>2 && parts[0].equals("p")){
                if (parts[1].equals("c")){
                    client.ip = IPAddress;
                    client.port = port;
                }else if (parts[1].equals("d")){
                    drone.ip = IPAddress;
                    drone.port = port;
                }
            }else{
                String capitalizedSentence = sentence.toUpperCase();
                sendData = capitalizedSentence.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            }
            receiveData = new byte[1024];
            sendData = new byte[1024];
        }
    }
    private static class Connection {
        public InetAddress ip = null;
        public int port = 0;
        private byte[] sendData = new byte[1024];
        public Connection(InetAddress ip, int port){
            this.ip = ip;
            this.port = port;
        }
        public Connection(){}
        public void sendData(String sendStr, DatagramSocket socket) throws Exception{
            System.out.println("Try Send... " + ip + ":" + port);
            if (ip==null || port==0)
                return;
            System.out.println("Sending: " + sendStr);
            sendData = sendStr.getBytes();
            socket.send(new DatagramPacket(sendData, sendData.length, ip, port));
        }
    }
}

