package com.client.liveowl.KeyLogger;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

import static com.client.liveowl.AppConfig.serverHostName;


public class GetFile {
    String codes;
    int check;
    public void downloadFile(String id, String code) {
    String filepath = "E:\\Downloads\\liveowl\\src\\main\\java\\com\\client\\liveowl\\KeyLogger\\" + "\\" + id + ".txt";

    try (Socket soc = new Socket(serverHostName, 8888);
         DataInputStream dis = new DataInputStream(soc.getInputStream());
         DataOutputStream dos = new DataOutputStream(soc.getOutputStream())) {

        System.out.println("Đã kết nối thành công");
        dos.writeUTF(id);
        dos.writeUTF(code);
        System.out.println("Gửi thành công");
        String line;
        while (!(line = dis.readUTF()).equals("EOF")) {
            codes = line;
        }
//        SwingUtilities.invokeLater(() -> {
//            if (GraphicsEnvironment.isHeadless()) {
//                System.out.println("Môi trường headless: không thể hiển thị giao diện đồ họa.");
//                return;
//            }
//
//            JFrame frame = new JFrame("Mô phỏng quá trình gõ phím của " + id);
//            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.setSize(700, 800);
//            frame.setLayout(new BorderLayout());
//
//            JTextArea textArea = new JTextArea();
//            textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
//            JScrollPane scrollPane = new JScrollPane(textArea);
//            frame.add(scrollPane, BorderLayout.CENTER);
//
//            JButton button = new JButton("Bắt đầu");
//            frame.add(button, BorderLayout.SOUTH);
//
//            button.addActionListener(e -> {
//                textArea.requestFocus();
//                new Thread(() -> {
//                    try {
//                        String inputText = codes;
//                        xuly(inputText);
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }).start();
//            });
//
//            frame.setLocationRelativeTo(null);
//            frame.setVisible(true);
//        });
        SwingUtilities.invokeLater(() -> {
            if (GraphicsEnvironment.isHeadless()) {
                System.out.println("Môi trường headless: không thể hiển thị giao diện đồ họa.");
                return;
            }

            JFrame frame = new JFrame("Mô phỏng quá trình gõ phím của " + id);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(700, 800);
            frame.setLayout(new BorderLayout());

            JTextArea textArea = new JTextArea();
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());

            JButton startButton = new JButton("Bắt đầu");
            JButton endButton = new JButton("Kết thúc");

            buttonPanel.add(startButton);
            buttonPanel.add(endButton);

            frame.add(buttonPanel, BorderLayout.SOUTH);

            startButton.addActionListener(e -> {
                check = 1;
                textArea.requestFocus();
                new Thread(() -> {
                    try {
                        String inputText = codes;
                        xuly(inputText);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            });

            endButton.addActionListener(e -> {
                check = 0;
                frame.dispose(); // Đóng frame
            });

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });



    } catch (IOException e) {
        System.out.println("Lỗi: " + e.getMessage());
    }
}

public void xuly(String input) throws AWTException {
    String[] resultArray = input.split(" ");
    StringBuilder code = new StringBuilder();
    Robot robot = new Robot();

    for (int i = 0; i < resultArray.length; i++) {
        String element = resultArray[i];
        if(check == 0)
            break;
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
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_9);
                        robot.keyRelease(KeyEvent.VK_9);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++; // Bỏ qua "9" vì đã xử lý
                        continue;
                    case "0":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_0);
                        robot.keyRelease(KeyEvent.VK_0);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "1":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_1);
                        robot.keyRelease(KeyEvent.VK_1);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "2":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_2);
                        robot.keyRelease(KeyEvent.VK_2);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "3":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_3);
                        robot.keyRelease(KeyEvent.VK_3);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "4":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_4);
                        robot.keyRelease(KeyEvent.VK_4);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "5":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_5);
                        robot.keyRelease(KeyEvent.VK_5);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "6":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_6);
                        robot.keyRelease(KeyEvent.VK_6);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "7":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_7);
                        robot.keyRelease(KeyEvent.VK_7);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "8":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_8);
                        robot.keyRelease(KeyEvent.VK_8);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "Comma":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_COMMA);
                        robot.keyRelease(KeyEvent.VK_COMMA);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "Period":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_PERIOD);
                        robot.keyRelease(KeyEvent.VK_PERIOD);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "Slash":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_SLASH);
                        robot.keyRelease(KeyEvent.VK_SLASH);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "Semicolon":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_SEMICOLON);
                        robot.keyRelease(KeyEvent.VK_SEMICOLON);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "Quote":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_QUOTE);
                        robot.keyRelease(KeyEvent.VK_QUOTE);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "Minus":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_MINUS);
                        robot.keyRelease(KeyEvent.VK_MINUS);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "Open":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_OPEN_BRACKET);
                        robot.keyRelease(KeyEvent.VK_OPEN_BRACKET);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i += 2;
                        continue;
                    case "Back":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_BACK_SLASH);
                        robot.keyRelease(KeyEvent.VK_BACK_SLASH);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i += 2;
                        continue;
                    case "Equals":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_EQUALS);
                        robot.keyRelease(KeyEvent.VK_EQUALS);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                    case "Close":
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(KeyEvent.VK_CLOSE_BRACKET);
                        robot.keyRelease(KeyEvent.VK_CLOSE_BRACKET);
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i += 2;
                        continue;
                    default:
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(nextElement.toUpperCase().charAt(0));
                        robot.keyRelease(nextElement.toUpperCase().charAt(0));
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        i++;
                        continue;
                }
            }
            continue;
        }

        switch (element) {
            case "Space":
                robot.keyPress(KeyEvent.VK_SPACE);
                robot.keyRelease(KeyEvent.VK_SPACE);
                break;
            case "Enter":
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
                break;
            case "Equals":
                robot.keyPress(KeyEvent.VK_EQUALS);
                robot.keyRelease(KeyEvent.VK_EQUALS);
                break;
            case "Semicolon":
                robot.keyPress(KeyEvent.VK_SEMICOLON);
                robot.keyRelease(KeyEvent.VK_SEMICOLON);
                break;
            case "Comma":
                robot.keyPress(KeyEvent.VK_COMMA);
                robot.keyRelease(KeyEvent.VK_COMMA);
                break;
            case "Period":
                robot.keyPress(KeyEvent.VK_PERIOD);
                robot.keyRelease(KeyEvent.VK_PERIOD);
                break;
            case "Slash":
                robot.keyPress(KeyEvent.VK_SLASH);
                robot.keyRelease(KeyEvent.VK_SLASH);
                break;
            case "Quote":
                robot.keyPress(KeyEvent.VK_QUOTE);
                robot.keyRelease(KeyEvent.VK_QUOTE);
                break;
            case "Minus":
                robot.keyPress(KeyEvent.VK_MINUS);
                robot.keyRelease(KeyEvent.VK_MINUS);
                break;
            case "Up":
                robot.keyPress(KeyEvent.VK_UP);
                robot.keyRelease(KeyEvent.VK_UP);
                break;
            case "Down":
                robot.keyPress(KeyEvent.VK_DOWN);
                robot.keyRelease(KeyEvent.VK_DOWN);
                break;
            case "Right":
                robot.keyPress(KeyEvent.VK_RIGHT);
                robot.keyRelease(KeyEvent.VK_RIGHT);
                break;
            case "Left":
                robot.keyPress(KeyEvent.VK_LEFT);
                robot.keyRelease(KeyEvent.VK_LEFT);
                break;
            case "Backspace":
                robot.keyPress(KeyEvent.VK_BACK_SPACE);
                robot.keyRelease(KeyEvent.VK_BACK_SPACE);
                break;
            default:
                robot.keyPress(element.toUpperCase().charAt(0));
                robot.keyRelease(element.toUpperCase().charAt(0));
                break;
        }
        robot.delay(150);
    }
}
//public static void main(String[] args) {
//    GetFile downloadFile = new GetFile();
//    downloadFile.downloadFile("fd720a2e");
//}
}
