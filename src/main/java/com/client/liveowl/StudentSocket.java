package com.client.liveowl;

import com.client.liveowl.util.UdpHandler;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Random;
public class StudentSocket{
    public static int serverPort = 9000;
    public static final String serverHostName = "127.0.0.1";
    public static int clientPortSend = 8000;
    public static int clientPortReceive = 7000;
    public static final int maxDatagramPacketLength = 32768;
    public static int imageId = 0;
    public static int studentId = 0;
    public static int imageCount = 0;
    public static Random rand = new Random();
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static final VideoCapture camera = new VideoCapture(0);
    public static DatagramSocket socketSend;
    public static DatagramSocket socketReceive;

    public StudentSocket() {
        try {
            clientPortSend = rand.nextInt(100)+8000;
            clientPortReceive = clientPortSend - 1000;
            System.out.println("Client port send: " + clientPortSend);
            socketSend = new DatagramSocket(clientPortSend);
            socketReceive = new DatagramSocket(clientPortReceive);
        } catch (SocketException e) {
            System.out.println("Lỗi khi khởi tạo Socket: " + e.getMessage());
        }
    }
    public void sendExitForTeacher() throws Exception {
        System.out.println("Send exit for teacher");
        UdpHandler.sendRequestExitToTeacher(new DatagramSocket(1234),studentId,InetAddress.getByName(serverHostName),serverPort);
        camera.release();
        socketReceive.close();
        socketSend.close();
    }
    public void LiveStream(String code) throws IOException {
        while (true) {
            UdpHandler.sendMsg(socketSend,"connect",InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi thành công chuỗi connect đến server!");
            UdpHandler.sendMsg(socketSend,"student",InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi role student!");
            UdpHandler.sendMsg(socketSend,code,InetAddress.getByName(serverHostName),serverPort);
            System.out.println("Gửi mã " + code + " cuộc thi thành công!");
            String message = UdpHandler.receiveMsg(socketSend,InetAddress.getByName(serverHostName),serverPort);
            System.out.println(message);
            if (message.equals("fail")) {
                System.out.println("Try again.");
                continue;
            }
            break;
        }
        studentId += UdpHandler.receivePort(socketSend);
        serverPort += UdpHandler.receivePort(socketSend);
        System.out.println("Port mới là: " + StudentSocket.serverPort);
        new Thread(new StudentTaskUdp(socketSend, socketReceive)).start();
    }
}
class StudentTaskUdp extends Thread {
    private DatagramSocket socketSend;
    private DatagramSocket socketRecieve;
    private static int captureFromCamera = 0;
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

                        String request = UdpHandler.receiveMsg(socketRecieve,InetAddress.getByName(StudentSocket.serverHostName),StudentSocket.serverPort);
                        System.out.println("msg: " + request);
                        if (request.equals("camera")) captureFromCamera ^= 1;
                        else if (request.equals("exit")) {
                            socketSend.close();
                            socketRecieve.close();
                            StudentSocket.camera.release();
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
            System.out.println("Error: " + e.getMessage());
        } finally {
            StudentSocket.camera.release();
        }
    }

    private static void captureImages(DatagramSocket socket,VideoCapture camera) throws Exception {

        while (true) {
            ++StudentSocket.imageCount;
            System.out.println("Gửi ảnh thứ " + StudentSocket.imageCount);
            if (captureFromCamera == 1) {
                if (!camera.isOpened()) {
                    System.err.println("Error: Không mở được camera!");
                    return;
                }
                Mat frame = new Mat();
                if (camera.read(frame)) {
                    if (!frame.empty()) {
                        sendImage(socket, frame);
                    } else {
                        System.out.println("Frame rỗng!");
                    }
                } else {
                    System.out.println("Không thể đọc từ camera!");
                    Thread.sleep(10);
                }
            } else {
                BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                sendImage(socket, screenCapture);
            }
        }
    }

    private static void sendImage(DatagramSocket socket, Mat frame) throws IOException {
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB); // Đảm bảo đúng định dạng màu
        sendImage(socket, matToBufferedImage(frame));
    }

    private static void sendImage(DatagramSocket socket, BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos); // Sử dụng định dạng JPEG
            byte[] imageBytes = baos.toByteArray();
            System.out.println("Đã gửi ảnh kích thước: " + imageBytes.length + " bytes");
            sendPacketImage(socket, imageBytes);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = new byte[mat.width() * mat.height() * (int) mat.elemSize()];
        mat.get(0, 0, data);
        image.getRaster().setDataElements(0, 0, mat.width(), mat.height(), data);
        return image;
    }

    private static void sendPacketImage(DatagramSocket socket, byte[] imageByteArray) throws Exception {

        //System.out.println("Bắt đầu gửi ảnh");
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
            // Create message
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
            Thread.sleep(1);
        }
        StudentSocket.imageId = (StudentSocket.imageId + 1) %  10;
    }

}
