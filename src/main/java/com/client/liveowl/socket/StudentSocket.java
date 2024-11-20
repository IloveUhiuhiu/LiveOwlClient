
package com.client.liveowl.socket;
import com.client.liveowl.util.UdpHandler;
import com.github.kwhat.jnativehook.NativeHookException;
import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;
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
    public static final String serverHostName = "192.168.1.21";
   // public static final String serverHostName = "localhost";
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
    public void sendExitNotificationToTeacher() throws Exception {
        System.out.println("Send exit for teacher");
        DatagramSocket socketExit = new DatagramSocket(8765);
        UdpHandler.sendRequestExitToTeacher(socketExit,studentId,InetAddress.getByName(serverHostName),serverPort);
        if (camera != null) camera.release();
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