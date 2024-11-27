package com.client.liveowl.socket;

import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.UdpHandler;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import static com.client.liveowl.AppConfig.maxDatagramPacketLength;

class TeacherTaskUdp extends Thread {
    DatagramSocket socketSend;
    DatagramSocket socketRecieve;
    public TeacherTaskUdp(DatagramSocket socketSend, DatagramSocket socketRecieve) throws SocketException {
        this.socketSend = socketSend;
        this.socketRecieve = socketRecieve;
        socketSend.setSoTimeout(100);
    }
    public void run() {
        try {
            while (TeacherSocket.isLive()) {
                byte[] message = new byte[maxDatagramPacketLength];
                try {
                    UdpHandler.receiveBytesArr(socketSend, message);
                    processMessage(message);
                } catch (SocketTimeoutException e) {
                    if (!TeacherSocket.isLive) {
                        break;
                    }
                }
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
        int destinationIndex = (sequenceNumber - 1) * (maxDatagramPacketLength - 12);
        String key = packetId + ":" + clientId;

        if (TeacherSocket.imageBuffer.containsKey(key)) {
            byte[] imageBytes = TeacherSocket.imageBuffer.get(key);
            int lengthOfImage = imageBytes.length;

            if (destinationIndex >= 0 && destinationIndex < lengthOfImage) {
                int bytesToCopy = Math.min(maxDatagramPacketLength - 12, lengthOfImage - destinationIndex);
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
        //TeacherSocket.isExit.put(clientId, true);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                TeacherSocket.queueExit.add(clientId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //TeacherSocket.isExit.remove(clientId);
        }).start();
    }

    private void closeResources() {
        try {
            System.out.println("Đang đóng socket");
            if (socketSend != null) {
                socketSend.close();
            }
            if (socketRecieve != null) {
                socketRecieve.close();
            }
            if (TeacherSocket.imageBuffer != null) TeacherSocket.imageBuffer.clear();
            if (TeacherSocket.queueExit != null) TeacherSocket.queueExit.clear();
            if (TeacherSocket.queueImage != null) TeacherSocket.queueImage.clear();
            TeacherSocket.setLive(false);
            System.out.println("Đóng TeacherTaskUdp thành công");
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng socket: " + e.getMessage());
        }
    }
//    @Override
//    public void run() {
//        try {
//
//            while (TeacherSocket.isLive) {
//                //System.out.println("Bắt đầu nhận ảnh");
//                byte[] message = new byte[TeacherSocket.maxDatagramPacketLength];
//                try {
//                    UdpHandler.receiveBytesArr(socketSend, message);
//                } catch (SocketTimeoutException e) {
//                    if (!TeacherSocket.isLive) {
//                        break;
//                    }
//                    continue;
//                }
//
//                int packetType = (message[0] & 0xff);
//                //System.out.println(packetType);
//                if (packetType == 0) {
//                    String clientId = new String(message, 1, 8);
//                    int imageId = (message[9] & 0xff);
//                    int lengthOfImage =  (message[10] & 0xff) << 16 | (message[11] & 0xff) << 8 | (message[12] & 0xff);
//                    byte[] imageBytes = new byte[lengthOfImage];
//                    String Key = imageId + ":" + clientId;
//                    TeacherSocket.imageBuffer.put(Key, imageBytes);
//                    //TeacherSocket.numberBuffer.put(Key, numberOfImage);
//                    System.out.println("nhận độ dai " + lengthOfImage);
//                } else if (packetType == 1){
//                    String clientId = new String(message, 1, 8);
//                    int packetId = (message[9] & 0xff);
//                    int sequenceNumber = (message[10] & 0xff);
//                    boolean isLastPacket = ((message[11] & 0xff) == 1);
//                    int destinationIndex = (sequenceNumber - 1) * (TeacherSocket.maxDatagramPacketLength-12);
//                    String Key = packetId + ":" + clientId;
//                    //System.out.println(sequenceNumber +", " + packetId + ":" + clientId + ", " + destinationIndex);
//                    if (TeacherSocket.imageBuffer.containsKey(Key)) {
//                        byte[] imageBytes = TeacherSocket.imageBuffer.get(Key);
//
//                        //TeacherSocket.numberBuffer.put(Key, TeacherSocket.numberBuffer.get(Key) - 1) ;
//                        int lengthOfImage = imageBytes.length;
//                        if (destinationIndex >= 0 && destinationIndex < lengthOfImage) {
//                            if (destinationIndex + TeacherSocket.maxDatagramPacketLength - 12 < lengthOfImage) {
//                                System.arraycopy(message, 12, imageBytes, destinationIndex, TeacherSocket.maxDatagramPacketLength-12);
//                            } else {
//                                System.arraycopy(message, 12, imageBytes, destinationIndex, lengthOfImage % (TeacherSocket.maxDatagramPacketLength-12));
//                            }
//                        } else {
//                            System.out.println("Chỉ số đích không hợp lệ: " + destinationIndex + ", lengthOFImage" + lengthOfImage);
//                        }
//                        if (isLastPacket) {
//                            ++TeacherSocket.imageAtual;
//                            System.out.println("Số ảnh nhận thực sự: " + TeacherSocket.imageAtual);
//                            if (imageBytes != null && imageBytes.length > 0) {
//                                TeacherSocket.imageCount++;
//                                System.out.println("Hiển thị ảnh thứ " + TeacherSocket.imageCount + ", " + imageBytes.length + ", " + TeacherSocket.sendList.size());
//                                try {
//                                    Image newImage = new Image(new ByteArrayInputStream(imageBytes));
//                                    TeacherSocket.sendList.add(new ImageData(clientId, newImage));
//                                } catch (Exception e) {
//                                    System.err.println("Lỗi khi tạo hình ảnh: " + e.getMessage());
//                                }
//                            } else {
//                                System.out.println("Ảnh null");
//                            }
//                            TeacherSocket.imageBuffer.remove(Key);
//                        }
//
//                    } else {
//                        System.out.println("Lỗi ID_Paket bị xóa khỏi buffer!");
//                    }
//                } else if (packetType == 4) {
//                    String clientId = new String(message,1,8);
//                    System.out.println("Nhận exit từ" + clientId);
//                    TeacherSocket.isExit.put(clientId,true);
//                    TeacherSocket.clientExit.add(clientId);
//                    new Thread(() -> {
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                        TeacherSocket.isExit.remove(clientId);
//                    }).start();
//                } else {
//
//                }
//            }
//            //System.out.println("Thoát while");
//
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        } finally {
//            System.out.println("Đang đóng socket");
//            socketSend.close();
//            socketRecieve.close();
//            TeacherSocket.imageBuffer.clear();
//            System.out.println("Đóng socket thành công");
//        }
//    }

}
