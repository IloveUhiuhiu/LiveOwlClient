package com.client.liveowl.socket;

import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.UdpHandler;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.net.DatagramSocket;

class TeacherTaskUdp extends Thread {
    DatagramSocket socketSend;
    DatagramSocket socketRecieve;
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
                    TeacherSocket.imageBuffer.put(Key, imageBytes);
                    //TeacherSocket.numberBuffer.put(Key, numberOfImage);
                    System.out.println("nhận độ dai " + lengthOfImage);
                } else if (packetType == 1){
                    int clientId = (message[1] & 0xff);
                    int packetId = (message[2] & 0xff);
                    int sequenceNumber = (message[3] & 0xff);
                    boolean isLastPacket = ((message[4] & 0xff) == 1);
                    int destinationIndex = (sequenceNumber - 1) * (TeacherSocket.maxDatagramPacketLength-5);
                    String Key = packetId + ":" + clientId;
                    //System.out.println(sequenceNumber +", " + packetId + ":" + clientId + ", " + destinationIndex);
                    if (TeacherSocket.imageBuffer.containsKey(Key)) {
                        byte[] imageBytes = TeacherSocket.imageBuffer.get(Key);

                        //TeacherSocket.numberBuffer.put(Key, TeacherSocket.numberBuffer.get(Key) - 1) ;
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
                        if (isLastPacket) {
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
                            TeacherSocket.imageBuffer.remove(Key);
                        }

//                        if (TeacherSocket.numberBuffer.get(Key) == 0) {
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
                TeacherSocket.imageBuffer.clear();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

}
