package com.client.liveowl;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSocket implements AutoCloseable{
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private static final int PORT = 9876;
    private static final String HOSTNAME = "localhost";
    private static volatile boolean captureFromCamera = false;

    public ClientSocket(String host, int port) throws IOException {
        System.out.println("Connecting to " + host + ":" + port);
        socket = new Socket(host, port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

    }

    public void sendMessage(String message) {
        // gửi message
        try {
            dos.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi gửi " + message);
        }
    }

    public String receiveResponse() throws IOException {
        // nhận phản hồi
        try {
            return dis.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void close() throws IOException {
        // Đóng kết nối
        if (dis != null) dis.close();
        if (dos != null) dos.close();
        if (socket != null) socket.close();
    }

    public void liveStream() {
        System.out.println("Vào liveStream");
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            throw new RuntimeException("Không thể mở Camera!");
        }
        try {
            new Thread(() -> listenForRequests(socket)).start();
            dos = new DataOutputStream(socket.getOutputStream());
            captureImages(dos, camera);
        } catch (IOException | AWTException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            camera.release();
        }
    }
    private static void listenForRequests(Socket socket) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            while (true) {
                String request = dis.readUTF();
                captureFromCamera = request.equals("startCamera");
            }
        } catch (IOException e) {
            System.err.println("Error listening required: " + e.getMessage());
        }
    }

    private static void captureImages(DataOutputStream dos, VideoCapture camera) throws AWTException, IOException, InterruptedException {
        Robot robot = new Robot();

        while (true) {
            if (captureFromCamera) {
                Mat frame = new Mat();
                if (camera.read(frame) && !frame.empty()) {
                    sendImage(dos, frame);
                }
            } else {
                BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                sendImage(dos, screenCapture);
            }
            // 0.1s gửi một ảnh
            Thread.sleep(100);
        }
    }

    private static void sendImage(DataOutputStream dos, Mat frame) throws IOException {
        // Chuyển sang định dạng  màu RGB
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);
        sendImage(dos, matToBufferedImage(frame));
    }

    private static void sendImage(DataOutputStream dos, BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos); // Sử dụng định dạng JPEG
            byte[] imageBytes = baos.toByteArray();

            dos.writeInt(imageBytes.length);
            dos.write(imageBytes);
            dos.flush();
            System.out.println("Đã gửi ảnh kích thước: " + imageBytes.length + " bytes");
        }
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = new byte[mat.width() * mat.height() * (int) mat.elemSize()];
        mat.get(0, 0, data);
        image.getRaster().setDataElements(0, 0, mat.width(), mat.height(), data);
        return image;
    }
}

