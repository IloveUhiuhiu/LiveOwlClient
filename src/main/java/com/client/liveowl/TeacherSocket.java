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
    public static int SERVER_PORT = 9000;
    public static String SERVER_HOSTNAME = "127.0.0.1";
    public static int LENGTH = 32768;
    public static int CLIENT_PORT_SEND = 6000;
    public static int CLIENT_PORT_RECEIVE = 5000;
    Map<Integer, byte[]> buffer = new HashMap<>();
    public static int COUNT = 0;
    DatagramSocket socketSend;
    static DatagramSocket socketRecieve;
    public TeacherSocket() {
        try {
            socketSend = new DatagramSocket(CLIENT_PORT_SEND);
            socketRecieve = new DatagramSocket(CLIENT_PORT_RECEIVE);
        } catch (SocketException e) {
            System.err.println("Lỗi trong khi khởi tạo Socket :" + e.getMessage());
        }
    }
    public void sendMsg(String message) throws IOException {
        InetAddress address = InetAddress.getByName(SERVER_HOSTNAME);
        int port = SERVER_PORT;
        byte[] messageBytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, port);
        socketSend.send(packet);
    }
    public void receivePort() throws IOException {
        byte[] messageBytes = new byte[1];
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);
        socketSend.receive(packet);
        SERVER_PORT += (messageBytes[0] & 0xff);
    }

    public void LiveStream(String code, LiveController liveController) throws IOException {

            sendMsg("connect");
            System.out.println("Gửi thành công chuỗi connect đến server!");
            sendMsg("teacher");
            System.out.println("Gửi role teacher!");
            sendMsg(code);
            System.out.println("Gửi mã " + code + " cuộc thi thành công!");
            receivePort();
            System.out.println("Port mới là :" + TeacherSocket.SERVER_PORT);
            System.out.println("Chờ mọi người tham gia!");
            new Thread(new TeacherTaskUdp(socketSend,socketRecieve,liveController)).start();

    }
    public static void sendMsgCamera(String message) throws IOException {
        InetAddress address = InetAddress.getByName(TeacherSocket.SERVER_HOSTNAME);
        int port = TeacherSocket.SERVER_PORT + 50;
        byte[] messageBytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, port);
        socketRecieve.send(packet);
    }
    public static void clickButton(int number) {
        try {

            System.out.println("Đã nhấn button");
            InetAddress address = InetAddress.getByName(TeacherSocket.SERVER_HOSTNAME);
            int port = TeacherSocket.SERVER_PORT + 50;
            byte[] numberOfClient = new byte[1];
            numberOfClient[0] = (byte) number;
            DatagramPacket packetOfClient = new DatagramPacket(numberOfClient, numberOfClient.length, address, port);
            socketRecieve.send(packetOfClient);
            System.out.println("Gửi thành công Id học sinh");
            sendMsgCamera("camera");
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
                TeacherSocket.COUNT++;
                System.out.println("Nhận ảnh thứ " + TeacherSocket.COUNT);
                System.out.println("Bắt đầu nhận ảnh");
                byte[] message = new byte[TeacherSocket.LENGTH];
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

                } else {
                    int ID_CLIENT = (message[1] & 0xff);
                    int ID_Packet = (message[2] & 0xff);
                    int sequenceNumber = (message[3] & 0xff);
                    boolean isLastPacket = ((message[4] & 0xff) == 1);
                    int destinationIndex = (sequenceNumber - 1) * (TeacherSocket.LENGTH-5);
                    String Key = ID_Packet + ":" + ID_CLIENT;
                    //System.out.println(sequenceNumber + ", " + isLastPacket + ", " + idPacket + ", " + destinationIndex);
                    if (buffer.containsKey(Key)) {
                        int LENGTH_IMAGE = buffer.get(Key).length;
                        if (destinationIndex >= 0 && destinationIndex < LENGTH_IMAGE) {
                            if (!isLastPacket) {
                                System.arraycopy(message, 5, buffer.get(Key), destinationIndex, TeacherSocket.LENGTH-5);
                            } else {
                                System.arraycopy(message, 5, buffer.get(Key), destinationIndex, LENGTH_IMAGE % (TeacherSocket.LENGTH-5));
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
                            }
                            //System.out.println("Nhận thành công packet thứ " + sequenceNumber);
                        } else {
                            System.out.println("Chỉ số đích không hợp lệ: " + destinationIndex);
                        }
                    } else {
                        System.out.println("Lỗi ID_Paket bị xóa khỏi buffer!");
                    }
                }



                System.out.println("Nhận thành công 1 ảnh!");
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
