package com.knowledgeVista.ImageCompressing;
import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
public class ImageResizer {
	 public static byte[] resizeImage(byte[] imageBytes, int width, int height) throws IOException {
	        // Convert byte array to BufferedImage
	        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
	        BufferedImage originalImage = ImageIO.read(bais);

	        // Create a resized image with specified dimensions
	        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

	        // Create a new BufferedImage to hold the resized image
	        BufferedImage bufferedResizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g2d = bufferedResizedImage.createGraphics();
	        g2d.drawImage(resizedImage, 0, 0, null);
	        g2d.dispose();

	        // Convert BufferedImage to byte array
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(bufferedResizedImage, "jpg", baos);
	        baos.flush();
	        byte[] resizedImageBytes = baos.toByteArray();
	        baos.close();

	        return resizedImageBytes;
	    }
	 public static byte[] resizeImage(MultipartFile file, int width, int height) throws IOException {
	        // Convert MultipartFile to BufferedImage
	        BufferedImage originalImage = ImageIO.read(file.getInputStream());

	        // Create a resized image with specified dimensions
	        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

	        // Create a new BufferedImage to hold the resized image
	        BufferedImage bufferedResizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g2d = bufferedResizedImage.createGraphics();
	        g2d.drawImage(resizedImage, 0, 0, null);
	        g2d.dispose();

	        // Convert BufferedImage to byte array
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(bufferedResizedImage, "jpg", baos);
	        baos.flush();
	        byte[] resizedImageBytes = baos.toByteArray();
	        baos.close();

	        return resizedImageBytes;
	    }

}
