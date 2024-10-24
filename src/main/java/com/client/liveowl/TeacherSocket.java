package com.client.liveowl;
import com.client.liveowl.controller.LiveController;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;




public class TeacherSocket{
    public static int SERVER_PORT = 9000;
    public static String SERVER_HOSTNAME = "localhost";
    public static int CLIENT_PORT_SEND = 6000;
    public static int CLIENT_PORT_RECIEVE = 5000;
    DatagramSocket socket;
    DatagramSocket socketButton;
    public TeacherSocket() {
        try {
            socket = new DatagramSocket(CLIENT_PORT_SEND);
            socketButton = new DatagramSocket(CLIENT_PORT_RECIEVE);
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
    public void LiveStream(String code, LiveController liveController) throws IOException {
        sendRequest("connect");
        System.out.println("Gửi thành công chuỗi connect đến server!");
        sendRequest("teacher");
        System.out.println("Gửi role teacher!");
        sendRequest(code);
        System.out.println("Gửi mã " + code + " cuộc thi thành công!");
        int newPort = receivePort();
        System.out.println("Port mới là :" + newPort);
        System.out.println("Chờ mọi người tham gia!");
        new Thread(new TeacherTaskUdp(socket,socketButton,liveController)).start();
    }


}

//Long
class TeacherTaskUdp extends Thread {
    DatagramSocket socket;
    DatagramSocket socketButton;
    LiveController liveController;

    public TeacherTaskUdp(DatagramSocket socket, DatagramSocket socketButton ,LiveController liveController) {
        this.socket = socket;
        this.socketButton = socketButton;
        this.liveController = liveController;

    }
    public void sendRequest(String message, InetAddress address, int port) throws IOException {
        byte[] messageBytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, port);
        socket.send(packet);
    }
    public void sendRequestButton(String message) throws IOException {
        InetAddress address = InetAddress.getByName(TeacherSocket.SERVER_HOSTNAME);
        int port = TeacherSocket.SERVER_PORT + 50;
        byte[] messageBytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, port);
        socketButton.send(packet);
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Bắt đầu nhận ảnh");
                boolean flag; // Have we reached end of file
                int sequenceNumber = 0; // Order of sequences
                int foundLast = 0; // The las sequence found
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] numberBytes = new byte[1];
                DatagramPacket packet = new DatagramPacket(numberBytes,numberBytes.length);
                socket.receive(packet);
                int number = (numberBytes[0] & 0xff);
                System.out.println("Học sinh thứ " + number + "gửi ảnh!");
                while (true) {
                    byte[] message = new byte[1024];
                    DatagramPacket receivedPacket;
                    try {
                        receivedPacket = new DatagramPacket(message, message.length);
                        socket.receive(receivedPacket);

                    } catch (SocketTimeoutException e) {

                        System.err.println("Timeout receiving packet: " + e.getMessage());
                        // Xử lý timeout (ví dụ: gửi lại yêu cầu hoặc báo lỗi)
                        continue; // Bỏ qua lần lặp hiện tại và thử lại
                    } catch (IOException e) {
                        System.err.println("Error receiving packet: " + e.getMessage());
                        throw e; // Re-throw the exception
                    }


                    sequenceNumber = ((message[0] & 0xff) << 8) + (message[1] & 0xff);
                    boolean isLastPacket = ((message[2] & 0xff) == 1) && foundLast > 10;


                    if (sequenceNumber == foundLast + 1) {

                        foundLast = sequenceNumber;
                        baos.write(message, 3, receivedPacket.getLength() - 3);
                        sendAck(foundLast);
                        System.out.println("Received: Sequence number: " + foundLast);

                    } else {
                        System.out.println("Expected sequence number: " + (foundLast + 1) + " but received " + sequenceNumber + ". DISCARDING");
                        sendAck(foundLast); // Gửi lại ACK cho gói tin cuối cùng nhận được thành công.
                    }

                    if (isLastPacket) {
                        break;
                    }
                }

                System.out.println("Nhận thành công 1 ảnh!");


                // Chuyển đổi byte[] thành BufferedImage

                byte[] imageBytes = baos.toByteArray();
                Image image = null;
                if (imageBytes != null && imageBytes.length > 0) {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                    image = new Image(inputStream); // Tạo đối tượng Image từ InputStream
                }
                if (number < LiveController.imageViews.size()) {
                    LiveController.imageViews.get(number).setImage(image);
                } else {
                    ImageView imageView = new ImageView(image);
                    LiveController.imageViews.add(imageView);
                    Button buttonView = new Button("TurnOn/TurnOff");
                    LiveController.buttonViews.add(buttonView);
                    buttonView.setOnAction(event -> {
                        try {
                            InetAddress address = InetAddress.getByName(TeacherSocket.SERVER_HOSTNAME);
                            int port = TeacherSocket.SERVER_PORT + 50;
                            byte[] numberOfClient = new byte[1];
                            numberOfClient[0] = (byte) number;
                            DatagramPacket packetOfClient = new DatagramPacket(numberOfClient, numberOfClient.length, address, port);
                            socketButton.send(packetOfClient);

                            if (LiveController.isCamera == false) sendRequestButton("Yes");
                            else sendRequestButton("No");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    });
                }
                Platform.runLater(() -> liveController.updateGridImage());

            }

        } catch (IOException | RuntimeException e) {
            // Lỗi java.io.EOFException
            System.err.println(e);
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private void sendAck(int foundLast) throws IOException {
        InetAddress address = InetAddress.getByName(TeacherSocket.SERVER_HOSTNAME);
        int port = TeacherSocket.SERVER_PORT;
        // send acknowledgement
        byte[] ackPacket = new byte[2];
        ackPacket[0] = (byte) (foundLast >> 8);
        ackPacket[1] = (byte) (foundLast);
        // the datagram packet to be sent
        DatagramPacket acknowledgement = new DatagramPacket(ackPacket, ackPacket.length, address, port);
        socket.send(acknowledgement);
        System.out.println("Sent ack: Sequence Number = " + foundLast);
    }
}


//Hung
//class TeacherTaskUdp extends Thread {
//    DatagramSocket socket;
//    DatagramSocket socketButton;
//    LiveController liveController;
//    private PrintWriter writer;
//
//    public TeacherTaskUdp(DatagramSocket socket, DatagramSocket socketButton, LiveController liveController) {
//        this.socket = socket;
//        this.socketButton = socketButton;
//        this.liveController = liveController;
//
//        // Mở file để ghi ký tự từ học sinh
//        try {
//            writer = new PrintWriter(new FileWriter("student_messages.txt", true), true); // true để append vào file
//        } catch (IOException e) {
//            System.err.println("Lỗi khi tạo file ghi: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void run() {
//        try {
//            while (true) {
//                System.out.println("Bắt đầu nhận dữ liệu từ server");
//
//                // Nhận số thứ tự học sinh
//                byte[] numberBytes = new byte[1];
//                DatagramPacket packet = new DatagramPacket(numberBytes, numberBytes.length);
//                socket.receive(packet);
//                int studentNumber = (numberBytes[0] & 0xff);
//                System.out.println("Dữ liệu nhận từ học sinh thứ: " + studentNumber);
//
//                // Nhận loại dữ liệu (ảnh hoặc ký tự)
//                byte[] typeBytes = new byte[1];
//                packet = new DatagramPacket(typeBytes, typeBytes.length);
//                socket.receive(packet);
//                int dataType = (typeBytes[0] & 0xff);
//
//                if (dataType == 0) {
//                    // Nhận ảnh từ học sinh
//                    receiveImage(studentNumber);
//                } else if (dataType == 1) {
//                    // Nhận ký tự từ học sinh
//                    receiveMessage(studentNumber);
//                }
//            }
//
//        } catch (IOException e) {
//            System.err.println(e);
//        } finally {
//            try {
//                socket.close();
//                if (writer != null) writer.close();
//            } catch (Exception e) {
//                // Ignore
//            }
//        }
//    }
//
//    private void receiveImage(int studentNumber) throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        int sequenceNumber = 0;
//        int foundLast = 0;
//        while (true) {
//            byte[] message = new byte[1024];
//            DatagramPacket receivedPacket = new DatagramPacket(message, message.length);
//            socket.receive(receivedPacket);
//
//            sequenceNumber = ((message[0] & 0xff) << 8) + (message[1] & 0xff);
//            boolean isLastPacket = ((message[2] & 0xff) == 1) && foundLast > 10;
//
//            if (sequenceNumber == foundLast + 1) {
//                foundLast = sequenceNumber;
//                baos.write(message, 3, receivedPacket.getLength() - 3);
//                sendAck(foundLast);
//                System.out.println("Received: Sequence number: " + foundLast);
//            } else {
//                System.out.println("Expected sequence number: " + (foundLast + 1) + " but received " + sequenceNumber + ". DISCARDING");
//                sendAck(foundLast);
//            }
//
//            if (isLastPacket) {
//                break;
//            }
//        }
//
//        byte[] imageBytes = baos.toByteArray();
//        Image image = null;
//        if (imageBytes != null && imageBytes.length > 0) {
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
//            image = new Image(inputStream); // Tạo đối tượng Image từ InputStream
//        }
//        if (studentNumber < LiveController.imageViews.size()) {
//            LiveController.imageViews.get(studentNumber).setImage(image);
//        } else {
//            ImageView imageView = new ImageView(image);
//            LiveController.imageViews.add(imageView);
//            Button buttonView = new Button("TurnOn/TurnOff");
//            LiveController.buttonViews.add(buttonView);
//            buttonView.setOnAction(event -> {
//                try {
//                    sendRequestButton(studentNumber);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        }
//        Platform.runLater(() -> liveController.updateGridImage());
//    }
//
//    private void receiveMessage(int studentNumber) throws IOException {
//        // Nhận tin nhắn từ server (dữ liệu học sinh)
//        byte[] messageBuffer = new byte[1024];
//        DatagramPacket messagePacket = new DatagramPacket(messageBuffer, messageBuffer.length);
//        socket.receive(messagePacket);
//        String message = new String(messagePacket.getData(), 0, messagePacket.getLength(), "UTF-8");
//        System.out.println("Nhận tin nhắn từ học sinh " + studentNumber + ": " + message);
//
//        // Lưu tin nhắn vào file
//        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        writer.println("[" + timestamp + "] Học sinh " + studentNumber + ": " + message);
//    }
//
//    private void sendRequestButton(int studentNumber) throws IOException {
//        InetAddress address = InetAddress.getByName(TeacherSocket.SERVER_HOSTNAME);
//        int port = TeacherSocket.SERVER_PORT + 50;
//        byte[] numberOfClient = new byte[1];
//        numberOfClient[0] = (byte) studentNumber;
//        DatagramPacket packetOfClient = new DatagramPacket(numberOfClient, numberOfClient.length, address, port);
//        socketButton.send(packetOfClient);
//    }
//
//    private void sendAck(int foundLast) throws IOException {
//        InetAddress address = InetAddress.getByName(TeacherSocket.SERVER_HOSTNAME);
//        int port = TeacherSocket.SERVER_PORT;
//        byte[] ackPacket = new byte[2];
//        ackPacket[0] = (byte) (foundLast >> 8);
//        ackPacket[1] = (byte) (foundLast);
//        DatagramPacket acknowledgement = new DatagramPacket(ackPacket, ackPacket.length, address, port);
//        socket.send(acknowledgement);
//        System.out.println("Sent ack: Sequence Number = " + foundLast);
//    }
//}