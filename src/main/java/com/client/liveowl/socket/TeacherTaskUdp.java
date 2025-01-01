package com.client.liveowl.socket;

import com.client.liveowl.model.User;
import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.UdpHandler;
import com.client.liveowl.util.UserHandler;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.net.DatagramSocket;
import java.net.SocketException;
import static com.client.liveowl.AppConfig.MAX_DATAGRAM_PACKET_LENGTH;

class TeacherTaskUdp extends Thread {
    DatagramSocket socketSend;
    DatagramSocket socketRecieve;
    public TeacherTaskUdp(DatagramSocket socketSend, DatagramSocket socketRecieve) {
        this.socketSend = socketSend;
        this.socketRecieve = socketRecieve;
    }
    public void run() {
        try {
            while (TeacherSocket.isRunning()) {
                byte[] message = new byte[MAX_DATAGRAM_PACKET_LENGTH];
                UdpHandler.receiveBytesArr(socketSend, message);
                processMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Error in TeacherTaskUdp: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void processMessage(byte[] message) {
        int packetType = (message[0] & 0xff);
        String clientId = new String(message, 1, 8).trim();
        if (!TeacherSocket.listUsers.containsKey(clientId)) {
            User user = UserHandler.getInforUserById(clientId);
            TeacherSocket.listUsers.put(clientId, user);
        }
        switch (packetType) {
            case 0:
                handleImageLengthPacket(message);
                break;
            case 1:
                handleImageDataPacket(message);
                break;
            case 4:
                handleExitPacket(message);
                break;
            default:
                System.out.println("Gói tin không xác định: " + packetType);
                break;
        }
    }

    private void handleImageLengthPacket(byte[] message) {
        String clientId = new String(message, 1, 8).trim();
        int imageId = (message[9] & 0xff);
        int lengthOfImage = (message[10] & 0xff) << 16 | (message[11] & 0xff) << 8 | (message[12] & 0xff);
        byte[] imageBytes = new byte[lengthOfImage];
        String key = imageId + ":" + clientId;
        TeacherSocket.imageBuffer.put(key, imageBytes);
        System.out.println("Nhận độ dài: " + lengthOfImage);
    }

    private void handleImageDataPacket(byte[] message) {
        String clientId = new String(message, 1, 8).trim();
        int packetId = (message[9] & 0xff);
        int sequenceNumber = (message[10] & 0xff);
        boolean isLastPacket = ((message[11] & 0xff) == 1);
        int destinationIndex = (sequenceNumber - 1) * (MAX_DATAGRAM_PACKET_LENGTH - 12);
        String key = packetId + ":" + clientId;

        if (TeacherSocket.imageBuffer.containsKey(key)) {
            byte[] imageBytes = TeacherSocket.imageBuffer.get(key);
            int lengthOfImage = imageBytes.length;

            if (destinationIndex >= 0 && destinationIndex < lengthOfImage) {
                int bytesToCopy = Math.min(MAX_DATAGRAM_PACKET_LENGTH - 12, lengthOfImage - destinationIndex);
                System.arraycopy(message, 12, imageBytes, destinationIndex, bytesToCopy);
                if (isLastPacket) {
                    handleCompleteImage(key, clientId, imageBytes);
                }
            } else {
                System.out.println("Error destinationIndex: " + destinationIndex + ", lengthOfImage: " + lengthOfImage);
            }
        } else {
            System.out.println("Error " + key + " is not in buffer!");
        }
    }

    private void handleCompleteImage(String key, String clientId, byte[] imageBytes) {
        ++TeacherSocket.imageAtual;
        System.out.println("Số ảnh nhận thực sự: " + TeacherSocket.imageAtual);
        if (imageBytes != null && imageBytes.length > 0) {
            TeacherSocket.imageCount++;
            System.out.println("Hiển thị ảnh thứ " + TeacherSocket.imageCount + ", " + imageBytes.length + ", " + TeacherSocket.queueImage.size());
            try {
                Image newImage = new Image(new ByteArrayInputStream(imageBytes));
                TeacherSocket.queueImage.add(new ImageData(clientId, newImage));
            } catch (Exception e) {
                System.err.println("Lỗi khi tạo hình ảnh: " + e.getMessage());
            }
        } else {
            System.out.println("Ảnh null");
        }
        TeacherSocket.imageBuffer.remove(key);
    }

    private void handleExitPacket(byte[] message) {
        String clientId = new String(message, 1, 8).trim();
        System.out.println("Nhận exit từ " + clientId);
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                TeacherSocket.queueExit.add(clientId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void closeResources() {
        try {
            System.out.println("Đang đóng socket");
            TeacherSocket.setRunning(false);
            if (socketSend != null) {
                socketSend.close();
            }
            if (socketRecieve != null) {
                socketRecieve.close();
            }
            if (TeacherSocket.imageBuffer != null) TeacherSocket.imageBuffer.clear();
            if (TeacherSocket.queueExit != null) TeacherSocket.queueExit.clear();
            if (TeacherSocket.queueImage != null) TeacherSocket.queueImage.clear();
            System.out.println("Đóng TeacherTaskUdp thành công");
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng socket: " + e.getMessage());
        }
    }


}
