package com.client.liveowl;

public class AppConfig {
    public static int MAX_DATAGRAM_PACKET_LENGTH = 1500;
    public static int SERVER_PORT = 9000;
    public static int VIDEO_SERVER_PORT = 1604;
    public static String SERVER_HOST_NAME = "127.0.0.1";
    public static int STUDENT_PORT = 8000;
    public static int TEACHER_PORT = 6000;
    public static final int SEND_INTERVAL = 7000;
    public static final int SERVER_PORT_LOGGER = 12345;
    public static final String BASE_URI = "http://"+ SERVER_HOST_NAME +":9090";
}
