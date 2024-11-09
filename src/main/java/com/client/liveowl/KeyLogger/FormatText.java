package com.client.liveowl.KeyLogger;

public class FormatText {
public static String formatInput(String input) {
    // Thay thế các từ khóa đặc biệt thành ký tự tương ứng
    input = input.replace("Space", " "); // Thay "Space" bằng khoảng trắng
    input = input.replace("Enter", "\n"); // Thay "Enter" bằng dấu xuống dòng
    return input;
}

public static void main(String[] args) {
    String input = "XINSpaceCHAOSpaceTOISpaceLASpaceCUCSpaceCUTSpaceDAYEnterVASpaceTOISpaceCXSpaceLASpaceCUCSpaceCUTSpaceTHUSpace2SpaceDAY";
    String formattedText = formatInput(input);
    System.out.println("Kết quả sau khi định dạng:");
    System.out.println(formattedText);
}
}
