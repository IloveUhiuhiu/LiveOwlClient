package com.client.liveowl.KeyLogger;

import java.io.*;
import java.net.Socket;

public class FTPDownloadFile {
    public static void main(String[] args) {
        try (Socket soc = new Socket("localhost", 8888); // Đổi thành cổng server
             BufferedInputStream bufferedInputStream = new BufferedInputStream(soc.getInputStream());
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("D:/dowloadfile.txt"))) {

            System.out.println("Đã kết nối thành công");

            // Gửi yêu cầu
            DataOutputStream pw = new DataOutputStream(soc.getOutputStream());
            pw.writeUTF("getFile");
            System.out.println("Gửi thành công");

            // Nhận file
            int c;
            while ((c = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(c);
            }

            System.out.println("Nhận file thành công");
        } catch (IOException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }
}
