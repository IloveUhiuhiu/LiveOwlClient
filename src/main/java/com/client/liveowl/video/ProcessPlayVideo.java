package com.client.liveowl.video;
import com.client.liveowl.util.UdpHandler;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import static com.client.liveowl.AppConfig.*;

public class ProcessPlayVideo {
    private static int serverPort;
    public static int port = 5215;
    public Map<String, byte[]> imageBuffer = new HashMap<>();
    public ConcurrentLinkedQueue<Image> packetBuffer = new ConcurrentLinkedQueue<>();
    public static DatagramSocket socket;
    public static volatile boolean _isLivestream =true;

    public static synchronized boolean isLivestream() {
        return _isLivestream;
    }
    public static synchronized void setLivestream(boolean isLivestream) {
        _isLivestream = isLivestream;
    }
    public ProcessPlayVideo() {
        try {
            serverPort = VIDEO_SERVER_PORT;
            socket = new DatagramSocket(port);
            setLivestream(true);
        } catch (SocketException e) {
            System.err.println("Lỗi trong khi khởi tạo Socket :" + e.getMessage());
        }
    }
    public void getVideo(String code, String clientId) throws IOException {
        String connect = clientId + ":" + code;
        //UdpHandler.sendMsg(socketSend, Authentication.getUserId(), InetAddress.getByName(serverHostName), serverPort);
        System.out.println("Gửi thành công chuỗi connect đến server!");
        //UdpHandler.sendMsg(socketSend, "teacher", InetAddress.getByName(serverHostName), serverPort);
        System.out.println("Gửi role teacher!");
        UdpHandler.sendMsg(socket, connect, InetAddress.getByName(SERVER_HOST_NAME), serverPort);
        System.out.println("Gửi mã " + code + " cuộc thi thành công!");
        serverPort += UdpHandler.receivePort(socket);
        System.out.println("Port mới là :" + serverPort);
        System.out.println("Chờ mọi người tham gia!");
        try {
        while (isLivestream()) {
            byte[] message = new byte[MAX_DATAGRAM_PACKET_LENGTH];
            try {
                UdpHandler.receiveBytesArr(socket, message);
            } catch (SocketTimeoutException e) {
                if (!isLivestream()) {
                    break;
                }
                continue;
            }

            int packetType = (message[0] & 0xff);
            //System.out.println(packetType);
            if (packetType == 0) {
                int imageId = (message[1] & 0xff);
                int lengthOfImage = (message[2] & 0xff) << 16 | (message[3] & 0xff) << 8 | (message[4] & 0xff);
                byte[] imageBytes = new byte[lengthOfImage];
                String Key = imageId + ":" + clientId;
                imageBuffer.put(Key, imageBytes);
                System.out.println("nhận độ dai " + lengthOfImage);
            } else if (packetType == 1) {
                int packetId = (message[1] & 0xff);
                int sequenceNumber = (message[2] & 0xff);
                boolean isLastPacket = ((message[3] & 0xff) == 1);
                int destinationIndex = (sequenceNumber - 1) * (MAX_DATAGRAM_PACKET_LENGTH - 4);
                String Key = packetId + ":" + clientId;
                //System.out.println(sequenceNumber +", " + packetId + ":" + clientId + ", " + destinationIndex);
                if (imageBuffer.containsKey(Key)) {
                    byte[] imageBytes = imageBuffer.get(Key);

                    //TeacherSocket.numberBuffer.put(Key, TeacherSocket.numberBuffer.get(Key) - 1) ;
                    int lengthOfImage = imageBytes.length;
                    if (destinationIndex >= 0 && destinationIndex < lengthOfImage) {
                        if (destinationIndex + MAX_DATAGRAM_PACKET_LENGTH - 4 < lengthOfImage) {
                            System.arraycopy(message, 4, imageBytes, destinationIndex, MAX_DATAGRAM_PACKET_LENGTH - 4);
                        } else {
                            System.arraycopy(message, 4, imageBytes, destinationIndex, lengthOfImage % (MAX_DATAGRAM_PACKET_LENGTH - 4));
                        }
                    } else {
                        System.out.println("Chỉ số đích không hợp lệ: " + destinationIndex + ", lengthOFImage" + lengthOfImage);
                    }
                    if (isLastPacket) {
                        if (imageBytes != null && imageBytes.length > 0) {
                            try {
                                Image newImage = new Image(new ByteArrayInputStream(imageBytes));
                                packetBuffer.add(newImage);
                                System.out.println("Size " + packetBuffer.size());
                            } catch (Exception e) {
                                System.err.println("Lỗi khi tạo hình ảnh: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Ảnh null");
                        }
                        imageBuffer.remove(Key);
                    }

                } else {
                    System.out.println("Lỗi ID_Paket bị xóa khỏi buffer!");
                }
            } else if (packetType == 2){
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        setLivestream(false);
                        System.out.println("issLive = false");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                });
                thread.start();

            } else {

            }
        }
        System.out.println("keets truc");
        } catch(Exception e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println("Đang đóng socket");
            if (socket != null) socket.close();
            imageBuffer.clear();
            System.out.println("Đóng socket thành công");
        }

    }

}

