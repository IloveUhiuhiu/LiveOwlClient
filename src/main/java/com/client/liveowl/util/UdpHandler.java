package com.client.liveowl.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpHandler {

    public static void sendBytesArray(DatagramSocket soc, byte[] bytesArr, InetAddress addr, int port) throws IOException {
        DatagramPacket packet = new DatagramPacket(bytesArr, bytesArr.length, addr, port);
        soc.send(packet);
    }
    public static void sendMsg(DatagramSocket soc,String msg, InetAddress addr, int port) throws IOException {
        DatagramPacket packet = new DatagramPacket(msg.getBytes(),msg.length(), addr, port);
        soc.send(packet);
    }
    public static int receivePort(DatagramSocket soc) throws IOException {
        byte[] messageBytes = new byte[1];
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);
        soc.receive(packet);
        return (messageBytes[0] & 0xff);
    }
    public static String receiveMsg(DatagramSocket soc) throws IOException {
        byte[] messageBytes = new byte[1024];
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);
        soc.receive(packet);
        return new String(packet.getData(), 0, packet.getLength());
    }
    public static void sendRequestCamera(DatagramSocket soc,String number, InetAddress addr, int port) throws IOException {
        byte[] bytesArr = new byte[9];
        bytesArr[0] = (byte)(2);
        System.arraycopy(number.getBytes(), 0, bytesArr, 1, 8);
        DatagramPacket packet = new DatagramPacket(bytesArr, bytesArr.length, addr, port);
        soc.send(packet);
    }
    public static void sendRequestExitToStudents(DatagramSocket soc,InetAddress addr, int port) throws IOException {
        byte[] bytesArr = new byte[1];
        bytesArr[0] = (byte)(3);
        DatagramPacket packet = new DatagramPacket(bytesArr, bytesArr.length, addr, port);
        soc.send(packet);
    }
    public static void sendRequestExitToTeacher(DatagramSocket soc,String clientId, InetAddress addr, int port) throws IOException {
        byte[] bytesArr = new byte[9];
        bytesArr[0] = (byte)(4);
        System.arraycopy(clientId.getBytes(), 0, bytesArr, 1, 8);
        DatagramPacket packet = new DatagramPacket(bytesArr, bytesArr.length, addr, port);
        soc.send(packet);
    }
    public static void receiveBytesArr(DatagramSocket soc,byte [] messageBytes) throws IOException {
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);
        soc.receive(packet);
    }


}
