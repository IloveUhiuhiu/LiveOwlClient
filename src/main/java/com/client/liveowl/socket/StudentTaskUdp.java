package com.client.liveowl.socket;

import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.UdpHandler;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

class StudentTaskUdp extends Thread {
    private DatagramSocket socketSend;
    private DatagramSocket socketRecieve;
    private static Robot robot = null;
    public StudentTaskUdp(DatagramSocket socketSend, DatagramSocket socketRecieve)  {
        this.socketSend = socketSend;
        this.socketRecieve = socketRecieve;
    }
    @Override
    public void run() {
        try {
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String request = UdpHandler.receiveMsg(socketRecieve, InetAddress.getByName(StudentSocket.serverHostName),StudentSocket.serverPort);
                        System.out.println("msg: " + request);
                        if (request.equals("camera")) StudentSocket.updateCamera();
                        else if (request.equals("exit")) {
                            socketSend.close();
                            socketRecieve.close();
                            if (StudentSocket.camera != null) StudentSocket.camera.release();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Lỗi khi lắng nghe bật camera: " + e.getMessage());
                }
            });
            thread.start();
            robot = new Robot();
            captureImages(socketSend, StudentSocket.camera);
        } catch (Exception e) {
            System.out.println("Loi ham run: " + e.getMessage());
        } finally {
            socketSend.close();
            socketRecieve.close();
            if (StudentSocket.camera != null)StudentSocket.camera.release();
        }
    }
    private static void captureImages(DatagramSocket socket, VideoCapture camera){
        try {
            Mat frame;
            BufferedImage screenCapture;
            while (true) {
                ++StudentSocket.imageCount;
                System.out.println("Gửi ảnh thứ " + StudentSocket.imageCount);
                if (StudentSocket.isLive) {
                    if (StudentSocket.captureFromCamera == 1) {
                        if (camera == null) {
                            camera = new VideoCapture(0);
                        }
                        if (!camera.isOpened()) {
                            System.err.println("Error: Không mở được camera!");
                            return;
                        }
                        frame = new Mat();
                        if (camera.read(frame)) {
                            if (!frame.empty()) {
                                sendImage(socket, frame);
                            }
                        }
                    } else {
                        if (camera != null) {
                            camera.release();
                            camera = null;
                        }
                        screenCapture = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                        sendImage(socket, screenCapture);
                    }
                } else {
                    if (camera == null) {
                        camera = new VideoCapture(0);
                    }
                    if (!camera.isOpened()) {
                        System.err.println("Error: Không mở được camera!");
                        return;
                    }
                    frame = new Mat();
                    if (camera.read(frame)) {
                        if (!frame.empty()) {
                            updateImage(frame);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (camera!= null) camera.release();
        }
    }
    private static void sendImage(DatagramSocket socket, Mat frame) {
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);
        sendImage(socket, ImageData.matToBufferedImage(frame));
    }
    private static void sendImage(DatagramSocket socket, BufferedImage originalImage) {
        try {
            byte[] imageBytes = ImageData.handleImage(originalImage);
            System.out.println("Đã gửi ảnh kích thước: " + imageBytes.length + " bytes");
            sendPacketImage(socket, imageBytes);
        } catch (Exception e) {
            socket.close();
            System.out.println("Loi ham sendImage:" + e.getMessage());
        }
    }

    //    private static void sendImage(DatagramSocket socket, BufferedImage image) throws IOException {
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            ImageIO.write(image, "jpg", baos); // Sử dụng định dạng JPEG
//            byte[] imageBytes = baos.toByteArray();
//            System.out.println("Đã gửi ảnh kích thước: " + imageBytes.length + " bytes");
//            sendPacketImage(socket, imageBytes);
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
    private static void updateImage(Mat frame) {
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);
        BufferedImage bufferedImage = ImageData.matToBufferedImage(frame);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            javafx.scene.image.Image newImage = new Image(new ByteArrayInputStream(imageBytes));
            StudentSocket.cache.add(newImage);
        } catch (Exception e) {
            System.out.println("Loi ham upateImage: " + e.getMessage());
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
                System.out.println("Loi ham updateImage: " + e.getMessage());
            }
        }
    }
    private static void sendPacketImage(DatagramSocket socket, byte[] imageByteArray) throws Exception {

        int sequenceNumber = 0;
        boolean flag;
        byte[] lengthBytes = new byte[7];
        int length = imageByteArray.length;
        lengthBytes[0] = (byte) 0;
        lengthBytes[1] = (byte) (StudentSocket.studentId);
        lengthBytes[2] = (byte) (StudentSocket.imageId);
        lengthBytes[3] = (byte) (length >> 16);
        lengthBytes[4] = (byte) (length >> 8);
        lengthBytes[5] = (byte) (length);
        lengthBytes[6] = (byte) ((length + StudentSocket.maxDatagramPacketLength - 6)/(StudentSocket.maxDatagramPacketLength - 5));
        UdpHandler.sendBytesArray(socket,lengthBytes,InetAddress.getByName(StudentSocket.serverHostName),StudentSocket.serverPort);
        for (int i = 0; i < imageByteArray.length; i = i + StudentSocket.maxDatagramPacketLength - 5) {
            sequenceNumber += 1;
            byte[] message = new byte[StudentSocket.maxDatagramPacketLength];
            message[0] = (byte)(1);
            message[1] = (byte)(StudentSocket.studentId);
            message[2] = (byte)(StudentSocket.imageId);
            message[3] = (byte) (sequenceNumber);
            if ((i + StudentSocket.maxDatagramPacketLength-5) >= imageByteArray.length) {
                flag = true;
                message[4] = (byte) (1);
            } else {
                flag = false;
                message[4] = (byte) (0);
            }
            if (!flag) {
                System.arraycopy(imageByteArray, i, message, 5, StudentSocket.maxDatagramPacketLength - 5);
            } else {
                System.arraycopy(imageByteArray, i, message, 5, imageByteArray.length - i);
            }
            UdpHandler.sendBytesArray(socket,message,InetAddress.getByName(StudentSocket.serverHostName),StudentSocket.serverPort);
        }
        StudentSocket.imageId = (StudentSocket.imageId + 1) % 5;
    }

}
