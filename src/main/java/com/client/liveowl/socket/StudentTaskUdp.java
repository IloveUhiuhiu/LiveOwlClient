package com.client.liveowl.socket;

import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.UdpHandler;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import static com.client.liveowl.socket.StudentSocket.camera;

class StudentTaskUdp extends Thread {
    private DatagramSocket socketSend;
    private DatagramSocket socketRecieve;
    private static Robot robot = null;
    private static int isCamera = 0;
    public StudentTaskUdp(DatagramSocket socketSend, DatagramSocket socketRecieve)  {
        this.socketSend = socketSend;
        this.socketRecieve = socketRecieve;
    }
    @Override
    public void run() {
        try {
            Thread thread = new Thread(() -> {
                try {
                    while (StudentSocket.isLive) {
                        String request = UdpHandler.receiveMsg(socketRecieve, InetAddress.getByName(StudentSocket.serverHostName),StudentSocket.serverPort);
                        System.out.println("msg: " + request);
                        if (request.equals("camera")) isCamera = 1;
                        else if (request.equals("exit")) {
                            System.out.println("Đã exit");
                            StudentSocket.isLive = false;
                            socketSend.close();
                            socketRecieve.close();
                            if (camera != null) camera.release();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Lỗi khi lắng nghe bật camera: " + e.getMessage());
                }
            });
            thread.start();
            robot = new Robot();
            captureImages(socketSend, camera);
        } catch (Exception e) {
            System.out.println("Loi ham run: " + e.getMessage());
        } finally {
            System.out.println("Đóng socket thành công!!!");
            socketSend.close();
            socketRecieve.close();
            if (camera != null)  camera.release();
        }
    }
    private static void captureImages(DatagramSocket socket, VideoCapture camera){
        try {
            Mat frame;
            BufferedImage screenCapture;
            while (StudentSocket.isLive) {
                ++StudentSocket.imageCount;
                System.out.println("Gửi ảnh thứ " + StudentSocket.imageCount);

                if (isCamera == 1) {
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
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (camera != null) camera.release();
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

    private static void sendPacketImage(DatagramSocket socket, byte[] imageByteArray) throws Exception {

        int sequenceNumber = 0;
        boolean flag;
        byte[] lengthBytes = new byte[13];
        int length = imageByteArray.length;
        lengthBytes[0] = (byte) 0;
        System.arraycopy(Authentication.getUserId().getBytes(), 0,lengthBytes, 1, 8);
        lengthBytes[9] = (byte) (StudentSocket.imageId);
        lengthBytes[10] = (byte) (length >> 16);
        lengthBytes[11] = (byte) (length >> 8);
        lengthBytes[12] = (byte) (length);

        UdpHandler.sendBytesArray(socket,lengthBytes,InetAddress.getByName(StudentSocket.serverHostName),StudentSocket.serverPort);
        for (int i = 0; i < imageByteArray.length; i = i + StudentSocket.maxDatagramPacketLength - 12) {
            sequenceNumber += 1;
            byte[] message = new byte[StudentSocket.maxDatagramPacketLength];
            message[0] = (byte)(1);
            System.arraycopy(Authentication.getUserId().getBytes(), 0,message , 1, 8);
            message[9] = (byte)(StudentSocket.imageId);
            message[10] = (byte) (sequenceNumber);
            if ((i + StudentSocket.maxDatagramPacketLength-12) >= imageByteArray.length) {
                flag = true;
                message[11] = (byte) (1);
            } else {
                flag = false;
                message[11] = (byte) (0);
            }
            if (!flag) {
                System.arraycopy(imageByteArray, i, message, 12, StudentSocket.maxDatagramPacketLength - 12);
            } else {
                System.arraycopy(imageByteArray, i, message, 12, imageByteArray.length - i);
            }
            UdpHandler.sendBytesArray(socket,message,InetAddress.getByName(StudentSocket.serverHostName),StudentSocket.serverPort);
        }
        StudentSocket.imageId = (StudentSocket.imageId + 1) % 5;
    }

}
