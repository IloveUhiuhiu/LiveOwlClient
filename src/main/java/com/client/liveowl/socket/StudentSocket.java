
package com.client.liveowl.socket;
import static com.client.liveowl.AppConfig.*;
import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.UdpHandler;
import com.github.kwhat.jnativehook.NativeHookException;
import org.opencv.videoio.VideoCapture;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentSocket{

    private static Random rand = new Random();
    private static final StringBuilder keyLogBuffer = new StringBuilder();
    //public static ConcurrentLinkedQueue<Image> cache = new ConcurrentLinkedQueue<>();
    public static final VideoCapture camera = null;
    private static DatagramSocket socketSend;
    private static DatagramSocket socketReceive;
    public static CountDownLatch latch;
    private static volatile boolean isLive = true;
    private int clientPortReceive;
    private int clientPortSend;
    public static int newServerPort;
    public StudentSocket() {
        try {
            latch = new CountDownLatch(1);
            newServerPort = serverPort;
            isLive = true;
            clientPortSend = rand.nextInt(999)+studentPort;
            clientPortReceive = clientPortSend - 1000;
            System.out.println("Client port: " + clientPortSend +", " + clientPortReceive);
            socketSend = new DatagramSocket(clientPortSend);
            socketReceive = new DatagramSocket(clientPortReceive);
        } catch (Exception e) {
            System.out.println("Lỗi khi khởi tạo Socket: " + e.getMessage());
        }
    }

    public boolean CheckConnect(String code) throws IOException {
        String connect = Authentication.getUserId() + ":student:" + code;
        System.out.println(socketSend + ", " + Authentication.getUserId() + serverPort);
        //UdpHandler.sendMsg(socketSend,Authentication.getUserId(),InetAddress.getByName(serverHostName),serverPort);
        System.out.println("Gửi thành công chuỗi connect đến server!");
        //UdpHandler.sendMsg(socketSend,"student",InetAddress.getByName(serverHostName),serverPort);
        System.out.println("Gửi role student!");
        UdpHandler.sendMsg(socketSend,connect,InetAddress.getByName(serverHostName),serverPort);
        System.out.println("Gửi mã " + code + " cuộc thi thành công!");
        //System.out.println(socketSend + ", " + serverHostName + ", " + serverPort);
        String message = UdpHandler.receiveMsg(socketSend);
        System.out.println(message);
        if (message.equals("fail")) {
            return false;
        }
        newServerPort += UdpHandler.receivePort(socketSend);
        System.out.println("Port mới là: " + newServerPort);
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
//            CLIENT_ID = UserHandler.getUserId();
            System.out.println(Authentication.getUserId());
            writer.println(Authentication.getUserId());
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
        Thread taskThread = new Thread(new StudentTaskUdp(socketSend, socketReceive));
        try {
            taskThread.start();
            taskThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
            cleanupResources();
        }
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
    private void cleanupResources() {
        if (socketReceive != null) socketReceive.close();
        if (socketSend != null) socketSend.close();
        if (camera != null) camera.release();
        setLive(false);
    }

    public static synchronized void setLive(boolean live) {isLive = live;}
    public static synchronized boolean isLive() {return isLive;}

}