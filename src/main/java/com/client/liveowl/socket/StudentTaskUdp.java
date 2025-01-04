package com.client.liveowl.socket;

import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ImageData;
import com.client.liveowl.util.UdpHandler;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.DatagramSocket;
import java.net.InetAddress;
import static com.client.liveowl.socket.StudentSocket.camera;
import static com.client.liveowl.AppConfig.*;

class StudentTaskUdp extends Thread {
    private static int imageId = 0;
    private static int countImages = 0;
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
            Thread listenerThread = new Thread(() -> {
                try {
                    while (StudentSocket.isRunning()) {
                        String request = UdpHandler.receiveMsg(socketRecieve);
                        System.out.println("msg: " + request);
                        switch (request) {
                            case "camera":
                                isCamera = (isCamera == 1) ? 0 : 1;
                                break;
                            case "exit":
                                System.out.println("Đã exit");
                                cleanupResources();
                                break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error in camera or exit: " + e.getMessage());
                }
            });
            listenerThread.start();
            robot = new Robot();
            captureImages(socketSend, camera);
        } catch (Exception e) {
            System.out.println("Error in StudentTaskUdp: " + e.getMessage());
        } finally {
            System.out.println("Đóng StudentTaskUdp thành công!!!");
            cleanupResources();
        }
    }
    private void captureImages(DatagramSocket socket, VideoCapture camera){
        try {
            Mat frame;
            BufferedImage screenCapture;
            while (StudentSocket.isRunning()) {
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
            System.out.println("Error in CaptureImage: " + e.getMessage());
        } finally {
            cleanupResources();
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
            System.out.println("Error in sendImage:" + e.getMessage());
        }
    }

    private static void sendPacketImage(DatagramSocket socket, byte[] imageByteArray) throws Exception {
        ++countImages;
        System.out.println("Gửi ảnh thứ " + countImages + " image, length: " + imageByteArray.length );
        int sequenceNumber = 0;
        boolean flag;
        byte[] lengthBytes = new byte[13];
        int length = imageByteArray.length;
        lengthBytes[0] = (byte) 0;
        System.arraycopy(Authentication.getUserId().getBytes(), 0,lengthBytes, 1, 8);
        lengthBytes[9] = (byte) (imageId);
        lengthBytes[10] = (byte) (length >> 16);
        lengthBytes[11] = (byte) (length >> 8);
        lengthBytes[12] = (byte) (length);

        UdpHandler.sendBytesArray(socket,lengthBytes,InetAddress.getByName(SERVER_HOST_NAME),StudentSocket.newServerPort);
        for (int i = 0; i < imageByteArray.length; i = i + MAX_DATAGRAM_PACKET_LENGTH - 12) {
            sequenceNumber += 1;
            byte[] message = new byte[MAX_DATAGRAM_PACKET_LENGTH];
            message[0] = (byte)(1);
            System.arraycopy(Authentication.getUserId().getBytes(), 0,message , 1, 8);
            message[9] = (byte)(imageId);
            message[10] = (byte) (sequenceNumber);
            if ((i + MAX_DATAGRAM_PACKET_LENGTH -12) >= imageByteArray.length) {
                flag = true;
                message[11] = (byte) (1);
            } else {
                flag = false;
                message[11] = (byte) (0);
            }
            if (!flag) {
                System.arraycopy(imageByteArray, i, message, 12, MAX_DATAGRAM_PACKET_LENGTH - 12);
            } else {
                System.arraycopy(imageByteArray, i, message, 12, imageByteArray.length - i);
            }
            UdpHandler.sendBytesArray(socket,message,InetAddress.getByName(SERVER_HOST_NAME),StudentSocket.newServerPort);
        }
        imageId = (imageId + 1) % 5;
    }

    private void cleanupResources() {
        try {
            StudentSocket.setRunning(false);
            if (camera != null) camera.release();
            if (socketSend != null) socketSend.close();
            if (socketRecieve != null) socketRecieve.close();

        } catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }


    }

}
