package com.client.liveowl;

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

public class StudentSocket{
    public static int SERVER_PORT = 9000;
    public static String SERVER_HOSTNAME = "127.0.0.1";
    public static int CLIENT_PORT_SEND = 8000;
    public static int CLIENT_PORT_RECIEVE = 7000;
    public static int LENGTH = 32768;
    public static int ID = 0;
    DatagramSocket socketSend;
    DatagramSocket socketRecieve;
    static  {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static VideoCapture camera = new VideoCapture(0);
    public StudentSocket() {
        try {
            socketSend = new DatagramSocket(CLIENT_PORT_SEND);
            socketRecieve = new DatagramSocket(CLIENT_PORT_RECIEVE);
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
    public int receivePort() throws IOException {
        byte[] receive = new byte[1];
        DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
        socketSend.receive(receivePacket);
        SERVER_PORT += (receive[0] & 0xff);
        return SERVER_PORT;
    }
    public String receiveMsg() throws IOException {
        byte[] receive = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
        socketSend.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }

    public void LiveStream(String code) throws IOException {
        while (true) {
            sendMsg("connect");
            System.out.println("Gửi thành công chuỗi connect đến server!");
            sendMsg("student");
            System.out.println("Gửi role student!");
            sendMsg(code);
            System.out.println("Gửi mã " + code + " cuộc thi thành công!");
            String message = receiveMsg();
            System.out.println(message);
            if (message.equals("fail")) {
                System.out.println("Try again.");
                continue;
            }
            break;
        }
        int newPort = receivePort();
        System.out.println("Port mới là :" + newPort);
        System.out.println("Livestream thôi!");
        new Thread(new StudentTaskUdp(socketSend, socketRecieve)).start();
    }


}
class StudentTaskUdp extends Thread {
    DatagramSocket socketSend;
    DatagramSocket socketRecieve;
    private static int captureFromCamera = 0;

    public StudentTaskUdp(DatagramSocket socketSend, DatagramSocket socketRecieve) {
        this.socketSend = socketSend;
        this.socketRecieve = socketRecieve;
    }

    public String receiveMsg() throws IOException {
        byte[] receive = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
        socketRecieve.receive(receivePacket);
        return new String(receivePacket.getData(),0,receivePacket.getLength());
    }

    @Override
    public void run() {

        try {
            InetAddress address = InetAddress.getByName(StudentSocket.SERVER_HOSTNAME);
            int port = StudentSocket.SERVER_PORT;

            Thread thread = new Thread(()->{
                System.out.println("Lắng nghe bật camera!");
                try {
                    while (true) {
                        String request = receiveMsg();
                        if (request.equals("camera")) captureFromCamera ^= 1;
                        System.out.println("yêu cầu: " + request + " và " + captureFromCamera);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi trong khi lắng nghe bật camera: " + e.getMessage());
                }

            });
            thread.start();
            captureImages(socketSend, StudentSocket.camera);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.out.println("Server đang đóng...");
        } finally {
            System.out.println("Camera release");
            StudentSocket.camera.release();
        }

    }

    private static void captureImages(DatagramSocket socket,VideoCapture camera) throws AWTException, IOException, InterruptedException {
        Robot robot = new Robot();

        while (true) {
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

        System.out.println("Bắt đầu gửi ảnh");
        int sequenceNumber = 0; // For order
        boolean flag; // To see if we got to the end of the file
        InetAddress address = InetAddress.getByName(StudentSocket.SERVER_HOSTNAME);
        int port = StudentSocket.SERVER_PORT;
        byte[] lengthBytes = new byte[4];
        int length = imageByteArray.length;
        // Lưu độ dài vào mảng byte
        lengthBytes[0] = (byte) (StudentSocket.ID);
        lengthBytes[1] = (byte) (length >> 16);
        lengthBytes[2] = (byte) (length >> 8);
        lengthBytes[3] = (byte) (length);
        DatagramPacket packet = new DatagramPacket(lengthBytes, lengthBytes.length, address, port);
        socket.send(packet);

        for (int i = 0; i < imageByteArray.length; i = i + StudentSocket.LENGTH - 4) {
            sequenceNumber += 1;
            // Tạo packet nhỏ có độ dài 2^16 với 2 byte đầu lưu thứ tự và byte thứ 3 lưu kết thúc hay chưa
            byte[] message = new byte[StudentSocket.LENGTH];
            message[0] = (byte) (sequenceNumber >> 8);
            message[1] = (byte) (sequenceNumber);

            if ((i + StudentSocket.LENGTH - 4) >= imageByteArray.length) {
                flag = true;
                message[2] = (byte) (1);
            } else {
                flag = false;
                message[2] = (byte) (0);
            }
            message[3] = (byte) (StudentSocket.ID);

            if (!flag) {
                System.arraycopy(imageByteArray, i, message, 4, StudentSocket.LENGTH - 4);
            } else {
                System.arraycopy(imageByteArray, i, message, 4, imageByteArray.length - i);
            }

            DatagramPacket sendPacket = new DatagramPacket(message, message.length, address, port);
            socket.send(sendPacket); // Sending the data
            Thread.sleep(100);
            System.out.println("Gửi thành công packet thứ :" + sequenceNumber);
        }
        StudentSocket.ID += 1;
        StudentSocket.ID %= 10;
    }
}
