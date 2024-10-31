package com.client.liveowl;
import com.client.liveowl.controller.LiveController;
import javafx.application.Platform;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;


public class TeacherSocket{
    public static int serverPort = 9000;
    public static String serverHostName = "127.0.0.1";
    public static int maxDatagramPacketLength = 32768;
    public static int clientPortSend = 6000;
    public static int clientPortReceive = 5000;
    public static Map<Integer, byte[]> buffer = new HashMap<>();
    public static int imageCount = 0;
    public static DatagramSocket socketSend;
    public static DatagramSocket socketRecieve;
    public TeacherSocket() {
        try {
            serverPort = 9000;
            socketSend = new DatagramSocket(clientPortSend);
            socketRecieve = new DatagramSocket(clientPortReceive);
        } catch (SocketException e) {
            System.err.println("Lỗi trong khi khởi tạo Socket :" + e.getMessage());
        }
    }
    public void sendMsg(String message) throws IOException {
        InetAddress address = InetAddress.getByName(serverHostName);
        int port = serverPort;
        byte[] messageBytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, port);
        socketSend.send(packet);
    }
    public void receivePort() throws IOException {
        byte[] messageBytes = new byte[1];
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);
        socketSend.receive(packet);
        serverPort += (messageBytes[0] & 0xff);
    }

    public void LiveStream(String code, LiveController liveController) throws IOException {

            sendMsg("connect");
            System.out.println("Gửi thành công chuỗi connect đến server!");
            sendMsg("teacher");
            System.out.println("Gửi role teacher!");
            sendMsg(code);
            System.out.println("Gửi mã " + code + " cuộc thi thành công!");
            receivePort();
            System.out.println("Port mới là :" + TeacherSocket.serverPort);
            System.out.println("Chờ mọi người tham gia!");
            new Thread(new TeacherTaskUdp(socketSend,socketRecieve,liveController)).start();

    }
    public static void clickButton(int number) {
        try {

            //System.out.println("Đã nhấn button");
            InetAddress address = InetAddress.getByName(TeacherSocket.serverHostName);
            int port = TeacherSocket.serverPort;
            byte[] numberOfClient = new byte[2];
            numberOfClient[0] = (byte)(2);
            numberOfClient[1] = (byte) number;

            DatagramPacket packetOfClient = new DatagramPacket(numberOfClient, numberOfClient.length, address, port);
            socketRecieve.send(packetOfClient);
            //System.out.println("Gửi thành công Id học sinh cho " + address.toString() + "," + port);
            if (LiveController.isCamera == false) {
                LiveController.isCamera = true;
            }
            else {
                LiveController.isCamera = false;
            }
            System.out.println("Gửi thành công yêu cầu button");
        } catch (IOException e) {
            System.out.println("Lỗi khi nhấn button " + e.getMessage());
        }

    }
    public void exitLive() {
        try {
            InetAddress address = InetAddress.getByName(TeacherSocket.serverHostName);
            int port = TeacherSocket.serverPort;
            byte[] messageExit = new byte[1];
            messageExit[0] = (byte)(3);
            DatagramPacket packet = new DatagramPacket(messageExit, messageExit.length, address, port);
            socketRecieve.send(packet);
            System.out.println("Gửi thành công yêu cầu exit");
            socketSend.close();
            socketRecieve.close();
            buffer.clear();
        } catch (IOException e) {
            System.out.println("Lỗi khi gửi thông điệp exit " + e.getMessage());
        }
    }

}

class TeacherTaskUdp extends Thread {
    DatagramSocket socketSend;
    static DatagramSocket socketRecieve;
    LiveController liveController;
    Map<String, byte[]> buffer = new HashMap<>();
    public TeacherTaskUdp(DatagramSocket socketSend, DatagramSocket socketRecieve ,LiveController liveController) {
        this.socketSend = socketSend;
        this.socketRecieve = socketRecieve;
        this.liveController = liveController;
    }

    @Override
    public void run() {
        try {

            while (true) {

                System.out.println("Bắt đầu nhận ảnh");
                byte[] message = new byte[TeacherSocket.maxDatagramPacketLength];
                DatagramPacket packet = new DatagramPacket(message,message.length);
                socketSend.receive(packet);
                int LORI = (message[0] & 0xff);

                if (LORI == 0) {
                    // Nhận được packet là LENGTH
                    int ID_CLIENT = (message[1] & 0xff);
                    int ID_IMAGE = (message[2] & 0xff);
                    int LENGTH_IMAGE =  (message[3] & 0xff) << 16 | (message[4] & 0xff) << 8 | (message[5] & 0xff);
                    int NUMBEROFPACKET = message[6] & 0xff;
                    byte[] imageBytes = new byte[LENGTH_IMAGE];
                    String Key = ID_IMAGE + ":" + ID_CLIENT;
                    if (buffer.containsKey(Key)) {
                        buffer.remove(Key);
                    }
                    buffer.put(Key, imageBytes);

                } else if (LORI == 1){
                    int ID_CLIENT = (message[1] & 0xff);
                    int ID_Packet = (message[2] & 0xff);
                    int sequenceNumber = (message[3] & 0xff);
                    boolean isLastPacket = ((message[4] & 0xff) == 1);
                    int destinationIndex = (sequenceNumber - 1) * (TeacherSocket.maxDatagramPacketLength-5);
                    String Key = ID_Packet + ":" + ID_CLIENT;
                    //System.out.println(sequenceNumber + ", " + isLastPacket + ", " + idPacket + ", " + destinationIndex);
                    if (buffer.containsKey(Key)) {
                        int LENGTH_IMAGE = buffer.get(Key).length;
                        if (destinationIndex >= 0 && destinationIndex < LENGTH_IMAGE) {
                            if (!isLastPacket) {
                                System.arraycopy(message, 5, buffer.get(Key), destinationIndex, TeacherSocket.maxDatagramPacketLength-5);
                            } else {
                                System.arraycopy(message, 5, buffer.get(Key), destinationIndex, LENGTH_IMAGE % (TeacherSocket.maxDatagramPacketLength-5));
                                byte[] imageBytes = buffer.get(Key);
                                if (imageBytes != null && imageBytes.length > 0) {
                                    // Tạo InputStream từ mảng byte
                                    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                                        Image newImage = new Image(inputStream); // Tạo đối tượng Image từ InputStream
                                        //longSystem.out.println("Cập nhật giao diện!");

                                        // Gọi runLater để cập nhật giao diện
                                        Platform.runLater(() -> liveController.updateImage(ID_CLIENT,newImage));
                                    } catch (Exception e) {
                                        System.err.println("Lỗi khi tạo hình ảnh: " + e.getMessage());
                                    }
                                } else {
                                    System.out.println("Ảnh null");
                                }
                                TeacherSocket.imageCount++;
                                System.out.println("Nhận ảnh thứ " + TeacherSocket.imageCount + ", " + LENGTH_IMAGE);
                            }

                            //System.out.println("Nhận thành công packet thứ " + sequenceNumber);
                        } else {
                            System.out.println("Chỉ số đích không hợp lệ: " + destinationIndex);
                        }
                    } else {
                        System.out.println("Lỗi ID_Paket bị xóa khỏi buffer!");
                    }
                } else if (LORI == 4) {
                    int ID_CLIENT = (message[1] & 0xff);
                    System.out.println("Nhận exit từ" + ID_CLIENT);
                    Platform.runLater(() -> {
                        liveController.requestExitFromStudent(ID_CLIENT);
                    });

                } else {

                }


            }

        } catch (IOException | RuntimeException e) {
            // Lỗi java.io.EOFException
            System.err.println(e);
        } finally {
            try {
                socketSend.close();
                socketRecieve.close();
            } catch (Exception e) {
                // Ignore
            }
        }
    }


}
