package com.client.liveowl;

import com.client.liveowl.controller.JoinExamController;
import com.client.liveowl.model.ImageData;
import com.client.liveowl.util.UdpHandler;
import com.github.kwhat.jnativehook.NativeHookException;
import javafx.application.Platform;
import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import javax.imageio.*;
        import java.awt.*;
        import java.awt.image.BufferedImage;
import java.io.*;
        import java.net.*;
        import java.util.Random;
        import com.client.liveowl.util.UserHandler;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentSocket{
public static int maxDatagramPacketLength = 1500;
public static int serverPort = 9000;
//public static final String serverHostName = "192.168.110.194";
public static final String serverHostName = "127.0.0.1";
public static int clientPortSend = 8000;
public static int clientPortReceive = 7000;
public static int imageId = 0;
public static int studentId = 0;
public static int imageCount = 0;
public static Random rand = new Random();
public static boolean isLive = false;
public static int captureFromCamera = 0;
private static String CLIENT_ID;
private static final StringBuilder keyLogBuffer = new StringBuilder();
private static final int SEND_INTERVAL = 7000;
public static final int SERVER_PORT_LOGGER = 12345;
public static ConcurrentLinkedQueue<Image> cache = new ConcurrentLinkedQueue<>();
static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
}
public static final VideoCapture camera = null;
public static DatagramSocket socketSend;
public static DatagramSocket socketReceive;
public static DatagramSocket socketExit;
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
    socketExit = new DatagramSocket(8765);
    UdpHandler.sendRequestExitToTeacher(socketExit,studentId,InetAddress.getByName(serverHostName),serverPort);
    camera.release();
    socketReceive.close();
    socketSend.close();
    socketExit.close();
}
public boolean CheckConnect(String code) throws IOException {
    UdpHandler.sendMsg(socketSend,"connect",InetAddress.getByName(serverHostName),serverPort);
    System.out.println("Gửi thành công chuỗi connect đến server!");
    UdpHandler.sendMsg(socketSend,"student",InetAddress.getByName(serverHostName),serverPort);
    System.out.println("Gửi role student!");
    UdpHandler.sendMsg(socketSend,code,InetAddress.getByName(serverHostName),serverPort);
    System.out.println("Gửi mã " + code + " cuộc thi thành công!");
    String message = UdpHandler.receiveMsg(socketSend,InetAddress.getByName(serverHostName),serverPort);
    System.out.println(message);
    if (message.equals("fail")) {
        return false;
    }
    studentId += UdpHandler.receivePort(socketSend);
    serverPort += UdpHandler.receivePort(socketSend);
    System.out.println("Port mới là: " + StudentSocket.serverPort);
    return true;
}
private static void sendKeyData() {
    String dataToSend;
    synchronized (keyLogBuffer) {
        if (keyLogBuffer.length() == 0) {
            return; // Không có dữ liệu để gửi
        }
        dataToSend = keyLogBuffer.toString();
        keyLogBuffer.setLength(0); // Xóa bộ đệm sau khi lấy dữ liệu
    }

    try (Socket socket = new Socket(serverHostName, SERVER_PORT_LOGGER);
         PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
        CLIENT_ID = UserHandler.getUserId();
        System.out.println(CLIENT_ID);
        writer.println(CLIENT_ID);
        writer.println(dataToSend);
        System.out.println("Dữ liệu đã được gửi: " + dataToSend);
    } catch (IOException e) {
        System.out.println("Lỗi kết nối. Dữ liệu sẽ được gửi lại lần sau.");
        synchronized (keyLogBuffer) {
            keyLogBuffer.insert(0, dataToSend); // Thêm lại dữ liệu vào bộ đệm nếu gửi thất bại
        }
    }
}
public void LiveStream() throws IOException {
    new Thread(new StudentTaskUdp(socketSend, socketReceive)).start();
    try {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendKeyData();
            }
        }, 0, SEND_INTERVAL);

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);


        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            private boolean isShift = false;
        //    private boolean capsLockOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK); // Lấy trạng thái CAPS LOCK ban đầu

            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                String ketText = NativeKeyEvent.getKeyText(e.getKeyCode());
                keyLogBuffer.append(ketText + " ");
            }
        });
} catch (NativeHookException e) {
        throw new RuntimeException(e);
    }
}

    public static synchronized void updateCamera() {
    captureFromCamera^=1;
}
public static synchronized void updateLive() {
    isLive = !isLive;
}
public static synchronized int getCamera() {
    return captureFromCamera;
}
public static synchronized boolean getLive() {
    return isLive;
}
}
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
                    String request = UdpHandler.receiveMsg(socketRecieve,InetAddress.getByName(StudentSocket.serverHostName),StudentSocket.serverPort);
                    System.out.println("msg: " + request);
                    if (request.equals("camera")) StudentSocket.updateCamera();
                    else if (request.equals("exit")) {
                        socketSend.close();
                        socketRecieve.close();
                        StudentSocket.socketExit.close();
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
        StudentSocket.socketExit.close();
        if (StudentSocket.camera != null)StudentSocket.camera.release();
    }
}
private static void captureImages(DatagramSocket socket,VideoCapture camera){
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
        Image newImage = new Image(new ByteArrayInputStream(imageBytes));
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