package com.client.liveowl;

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

public class StudentSocket implements AutoCloseable{
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private static final int PORT = 9876;
    private static final String HOSTNAME = "localhost";
    private static boolean captureFromCamera = false;

    public StudentSocket(String host, int port) throws IOException {
        System.out.println("Connecting to " + host + ":" + port);
        socket = new Socket(host, port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

    }
    public void sendRequest(String message) {
        try {
            dos.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi gửi " + message);
        }
    }
    public String receiveResponse() {
        try {
            return dis.readUTF();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi nhập phản hổi!!!");
        }

    }    @Override
    public void close() throws IOException {
        if (dis != null) dis.close();
        if (dos != null) dos.close();
        if (socket != null) socket.close();
    }

    public void liveStream() {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            throw new RuntimeException("Không thể mở Camera!");
        }
        try {
            new Thread(() -> listenForRequests(socket)).start();
            dos = new DataOutputStream(socket.getOutputStream());
            captureImages(dos, camera);
        } catch (IOException e) {
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
            throw new RuntimeException("Lỗi trong khi lắng nghe bật camera!!!");
        }
    }

    private static void captureImages(DataOutputStream dos, VideoCapture camera) {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("Lỗi trong khi khởi tạo robot!!!");
        }
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

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException("Lỗi lệnh Sleep!!!");
            }
        }
    }

    private static void sendImage(DataOutputStream dos, Mat frame) {
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);
        sendImage(dos, matToBufferedImage(frame));
    }

    private static void sendImage(DataOutputStream dos, BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            dos.writeInt(imageBytes.length);
            dos.write(imageBytes);
            dos.flush();
            System.out.println("Đã gửi ảnh kích thước: " + imageBytes.length + " bytes");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi trong quá trình gửi ảnh!!!");
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
