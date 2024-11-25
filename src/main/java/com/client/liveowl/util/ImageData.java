package com.client.liveowl.util;
import javafx.scene.image.Image;
import org.opencv.core.Mat;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class ImageData {

private String clientId;
private Image image;

public ImageData(String clientId, Image image) {
    this.clientId = clientId;
    this.image = image;
}

public String getClientId() {
    return clientId;
}

public Image getImage() {
    return image;
}

public static BufferedImage matToBufferedImage(Mat mat) {
    BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
    byte[] data = new byte[mat.width() * mat.height() * (int) mat.elemSize()];
    mat.get(0, 0, data);
    image.getRaster().setDataElements(0, 0, mat.width(), mat.height(), data);
    return image;
}
public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
    BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = resizedImage.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
    g.dispose();
    return resizedImage;
}
public static byte[] handleImage(BufferedImage originalImage) {
    //System.out.println(originalImage.getWidth() + ", " + originalImage.getHeight() + ", " + originalImage.getWidth()/ originalImage.getHeight());
    BufferedImage image = ImageData.resizeImage(originalImage, 768, 432);
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        float quality = 0.8f;
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);
        writer.setOutput(ImageIO.createImageOutputStream(baos));
        writer.write(null, new IIOImage(image, null, null), param);
        writer.dispose();
        return baos.toByteArray();
    } catch (Exception e) {
        throw new RuntimeException("Lỗi xử lý ảnh: " + e.getMessage());
    }
}
}