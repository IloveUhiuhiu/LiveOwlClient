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
import java.util.Random;
import java.util.logging.Logger;

public class StudentSocket{

    public static int SERVER_PORT = 9000;
    public static final String SERVER_HOSTNAME = "127.0.0.1";
    public static int CLIENT_PORT_SEND = 8000;
    public static int CLIENT_PORT_RECEIVE = 7000;
    public static final int LENGTH = 32768;
    public static int ID_IMAGE = 0;
    public static int COUNT = 0;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static final VideoCapture camera = new VideoCapture(0);
    private DatagramSocket socketSend;
    private DatagramSocket socketReceive;

    public StudentSocket() {
        try {
            Random rand = new Random();
            CLIENT_PORT_SEND = rand.nextInt(100)+8000;
            CLIENT_PORT_RECEIVE = CLIENT_PORT_SEND - 1000;
            System.out.println("Client port send: " + CLIENT_PORT_SEND);
            socketSend = new DatagramSocket(CLIENT_PORT_SEND);
            socketReceive = new DatagramSocket(CLIENT_PORT_RECEIVE);
        } catch (SocketException e) {
            System.out.println("Lỗi khi khởi tạo Socket: " + e.getMessage());
        }
    }

    public void sendMsg(String message) throws IOException {
        InetAddress address = InetAddress.getByName(SERVER_HOSTNAME);
        byte[] messageBytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, SERVER_PORT);
        socketSend.send(packet);
    }


    public void receivePort() throws IOException {
        byte[] messageBytes = new byte[1];
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);
        socketSend.receive(packet);
        SERVER_PORT += (messageBytes[0] & 0xff);
    }

    public String receiveMsg() throws IOException {
        byte[] messageBytes = new byte[1024];
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);
        socketSend.receive(packet);
        return new String(packet.getData(), 0, packet.getLength());
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
        receivePort();
        System.out.println("Port mới là: " + StudentSocket.SERVER_PORT);
        new Thread(new StudentTaskUdp(socketSend, socketReceive)).start();
    }


}
class StudentTaskUdp extends Thread {
    DatagramSocket socketSend;
    DatagramSocket socketRecieve;
    private static int captureFromCamera = 0;
    private static Robot robot = null;
    public StudentTaskUdp(DatagramSocket socketSend, DatagramSocket socketRecieve)  {
        this.socketSend = socketSend;
        this.socketRecieve = socketRecieve;
    }

    public String receiveMsg() throws IOException {
        byte[] messageBytes = new byte[1024];
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);
        socketRecieve.receive(packet);
        return new String(packet.getData(),0,packet.getLength());
    }

    @Override
    public void run() {
        try {
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String request = receiveMsg();
                        if (request.equals("camera")) captureFromCamera ^= 1;
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
            ++StudentSocket.COUNT;
            System.out.println("Gửi ảnh thứ " + StudentSocket.COUNT);
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
        boolean flag;
        InetAddress address = InetAddress.getByName(StudentSocket.SERVER_HOSTNAME);
        int port = StudentSocket.SERVER_PORT;
        byte[] lengthBytes = new byte[6];
        int length = imageByteArray.length;

        lengthBytes[0] = (byte) (0);
        lengthBytes[1] = (byte) (StudentSocket.ID_IMAGE);
        lengthBytes[2] = (byte) (length >> 16);
        lengthBytes[3] = (byte) (length >> 8);
        lengthBytes[4] = (byte) (length);
        lengthBytes[5] = (byte) ((length + StudentSocket.LENGTH - 5)/(StudentSocket.LENGTH - 4));
        DatagramPacket packet = new DatagramPacket(lengthBytes, lengthBytes.length, address, port);
        socket.send(packet);

        for (int i = 0; i < imageByteArray.length; i = i + StudentSocket.LENGTH - 4) {
            sequenceNumber += 1;

            byte[] message = new byte[StudentSocket.LENGTH];
            message[0] = (byte) (1);
            message[1] = (byte) (StudentSocket.ID_IMAGE);
            message[2] = (byte) (sequenceNumber);

            if ((i + StudentSocket.LENGTH - 4) >= imageByteArray.length) {
                flag = true;
                message[3] = (byte) (1);
            } else {
                flag = false;
                message[3] = (byte) (0);
            }


            if (!flag) {
                System.arraycopy(imageByteArray, i, message, 4, StudentSocket.LENGTH - 4);
            } else {
                System.arraycopy(imageByteArray, i, message, 4, imageByteArray.length - i);
            }

            DatagramPacket sendPacket = new DatagramPacket(message, message.length, address, port);
            socket.send(sendPacket); // Sending the data
            Thread.sleep(1);
            //System.out.println("Gửi thành công packet thứ :" + sequenceNumber);
        }

        StudentSocket.ID_IMAGE = (StudentSocket.ID_IMAGE + 1) %  200;
    }
}
