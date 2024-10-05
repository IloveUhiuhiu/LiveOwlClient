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
    DatagramSocket socket;
    DatagramSocket socket2;
    static  {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public StudentSocket() {
        try {
            socket = new DatagramSocket(CLIENT_PORT_SEND);
            socket2 = new DatagramSocket(CLIENT_PORT_RECIEVE);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendRequest(String message) throws IOException {
        InetAddress address = InetAddress.getByName(SERVER_HOSTNAME);
        int port = SERVER_PORT;
        byte[] messageBytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, port);
        socket.send(packet);
    }
    public int receivePort() throws IOException {
        byte[] receive = new byte[1];
        DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
        socket.receive(receivePacket);
        SERVER_PORT += (receive[0] & 0xff);
        return SERVER_PORT;
    }
    public void LiveStream(String code) throws IOException {
        sendRequest("connect");
        System.out.println("Gửi thành công chuỗi connect đến server!");
        sendRequest("student");
        System.out.println("Gửi role student!");
        sendRequest(code);
        System.out.println("Gửi mã " + code + " cuộc thi thành công!");
        int newPort = receivePort();
        System.out.println("Port mới là :" + newPort);
        System.out.println("Livestream thôi!");
        new Thread(new StudentTaskUdp(socket,socket2)).start();
    }


}
class StudentTaskUdp extends Thread {
    DatagramSocket socket;
    DatagramSocket socket2;
    private static volatile boolean captureFromCamera = false;

    public static VideoCapture camera = new VideoCapture(0);

    public StudentTaskUdp(DatagramSocket socket, DatagramSocket socket2) {
        this.socket = socket;
        this.socket2 = socket2;
    }
    public void sendRequest(String message, InetAddress address, int port) throws IOException {
        byte[] messageBytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, port);
        socket.send(packet);
    }

    @Override
    public void run() {

        try {
            InetAddress address = InetAddress.getByName(StudentSocket.SERVER_HOSTNAME);
            int port = StudentSocket.SERVER_PORT;
            if (!camera.isOpened()) {
                System.err.println("Error: Không mở được camera!");
                return;
            }
            Thread thread1 = new Thread(()->{
                try {
                    captureImages(socket, camera);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread thread2 = new Thread(()->{
                System.out.println("Lắng nghe bật camera!");
                try {
                    while (true) {
                        byte[] receive = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
                        socket2.receive(receivePacket);
                        byte [] data = receivePacket.getData();
                        String request = new String(data, 0, receivePacket.getLength());

                        captureFromCamera = request.equals("Yes");
                        System.out.println("yêu cầu: " + request + " và " + captureFromCamera);
                    }
                } catch (Exception e) {
                    System.err.println("Error listening required: " + e.getMessage());
                }

            });
            thread1.start();
            thread2.start();


        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.out.println("Server đang đóng...");
        } finally {
            camera.release();
        }

    }

    private static void captureImages(DatagramSocket socket,VideoCapture camera) throws AWTException, IOException, InterruptedException {
        Robot robot = new Robot();
        while (true) {
            if (captureFromCamera) {
                System.out.println("Vào camerra rồi nè!");
                Mat frame = new Mat();
                if (camera.read(frame) && !frame.empty()) {
                    sendImage(socket, frame);
                } else {
                    System.out.println("Có vấn de roi!!!");
                }
            } else {

                BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                sendImage(socket, screenCapture);
            }
            Thread.sleep(100);
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


        }
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = new byte[mat.width() * mat.height() * (int) mat.elemSize()];
        mat.get(0, 0, data);
        image.getRaster().setDataElements(0, 0, mat.width(), mat.height(), data);
        return image;
    }

    private static void sendPacketImage(DatagramSocket socket, byte[] imageByteArray) throws IOException {



//        int numberToSend = imageByteArray.length;
//        System.out.println("Sending length of image" + numberToSend);
//        byte[] requestPacket = new byte[4]; // Một số nguyên 4 byte
//        requestPacket[0] = (byte) (numberToSend >> 24);
//        requestPacket[1] = (byte) (numberToSend >> 16);
//        requestPacket[2] = (byte) (numberToSend >> 8);
//        requestPacket[3] = (byte) (numberToSend);
//        DatagramPacket requestDatagramPacket = new DatagramPacket(requestPacket, requestPacket.length, InetAddress.getByName(HOSTNAME), PORT);
//        socket.send(requestDatagramPacket);


        System.out.println("Bắt đầu gửi ảnh");
        int sequenceNumber = 0; // For order
        boolean flag; // To see if we got to the end of the file
        int ackSequence = 0; // To see if the datagram was received correctly

        for (int i = 0; i < imageByteArray.length; i = i + 1021) {
            sequenceNumber += 1;

            // Create message
            byte[] message = new byte[1024]; // First two bytes of the data are for control (datagram integrity and order)
            message[0] = (byte) (sequenceNumber >> 8);
            message[1] = (byte) (sequenceNumber);

            if ((i + 1021) >= imageByteArray.length) { // Have we reached the end of file?
                flag = true;
                message[2] = (byte) (1); // We reached the end of the file (last datagram to be send)
            } else {
                flag = false;
                message[2] = (byte) (0); // We haven't reached the end of the file, still sending datagrams
            }

            if (!flag) {
                System.arraycopy(imageByteArray, i, message, 3, 1021);
            } else { // If it is the last datagram
                System.arraycopy(imageByteArray, i, message, 3, imageByteArray.length - i);
            }
            InetAddress address = InetAddress.getByName(StudentSocket.SERVER_HOSTNAME);
            int port = StudentSocket.SERVER_PORT;
            DatagramPacket sendPacket = new DatagramPacket(message, message.length, address, port); // The data to be sent
            socket.send(sendPacket); // Sending the data
            System.out.println("Gửi thành công packet thứ :" + sequenceNumber);

            boolean ackRec; // Was the datagram received?

            while (true) {
                System.out.println("Gửi ack!");
                byte[] ack = new byte[2]; // Create another packet for datagram ackknowledgement
                DatagramPacket ackpack = new DatagramPacket(ack, ack.length);

                try {
                    socket.setSoTimeout(500); // Waiting for the server to send the ack
                    socket.receive(ackpack);
                    ackSequence = ((ack[0] & 0xff) << 8) + (ack[1] & 0xff); // Figuring the sequence number
                    System.out.println(ackSequence);
                    ackRec = true; // We received the ack
                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timed out waiting for ack");
                    ackRec = false; // We did not receive an ack
                }

                // If the package was received correctly next packet can be sent
                if ((ackSequence == sequenceNumber) && (ackRec)) {
                    System.out.println("Ack received: Sequence Number = " + ackSequence);
                    break;
                } // Package was not received, so we resend it
                else {
                    socket.send(sendPacket);
                    System.out.println("Resending: Sequence Number = " + sequenceNumber);
                }
            }
        }
    }
}
