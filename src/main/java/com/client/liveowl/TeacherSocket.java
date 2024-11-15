package com.client.liveowl;
import com.client.liveowl.controller.LiveController;
import com.client.liveowl.model.ImageData;
import com.client.liveowl.util.UdpHandler;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


public class TeacherSocket{
public static int maxDatagramPacketLength = 1500;
public static int serverPort = 9000;
public static String serverHostName = "127.0.0.1";
public static int clientPortSend = 6000;
public static int clientPortReceive = 5000;
public static Map<String, byte[]> buffer = new HashMap<>();
public static Map<String, Integer> numberBuffer = new HashMap<>();
public static int imageCount = 0;
public static int imageAtual = 0;
public static DatagramSocket socketSend;
public static DatagramSocket socketRecieve;
public static ConcurrentLinkedQueue<ImageData> sendList = new ConcurrentLinkedQueue<>();
public static int isExit = -1;
public TeacherSocket() {
    try {
        serverPort = 9000;
        socketSend = new DatagramSocket(clientPortSend);
        socketRecieve = new DatagramSocket(clientPortReceive);
    } catch (SocketException e) {
        System.err.println("Lỗi trong khi khởi tạo Socket :" + e.getMessage());
    }
}
public void LiveStream(String code, LiveController liveController) throws IOException {
    UdpHandler.sendMsg(socketSend,"connect",InetAddress.getByName(serverHostName),serverPort);
    System.out.println("Gửi thành công chuỗi connect đến server!");
    UdpHandler.sendMsg(socketSend,"teacher",InetAddress.getByName(serverHostName),serverPort);
    System.out.println("Gửi role teacher!");
    UdpHandler.sendMsg(socketSend,code,InetAddress.getByName(serverHostName),serverPort);
    System.out.println("Gửi mã " + code + " cuộc thi thành công!");
    serverPort +=  UdpHandler.receivePort(socketSend);
    System.out.println("Port mới là :" + serverPort);
    System.out.println("Chờ mọi người tham gia!");
    new Thread(new TeacherTaskUdp(socketSend,socketRecieve)).start();
    //new Thread(new getImage()).start();
}
public static void clickBtnCamera(int number) {
    try {
        UdpHandler.sendRequestCamera(socketRecieve,number,InetAddress.getByName(serverHostName),serverPort);
        System.out.println("Gửi thành công yêu cầu button camera");
    } catch (IOException e) {
        System.out.println("Lỗi khi nhấn button camera" + e.getMessage());
    }
}
public void clickBtnExit() {
    try {
        UdpHandler.sendRequestExitToStudents(socketRecieve,InetAddress.getByName(serverHostName),serverPort);
        System.out.println("Gửi thành công yêu cầu exit");
        socketSend.close();
        socketRecieve.close();
        buffer.clear();
    } catch (IOException e) {
        System.out.println("Lỗi khi gửi thông điệp exit " + e.getMessage());
    }
}
public static synchronized int getExit() {
    return isExit;
}
public static synchronized void setExit(int exit) {
    isExit = exit;
}
}

class TeacherTaskUdp extends Thread {
DatagramSocket socketSend;
static DatagramSocket socketRecieve;
public TeacherTaskUdp(DatagramSocket socketSend, DatagramSocket socketRecieve) {
    this.socketSend = socketSend;
    this.socketRecieve = socketRecieve;
}

@Override
public void run() {
    try {

        while (true) {
            //System.out.println("Bắt đầu nhận ảnh");
            byte[] message = new byte[TeacherSocket.maxDatagramPacketLength];
            UdpHandler.receiveBytesArr(socketSend,message);
            int packetType = (message[0] & 0xff);
            if (packetType == 0) {
                int clientId = (message[1] & 0xff);
                int imageId = (message[2] & 0xff);
                int lengthOfImage =  (message[3] & 0xff) << 16 | (message[4] & 0xff) << 8 | (message[5] & 0xff);
                int numberOfImage = message[6] & 0xff;
                byte[] imageBytes = new byte[lengthOfImage];
                String Key = imageId + ":" + clientId;
                TeacherSocket.buffer.put(Key, imageBytes);
                TeacherSocket.numberBuffer.put(Key, numberOfImage);
                System.out.println("nhận độ dai " + lengthOfImage);

            } else if (packetType == 1){
                int clientId = (message[1] & 0xff);
                int packetId = (message[2] & 0xff);
                int sequenceNumber = (message[3] & 0xff);
                boolean isLastPacket = ((message[4] & 0xff) == 1);
                int destinationIndex = (sequenceNumber - 1) * (TeacherSocket.maxDatagramPacketLength-5);
                String Key = packetId + ":" + clientId;
                //System.out.println(sequenceNumber +", " + packetId + ":" + clientId + ", " + destinationIndex);
                if (TeacherSocket.buffer.containsKey(Key)) {
                    byte[] imageBytes = TeacherSocket.buffer.get(Key);

                    TeacherSocket.numberBuffer.put(Key, TeacherSocket.numberBuffer.get(Key) - 1) ;
                    int lengthOfImage = imageBytes.length;
                    if (destinationIndex >= 0 && destinationIndex < lengthOfImage) {
                        if (destinationIndex + TeacherSocket.maxDatagramPacketLength - 5 < lengthOfImage) {
                            System.arraycopy(message, 5, imageBytes, destinationIndex, TeacherSocket.maxDatagramPacketLength-5);
                        } else {
                            System.arraycopy(message, 5, imageBytes, destinationIndex, lengthOfImage % (TeacherSocket.maxDatagramPacketLength-5));

                        }
                    } else {
                        System.out.println("Chỉ số đích không hợp lệ: " + destinationIndex + ", lengthOFImage" + lengthOfImage);
                    }

                    if (TeacherSocket.numberBuffer.get(Key) == 0) {
                        ++TeacherSocket.imageAtual;
                        System.out.println("Số ảnh nhận thực sự: " + TeacherSocket.imageAtual);
                        if (imageBytes != null && imageBytes.length > 0) {
                            TeacherSocket.imageCount++;
                            System.out.println("Hiển thị ảnh thứ " + TeacherSocket.imageCount + ", " + imageBytes.length + ", " + TeacherSocket.sendList.size());
                            try {
                                Image newImage = new Image(new ByteArrayInputStream(imageBytes));
                                TeacherSocket.sendList.add(new ImageData(clientId, newImage));
                            } catch (Exception e) {
                                System.err.println("Lỗi khi tạo hình ảnh: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Ảnh null");
                        }
                        TeacherSocket.buffer.remove(Key);
                    }
                } else {
                    System.out.println("Lỗi ID_Paket bị xóa khỏi buffer!");
                }
            } else if (packetType == 4) {
                int clientId = (message[1] & 0xff);
                System.out.println("Nhận exit từ" + clientId);
                TeacherSocket.setExit(clientId);
            } else {

            }
        }

    } catch (Exception e) {
        System.err.println(e.getMessage());
    } finally {
        try {
            socketSend.close();
            socketRecieve.close();
            TeacherSocket.buffer.clear();
        } catch (Exception e) {
            // Ignore
        }
    }
}

}