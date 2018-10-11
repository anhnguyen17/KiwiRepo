package utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class UtilsForOpenCV {

	/**
	 * Converts an OpenCV Mat (matrix) image into a JavaFX Image object.
	 * @param matImg - the OpenCV matrix to convert
	 * @return an equivalent JavaFX Image
	 */
	public static Image matToJavaFXImage(Mat matImg) {
		// Note: the code in this method is thanks to Luigi De Russis and/or Alberto Sacco
		// https://github.com/opencv-java/getting-started/tree/master/FXHelloCV
		int width = matImg.width(), height = matImg.height(), channels = matImg.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		matImg.get(0, 0, sourcePixels);

		BufferedImage image = null;
		if (matImg.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return SwingFXUtils.toFXImage(image, null);		
	}
}
